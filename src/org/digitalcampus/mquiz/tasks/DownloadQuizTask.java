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
import android.widget.Toast;

public class DownloadQuizTask extends AsyncTask<APIRequest, String, List<String>>{
	
	private final static String TAG = "DownloadQuizTask";
	private Context myCtx;
	private boolean notify;
	
	public DownloadQuizTask(Context ctx, boolean notify){
		myCtx = ctx;
		this.notify = notify;
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
			
			// check is quiz already installed
			DbHelper dbh = new DbHelper(myCtx);
			Cursor isInstalled = dbh.getQuiz(apir.refId);
			if(isInstalled.getCount() == 0){
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
					
					try {
						JSONObject json = new JSONObject(response);
						DbHelper dbHelper = new DbHelper(myCtx);
	    				dbHelper.insertQuiz(json);
	    				if(json.has("title")){
	    					publishProgress(json.getString("title"));
	    				}
	    				dbHelper.close();
	    			} catch (JSONException e){
	    			}
					
					
				} catch (Exception e) {
					e.printStackTrace();
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
		
		if(notify){
			Toast.makeText(myCtx, "Finished downloading:" + strings[0], Toast.LENGTH_SHORT).show();
		}

	}
	
	@Override
	protected void onPostExecute(List<String> results) {
		
	}
}