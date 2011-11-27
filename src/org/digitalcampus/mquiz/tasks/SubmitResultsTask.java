package org.digitalcampus.mquiz.tasks;

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
import org.digitalcampus.mquiz.listeners.SubmitResultsListener;
import org.digitalcampus.mquiz.model.DbHelper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SubmitResultsTask extends AsyncTask<APIRequest, String, List<String>>{
	
	private final static String TAG = "SubmitResultsTask";
	
	private Context myCtx;
	private SubmitResultsListener mStateListener;
	
	public SubmitResultsTask(Context ctx){
		myCtx = ctx;
		
	}
	
	@Override
	protected List<String> doInBackground(APIRequest... resultsToSend){
		
		List<String> toRet = new ArrayList<String>();
		int counter = 1;
		for (APIRequest apir : resultsToSend) {
			String response = "";
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, apir.timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, apir.timeoutSocket);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			
			HttpPost httpPost = new HttpPost(apir.fullurl);
			try {
				// update progress dialog
				String msg = "Sending "+ String.valueOf(counter)+ " of " + String.valueOf(resultsToSend.length)+" ...";
				publishProgress(msg);
				
				// add post params
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("username", apir.username));
				nameValuePairs.add(new BasicNameValuePair("password", apir.password));
				nameValuePairs.add(new BasicNameValuePair("content", apir.content));
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
					DbHelper dbHelper = new DbHelper(myCtx);
					dbHelper.setSubmitted(apir.rowId);
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

		synchronized (this) {
            if (mStateListener != null) {
                // update progress and total
                mStateListener.progressUpdate(strings[0]);
            }
        }
		
	}
	
	@Override
	protected void onPostExecute(List<String> results) {
		String msg = "";
		for (String s : results){
		     msg += s;
		     msg += "\n";
		}
		
		synchronized (this) {
            if (mStateListener != null) {
                mStateListener.submitResultsComplete(msg);
            }
        }
	}
	
	public void setDownloaderListener(SubmitResultsListener srl) {
        synchronized (this) {
            mStateListener = srl;
        }
    }

}
