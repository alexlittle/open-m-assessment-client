package org.digitalcampus.assessment.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.digitalcampus.assessment.model.Question;

public class Quiz implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2416034891439585524L;
	private static final String TAG = "Quiz";
	private String refid;
	private String title;
	private String url;
	private int maxscore;
	private boolean checked;
	private int currentq = 0;
	private int userscore;
	
	
	public List<Question> questions = new ArrayList<Question>();
	
	public Quiz(String refid){
		this.setRefId(refid);
	}
	
	public void addQuestion(Question q){
		questions.add(q);
	}
	
	public boolean hasNext(){
		if (this.currentq+1 < questions.size()){
			return true;
		}
		return false;
	}
	
	public boolean hasPrevious(){
		if (this.currentq > 0){
			return true;
		}
		return false;
	}
	
	public void moveNext(){
		if (currentq+1 < questions.size()){
			currentq++;
		}
	}

	public void movePrevious(){
		if (currentq > 0){
			currentq--;
		}
	}
	
	public void mark(){
		int total = 0;
		for (Question q : questions){
			q.mark();
			total += q.getUserscore();
		}
		if (total > maxscore){
			userscore = maxscore;
		} else {
			userscore = total;
		}
		//Log.d(TAG,"Total score: " + String.valueOf(userscore) + " out of "+ String.valueOf(maxscore));
	}
	
	public String getRefId() {
		return refid;
	}
	public void setRefId(String refid) {
		this.refid = refid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String t) {
		this.title = t;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public int getCurrentq() {
		return currentq;
	}

	public void setCurrentq(int currentq) {
		this.currentq = currentq;
	}

	public int getUserscore() {
		return userscore;
	}

	public int getMaxscore() {
		return maxscore;
	}

	public void setMaxscore(int maxscore) {
		this.maxscore = maxscore;
	}
	
	
}
