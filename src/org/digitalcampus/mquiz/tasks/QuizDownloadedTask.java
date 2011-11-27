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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class QuizDownloadedTask extends AsyncTask<APIRequest, String, List<String>>{

	private final static String TAG = "QuizDownloadedTask";
	
	public QuizDownloadedTask(Context ctx){
	
	}
	
	@Override
	protected List<String> doInBackground(APIRequest... apirs){
		
		List<String> toRet = new ArrayList<String>();
		
		for (APIRequest apir : apirs) {
			String response = "";
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, apir.timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, apir.timeoutSocket);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			
			HttpPost httpPost = new HttpPost(apir.fullurl);
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
				}

				Log.d(TAG,response);
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "Connection error or invalid response from server");
			}
		}
		return toRet;
	}
	
	@Override
	protected void onProgressUpdate(String... strings){
		super.onProgressUpdate(strings);
		Log.d(TAG, String.valueOf( strings[0] ) );

	}
	
	@Override
	protected void onPostExecute(List<String> results) {
	
		
	}
}
