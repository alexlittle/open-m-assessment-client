package org.digitalcampus.assessment;

import java.util.List;

import org.digitalcampus.mquiz.model.Quiz;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class QuizAdapter extends ArrayAdapter<Quiz>{
	
	private static final String TAG = "QuizAdapter";
	private LayoutInflater inflater; 
	public List<Quiz> qList;
	
	public QuizAdapter(Activity context, List<Quiz> quizList) {
		super( context, R.layout.simplerow, R.id.rowTextView, quizList ); 
		inflater = LayoutInflater.from(context) ; 
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Quiz q = (Quiz) this.getItem( position ); 
		CheckBox checkBox ;   
	    TextView textView ;  
		
	    if ( convertView == null ) {  
	        convertView = inflater.inflate(R.layout.simplerow, null);  
	          
	        // Find the child views.  
	        textView = (TextView) convertView.findViewById( R.id.rowTextView );  
	        checkBox = (CheckBox) convertView.findViewById( R.id.CheckBox01 );  
	          
	        // Optimization: Tag the row with it's child views, so we don't have to   
	        // call findViewById() later when we reuse the row.  
	        convertView.setTag( new QuizViewHolder(textView,checkBox) );  
	  
	        // If CheckBox is toggled, update the planet it is tagged with.  
	        checkBox.setOnClickListener( new View.OnClickListener() {  
		          public void onClick(View v) {  
		            CheckBox cb = (CheckBox) v ;  
		            Quiz q = (Quiz) cb.getTag();  
		            q.setChecked( cb.isChecked() );  
		          }  
		        });          
	      } else  {  
	          // Because we use a ViewHolder, we avoid having to call findViewById().  
	          QuizViewHolder viewHolder = (QuizViewHolder) convertView.getTag();  
	          checkBox = viewHolder.getCheckBox() ;  
	          textView = viewHolder.getTextView() ;  
	        }  
	    
	        // Tag the CheckBox with the Planet it is displaying, so that we can  
	        // access the planet in onClick() when the CheckBox is toggled.  
	        checkBox.setTag( q );   
	          
	        // Display planet data  
	        checkBox.setChecked( q.isChecked() );  
	        textView.setText( q.getTitle() );        
	          
	        return convertView;  
	      }  
	
	  private static class QuizViewHolder {  
		    private CheckBox checkBox ;  
		    private TextView textView ;  
		    public QuizViewHolder() {}  
		    public QuizViewHolder( TextView textView, CheckBox checkBox ) {  
		      this.checkBox = checkBox ;  
		      this.textView = textView ;  
		    }  
		    public CheckBox getCheckBox() {  
		      return checkBox;  
		    }  
		    public void setCheckBox(CheckBox checkBox) {  
		      this.checkBox = checkBox;  
		    }  
		    public TextView getTextView() {  
		      return textView;  
		    }  
		    public void setTextView(TextView textView) {  
		      this.textView = textView;  
		    }      
		  } 
}

