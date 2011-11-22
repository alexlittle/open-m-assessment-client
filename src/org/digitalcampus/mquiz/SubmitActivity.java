package org.digitalcampus.mquiz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SubmitActivity extends Activity {
	
	private static final String TAG = "SubmitActivity";
	
	private DbHelper dbHelper;
	private TextView progress;
	private TextView submitTitle;
	private int noToSubmit;
	private Button submitNow;
	private ProgressDialog pDialog;
	
	SharedPreferences prefs;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        setContentView(R.layout.submit);
        
        submitTitle = (TextView) findViewById(R.id.submittitle);
        progress = (TextView) findViewById(R.id.submitprogress);
        
        submitNow = (Button) findViewById(R.id.submitnowBtn);
        
        submitNow.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		submit();
        	}
        });
    }
    
    protected void onStart(){
    	super.onStart();
    	
    	dbHelper = new DbHelper(this);
        Cursor cur = dbHelper.getUnsubmitted();
        noToSubmit = cur.getCount();
        cur.close();
        dbHelper.close();
        
        submitTitle.setText(String.format(getString(R.string.tosubmit),noToSubmit));
        
        // if no records to submit set btn to disabled
        if(noToSubmit == 0){
        	submitNow.setEnabled(false);
        }
        
    	// if no internet connection inform user and disable submitbtn
        if(!isNetworkAvailable()){
        	progress.append(getString(R.string.noconnection));
        	submitNow.setEnabled(false);
        } else {
        	submit();
        }
    }
    
    
    
    public void submit(){   
    	// connect to db and get records to send
    	dbHelper = new DbHelper(this);
        Cursor cur = dbHelper.getUnsubmitted();
        noToSubmit = cur.getCount();
        
        //set up counter and array to pass to AsyncTask
        int counter = 0;
        Result[] resultsToSend = new Result[noToSubmit];
        
        // prevent user clicking on send button again
        submitNow.setEnabled(false);
        progress.setText("");
        
        // show progress dialog
        pDialog = new ProgressDialog(SubmitActivity.this);
        pDialog.setTitle("Sending");
        pDialog.setMessage("Sending results...");
        pDialog.setCancelable(true);
        pDialog.show();
        
        // set up array of records to send
        cur.moveToFirst();
        while (cur.isAfterLast() == false) {       	
        	try {
        		JSONObject json = new JSONObject();
        		// general quiz overview
        		json.put("username", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_USERNAME)));
        		json.put("quizid", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_QUIZREFID)));
        		json.put("quizdate", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_QUIZDATE)));
        		json.put("userscore", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_USERSCORE)));
        		json.put("maxscore", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_MAXSCORE)));
        		
        		// individual quiz responses
        		JSONArray responses = new JSONArray();
        		Cursor attCur = dbHelper.getAttemptResponses(cur.getInt(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_ID)));
        		attCur.moveToFirst();
        		while (attCur.isAfterLast() == false) {
        			JSONObject r = new JSONObject();
        			r.put("qid", attCur.getString(attCur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_QUESTIONREFID)));
        			r.put("qrid", attCur.getString(attCur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_RESPONSEREFID)));
            		r.put("score", attCur.getFloat(attCur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_SCORE)));
            		responses.put(r);
            		attCur.moveToNext();   
        		}

        		json.put("responses", responses);
        		
        		String url = prefs.getString("prefServer", getString(R.string.prefServerDefault)) + prefs.getString("prefServerSubmitPath", getString(R.string.prefServerSubmitPathDefault));
            	
            	Result r = new Result();
            	r.url = url;
            	r.rowId = cur.getInt(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_ID));
            	r.username = prefs.getString("prefUsername", "");
            	r.password = prefs.getString("prefPassword", "");
            	r.content = json.toString();
            	resultsToSend[counter] = r;
        	} catch (Exception e){
        		e.printStackTrace();
        	}
      
        	counter++;
       	    cur.moveToNext();      	    
        }
        cur.close();
        dbHelper.close();
        
        // send results as AsyncTask
        SubmitResultTask task = new SubmitResultTask();
		task.execute(resultsToSend);
    }
    
    public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null, otherwise check
		// if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
    
    private class Result{
    	int rowId;
    	String url;
    	String username;
    	String password;
    	String content;
    }
    
    private class SubmitResultTask extends AsyncTask<Result, String, List<String>>{
    	
    	@Override
    	protected List<String> doInBackground(Result... resultsToSend){
    		
    		List<String> toRet = new ArrayList<String>();
    		int counter = 1;
    		for (Result r : resultsToSend) {
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
    			HttpPost httpPost = new HttpPost(r.url);
    			try {
    				// update progress dialog
    				String msg = "Sending "+ String.valueOf(counter)+ " of " + String.valueOf(resultsToSend.length)+" ...";
    				publishProgress(msg);
    				
    				// add post params
    				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
    				nameValuePairs.add(new BasicNameValuePair("username", r.username));
    				nameValuePairs.add(new BasicNameValuePair("password", r.password));
    				nameValuePairs.add(new BasicNameValuePair("content", r.content));
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
					
					// process response
					if (response.equals("success")){
						dbHelper = new DbHelper(SubmitActivity.this);
						dbHelper.setSubmitted(r.rowId);
						dbHelper.close();
					} 
					
					String str = "Result "+String.valueOf(counter) + ": " + response;
					toRet.add(str);
					Log.d(TAG,str);
					
    			} catch (Exception e) {
    				e.printStackTrace();
					toRet.add("Error connecting to network");
				}
    			counter++;
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
    	protected void onPostExecute(List<String> results) {
    		dbHelper.close();
    		
    		// close dialog and display results to user
    		pDialog.dismiss();
    		for (String s : results){
    			progress.append(s);
    			progress.append("\n");
    		}
    		
    		// see if any remining records to submit (if there were failures last attempt)
    		dbHelper = new DbHelper(SubmitActivity.this);
    		Cursor cur = dbHelper.getUnsubmitted();
            noToSubmit = cur.getCount();
            cur.close();
            dbHelper.close();
            
            // set submit button accordingly
            if(noToSubmit > 0){
            	submitNow.setEnabled(true);
            	submitNow.setText(R.string.submitnow_btn_text);
            } else {
            	submitNow.setText("All records sent");
            }
    	}

    }
}
