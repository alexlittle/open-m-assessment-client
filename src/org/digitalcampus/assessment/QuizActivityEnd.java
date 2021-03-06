package org.digitalcampus.assessment;

import org.digitalcampus.mquiz.listeners.SubmitResultsListener;
import org.digitalcampus.mquiz.model.DbHelper;
import org.digitalcampus.mquiz.model.Quiz;
import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.tasks.APIRequest;
import org.digitalcampus.mquiz.tasks.SubmitResultsTask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivityEnd extends Activity implements SubmitResultsListener{
	
	static final String TAG = "TestActivityEnd";
	private Quiz quiz;
	
	private DbHelper dbHelper;
	private SharedPreferences prefs;
	private Long attemptId;
	private Button submitBtn;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.end);

        TextView scoreText = (TextView) findViewById(R.id.score_text);
        
        // get data from previous activity
        Bundle b = this.getIntent().getExtras(); 
        if(b !=null) {
        	quiz = (Quiz) b.getSerializable("quiz");
        	float s = quiz.getUserscore()*100/quiz.getMaxscore();
        	scoreText.setText(String.format("%.0f%%",s)); 
        	saveScores();
        	sendResults(attemptId);
        } 
       
        Button takeAnotherBtn = (Button) findViewById(R.id.take_another_quiz_btn);
        takeAnotherBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent i = new Intent(QuizActivityEnd.this, SelectQuizActivity.class);
        		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        		startActivity(i);
        	}
        });
        
        Button shareBtn = (Button) findViewById(R.id.quiz_end_share_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        		sharingIntent.setType("text/plain");
        		float sc = quiz.getUserscore()*100/quiz.getMaxscore();
        		String title = quiz.getTitle();
        		String link = "http://mquiz.org/m/#"+quiz.getRef();
        		String shareText = getString(R.string.quiz_end_share_text,sc,title,link);
        		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        		startActivity(Intent.createChooser(sharingIntent,"Share using"));
        	}
        });
        
        /*submitBtn = (Button) findViewById(R.id.quiz_end_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		sendResults(attemptId);
        	}
        });*/
    }
   
    
    private void saveScores(){
    	// save scores to db
        dbHelper = new DbHelper(this);
    	ContentValues qValues = new ContentValues();
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_QUIZDATE, System.currentTimeMillis()/1000L);
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_QUIZREFID, quiz.getRef());
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_USERNAME, prefs.getString("prefUsername", ""));
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_USERSCORE, quiz.getUserscore());
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_MAXSCORE, quiz.getMaxscore());
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_SUBMITTED, false);
    	attemptId = dbHelper.saveQuizAttempt(qValues);
    	
    	if (attemptId != -1){
	    	for (QuizQuestion a: quiz.questions){
	    		ContentValues rValues = new ContentValues();
	    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_ATTEMPTID, attemptId);
	    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_QUIZREFID,quiz.getRef());
	    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_QUESTIONREFID, a.getRefid());
	    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_SCORE, a.getUserscore());
	    		// build a new string from the response
	    		String response = a.getUserResponses().toString();
	    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_TEXT, response);
	    		dbHelper.saveQuizAttemptResponse(rValues);
	    	}
    	}
    	
        dbHelper.close();

    }
    
    public void onResultsSubmitted(boolean result){
    	
    }
    
    private void sendResults(Long attemptId){
    	dbHelper = new DbHelper(this);
    	Cursor cur = dbHelper.getUnsubmitted(attemptId.intValue());
    	cur.moveToFirst();
    	String content = "";
		while (cur.isAfterLast() == false) {
			content = dbHelper.createSubmitResponseObject(cur);
			cur.moveToNext();
		}
		cur.close();
		dbHelper.close();
		
		APIRequest[] resultsToSend = new APIRequest[1];
		APIRequest r = new APIRequest();
        r.fullurl = prefs.getString("prefServer", getString(R.string.prefServerDefault))  + "api/?method=submit&format=json";
        r.rowId = attemptId.intValue();
        r.username = prefs.getString("prefUsername", "");
        r.password = prefs.getString("prefPassword", "");
        r.timeoutConnection = Integer.parseInt(prefs.getString("prefServerTimeoutConnection", "10000"));
		r.timeoutSocket = Integer.parseInt(prefs.getString("prefServerTimeoutResponse", "10000"));
        r.content = content;
        resultsToSend[0] = r;
        
        Toast.makeText(this, "Sending results", Toast.LENGTH_SHORT).show();
        
        // send results to server
        SubmitResultsTask task = new SubmitResultsTask(QuizActivityEnd.this);
        task.setDownloaderListener(this);
        task.execute(resultsToSend);
    }


	@Override
	public void submitResultsComplete() {
		Toast.makeText(this, "Results submitted.", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void progressUpdate(String msg) {

	}
}