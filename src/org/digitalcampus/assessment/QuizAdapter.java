package org.digitalcampus.assessment;

import java.util.ArrayList;
import java.util.HashMap;

import org.digitalcampus.mquiz.model.Quiz;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class QuizAdapter extends SimpleAdapter{
	
	private static final String TAG = "QuizAdapter";
	private final Activity context;
	private final ArrayList<HashMap<String,String>> data;
	public HashMap<String,Quiz> checkedQuizzes;
	
	public QuizAdapter(Activity context, ArrayList<HashMap<String,String>> data, int resource, String[] from, int[] to) {
		super(context, data, R.layout.quizlist, from, to);
		this.context = context;
		this.data = data;
		checkedQuizzes = new HashMap<String,Quiz>(data.size());
		for(HashMap<String, String> d : data){
			Quiz q = new Quiz(d.get("id"));
			q.setTitle(d.get("name"));
			q.setUrl( d.get("url"));
			q.setChecked(false);
			checkedQuizzes.put(d.get("id"), q);
		}
	}
	
	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.quizlist, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.name);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
							String msg = (String) viewHolder.checkbox.getTag();
							checkedQuizzes.get(msg).setChecked(isChecked);
						}
					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(data.get(position).get("id"));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(data.get(position).get("id"));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(data.get(position).get("name"));
		return view;
	}

}
