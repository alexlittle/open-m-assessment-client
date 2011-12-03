package org.digitalcampus.mquiz.model.questiontypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.model.Response;

public class Essay implements Serializable, QuizQuestion {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1531985882092686497L;
	public static final String TAG = "Essay";
	private int dbid;
	private String refid;
	private String quizrefid;
	private int orderno;
	private String qtext;
	private String qhint;
	private int userscore = 0;
	private List<String> userResponses = new ArrayList<String>();
	private HashMap<String,String> props = new HashMap<String,String>();
	
	@Override
	public void addResponseOption(Response r) {
		// do nothing
	}
	@Override
	public List<Response> getResponseOptions() {
		return null;
	}
	
	@Override
	public List<String> getUserResponses() {
		return this.userResponses;
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
	public void setResponseOptions(List<Response> responses) {
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

}
