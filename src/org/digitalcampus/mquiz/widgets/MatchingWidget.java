package org.digitalcampus.mquiz.widgets;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.digitalcampus.assessment.R;
import org.digitalcampus.mquiz.model.Response;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class MatchingWidget extends QuestionWidget {

	private static final String TAG = "MatchingWidget";
	private LinearLayout responsesLL;
	private LinearLayout[] responseLayouts;
	private Context ctx;
	
	public MatchingWidget(Context context) {
		super(context);
		this.ctx = context;
		
		LinearLayout ll = (LinearLayout) ((Activity) ctx).findViewById(R.id.quizResponseWidget);
		ll.removeAllViews();
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vv = vi.inflate(R.layout.widget_matching, null);
		ll.addView(vv);
	}

	@Override
	public void setQuestionResponses(List<Response> responses, List<String> currentAnswer) {
		responsesLL = (LinearLayout) ((Activity) ctx).findViewById(R.id.questionresponses);
    	responsesLL.removeAllViews();
    	
    	// this could be tidied up - to use ArrayAdapters/Lists
    	HashMap<String,String> possibleAnswers = new HashMap<String,String>();
    	for (Response r : responses){
    		String[] temp = r.getText().split(" -&gt; ");
    		possibleAnswers.put(temp[0],temp[1]);
    	}
    	
    	Iterator<Entry<String, String>> responseIt = possibleAnswers.entrySet().iterator();
    	int counter = 0;
    	responseLayouts = new LinearLayout[responses.size()];
    	while (responseIt.hasNext()) {
    		HashMap.Entry<String,String> responsePairs = (HashMap.Entry<String,String>) responseIt.next();
    		LinearLayout responseLayout = new LinearLayout(ctx);
    		TextView tv = new TextView(ctx);
    		
    		tv.setText(responsePairs.getKey());
    		
    		Spinner spinner = new Spinner(ctx);
    		ArrayAdapter<CharSequence> responseAdapter = new ArrayAdapter<CharSequence>(ctx, android.R.layout.simple_spinner_item); 
    		responseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		spinner.setAdapter(responseAdapter); 
    		Iterator<Entry<String, String>> it = possibleAnswers.entrySet().iterator();
    		responseAdapter.add(""); 
    	    while (it.hasNext()) {
    	        HashMap.Entry<String,String> pairs = (HashMap.Entry<String,String>) it.next();
    	        responseAdapter.add(pairs.getValue()); 
    	    }
    		responseLayout.addView(tv);
    		responseLayout.addView(spinner);
    		
    		// set the selected item based on current responses
    		for (String s : currentAnswer){
        		String[] temp = s.split(" -&gt; ",-1);
        		if(temp[0].equals(responsePairs.getKey())){
        			int i = responseAdapter.getPosition(temp[1]);
        			spinner.setSelection(i);	
        		}
        	}
    		responsesLL.addView(responseLayout);
    		responseLayouts[counter] = responseLayout;
    		counter++;
    	}
		
	}
	
	public List<String> getQuestionResponses(List<Response> responses){
		
		List<String> userResponses = new ArrayList<String>();
		
		for (LinearLayout ll : this.responseLayouts){
			TextView tv = (TextView) ll.getChildAt(0);
			Spinner sp = (Spinner) ll.getChildAt(1);
			String response = tv.getText().toString() + " -&gt; " + sp.getSelectedItem().toString();
			userResponses.add(response);
		}
    	return userResponses;
	}

}
