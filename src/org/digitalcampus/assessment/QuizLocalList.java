package org.digitalcampus.assessment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.digitalcampus.mquiz.model.DbHelper;
import org.digitalcampus.mquiz.model.Quiz;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class QuizLocalList extends ListActivity{
	
	private static final String TAG = "QuizLocalList";
	
	private QuizAdapter qa;
	private Button deleteBtn;
	private ArrayList<Quiz> quizList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizlocallist);
        
        deleteBtn = (Button) findViewById(R.id.deleteQuizBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(QuizLocalList.this);
        		builder.setTitle("Clear results");
        		builder.setMessage("Are you sure you want to remove all selected quizzes?");
        		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) { 
        	            // continue with delete
        	        	deleteQuizzes();
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
        setupList();
	}
	
	
	private void setupList(){
       
        DbHelper dbHelper = new DbHelper(this);
        Cursor cur = dbHelper.getQuizzes();
        
        cur.moveToFirst();
        int c= 0;
        while(cur.isAfterLast() == false){
        	c++;
        	cur.moveToNext();
        }
        Quiz[] quizzes = new Quiz[c];
        
        cur.moveToFirst();
        int i = 0;
        while(cur.isAfterLast() == false){
        	Quiz q = new Quiz(cur.getString(cur.getColumnIndex(DbHelper.QUIZ_C_REFID)));
		    q.setTitle(cur.getString(cur.getColumnIndex(DbHelper.QUIZ_C_TITLE)));
		    q.setUrl("");
		    quizzes[i] = q;
        	i++;
        	cur.moveToNext();
        }
        cur.close();
        dbHelper.close();
        quizList = new ArrayList<Quiz>();  
		quizList.addAll( Arrays.asList(quizzes) );
		
		qa = new QuizAdapter(this, quizList);
		
		this.setListAdapter(qa);
	}
	
	private void deleteQuizzes(){
		
		DbHelper dbh = new DbHelper(QuizLocalList.this);
		for(int i=0;i<quizList.size();i++){
    		Quiz q = (Quiz) (quizList.get(i));
    		if(q.isChecked()){
    			dbh.clearQuiz(q.getRef());
    		}
    	}
		dbh.close();
		// now reset list
		setupList();
		
		/*Iterator<String> itr = qa.checkedQuizzes.keySet().iterator();
		DbHelper dbh = new DbHelper(QuizLocalList.this);
		while(itr.hasNext()){
			String id = itr.next();
			if (qa.checkedQuizzes.get(id).isChecked()){
				Log.d(TAG,"removing: " + id);
				dbh.clearQuiz(id);
			}
		}
		dbh.close();
		// now reset list
		setupList();*/
	}
}
