package org.digitalcampus.mquiz.model.questiontypes;

import java.io.Serializable;
import java.util.List;

import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.model.Response;

public class Essay implements Serializable, QuizQuestion {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1531985882092686497L;
	private static final String TAG = "Essay";
	private int dbid;
	private String refid;
	private String quizrefid;
	private int orderno;
	private int maxscore;
	private String qtype;
	private String qtext;
	private String qhint;
	private int userscore = 0;
	private String responseText = "";
	
	@Override
	public void addResponse(Response r) {
		// do nothing
	}
	@Override
	public List<Response> getResponses() {
		return null;
	}
	
	@Override
	public void setResponse(String str) {
		this.responseText = str;
		
	}
	
	@Override
	public String getResponse() {
		return this.responseText;
	}
	@Override
	public void mark() {
		this.userscore = 0;
	}
	@Override
	public String getRefid() {
		// TODO Auto-generated method stub
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
	public int getMaxscore() {
		return this.maxscore;
	}
	@Override
	public void setMaxscore(int maxscore) {
		this.maxscore = maxscore;
		
	}
	@Override
	public String getQtype() {
	
		return this.qtype;
	}
	@Override
	public void setQtype(String qtype) {
		this.qtype = qtype;
		
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
		// TODO Auto-generated method stub
		return this.dbid;
	}
	@Override
	public void setDbid(int dbid) {
		this.dbid = dbid;
		
	}
	@Override
	public void setResponses(List<Response> responses) {
		// do nothing
	}
	@Override
	public int getUserscore() {
		// TODO Auto-generated method stub
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

}
