package org.digitalcampus.mquiz.model;

import java.io.Serializable;
import java.util.List;


public interface QuizQuestion extends Serializable {
	
	public void addResponse(Response r);
	
	public List<Response> getResponses();
	
	public Response setResponseSelected(int id);
	
	public void mark();
	
	public String getRefid();
	
	public void setRefid(String refid);
	
	public String getQuizRefid();
	
	public void setQuizRefid(String quizrefid);
	
	public int getOrderno();
	
	public void setOrderno(int orderno);
	
	public int getMaxscore();
	
	public void setMaxscore(int maxscore);
	
	public String getQtype();
	
	public void setQtype(String qtype);
	
	public String getQtext();
	
	public void setQtext(String qtext);

	public int getDbid();

	public void setDbid(int dbid);

	public void setResponses(List<Response> responses);

	public int getUserscore();

	public String getQhint();

	public void setQhint(String qhint);
}
