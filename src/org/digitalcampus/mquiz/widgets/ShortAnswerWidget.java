package org.digitalcampus.mquiz.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.digitalcampus.assessment.R;
import org.digitalcampus.mquiz.model.Response;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ShortAnswerWidget extends QuestionWidget {

	private static final String TAG = "ShortAnswerWidget";
	
	private Context ctx;
	
	public ShortAnswerWidget(Context context) {
		super(context);
		this.ctx = context;
		
		LinearLayout ll = (LinearLayout) ((Activity) ctx).findViewById(R.id.quizResponseWidget);
		ll.removeAllViews();
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vv = vi.inflate(R.layout.widget_shortanswer, null);
		ll.addView(vv);
	}


	@Override
	public void setQuestionResponses(List<Response> responses, List<String> currentAnswers) {
		EditText et = (EditText) ((Activity) ctx).findViewById(R.id.responsetext);
		Iterator<String> itr = currentAnswers.iterator(); 
		while(itr.hasNext()) {
		    String answer = itr.next(); 
		    et.setText(answer);
		} 
	}
	
	public List<String> getQuestionResponses(List<Response> responses){
		EditText et = (EditText) ((Activity) ctx).findViewById(R.id.responsetext);
		if(et.getText().toString().equals("")){
			return null;
		} else {
			List<String> response = new ArrayList<String>();
			response.add(et.getText().toString());
			return response;
		}
	}

}
