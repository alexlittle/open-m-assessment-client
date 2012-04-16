package org.digitalcampus.assessment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.EmailValidator;
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
import org.digitalcampus.mquiz.tasks.APIRequest;
import org.digitalcampus.mquiz.tasks.SubmitResultsTask;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentActivity extends Activity implements OnSharedPreferenceChangeListener, SubmitResultsListener{
	
	private final static String TAG = "AssessmentActivity";
	
	private Button takeQuizBtn;
	private Button resultsBtn;
	private Button submitBtn;
	private Button manageQuizBtn;
	
	private TextView unregisteredTV;
	private TextView emailTitleTV;
	private EditText emailField;
	private TextView passwordTitleTV;
	private EditText passwordField;
	private Button loginBtn;
	private Button registerBtn;
	
	private ProgressDialog pDialog;
	
	SharedPreferences prefs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        prefs.registerOnSharedPreferenceChangeListener(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        
        takeQuizBtn = (Button) findViewById(R.id.take_quiz_btn);
        takeQuizBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(AssessmentActivity.this, SelectQuizActivity.class);
        		startActivity(i);
        	}
        });
        
       manageQuizBtn = (Button) findViewById(R.id.manage_quiz_btn);
        //manageQuizBtn.setEnabled(false);
        manageQuizBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(AssessmentActivity.this, ManageQuizActivity.class);
        		startActivity(i);
        	}
        });
        
        resultsBtn = (Button) findViewById(R.id.results_btn);
        resultsBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(AssessmentActivity.this, ResultsActivity.class);
        		startActivity(i);
        	}
        });
        
        submitBtn = (Button) findViewById(R.id.submit_btn);
        
        submitBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		//submit results...
        		sendResults();
        	}
        });
        
        unregisteredTV = (TextView) findViewById(R.id.main_unregistered);
        emailTitleTV = (TextView) findViewById(R.id.main_email_title);
        emailField = (EditText) findViewById(R.id.main_email_field);
        passwordTitleTV = (TextView) findViewById(R.id.main_password_title);
        passwordField = (EditText) findViewById(R.id.main_password_field);
        
        registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(AssessmentActivity.this, RegisterActivity.class);
        		startActivity(i);
        	}
        });
        
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		AssessmentActivity.this.loginUser();
        	}
        });
    }
    
    protected void onStart(){
    	super.onStart();
    	DbHelper dbHelper = new DbHelper(AssessmentActivity.this);
    	
    	Cursor cur = dbHelper.getUnsubmitted();
        int noToSubmit = cur.getCount();
        cur.close();
        
        if(noToSubmit == 0){
        	submitBtn.setEnabled(false);
        } else {
        	submitBtn.setEnabled(true);
        }
        
        submitBtn.setText(String.format(getString(R.string.submit_btn_text), noToSubmit)); 
       
        // check to see if username/password set
        this.setScreen();
        
        dbHelper.close();
        
    }
    
    private boolean isLoggedIn(){
    	String username = prefs.getString("prefUsername", "");
    	if(username.equals("")){
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public void setScreen(){
    	if(this.isLoggedIn()){
        	takeQuizBtn.setVisibility(View.VISIBLE);
        	manageQuizBtn.setVisibility(View.VISIBLE);
        	resultsBtn.setVisibility(View.VISIBLE);
        	submitBtn.setVisibility(View.VISIBLE);
        	
        	unregisteredTV.setVisibility(View.GONE);
        	emailTitleTV.setVisibility(View.GONE);
        	emailField.setVisibility(View.GONE);
        	passwordTitleTV.setVisibility(View.GONE);
        	passwordField.setVisibility(View.GONE);
        	loginBtn.setVisibility(View.GONE);
        	registerBtn.setVisibility(View.GONE);
        	
        } else {
        	takeQuizBtn.setVisibility(View.GONE);
        	manageQuizBtn.setVisibility(View.GONE);
        	resultsBtn.setVisibility(View.GONE);
        	submitBtn.setVisibility(View.GONE);
        	
        	unregisteredTV.setVisibility(View.VISIBLE);
        	emailTitleTV.setVisibility(View.VISIBLE);
        	emailField.setVisibility(View.VISIBLE);
        	passwordTitleTV.setVisibility(View.VISIBLE);
        	passwordField.setVisibility(View.VISIBLE);
        	loginBtn.setVisibility(View.VISIBLE);
        	registerBtn.setVisibility(View.VISIBLE);
        }
    }
   
    public void loginUser(){
    	// get text from email
    	String email = (String) emailField.getText().toString();
    	//check valid email address format
    	boolean isValidEmail = EmailValidator.getInstance().isValid(email);
    	if(!isValidEmail){
    		this.showAlert("Error","Please enter a valid email address format");
    		return;
    	}
    	
    	// get text from email
    	String password = (String) passwordField.getText().toString();
    	//check length
    	if(password.length()<6){
    		this.showAlert("Error","You password should be 6 characters or more");
    		return;
    	}
    	
    	// show progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Login");
        pDialog.setMessage("Logging in...");
        pDialog.setCancelable(true);
        pDialog.show();
        
    	// send results as AsyncTask
        LoginTask task = new LoginTask();
        User[] user = new User[1];
        User u = new User();
        u.url = prefs.getString("prefServer", getString(R.string.prefServerDefault))+"api/?format=json&method=login";
        u.username = email;
        u.password = password;
        user[0] = u;
        
		task.execute(user);
    	
    }
    
    
    private void showAlert(String title, String msg){
    	AlertDialog.Builder builder = new AlertDialog.Builder(AssessmentActivity.this);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
			}
	     });
		builder.show();
    }
    
    // Called first time user clicks on the menu button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true; 
    }

    // Called when an options item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.itemPrefs:
		    	startActivity(new Intent(this, PrefsActivity.class)); 
		    	break;
		    case R.id.itemAbout:
		    	startActivity(new Intent(this, AboutActivity.class));
		    	break;
	    }
	    return true;
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    }

    public void sendResults(){
		DbHelper dbHelper = new DbHelper(this);
		Cursor cur = dbHelper.getUnsubmitted();
		int noToSubmit = cur.getCount();

		if(noToSubmit == 0){
			return;
		}
        
		// set up counter and array to pass to AsyncTask
		int counter = 0;
		APIRequest[] resultsToSend = new APIRequest[noToSubmit];

		// set up array of records to send
		cur.moveToFirst();
		while (cur.isAfterLast() == false) {
			try {
				String content = dbHelper.createSubmitResponseObject(cur);

				APIRequest r = new APIRequest();
				r.fullurl = prefs.getString("prefServer",getString(R.string.prefServerDefault)) + "api/?method=submit&format=json";
				r.rowId = cur.getInt(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_ID));
				r.username = prefs.getString("prefUsername", "");
				r.password = prefs.getString("prefPassword", "");
				r.timeoutConnection = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
				r.timeoutSocket = Integer.parseInt(prefs.getString("prefServerTimeoutResponse", "10000"));
				r.content = content;
				resultsToSend[counter] = r;
			} catch (Exception e) {
				e.printStackTrace();
			}

			counter++;
			cur.moveToNext();
		}
		cur.close();
		dbHelper.close();

		// send results as AsyncTask
		SubmitResultsTask task = new SubmitResultsTask(this);
		task.setDownloaderListener(this);
		task.execute(resultsToSend);
		Toast.makeText(this, "Sending results", Toast.LENGTH_SHORT).show();
    }

    @Override
	public void submitResultsComplete() {
    	Toast.makeText(this, "Results submitted.", Toast.LENGTH_SHORT).show();
    	
    	DbHelper dbHelper = new DbHelper(AssessmentActivity.this);
    	
    	Cursor cur = dbHelper.getUnsubmitted();
        int noToSubmit = cur.getCount();
        cur.close();
        dbHelper.close();
        
        if(noToSubmit == 0){
        	submitBtn.setEnabled(false);
        } else {
        	submitBtn.setEnabled(true);
        }
        
        submitBtn.setText(String.format(getString(R.string.submit_btn_text), noToSubmit)); 
	}


	@Override
	public void progressUpdate(String msg) {
		Log.d(TAG,"progress update....");
	}
	
	


	private class User{
		String url;
		String username;
		String password;
	}

	private class LoginTask extends AsyncTask<User, String, String>{
		
		@Override
		protected String doInBackground(User... user){
			
			String toRet = "";
			for (User u : user) {
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
				HttpPost httpPost = new HttpPost(u.url);
				try {
					// add post params
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("username", u.username));
					nameValuePairs.add(new BasicNameValuePair("password", u.password));
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
					toRet = "{\"error\":\"Connection error or invalid response from server\"}";
				}
			}
			return toRet;
		}
		
		@Override
		protected void onPostExecute(String response) {
			// close dialog and process results
			pDialog.dismiss();
			Log.d(TAG,response);
			try{
				JSONObject json = new JSONObject(response);
			
				if(json.has("error")){
					String error = (String) json.get("error");
					showAlert("Error", error);
				} else if (json.has("login")){
					// set the preferences
	    			Editor editor = prefs.edit();
	    	    	editor.putString("prefUsername", emailField.getText().toString());
	    	    	editor.putString("prefPassword", passwordField.getText().toString());
	    	    	editor.commit();
	    	    	AssessmentActivity.this.setScreen();
	    	    	
				} else {
					showAlert("Error", "Login failed");
				}
				
			} catch (JSONException e){
				e.printStackTrace();
				showAlert("Error", "Login failed");
			}
			
		}
	}
	

}