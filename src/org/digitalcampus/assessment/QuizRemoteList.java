package org.digitalcampus.assessment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.digitalcampus.mquiz.tasks.DownloadQuizTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class QuizRemoteList extends ListActivity{

	private static final String TAG = "QuizRemoteList";
	private ProgressDialog pDialog;
	private SharedPreferences prefs;
	private Button actionBtn;
	private boolean downloadedQuizzes;
	private QuizAdapter qa;
	
	private ArrayList<Quiz> quizList;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        setContentView(R.layout.quizremotelist);
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
        url[0] = prefs.getString("prefServer", getString(R.string.prefServerDefault))+"api/?&format=json&method=list";
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
				showAlert("Error", response);
				e.printStackTrace();
			}
			
    	}
    }
    
    private void parseResponse(String response){
    	//process the response and display on screen in listview
    	// Create an array of Strings, that will be put to our ListActivity
    	
    	try {
    		
    		JSONArray json = new JSONArray(response);
    		Quiz[] quizzes = new Quiz[json.length()];
    		
    		for(int i=0;i<(json.length());i++){
			    JSONObject json_obj=json.getJSONObject(i);
			    
			    Quiz q = new Quiz(json_obj.getString("id"));
			    q.setTitle(json_obj.getString("name"));
			    q.setUrl(json_obj.getString("url"));
			    quizzes[i] = q;
	    
			}
    		
    		quizList = new ArrayList<Quiz>();  
    		quizList.addAll( Arrays.asList(quizzes) );
    		    
    		qa = new QuizAdapter(this, quizList);
			
			this.setListAdapter(qa);
			
			// set button to be download items
			downloadedQuizzes = true;
			actionBtn.setText("Download selected");
			
		} catch (JSONException e){
			e.printStackTrace();
			showAlert("Error","Error processing server response");
		}
    	
		
    }
    
    private void showAlert(String title, String msg){
    	AlertDialog alertDialog = new AlertDialog.Builder(QuizRemoteList.this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}});
		alertDialog.show();
    }
    
    private void downloadQuizzes(){
    	
    	int c = 0;
    	for(int i=0;i<quizList.size();i++){
    		Quiz q = (Quiz) (quizList.get(i));
    		if(q.isChecked()){
    			c++;
    		}
    	}
    	
    	if(c > 0){
    		Toast.makeText(this, "Downloading selected quizzes", Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(this, "No quizzes selected", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	APIRequest[] quizzes = new APIRequest[c];
    	
    	int counter = 0;
    	for(int i=0;i<quizList.size();i++){
    		Quiz q = (Quiz) (quizList.get(i));
    		if(q.isChecked()){
    			APIRequest r = new APIRequest();
    			r.refId = q.getRef();
				r.fullurl = q.getUrl();
				r.username = prefs.getString("prefUsername", "");
				r.password = prefs.getString("prefPassword", "");
				r.timeoutConnection = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
				r.timeoutSocket = Integer.parseInt(prefs.getString("prefServerTimeoutResponse", "10000"));
				quizzes[counter] = r;
				counter++;
    		}
    	}
    	
    	DownloadQuizTask task = new DownloadQuizTask(QuizRemoteList.this,true);
		task.execute(quizzes);
    }
    
}
