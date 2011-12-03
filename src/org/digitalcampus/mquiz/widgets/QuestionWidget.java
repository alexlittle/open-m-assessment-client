package org.digitalcampus.mquiz.widgets;

import java.util.List;

import org.digitalcampus.assessment.R;
import org.digitalcampus.mquiz.model.Response;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class QuestionWidget extends LinearLayout{
	
	private Context ctx;
	
	public QuestionWidget(Context context) {
		super(context);
		this.ctx = context;
	}
	
	public void setQuestionText(String text) {
		TextView qText = (TextView) ((Activity) ctx).findViewById(R.id.questiontext);
    	qText.setText(text);
		
	}
	
	public void setQuestionHint(String text) {
		TextView qHint = (TextView) ((Activity) ctx).findViewById(R.id.questionhint);
    	if(text.equals("")){
    		qHint.setVisibility(View.GONE);
    	} else {
    		qHint.setText(text);
    		qHint.setVisibility(View.VISIBLE);
    	}
		
	}
	// Abstract methods
	public abstract void setQuestionResponses(List<Response> responses, List<String> currentAnswers);
	public abstract List<String> getQuestionResponses(List<Response> responses);

}
