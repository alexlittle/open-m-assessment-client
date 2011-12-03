package org.digitalcampus.mquiz.model.questiontypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.model.Response;

public class MultiSelect implements Serializable, QuizQuestion {

	private static final long serialVersionUID = 936284577467681053L;
	public static final String TAG = "MultiSelect";
	private int dbid;
	private String refid;
	private String quizrefid;
	private int orderno;
	private String qtext;
	private String qhint;
	private List<Response> responses = new ArrayList<Response>();
	private int userscore = 0;
	private List<String> response = new ArrayList<String>();
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
			Iterator<String> itr = this.response.iterator();
			while(itr.hasNext()) {
				String a = itr.next(); 
				if (r.getText().equals(a)){
					total += r.getScore();
				}
			}
			// fix marking so that if one of the incorrect scores is selected final mark is 0
			Iterator<String> itr2 = this.response.iterator();
			while(itr2.hasNext()) {
				String a = itr2.next(); 
				if (r.getText().equals(a) && r.getScore() == 0){
					total = 0;
				}
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

	public void setResponse(List<String> str) {
		this.response= str;
		
	}

	public List<String> getResponse() {
		return this.response;
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
