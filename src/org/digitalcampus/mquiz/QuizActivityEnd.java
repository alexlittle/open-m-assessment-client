package org.digitalcampus.mquiz;

import org.digitalcampus.mquiz.model.*;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuizActivityEnd extends Activity {
	
	static final String TAG = "TestActivityEnd";
	private Quiz quiz;
	
	DbHelper dbHelper;
	SharedPreferences prefs;
	
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
        	// TODO save scores to DB
        	saveScores();
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
    }
   
    
    private void saveScores(){
    	// TODO save to db
        dbHelper = new DbHelper(this);
    	ContentValues qValues = new ContentValues();
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_QUIZDATE, System.currentTimeMillis()/1000L);
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_QUIZREFID, quiz.getRefId());
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_USERNAME, prefs.getString("prefUsername", ""));
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_USERSCORE, quiz.getUserscore());
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_MAXSCORE, quiz.getMaxscore());
    	qValues.put(DbHelper.QUIZ_ATTEMPT_C_SUBMITTED, false);
    	long attemptId = dbHelper.saveQuizAttempt(qValues);
    	
    	if (attemptId != -1){
	    	for (Question a: quiz.questions){
	    		for (Response r : a.getResponses()){
	    			if(r.isSelected()){
			    		ContentValues rValues = new ContentValues();
			    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_ATTEMPTID, attemptId);
			    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_QUIZREFID,quiz.getRefId());
			    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_QUESTIONREFID, a.getRefid());
			    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_RESPONSEREFID, r.getRefid());
			    		rValues.put(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_SCORE, r.getScore());
			    		dbHelper.saveQuizAttemptResponse(rValues);
	    			} 
	    		}
	    	}
    	}
    	
        dbHelper.close();

    }
}