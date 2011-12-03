package org.digitalcampus.mquiz.widgets;

import java.util.List;

import org.digitalcampus.assessment.R;
import org.digitalcampus.mquiz.model.Response;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MultiSelectWidget extends QuestionWidget {

	private static final String TAG = "MultiSelectWidget";
	private Context ctx;
	private LinearLayout responsesLL;
	
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
	public void setQuestionResponses(List<Response> responses, List<String> currentAnswer) {
		responsesLL = (LinearLayout) ((Activity) ctx).findViewById(R.id.questionresponses);
    	responsesLL.removeAllViews();
    	
    	for (Response r : responses){
    		CheckBox chk= new CheckBox(ctx);  
    		chk.setText(r.getText());
    		responsesLL.addView(chk);
    		// TODO find out which are already checked and check these
    	}	
	}

	@Override
	public List<String> getQuestionResponses(List<Response> responses) {
		int count = responsesLL.getChildCount();
		for (int i=0; i<count; i++) {
			CheckBox cb = (CheckBox) responsesLL.getChildAt(i);
			if(cb.isChecked()){
				Log.d(TAG,cb.getText().toString());
			}
		}

		/*RadioGroup responsesRG = (RadioGroup) ((Activity) ctx).findViewById(234523465);
		int resp = responsesRG.getCheckedRadioButtonId();
    	View rb = responsesRG.findViewById(resp);
    	int idx = responsesRG.indexOfChild(rb);
    	if (idx >= 0){
    		return responses.get(idx).getText();
    	}*/
    	return null;
	}

}
