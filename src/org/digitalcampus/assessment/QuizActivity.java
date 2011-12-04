package org.digitalcampus.assessment;

import java.util.HashMap;
import java.util.List;

import org.digitalcampus.mquiz.model.DbHelper;
import org.digitalcampus.mquiz.model.Quiz;
import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.model.Response;
import org.digitalcampus.mquiz.model.questiontypes.Essay;
import org.digitalcampus.mquiz.model.questiontypes.Matching;
import org.digitalcampus.mquiz.model.questiontypes.MultiChoice;
import org.digitalcampus.mquiz.model.questiontypes.MultiSelect;
import org.digitalcampus.mquiz.model.questiontypes.Numerical;
import org.digitalcampus.mquiz.model.questiontypes.ShortAnswer;
import org.digitalcampus.mquiz.widgets.EssayWidget;
import org.digitalcampus.mquiz.widgets.MatchingWidget;
import org.digitalcampus.mquiz.widgets.MultiChoiceWidget;
import org.digitalcampus.mquiz.widgets.MultiSelectWidget;
import org.digitalcampus.mquiz.widgets.NumericalWidget;
import org.digitalcampus.mquiz.widgets.QuestionWidget;
import org.digitalcampus.mquiz.widgets.ShortAnswerWidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class QuizActivity extends Activity {
	
	private static final String TAG = "QuizActivity";
	private String quizrefid = "";
	private Quiz quiz;
	
	private DbHelper dbHelper;
	
	private Button prevBtn;
	private Button nextBtn;
	
	private QuestionWidget qw;
	
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
        
        
    }
    
    protected void onStart(){
    	super.onStart();
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
    	
    	// show title
    	int qNo = quiz.getCurrentq()+1;
    	int qTotal = quiz.questions.size();
    	this.setTitle(getString(R.string.title_quiz,quiz.getTitle(),qNo,qTotal));

    	if(q.getProp("type").equals(MultiChoice.TAG.toLowerCase())){
    		qw = new MultiChoiceWidget(QuizActivity.this);
    	}
    	if(q.getProp("type").equals(Essay.TAG.toLowerCase())){
    		qw = new EssayWidget(QuizActivity.this);
    	}
    	if(q.getProp("type").equals(MultiSelect.TAG.toLowerCase())){
    		qw = new MultiSelectWidget(QuizActivity.this);
    	}
    	if(q.getProp("type").equals(ShortAnswer.TAG.toLowerCase())){
    		qw = new ShortAnswerWidget(QuizActivity.this);
    	}
    	if(q.getProp("type").equals(Matching.TAG.toLowerCase())){
    		qw = new MatchingWidget(QuizActivity.this);
    	}
    	if(q.getProp("type").equals(Numerical.TAG.toLowerCase())){
    		qw = new NumericalWidget(QuizActivity.this);
    	}
    	
    	// show the responses
    	qw.setQuestionText(q.getQtext());
		qw.setQuestionHint(quiz.questions.get(quiz.getCurrentq()).getQhint());
		qw.setQuestionResponses(q.getResponseOptions(),q.getUserResponses());
    	
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
    	List<String> answers = qw.getQuestionResponses(quiz.questions.get(quiz.getCurrentq()).getResponseOptions());
    	if(answers != null){
    		quiz.questions.get(quiz.getCurrentq()).setUserResponses(answers);
    		return true;
    	}
		return false;
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
			QuizQuestion q = null;
			
			String type = questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_TYPE));
			if(type.equals(MultiChoice.TAG.toLowerCase())){
				q = new MultiChoice();
			}
			if(type.equals(Essay.TAG.toLowerCase())){
				q = new Essay();
			}
			if(type.equals(MultiSelect.TAG.toLowerCase())){
				q = new MultiSelect();
			}
			if(type.equals(ShortAnswer.TAG.toLowerCase())){
				q = new ShortAnswer();
			}
			if(type.equals(Matching.TAG.toLowerCase())){
				q = new Matching();
			}
			if(type.equals(Numerical.TAG.toLowerCase())){
				q = new Numerical();
			}
			
			if(q != null){
				//Log.d(TAG,"refid:"+questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_REFID)));
				HashMap<String,String> props = dbHelper.getQuestionProps(questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_REFID)));
				q.setProps(props);
				q.setDbid(questionCur.getInt(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_ID)));
				q.setRefid(questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_REFID)));
				q.setQuizRefid(quizrefid);
				q.setOrderno(questionCur.getInt(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_ORDERNO)));
				q.setQtext(questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_TEXT)));
				q.setQhint(questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_HINT)));
				
				// add responses
				Cursor respCur = dbHelper.getResponsesForQuestion(quizrefid, questionCur.getString(questionCur.getColumnIndex(DbHelper.QUIZ_QUESTION_C_REFID)));
				
				respCur.moveToFirst();
				while (respCur.isAfterLast() == false) { 
					Response r = new Response();
					HashMap<String,String> rProps = dbHelper.getResponseProps(respCur.getString(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_REFID)));
					r.setProps(rProps);
					r.setDbid(respCur.getInt(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_ID)));
					r.setQuizRefid(respCur.getString(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_QUIZREFID)));
					r.setQuestionRefid(respCur.getString(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_QUESTIONREFID)));
					r.setScore(respCur.getFloat(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_SCORE)));
					r.setOrderno(respCur.getInt(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_ORDERNO)));
					r.setText(respCur.getString(respCur.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_C_TEXT)));
					
					q.addResponseOption(r);
					respCur.moveToNext();
				}
				quiz.addQuestion(q);
			}
			questionCur.moveToNext();
		}
		
		dbHelper.close();
    }

}
