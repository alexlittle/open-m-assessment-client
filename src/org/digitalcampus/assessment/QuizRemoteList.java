package org.digitalcampus.assessment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.digitalcampus.mquiz.model.DbHelper;
import org.digitalcampus.mquiz.model.Quiz;
import org.digitalcampus.mquiz.tasks.APIRequest;
import org.digitalcampus.mquiz.tasks.QuizDownloadedTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class QuizRemoteList extends ListActivity{

	private static final String TAG = "ManageQuizActivity";
	private ProgressDialog pDialog;
	SharedPreferences prefs;
	private Button actionBtn;
	private boolean downloadedQuizzes;
	public String[] checkedItems;
	private QuizAdapter qa;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        setContentView(R.layout.manage);
        downloadedQuizzes = false;
        
        actionBtn = (Button) findViewById(R.id.listquizBtn);
        actionBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		if(!downloadedQuizzes){
        			getList();
        		} else {
        			downloadQuizzes();
        		}
        	}
        });
        
    }
    protected void onStart(){
    	super.onStart();
    	getList();
    }
    
    private void getList(){
  
    	// show progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading");
        pDialog.setMessage("Getting list of quizzes");
        pDialog.setCancelable(true);
        pDialog.show();
        
    	// send results as AsyncTask
        GetQuizListTask task = new GetQuizListTask();
        String[] url = new String[1];
        url[0] = prefs.getString("prefServer", getString(R.string.prefServerDefault))+"list/";
        task.execute(url);
    }
    
    
    private class GetQuizListTask extends AsyncTask<String, String, String>{
    	
    	@Override
    	protected String doInBackground(String... urls){
    		
    		String toRet = "";
    		for (String u : urls) {
    			String response = "";
    			
    			HttpParams httpParameters = new BasicHttpParams();
    			int timeoutConnection = 10000;
    			try {
    				timeoutConnection = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
    			} catch (NumberFormatException e){
    				// do nothing - will remain as default as above
    				e.printStackTrace();
    			}
    			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    			int timeoutSocket = 10000;
    			try {
    				timeoutSocket= Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
    			} catch (NumberFormatException e){
    				// do nothing - will remain as default as above
    				e.printStackTrace();
    			}
    			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

    			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
    			Log.d(TAG,u);
    			HttpPost httpPost = new HttpPost(u);
    			try {
    				// add post params
    				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    				nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("prefUsername", "")));
    				nameValuePairs.add(new BasicNameValuePair("password", prefs.getString("prefPassword", "")));
    				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    				
    				// make request
					HttpResponse execute = client.execute(httpPost);
				
					// read response
					InputStream content = execute.getEntity().getContent();
					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
						Log.d(TAG,s);
					}
					
					toRet = response;
					
    			} catch (Exception e) {
    				e.printStackTrace();
					toRet = "Connection error or invalid response from server";
				}
			}
			return toRet;
    	}
    	
    	@Override
    	protected void onPostExecute(String response) {
    		// close dialog and process results
    		pDialog.dismiss();
			try {
				new JSONArray(response);
				parseResponse(response);
			} catch (JSONException e){
				Log.d(TAG,response);
				showErrorAlert(response);
				e.printStackTrace();
			}
			
    	}
    }
    
    private void parseResponse(String response){
    	//process the response and display on screen in listview
    	// Create an array of Strings, that will be put to our ListActivity
    	
    	try {
    		
    		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    		
    		JSONArray json = new JSONArray(response);
			
    		for(int i=0;i<(json.length());i++){
			    JSONObject json_obj=json.getJSONObject(i);
			    HashMap<String,String> item = new HashMap<String,String>();
			    
			    item.put("id", json_obj.getString("id"));
			    item.put("name", json_obj.getString("name"));
			    item.put("url",json_obj.getString("url"));
			    list.add(item);
			}
			
			qa = new QuizAdapter(this,
								list,
								R.layout.quizlist,
								new String[] {"name"},
								new int[] {R.id.name});
			
			this.setListAdapter(qa);
			
			// set button to be download items
			downloadedQuizzes = true;
			actionBtn.setText("Download selected");
			
		} catch (JSONException e){
			e.printStackTrace();
			showErrorAlert("Error processing server response");
		}
    	
		
    }
    
    private void showErrorAlert(String msg){
    	AlertDialog alertDialog = new AlertDialog.Builder(QuizRemoteList.this).create();
		alertDialog.setTitle("Error");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}});
		alertDialog.show();
    }
    
    private void downloadQuizzes(){
    	
    	// TODO this need tidying up - why looping through twice?

    	// find which quizzes have been marked for download
		Iterator<String> itr = qa.checkedQuizzes.keySet().iterator();
		
		int counter = 0;
		while(itr.hasNext()){
			String id = itr.next();
			if (qa.checkedQuizzes.get(id).isChecked()){
				counter++;	
			}
		}
		
		// if nothing selected then just return - no need to continue
		if (counter == 0){
			return;
		}
		
		Quiz[] quizzes = new Quiz[counter];
		itr = qa.checkedQuizzes.keySet().iterator();
		int c=0;
		while(itr.hasNext()){
			String id = itr.next();
			if (qa.checkedQuizzes.get(id).isChecked()){
				quizzes[c] = qa.checkedQuizzes.get(id);
				c++;
			}
		}
		
		// show progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Downloading");  
        pDialog.setMessage("Starting download");
        pDialog.setCancelable(true);
        pDialog.show();
		
        actionBtn.setEnabled(false);
        
        // send results as AsyncTask
        DownloadQuizzesTask task = new DownloadQuizzesTask();
		task.execute(quizzes);
    }
    
    private class DownloadResult{
    	String name;
    	String responseObj;
    }
    
    
    private class DownloadQuizzesTask extends AsyncTask<Quiz, String, List<DownloadResult>>{
    	
    	@Override
    	protected List<DownloadResult> doInBackground(Quiz... urls){
    		
    		List<DownloadResult> toRet = new ArrayList<DownloadResult>();
    		
    		for (Quiz u : urls) {
    			String response = "";
    			
    			HttpParams httpParameters = new BasicHttpParams();
    			int timeoutConnection = 10000;
    			try {
    				timeoutConnection = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
    			} catch (NumberFormatException e){
    				// do nothing - will remain as default as above
    				e.printStackTrace();
    			}
    			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    			int timeoutSocket = 10000;
    			try {
    				timeoutSocket= Integer.parseInt(prefs.getString("prefServerTimeoutResponse", "10000"));
    			} catch (NumberFormatException e){
    				// do nothing - will remain as default as above
    				e.printStackTrace();
    			}
    			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

    			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
    			
    			HttpPost httpPost = new HttpPost(u.getUrl());
    			try {
    				String msg = "Downloading '" + u.getTitle() + "'";
    				publishProgress(msg);
    				
    				// add post params
    				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    				nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("prefUsername", "")));
    				nameValuePairs.add(new BasicNameValuePair("password", prefs.getString("prefPassword", "")));
    				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    				
    				// make request
					HttpResponse execute = client.execute(httpPost);
					
					// read response
					InputStream content = execute.getEntity().getContent();
					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
					
					DownloadResult dr = new DownloadResult();
					dr.name	= u.getTitle();
					try {
	    				new JSONObject(response);
	    				dr.responseObj = response;
	    			} catch (JSONException e){
	    				dr.responseObj = response;
	    			}
					
					toRet.add(dr);
    				
    			} catch (Exception e) {
    				e.printStackTrace();
    				DownloadResult dr = new DownloadResult();
					dr.name	= u.getTitle();
					dr.responseObj = "Connection error or invalid response from server";
					toRet.add(dr);
				}
			}
			return toRet;
    	}
    	
    	@Override
    	protected void onProgressUpdate(String... strings){
    		super.onProgressUpdate(strings);
    		Log.d(TAG, "onProgressUpdate(): " +  String.valueOf( strings[0] ) );
    		pDialog.setMessage(strings[0]);

    	}
    	
    	@Override
    	protected void onPostExecute(List<DownloadResult> results) {
    		
    		pDialog.dismiss();
    
    		String t = "";
    		for (DownloadResult s : results){
    			try {
    				JSONObject json = new JSONObject(s.responseObj);
    				// add to database
    				DbHelper dbHelper = new DbHelper(QuizRemoteList.this);
    				boolean loaded = dbHelper.insertQuiz(json);
    				dbHelper.close();
    				if (loaded){
    					t += s.name+ ": successfully downloaded\n";
    					APIRequest[] dlQuizzes = new APIRequest[1]; 
        				APIRequest dlQuiz = new APIRequest();
        				String quizRefId = (String) json.get("refid");
        				dlQuiz.fullurl = prefs.getString("prefServer", "") + "api/?method=downloaded&quizref="+ quizRefId;
        				dlQuiz.username = prefs.getString("prefUsername", "");
        				dlQuiz.password = prefs.getString("prefPassword", "");
        				dlQuiz.timeoutConnection = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
        				dlQuiz.timeoutSocket= Integer.parseInt(prefs.getString("prefServerTimeoutResponse", "10000"));
        				dlQuizzes[0] = dlQuiz;	
        				QuizDownloadedTask task = new QuizDownloadedTask(QuizRemoteList.this);
        	     		task.execute(dlQuizzes);
    				} else {
    					t += s.name+ ": error parsing quiz\n";
    				}
    			} catch (JSONException e){
    				t += s.name+ ": failed ('"+ s.responseObj + "')\n";
    			}
    			Log.d(TAG, s.responseObj);
    		}
    		AlertDialog ab = new AlertDialog.Builder(QuizRemoteList.this).create();
    		ab.setTitle("Download Results");
    		ab.setMessage(t);
    		ab.setButton("Close", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dialog.cancel();
    			}});
    		ab.show();
    		
    		actionBtn.setEnabled(true);
			
    	}
    }
    
}
