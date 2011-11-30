package org.digitalcampus.assessment;

import org.digitalcampus.mquiz.model.*;
import org.digitalcampus.mquiz.model.questiontypes.MultipleChoice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends Activity {
	
	private static final String TAG = "QuizActivity";
	private String quizrefid = "";
	private Quiz quiz;
	
	private DbHelper dbHelper;
	
	private Button prevBtn;
	private Button nextBtn;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);
       
        
        prevBtn = (Button) findViewById(R.id.prev_btn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		// save answer
        		saveAnswer();
        		
        		if(quiz.hasPrevious()){
        			quiz.movePrevious();
    				showQuestion();
    			}
        	}
        });
        
        nextBtn = (Button) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		// save answer
        		if (saveAnswer()){
	        		if(quiz.hasNext()){
	    				quiz.moveNext();
	    				showQuestion();
	    			} else {
	    				quiz.mark();
	    				//  prevent ability to track back to quiz
	    				//
	    				Intent i = new Intent(QuizActivity.this, QuizActivityEnd.class);
	    				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    				Bundle rb = new Bundle();
	    				rb.putSerializable("quiz",quiz);
	    				i.putExtras(rb);
	    				startActivity(i);
	    				finish();
	    			}
        		} else {
        			Context context = getApplicationContext();
        			CharSequence text = context.getString(R.string.please_answer);
        			int duration = Toast.LENGTH_SHORT;

        			Toast toast = Toast.makeText(context, text, duration);
        			toast.show();
        		}
        	}
        });
        
        // get data from previous activity
        // tells us which quiz to load
        Bundle b = this.getIntent().getExtras(); 
        if(b != null) {
        	quizrefid = b.getString("refid");
        	loadQuiz();
        	showQuestion();
        }
    }
    
    private void showQuestion(){    
    	// show the question
    	QuizQuestion q = quiz.questions.get(quiz.getCurrentq());
    	TextView qText = (TextView) findViewById(R.id.questiontext);
    	qText.setText(q.getQtext());
    
    	// show title
    	int qNo = quiz.getCurrentq()+1;
    	int qTotal = quiz.questions.size();
    	this.setTitle(getString(R.string.title_quiz,quiz.getTitle(),qNo,qTotal));
    	
    	// show hint
    	TextView qHint = (TextView) findViewById(R.id.questionhint);
    	if(quiz.questions.get(quiz.getCurrentq()).getQhint().equals("")){
    		qHint.setVisibility(View.GONE);
    	} else {
    		qHint.setText(quiz.questions.get(quiz.getCurrentq()).getQhint());
    		qHint.setVisibility(View.VISIBLE);
    	}
    	
    	// show the responses
    	LinearLayout responsesLL = (LinearLayout) findViewById(R.id.questionresponses);
    	responsesLL.removeAllViews();
    	RadioGroup responsesRG = new RadioGroup(this);
    	responsesRG.setId(q.getDbid());
    	responsesLL.addView(responsesRG);
    	
    	for (Response r : q.getResponses()){
    		RadioButton rb = new RadioButton(this);
    		rb.setId(r.getDbid()*1000);
    	
			rb.setText(r.getText());
			responsesRG.addView(rb);
			if (r.isSelected()){
				rb.setChecked(true);
			}
    	}
    	
    	if (!quiz.hasNext()){
    		nextBtn.setText(R.string.quiz_end);
    	} else {
    		nextBtn.setText(R.string.quiz_next);
    	}
    	if (!quiz.hasPrevious()){
    		prevBtn.setEnabled(false);
    	} else {
    		prevBtn.setEnabled(true);
    	}
    	
    	
    }
    
    private boolean saveAnswer(){
    	RadioGroup responsesRG = (RadioGroup) findViewById(quiz.questions.get(quiz.getCurrentq()).getDbid());
    	int resp = responsesRG.getCheckedRadioButtonId();
    	View rb = responsesRG.findViewById(resp);
    	int idx = responsesRG.indexOfChild(rb);
    	// set all previous responses to false
    	for (Response r : quiz.questions.get(quiz.getCurrentq()).getResponses()){
    		r.setSelected(false);
    	}
    	if (idx >= 0){
    		quiz.questions.get(quiz.getCurrentq()).setResponseSelected(idx);
    		quiz.questions.get(quiz.getCurrentq()).mark();
    		return true;
    	} else {
    		return false;
    	}
    	
    }
    
    private void loadQuiz(){
    	//load quiz
    	quiz = new Quiz(quizrefid);
    	dbHelper = new DbHelper(this);
		
		// set quiz obj
		Cursor quizCur = dbHelper.getQuiz(quizrefid);
		quizCur.moveToFirst();
		while (quizCur.isAfterLast() == false) { 
			quiz.setTitle(quizCur.getString(quizCur.getColumnIndex(DbHelper.QUIZ_C_TITLE)));
			quiz.setMaxscore(quizCur.getInt(quizCur.getColumnIndex(DbHelper.QUIZ_C_MAXSCORE)));
			quizCur.moveToNext(); 
		}
		
		// set up questions
		Cursor questionCur = dbHelper.getQuestionsForQuiz(quizrefid);
		questionCur.moveToFirst();
		while (questionCur.isAfterLast() == false) { 
			QuizQuestion q = new MultipleChoice();
			q.setDbid(questionCur.getInt(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_ID)));
			q.setRefid(questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_REFID)));
			q.setQuizRefid(quizrefid);
			q.setMaxscore(questionCur.getInt(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_MAXSCORE)));
			q.setOrderno(questionCur.getInt(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_ORDERNO)));
			q.setQtext(questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_TEXT)));
			q.setQtype(questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_TYPE)));
			q.setQhint(questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_HINT)));
			
			// add responses
			Cursor respCur = dbHelper.getResponsesForQuestion(quizrefid, questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_REFID)));
			respCur.moveToFirst();
			while (respCur.isAfterLast() == false) { 
				Response r = new Response();
				r.setDbid(respCur.getInt(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_ID)));
				r.setRefid(respCur.getString(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_REFID)));
				r.setQuizRefid(respCur.getString(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_QUIZREFID)));
				r.setQuestionRefid(respCur.getString(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_QUESTIONREFID)));
				r.setScore(respCur.getFloat(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_SCORE)));
				r.setOrderno(respCur.getInt(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_ORDERNO)));
				r.setText(respCur.getString(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_TEXT)));
				r.setSelected(false);
				
				q.addResponse(r);
				respCur.moveToNext();
			}
			
			quiz.addQuestion(q);
			questionCur.moveToNext();
		}
		
		dbHelper.close();
    }

}
