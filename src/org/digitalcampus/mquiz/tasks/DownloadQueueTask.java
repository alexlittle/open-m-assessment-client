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
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadQueueTask extends AsyncTask<APIRequest, String, String>{
	
	private final static String TAG = "DownloadQueueTask";
	private APIRequest currentRequest;
	private Context myCtx;
	
	public DownloadQueueTask(Context ctx){
		myCtx = ctx;
	}
	
	@Override
	protected String doInBackground(APIRequest... apirs){
	
		
		String toRet = "";
		for (APIRequest apir : apirs) {
			currentRequest = apir;
			String response = "";
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, apir.timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, apir.timeoutSocket);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			
			Log.d(TAG,apir.fullurl);
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
			JSONArray json = new JSONArray(response);
			APIRequest[] dlQuizzes = new APIRequest[json.length()]; 
			//for each quiz in queue download it
			for(int i=0;i<(json.length());i++){
				JSONObject json_obj=json.getJSONObject(i);
				APIRequest dlQuiz = currentRequest.clone();
				dlQuiz.fullurl = currentRequest.baseurl + "list/getquiz.php?ref="+ json_obj.getString("quizref");
				dlQuiz.refId = json_obj.getString("quizref");
				dlQuizzes[i] = dlQuiz;	
			}
			
			DownloadQuizTask task = new DownloadQuizTask(myCtx);
     		task.execute(dlQuizzes);
			
		} catch (JSONException e){
			Log.d(TAG,"Error parsing returned JSON");
			e.printStackTrace();
		}
		
	}
	
}
