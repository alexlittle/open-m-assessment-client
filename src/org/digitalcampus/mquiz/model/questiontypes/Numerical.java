package org.digitalcampus.mquiz.model.questiontypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.model.Response;

import android.util.Log;

public class Numerical implements Serializable, QuizQuestion {
	
	private static final long serialVersionUID = 808485823168202643L;
	public static final String TAG = "Numerical";
	private int dbid;
	private String refid;
	private String quizrefid;
	private int orderno;
	private String qtext;
	private String qhint;
	private List<Response> responseOptions = new ArrayList<Response>();
	private float userscore = 0;
	private List<String> userResponses = new ArrayList<String>();
	private HashMap<String,String> props = new HashMap<String,String>();
	private String feedback = "";
	
	@Override
	public void addResponseOption(Response r) {
		responseOptions.add(r);
	}
	@Override
	public List<Response> getResponseOptions() {
		return responseOptions;
	}
	
	@Override
	public List<String> getUserResponses() {
		return this.userResponses;
	}
	
	@Override
	public void mark() {
		Float userAnswer = null;
		this.userscore = 0;
		Iterator<String> itr = this.userResponses.iterator();
		while(itr.hasNext()) {
			String a = itr.next(); 
			try{
				userAnswer = Float.parseFloat(a);
			} catch (NumberFormatException nfe){
				
			}
		}
		float score = 0;
		if(userAnswer != null){
			float currMax = 0;
			// loop through the valid answers and check against these
			for (Response r : responseOptions){
				try{
					Float respNumber = Float.parseFloat(r.getText());
					Float tolerance = Float.parseFloat(r.getProp("tolerance"));
					if ((respNumber - tolerance <= userAnswer) && (userAnswer <= respNumber + tolerance)){
						if(r.getScore() > currMax){
							score = r.getScore();
							currMax = r.getScore();
							if(!(r.getProp("feedback") == null)){
								this.feedback = r.getProp("feedback");
							}
						} 
					}
				} catch (NumberFormatException nfe){
					
				}
			}
		}
		
		int maxscore = Integer.parseInt(this.getProp("maxscore"));
		if (score > maxscore){
			this.userscore = maxscore;
		} else {
			this.userscore = score;
		}
	}
	
	@Override
	public String getRefid() {
		return this.refid;
	}
	
	@Override
	public void setRefid(String refid) {
		this.refid = refid;	
	}
	
	@Override
	public String getQuizRefid() {
		return this.quizrefid;
	}
	
	@Override
	public void setQuizRefid(String quizrefid) {
		this.quizrefid = quizrefid;	
	}
	
	@Override
	public int getOrderno() {
		return this.orderno;
	}
	
	@Override
	public void setOrderno(int orderno) {
		this.orderno = orderno;	
	}

	@Override
	public String getQtext() {
		return this.qtext;
	}
	
	@Override
	public void setQtext(String qtext) {
		this.qtext = qtext;	
	}
	
	@Override
	public int getDbid() {
		return this.dbid;
	}
	
	@Override
	public void setDbid(int dbid) {
		this.dbid = dbid;
	}
	
	@Override
	public void setResponseOptions(List<Response> responses) {
		this.responseOptions = responses;
	}
	
	@Override
	public float getUserscore() {
		return this.userscore;
	}
	
	@Override
	public String getQhint() {
		return this.qhint;
	}
	
	@Override
	public void setQhint(String qhint) {
		this.qhint = qhint;
	}
	
	@Override
	public void setProps(HashMap<String,String> props) {
		this.props = props;
	}
	
	@Override
	public String getProp(String key) {
		return props.get(key);
	}
	@Override
	public void setUserResponses(List<String> str) {
		this.userResponses = str;
	}
	
	@Override
	public String getFeedback() {
		this.mark();
		return this.feedback;
	}

}
