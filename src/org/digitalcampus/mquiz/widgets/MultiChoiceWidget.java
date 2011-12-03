package org.digitalcampus.mquiz.widgets;


import java.util.List;

import org.digitalcampus.assessment.R;
import org.digitalcampus.mquiz.model.Response;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MultiChoiceWidget extends QuestionWidget {

	private static final String TAG = "MultipleChoiceWidget";
	
	private Context ctx;
	
	public MultiChoiceWidget(Context context) {
		super(context);
		this.ctx = context;
		
		LinearLayout ll = (LinearLayout) ((Activity) ctx).findViewById(R.id.quizResponseWidget);
		ll.removeAllViews();
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vv = vi.inflate(R.layout.widget_multichoice, null);
		ll.addView(vv);
	}

	@Override
	public void setQuestionResponses(List<Response> responses, String currentAnswer) {
		LinearLayout responsesLL = (LinearLayout) ((Activity) ctx).findViewById(R.id.questionresponses);
    	responsesLL.removeAllViews();
    	RadioGroup responsesRG = new RadioGroup(ctx);
    	responsesRG.setId(234523465);
    	responsesLL.addView(responsesRG);
    	
    	for (Response r : responses){
    		RadioButton rb = new RadioButton(ctx);
    		rb.setId(r.getDbid()*1000);
    	
			rb.setText(r.getText());
			responsesRG.addView(rb);
			if (r.getText() == currentAnswer){
				rb.setChecked(true);
			}
    	}
		
	}
	
	public String getQuestionResponse(List<Response> responses){
		RadioGroup responsesRG = (RadioGroup) ((Activity) ctx).findViewById(234523465);
		int resp = responsesRG.getCheckedRadioButtonId();
    	View rb = responsesRG.findViewById(resp);
    	int idx = responsesRG.indexOfChild(rb);
    	if (idx >= 0){
    		return responses.get(idx).getText();
    	}
    	return null;
	}

}
