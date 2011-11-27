package org.digitalcampus.assessment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.digitalcampus.mquiz.model.DbHelper;

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
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        
        DbHelper dbHelper = new DbHelper(this);
        Cursor cur = dbHelper.getQuizzes();
        cur.moveToFirst();
        while(cur.isAfterLast() == false){
        	HashMap<String,String> item = new HashMap<String,String>();
		    
		    item.put("id", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_C_REFID)));
		    item.put("name", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_C_TITLE)));
		    item.put("url","");
		    list.add(item);
        	cur.moveToNext();
        }
        cur.close();
        dbHelper.close();
        qa = new QuizAdapter(this,
				list,
				R.layout.quizlist,
				new String[] {"name"},
				new int[] {R.id.name});

        this.setListAdapter(qa);
	}
	
	private void deleteQuizzes(){
		Iterator<String> itr = qa.checkedQuizzes.keySet().iterator();
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
		setupList();
	}
}
