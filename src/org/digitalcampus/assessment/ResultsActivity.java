package org.digitalcampus.assessment;

import org.digitalcampus.assessment.model.DbHelper;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

public class ResultsActivity extends ListActivity {
	
	DbHelper dbHelper;
	static final String TAG = "ResultsActivity";
	
	AlertDialog builder;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        
        resetListing();
        
        Button homeBtn = (Button) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(ResultsActivity.this, AssessmentActivity.class);
        		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        		startActivity(i);
        		finish();
        	}
        });       
        
        
        Button clearBtn = (Button) findViewById(R.id.clear_submitted_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(ResultsActivity.this);
        		builder.setTitle("Clear results");
        		builder.setMessage("Are you sure you want to remove all submitted results?");
        		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) { 
        	            // continue with delete
        	        	dbHelper = new DbHelper(ResultsActivity.this);
        	        	dbHelper.clearSubmitted();
        	        	dbHelper.close();
        	        	resetListing();
        	        }
        	     });
        	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) { 
        	            // do nothing
        	        }
        	     });
        	    builder.show();
        	}
        });
        
    }
    
    private void resetListing(){
    	dbHelper = new DbHelper(this);
        Cursor cur = dbHelper.getSavedResults();
        startManagingCursor(cur);
        
        String[] columns = new String[] { "QuizDateStr", DbHelper.QUIZ_C_TITLE, "Score" };
        int[] binding = new int[] { R.id.date, R.id.quizname, R.id.score };
        
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter( this,
																R.layout.resultslist, 
																cur, 
																columns,
																binding);

		// Bind to our new adapter.
		setListAdapter(mAdapter);

		dbHelper.close();
    }
}
