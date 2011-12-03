package org.digitalcampus.mquiz.model.questiontypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.model.Response;

public class MultiChoice implements Serializable, QuizQuestion {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6605393327170759582L;
	public static final String TAG = "MultiChoice";
	private int dbid;
	private String refid;
	private String quizrefid;
	private int orderno;
	private String qtext;
	private String qhint;
	private List<Response> responses = new ArrayList<Response>();
	private int userscore = 0;
	private String responseText = "";
	private HashMap<String,String> props = new HashMap<String,String>();
	
	public void addResponse(Response r){
		responses.add(r);
	}
	
	public List<Response> getResponses(){
		return responses;
	}
	
	public void mark(){
		// loop through the responses
		// find whichever are set as selected and add up the responses
		int total = 0;
		for (Response r : responses){
			if (r.getText() == this.responseText){
				total += r.getScore();
			}
		}
		int maxscore = Integer.parseInt(this.getProp("maxscore"));
		if (total > maxscore){
			userscore = maxscore;
		} else {
			userscore = total;
		}
	}
	
	public String getRefid() {
		return refid;
	}
	
	public void setRefid(String refid) {
		this.refid = refid;
	}
	
	public String getQuizRefid() {
		return quizrefid;
	}
	
	public void setQuizRefid(String quizrefid) {
		this.quizrefid = quizrefid;
	}
	
	public int getOrderno() {
		return orderno;
	}
	
	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}
	
	public String getQtext() {
		return qtext;
	}
	
	public void setQtext(String qtext) {
		this.qtext = qtext;
	}

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}

	public int getUserscore() {
		return userscore;
	}

	public String getQhint() {
		return qhint;
	}

	public void setQhint(String qhint) {
		this.qhint = qhint;
	}

	public void setResponse(String str) {
		this.responseText = str;
		
	}

	public String getResponse() {
		return this.responseText;
	}

	@Override
	public void setProps(HashMap<String,String> props) {
		this.props = props;
	}
	
	@Override
	public String getProp(String key) {
		return props.get(key);
	}

}

