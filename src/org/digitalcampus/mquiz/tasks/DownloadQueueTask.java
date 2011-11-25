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
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadQueueTask extends AsyncTask<APIRequest, String, String>{
	
	private final static String TAG = "DownloadQueueTask";
	
	@Override
	protected String doInBackground(APIRequest... apirs){
	
		
		String toRet = "";
		for (APIRequest apir : apirs) {
			String response = "";
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, apir.timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, apir.timeoutSocket);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			
			Log.d(TAG,apir.url);
			HttpPost httpPost = new HttpPost(apir.url);
			try {
				// add post params
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("username", apir.username));
				nameValuePairs.add(new BasicNameValuePair("password", apir.password));
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

		try {
			Log.d(TAG,response);
			new JSONArray(response);
			//parseResponse(response);
			//for each quiz in queue download it
		} catch (JSONException e){
			Log.d(TAG,response);
			e.printStackTrace();
		}
		
	}
	
}
