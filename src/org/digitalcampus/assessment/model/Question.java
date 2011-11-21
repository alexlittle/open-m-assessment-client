package org.digitalcampus.assessment.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Question implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3025790560015898491L;
	private static final String TAG = "Question";
	private int dbid;
	private String refid;
	private String quizrefid;
	private int orderno;
	private int maxscore;
	private String qtype;
	private String qtext;
	private String qhint;
	private List<Response> responses = new ArrayList<Response>();
	private int userscore = 0;
	
	public void addResponse(Response r){
		responses.add(r);
	}
	
	public List<Response> getResponses(){
		return responses;
	}
	public Response setResponseSelected(int id){
		Response r = responses.get(id);
		r.setSelected(true);
		return r;
	}
	
	public void mark(){
		// loop through the responses
		// find whichever are set as selected and add up the responses
		int total = 0;
		for (Response r : responses){
			if (r.isSelected()){
				total += r.getScore();
			}
		}
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
	
	public int getMaxscore() {
		return maxscore;
	}
	
	public void setMaxscore(int maxscore) {
		this.maxscore = maxscore;
	}
	
	public String getQtype() {
		return qtype;
	}
	
	public void setQtype(String qtype) {
		this.qtype = qtype;
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
	


}
