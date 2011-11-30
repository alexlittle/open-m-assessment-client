package org.digitalcampus.mquiz.widgets;

import java.util.List;

import org.digitalcampus.assessment.R;
import org.digitalcampus.mquiz.model.Response;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class EssayWidget extends QuestionWidget {

	private static final String TAG = "EssayWidget";
	
	private Context ctx;
	
	public EssayWidget(Context context) {
		super(context);
		this.ctx = context;
		
		LinearLayout ll = (LinearLayout) ((Activity) ctx).findViewById(R.id.quizQuestion);
		ll.removeAllViews();
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vv = vi.inflate(R.layout.widget_essay, null);
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
	public void setQuestionResponses(List<Response> responses, String currentAnswer) {
		EditText et = (EditText) ((Activity) ctx).findViewById(R.id.responsetext);
		et.setText(currentAnswer);
	}
	
	public String getQuestionResponse(List<Response> responses){
		EditText et = (EditText) ((Activity) ctx).findViewById(R.id.responsetext);
		if(et.getText().toString().equals("")){
			return null;
		} else {
			return et.getText().toString();
		}
	}

}
