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
import org.digitalcampus.mquiz.model.DbHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadQuizTask extends AsyncTask<APIRequest, String, List<String>>{
	
	private final static String TAG = "DownloadQuizTask";
	private APIRequest currentRequest;
	private Context myCtx;
	
	public DownloadQuizTask(Context ctx){
		myCtx = ctx;
	}
	
	@Override
	protected List<String> doInBackground(APIRequest... apirs){
		
		List<String> toRet = new ArrayList<String>();
		
		for (APIRequest apir : apirs) {
			currentRequest = apir;
			String response = "";
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, apir.timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, apir.timeoutSocket);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			
			// check is quiz already installed
			DbHelper dbh = new DbHelper(myCtx);
			Cursor isInstalled = dbh.getQuiz(apir.refId);
			if(isInstalled.getCount() > 0){
				response = "already installed";
				toRet.add(response);
				Log.d(TAG,apir.refId + ": " + response);
				// now log quiz as having being downloaded
				APIRequest[] dlQuizzes = new APIRequest[1]; 
				APIRequest dlQuiz = currentRequest.clone();
				dlQuiz.fullurl = currentRequest.baseurl + "api/?method=downloaded&quizref="+ apir.refId;
				dlQuizzes[0] = dlQuiz;	
				QuizDownloadedTask task = new QuizDownloadedTask(myCtx);
	     		task.execute(dlQuizzes);
			} else {
				HttpPost httpPost = new HttpPost(apir.fullurl);
				try {
					String msg = "Downloading '" + apir.fullurl + "'";
					publishProgress(msg);
					
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
					
					//DownloadResult dr = new DownloadResult();
					//dr.name	= u.getTitle();
					try {
						Log.d(TAG,response);
						JSONObject json = new JSONObject(response);
						DbHelper dbHelper = new DbHelper(myCtx);
	    				dbHelper.insertQuiz(json);
	    				dbHelper.close();
	    				// now log quiz as having being downloaded
	    				String quizRefId = (String) json.get("refid");
	    				APIRequest[] dlQuizzes = new APIRequest[1]; 
	    				APIRequest dlQuiz = currentRequest.clone();
	    				dlQuiz.fullurl = currentRequest.baseurl + "api/?method=downloaded&quizref="+ quizRefId;
	    				dlQuizzes[0] = dlQuiz;	
	    				QuizDownloadedTask task = new QuizDownloadedTask(myCtx);
	    	     		task.execute(dlQuizzes);
	    	     		
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
			isInstalled.close();
			dbh.close();
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