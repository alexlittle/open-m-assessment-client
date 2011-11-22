package org.digitalcampus.mquiz;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity implements OnSharedPreferenceChangeListener{

	private static final String TAG = "RegisterActivity";
	
	private Button registerBtn;
	private EditText emailField;
	private EditText passwordField;
	private EditText passwordAgainField;
	private EditText firstnameField;
	private EditText lastnameField;
	
	private ProgressDialog pDialog;
	
	SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        prefs.registerOnSharedPreferenceChangeListener(this);
        
		emailField = (EditText) findViewById(R.id.register_form_email_field);
		passwordField = (EditText) findViewById(R.id.register_form_password_field);
		passwordAgainField = (EditText) findViewById(R.id.register_form_password_again_field);
		firstnameField = (EditText) findViewById(R.id.register_form_firstname_field);
		lastnameField = (EditText) findViewById(R.id.register_form_lastname_field);
		
		registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		
        		register();
        	}
        });
	}

	private void register(){
		// get form fields
    	String email = (String) emailField.getText().toString();
    	String password = (String) passwordField.getText().toString();
    	String passwordAgain = (String) passwordAgainField.getText().toString();
    	String firstname = (String) firstnameField.getText().toString();
    	String lastname = (String) lastnameField.getText().toString();
    	
    	// do validation
    	//check valid email address format
    	boolean isValidEmail = EmailValidator.getInstance().isValid(email);
    	if(!isValidEmail){
    		this.showAlert("Error","Please enter a valid email address format");
    		return;
    	}
    	// check password length
    	if(password.length()<6){
    		this.showAlert("Error","Your password must be 6 or more characters");
    		return;
    	}
    	// check password match
    	if(!password.equals(passwordAgain)){
    		this.showAlert("Error","Your passwords don't match");
    		return;
    	}
    	// check firstname
    	if(firstname.length()<2){
    		this.showAlert("Error","Please enter your firstname");
    		return;
    	}
    	
    	// check lastname
    	if(lastname.length()<2){
    		this.showAlert("Error","Please enter your lastname");
    		return;
    	}
    	
    	registerBtn.setEnabled(false);
    	
    	// show progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Registering");
        pDialog.setMessage("Sending your details...");
        pDialog.setCancelable(true);
        pDialog.show();
        
        // send results as AsyncTask
        RegisterTask task = new RegisterTask();
        Register[] registration = new Register[1];
        Register r = new Register();
        r.url = prefs.getString("prefServer", getString(R.string.prefServerDefault))+"api/?method=register";
        r.email = email;
        r.password = password;
        r.passwordAgain = passwordAgain;
        r.firstname = firstname;
        r.lastname = lastname;
        registration[0] = r;
        
		task.execute(registration);
    	
	}

	private void showAlert(String title, String msg){
    	AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// do nothing
			}
	     });
		builder.show();
    }
	
	private class Register{
    	String url;
    	String email;
    	String password;
    	String passwordAgain;
    	String firstname;
    	String lastname;
    }
	
	private class RegisterTask extends AsyncTask<Register, String, String>{
    	
    	@Override
    	protected String doInBackground(Register... registration){
    		
    		String toRet = "";
    		for (Register r : registration) {
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
    				// add post params
    				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    				nameValuePairs.add(new BasicNameValuePair("email", r.email));
    				nameValuePairs.add(new BasicNameValuePair("password", r.password));
    				nameValuePairs.add(new BasicNameValuePair("passwordagain", r.passwordAgain));
    				nameValuePairs.add(new BasicNameValuePair("firstname", r.firstname));
    				nameValuePairs.add(new BasicNameValuePair("lastname", r.lastname));
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
    		// close dialog and process results
    		pDialog.dismiss();
			
    		if(response.equals("success")){
    			// set the preferences
    			Editor editor = prefs.edit();
    	    	editor.putString("prefUsername", emailField.getText().toString());
    	    	editor.putString("prefPassword", passwordField.getText().toString());
    	    	editor.commit();
    	    	
    			AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
    			builder.setTitle("Success");
    			builder.setMessage("You are now registered");
    			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

    				@Override
    				public void onClick(DialogInterface arg0, int arg1) {
    					// close the activity?
    					finish();
    				}
    		     });
    			builder.show();
    		} else {
    			showAlert("Error", response);
    		}
			registerBtn.setEnabled(true);
			
    	}
    }
	
	 public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
	 }
}