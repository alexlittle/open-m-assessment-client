package org.digitalcampus.assessment;

import org.digitalcampus.mquiz.model.DbHelper;
import org.digitalcampus.mquiz.tasks.APIRequest;
import org.digitalcampus.mquiz.tasks.DownloadQueueTask;
import org.apache.commons.validator.EmailValidator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
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

public class AssessmentActivity extends Activity implements OnSharedPreferenceChangeListener{
	
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
		
	SharedPreferences prefs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        prefs.registerOnSharedPreferenceChangeListener(this);
       
        
        setDefaultPrefs();
        
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
        		Intent i = new Intent(AssessmentActivity.this, SubmitActivity.class);
        		startActivity(i);
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
       
        //check to see if username/password set
        this.setScreen();
        
       
        boolean runDownload = dbHelper.runAutoDownload();
        dbHelper.close();
        // check to see if any quizzes are waiting to be downloaded
        if(this.isLoggedIn() && runDownload){
        	APIRequest[] req = new APIRequest[1];
        	APIRequest apiR = new APIRequest(); 
        	apiR.baseurl =  prefs.getString("prefServer", getString(R.string.prefServerDefault));
        	apiR.fullurl =  prefs.getString("prefServer", getString(R.string.prefServerDefault))+"api/?method=downloadqueue";
        	apiR.username = prefs.getString("prefUsername", "");
        	apiR.password = prefs.getString("prefPassword", "");
        	apiR.timeoutConnection = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
        	apiR.timeoutSocket = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
        	req[0] = apiR;
        	DownloadQueueTask task = new DownloadQueueTask(AssessmentActivity.this);
     		task.execute(req);
        }
        
    }
    
    private boolean isLoggedIn(){
    	String username = prefs.getString("prefUsername", "");
    	if(username.equals("")){
    		return false;
    	} else {
    		return true;
    	}
    }
    
    private void setScreen(){
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
    	
    	// set the preferences
    	Editor editor = prefs.edit();
    	editor.putString("prefUsername", email);
    	editor.putString("prefPassword", password);
    	editor.commit();
    	
    	// set to normal main screen
    	this.setScreen();
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

    private void setDefaultPrefs(){
    	if(!isLoggedIn()){
	    	Editor editor = prefs.edit();
	    	editor.putString("prefServer", getString(R.string.prefServerDefault));
	    	editor.putString("prefUsername", "");
	    	editor.putString("prefPassword", "");
	    	editor.putString("prefServerListPath", getString(R.string.prefServerListPathDefault));
	    	editor.putString("prefServerSubmitPath", getString(R.string.prefServerSubmitPathDefault));
	    	editor.commit();
    	}
    }
}