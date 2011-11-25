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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadQuizTask extends AsyncTask<APIRequest, String, List<String>>{
	
	private final static String TAG = "DownloadQuizTask";
	
	@Override
	protected List<String> doInBackground(APIRequest... apirs){
		
		List<String> toRet = new ArrayList<String>();
		
		for (APIRequest apir : apirs) {
			String response = "";
			
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 10000;
			try {
				//timeoutConnection = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
			} catch (NumberFormatException e){
				// do nothing - will remain as default as above
				e.printStackTrace();
			}
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 10000;
			try {
				//timeoutSocket= Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
			} catch (NumberFormatException e){
				// do nothing - will remain as default as above
				e.printStackTrace();
			}
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			
			HttpPost httpPost = new HttpPost(apir.url);
			try {
				String msg = "Downloading '" + apir.url + "'";
				publishProgress(msg);
				
				// add post params
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				//nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("prefUsername", "")));
				//nameValuePairs.add(new BasicNameValuePair("password", prefs.getString("prefPassword", "")));
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
				
				//DownloadResult dr = new DownloadResult();
				//dr.name	= u.getTitle();
				try {
    				new JSONObject(response);
    				//dr.responseObj = response;
    			} catch (JSONException e){
    				//dr.responseObj = response;
    			}
				
				//toRet.add(dr);
				
			} catch (Exception e) {
				e.printStackTrace();
				//DownloadResult dr = new DownloadResult();
				//dr.name	= u.getTitle();
				//dr.responseObj = "Connection error or invalid response from server";
				//toRet.add(dr);
			}
		}
		return toRet;
	}
	
	@Override
	protected void onProgressUpdate(String... strings){
		super.onProgressUpdate(strings);
		Log.d(TAG, "onProgressUpdate(): " +  String.valueOf( strings[0] ) );

	}
	
	@Override
	protected void onPostExecute(List<String> results) {
	
		
	}
}