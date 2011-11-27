package org.digitalcampus.mquiz.tasks;

public class APIRequest implements Cloneable {
	public String fullurl;
	public String baseurl;
	public String username;
	public String password;
	public int timeoutConnection;
	public int timeoutSocket;
	
	public String content;
	
	//TODO not best place for these but for now leave here....
	public int rowId;
	public String refId;
	
	public APIRequest clone(){
        try{
            return (APIRequest) super.clone();
        } catch( CloneNotSupportedException e ){
            return null;
        }
    } 
}
