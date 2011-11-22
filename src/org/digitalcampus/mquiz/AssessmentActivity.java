package org.digitalcampus.mquiz;

import org.digitalcampus.mquiz.model.DbHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AssessmentActivity extends Activity implements OnSharedPreferenceChangeListener{
	
	private DbHelper dbHelper;
	private Button submitBtn;
	SharedPreferences prefs;

	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        prefs.registerOnSharedPreferenceChangeListener(this);
       
        dbHelper = new DbHelper(this);
        
        Button takeQuizBtn = (Button) findViewById(R.id.take_quiz_btn);
        takeQuizBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(AssessmentActivity.this, SelectQuizActivity.class);
        		startActivity(i);
        	}
        });
        
        Button manageQuizBtn = (Button) findViewById(R.id.manage_quiz_btn);
        //manageQuizBtn.setEnabled(false);
        manageQuizBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(AssessmentActivity.this, ManageQuizActivity.class);
        		startActivity(i);
        	}
        });
        
        Button resultsBtn = (Button) findViewById(R.id.results_btn);
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
        
        Button registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(AssessmentActivity.this, RegisterActivity.class);
        		startActivity(i);
        	}
        });
        
        //check to see if username/password set
        if(this.isLoggedIn()){
        	takeQuizBtn.setVisibility(View.VISIBLE);
        	manageQuizBtn.setVisibility(View.VISIBLE);
        	resultsBtn.setVisibility(View.VISIBLE);
        	submitBtn.setVisibility(View.VISIBLE);
        } else {
        	takeQuizBtn.setVisibility(View.GONE);
        	manageQuizBtn.setVisibility(View.GONE);
        	resultsBtn.setVisibility(View.GONE);
        	submitBtn.setVisibility(View.GONE);
        }
        
    }
    
    protected void onStart(){
    	super.onStart();
    	Cursor cur = dbHelper.getUnsubmitted();
        int noToSubmit = cur.getCount();
        cur.close();
        
        if(noToSubmit == 0){
        	submitBtn.setEnabled(false);
        } else {
        	submitBtn.setEnabled(true);
        }
        
        submitBtn.setText(String.format(getString(R.string.submit_btn_text), noToSubmit)); 
    }
    
    public boolean isLoggedIn(){
    	return false;
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

}