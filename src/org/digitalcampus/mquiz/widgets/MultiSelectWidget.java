package org.digitalcampus.mquiz.widgets;

import java.util.List;

import org.digitalcampus.assessment.R;
import org.digitalcampus.mquiz.model.Response;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MultiSelectWidget extends QuestionWidget {

	private Context ctx;
	public MultiSelectWidget(Context context) {
		super(context);
		this.ctx = context;
		
		LinearLayout ll = (LinearLayout) ((Activity) ctx).findViewById(R.id.quizResponseWidget);
		ll.removeAllViews();
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vv = vi.inflate(R.layout.widget_multiselect, null);
		ll.addView(vv);
	}

	@Override
	public void setQuestionResponses(List<Response> responses, String currentAnswer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getQuestionResponse(List<Response> responses) {
		// TODO Auto-generated method stub
		return null;
	}

}
