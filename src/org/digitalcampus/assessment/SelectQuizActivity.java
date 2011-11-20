package org.digitalcampus.assessment;

import org.digitalcampus.assessment.model.DbHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SelectQuizActivity extends Activity{

	LinearLayout quizzesLL;
	private DbHelper dbHelper;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectquiz);
        
        LinearLayout quizzesLL = (LinearLayout) findViewById(R.id.quizzes);
        
        dbHelper = new DbHelper(this);
        Cursor cur = dbHelper.getQuizzes();
        cur.moveToFirst();
        while (cur.isAfterLast() == false) {  
        	Button b = new Button(this);
        	b.setTag(cur.getString(cur.getColumnIndex(DbHelper.QUIZ_C_REFID)));
        	b.setTypeface(Typeface.DEFAULT_BOLD);
        	b.setTextSize(20);
        	b.setText(cur.getString(cur.getColumnIndex(DbHelper.QUIZ_C_TITLE)));
        	
        	b.setOnClickListener(new View.OnClickListener() {
             	@Override
     			public void onClick(View v) {
             		Intent i = new Intent(SelectQuizActivity.this, QuizActivity.class);
             		Bundle tb = new Bundle();
    				tb.putString("refid", (String) v.getTag());
    				i.putExtras(tb);
             		startActivity(i);
             	}
             });        	
        	quizzesLL.addView(b);
        	
        	cur.moveToNext(); 
        }
        cur.close();
        dbHelper.close();        
        
    }
}
