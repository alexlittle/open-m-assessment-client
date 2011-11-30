package org.digitalcampus.mquiz.widgets;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class QuestionWidget extends LinearLayout{
	
	public QuestionWidget(Context context) {
		super(context);
	}
	
	// Abstract method
	public abstract void setQuestionText(String text);
	public abstract void setQuestionHint(String text);
	public abstract void setQuestionResponse();

}
