package org.digitalcampus.mquiz.widgets;


import org.digitalcampus.assessment.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MultipleChoiceWidget extends QuestionWidget {

	private static final String TAG = "MultipleChoiceWidget";
	
	private Context ctx;
	
	public MultipleChoiceWidget(Context context, LinearLayout ll) {
		super(context);
		//Resources res = getResources();
		//Activity a = (Activity) context;
		//a.setContentView(R.layout.widget_multichoice);
		this.ctx = context;
		
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vv = vi.inflate(R.layout.widget_multichoice, null);
		ll.addView(vv);
	}

	
	@Override
	public void setQuestionText(String text) {
		TextView qText = (TextView) ((Activity) ctx).findViewById(R.id.questiontext);
    	qText.setText(text);
		
	}

	@Override
	public void setQuestionHint(String text) {
		TextView qHint = (TextView) ((Activity) ctx).findViewById(R.id.questionhint);
    	if(text.equals("")){
    		qHint.setVisibility(View.GONE);
    	} else {
    		qHint.setText(text);
    		qHint.setVisibility(View.VISIBLE);
    	}
		
	}

	@Override
	public void setQuestionResponse() {
		// TODO Auto-generated method stub
		
	}

}
