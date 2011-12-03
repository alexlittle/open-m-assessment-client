package org.digitalcampus.mquiz.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{
	static final String TAG = "DbHelper";
	static final String DB_NAME = "assessment.db"; 
	static final int DB_VERSION = 26; 
	
	public static final String PROPS_TABLE = "Settings";
	public static final String PROPS_C_ID = BaseColumns._ID;
	public static final String PROPS_C_NAME = "propname";
	public static final String PROPS_C_VALUE = "propvalue";
	
	// Quiz Table
	public static final String QUIZ_TABLE = "Quiz";
	public static final String QUIZ_C_ID = BaseColumns._ID;
	public static final String QUIZ_C_REFID = "QuizRefID";
	public static final String QUIZ_C_TITLE = "QuizTitle";
	public static final String QUIZ_C_MAXSCORE = "QMaxScore";
	
	// QuizQuestion Table
	public static final String QUIZ_QUESTION_TABLE = "QuizQuestion";
	public static final String QUIZ_QUESTION_C_ID = BaseColumns._ID;
	public static final String QUIZ_QUESTION_C_REFID = "QuizQuestionRefID";
	public static final String QUIZ_QUESTION_C_QUIZREFID = "QuizRefID"; // references Q_C_REFID
	public static final String QUIZ_QUESTION_C_ORDERNO = "QuizQuestionOrderNo";
	public static final String QUIZ_QUESTION_C_TYPE = "QuizQuestionType";
	public static final String QUIZ_QUESTION_C_TEXT = "QuizQuestionText";
	public static final String QUIZ_QUESTION_C_HINT = "QuizQuestionHint";
	
	// QuizQuestionProps Table
	public static final String QUIZ_QUESTION_PROPS_TABLE = "QuizQuestionProps";
	public static final String QUIZ_QUESTION_PROPS_C_ID = BaseColumns._ID;
	public static final String QUIZ_QUESTION_PROPS_C_QUESTIONREFID = "QuizQuestionRefID";
	public static final String QUIZ_QUESTION_PROPS_C_KEY = "QQPKey";
	public static final String QUIZ_QUESTION_PROPS_C_VALUE = "QQPValue";
	
	// QuizQuestionResponse Table
	public static final String QUIZ_QUESTION_RESPONSE_TABLE = "QuizQuestionResponse";
	public static final String QUIZ_QUESTION_RESPONSE_C_ID = BaseColumns._ID;
	public static final String QUIZ_QUESTION_RESPONSE_C_REFID = "QuizQuestionResponseRefID";
	public static final String QUIZ_QUESTION_RESPONSE_C_QUIZREFID = "QuizRefID"; // references Q_C_REFID
	public static final String QUIZ_QUESTION_RESPONSE_C_QUESTIONREFID = "QuizQuestionRefID"; // references QQ_C_REFID
	public static final String QUIZ_QUESTION_RESPONSE_C_ORDERNO = "QuizQuestionResponseOrderNo";
	public static final String QUIZ_QUESTION_RESPONSE_C_TEXT = "QuizQuestionResponseText";
	public static final String QUIZ_QUESTION_RESPONSE_C_SCORE = "QuizQuestionResponseScore";
	
	// QuizQuestionResponseProps Table
	public static final String QUIZ_QUESTION_RESPONSE_PROPS_TABLE = "QuizQuestionResponseProps";
	public static final String QUIZ_QUESTION_RESPONSE_PROPS_C_ID = BaseColumns._ID;
	public static final String QUIZ_QUESTION_RESPONSE_PROPS_C_RESPONSEREFID = "QuizQuestionResponseRefID";
	public static final String QUIZ_QUESTION_RESPONSE_PROPS_C_KEY = "QQRPKey";
	public static final String QUIZ_QUESTION_RESPONSE_PROPS_C_VALUE = "QQRPValue";
	
	// QuizAttempt Table
	public static final String QUIZ_ATTEMPT_TABLE = "QuizAttempt"; 
	public static final String QUIZ_ATTEMPT_C_ID = BaseColumns._ID;
	public static final String QUIZ_ATTEMPT_C_QUIZDATE = "QuizAttemptDate";
	public static final String QUIZ_ATTEMPT_C_QUIZREFID = "QuizRefId"; //references Q_C_REFID
	public static final String QUIZ_ATTEMPT_C_USERNAME = "UserName";
	public static final String QUIZ_ATTEMPT_C_USERSCORE = "UserScore";
	public static final String QUIZ_ATTEMPT_C_MAXSCORE = "MaxScore";
	public static final String QUIZ_ATTEMPT_C_SUBMITTED = "Submitted";

	public static final String QUIZ_ATTEMPT_GET_ALL_ORDER_BY_DESC = DbHelper.QUIZ_ATTEMPT_C_QUIZDATE + " DESC";
	public static final String QUIZ_ATTEMPT_GET_ALL_ORDER_BY_ASC = DbHelper.QUIZ_ATTEMPT_C_QUIZDATE + " ASC";
	
	// QuizAttemptResponse Table
	public static final String QUIZ_ATTEMPT_RESPONSE_TABLE = "QuizAttemptResponse";
	public static final String QUIZ_ATTEMPT_RESPONSE_C_ID = BaseColumns._ID;
	public static final String QUIZ_ATTEMPT_RESPONSE_C_ATTEMPTID = "AttemptID"; //references QUIZ_ATTEMPT_C_ID
	public static final String QUIZ_ATTEMPT_RESPONSE_C_QUIZREFID = "QuizRefID"; // references Q_C_REFID
	public static final String QUIZ_ATTEMPT_RESPONSE_C_QUESTIONREFID = "QuizQuestionRefID"; // references QQ_C_REFID
	public static final String QUIZ_ATTEMPT_RESPONSE_C_SCORE = "QQRScore";
	public static final String QUIZ_ATTEMPT_RESPONSE_C_TEXT = "QQRText";
		
	private SQLiteDatabase db;
	
	// Constructor
	public DbHelper(Context context) { //
		super(context, DB_NAME, null, DB_VERSION);
		db = this.getReadableDatabase();
	}
	
	// Called only once, first time the DB is created
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// create Settings Table
		String s_sql = "create table " + PROPS_TABLE + " (" + 
								PROPS_C_ID + " integer primary key autoincrement, " + 
								PROPS_C_NAME + " text, " + 
								PROPS_C_VALUE + " text)"; 
		db.execSQL(s_sql);
		Log.d(TAG, "Quiz sql: " + s_sql);
		// create Quiz Table
		String q_sql = "create table " + QUIZ_TABLE + " (" + 
								QUIZ_C_ID + " integer primary key autoincrement, " + 
								QUIZ_C_REFID + " text, " + 
								QUIZ_C_TITLE + " text, " + 
								QUIZ_C_MAXSCORE + " integer)"; 
		db.execSQL(q_sql);
		Log.d(TAG, "Quiz sql: " + q_sql);
		
		// create QuizQuestion Table
		String qq_sql = "create table " + QUIZ_QUESTION_TABLE + " (" + 
							QUIZ_QUESTION_C_ID + " integer primary key autoincrement, " + 
							QUIZ_QUESTION_C_REFID + " text, " +
							QUIZ_QUESTION_C_QUIZREFID + " text, " + 
							QUIZ_QUESTION_C_ORDERNO + " integer, " + 
							QUIZ_QUESTION_C_TEXT + " text, " +
							QUIZ_QUESTION_C_TYPE + " text, " +
							QUIZ_QUESTION_C_HINT + " text)";
		db.execSQL(qq_sql);
		Log.d(TAG, "QuizQuestion sql: " + qq_sql);
		
		// create QuizQuestionProps Table
		String qqp_sql = "create table " + QUIZ_QUESTION_PROPS_TABLE + " (" + 
							QUIZ_QUESTION_PROPS_C_ID + " integer primary key autoincrement, " + 
							QUIZ_QUESTION_PROPS_C_QUESTIONREFID + " text, " +
							QUIZ_QUESTION_PROPS_C_KEY + " text, " + 
							QUIZ_QUESTION_PROPS_C_VALUE + " text)";
		db.execSQL(qqp_sql);
		Log.d(TAG, "QuizQuestion sql: " + qqp_sql);
		
		// create QuizQuestionResponse Table
		String qqr_sql = "create table " + QUIZ_QUESTION_RESPONSE_TABLE + " (" + 
							QUIZ_QUESTION_RESPONSE_C_ID + " integer primary key autoincrement, " + 
							QUIZ_QUESTION_RESPONSE_C_REFID + " text, " + 
							QUIZ_QUESTION_RESPONSE_C_QUIZREFID + " text, " +
							QUIZ_QUESTION_RESPONSE_C_QUESTIONREFID + " text, " + 
							QUIZ_QUESTION_RESPONSE_C_ORDERNO + " integer, " + 
							QUIZ_QUESTION_RESPONSE_C_TEXT + " text, " + 
							QUIZ_QUESTION_RESPONSE_C_SCORE + " real)"; 
		db.execSQL(qqr_sql);
		Log.d(TAG, "QuizQuestion sql: " + qqr_sql);
		
		// create QuizQuestionProps Table
		String qqrp_sql = "create table " + QUIZ_QUESTION_RESPONSE_PROPS_TABLE + " (" + 
							QUIZ_QUESTION_RESPONSE_PROPS_C_ID + " integer primary key autoincrement, " + 
							QUIZ_QUESTION_RESPONSE_PROPS_C_RESPONSEREFID + " text, " +
							QUIZ_QUESTION_RESPONSE_PROPS_C_KEY + " text, " + 
							QUIZ_QUESTION_RESPONSE_PROPS_C_VALUE + " text)";
		db.execSQL(qqrp_sql);
		Log.d(TAG, "QuizQuestion sql: " + qqrp_sql);
				
		// create QuizAttempt Table
		String qa_sql = "create table " + QUIZ_ATTEMPT_TABLE + " (" + 
							QUIZ_ATTEMPT_C_ID + " integer primary key autoincrement, " + 
							QUIZ_ATTEMPT_C_QUIZDATE + " integer, " + 
							QUIZ_ATTEMPT_C_QUIZREFID + " text, " + 
							QUIZ_ATTEMPT_C_USERNAME + " text, " +
							QUIZ_ATTEMPT_C_USERSCORE + " integer, " +
							QUIZ_ATTEMPT_C_MAXSCORE + " integer, " +
							QUIZ_ATTEMPT_C_SUBMITTED + " integer)"; 
		Log.d(TAG, "QuizAttempt sql: " + qa_sql);
		db.execSQL(qa_sql);
	
		
		// create QuizAttemptResponse Table
		String qar_sql = "create table " + QUIZ_ATTEMPT_RESPONSE_TABLE + " (" + 
								QUIZ_ATTEMPT_RESPONSE_C_ID + " integer primary key autoincrement, " + 
								QUIZ_ATTEMPT_RESPONSE_C_ATTEMPTID + " int, " +
								QUIZ_ATTEMPT_RESPONSE_C_QUIZREFID + " text, " + 
								QUIZ_ATTEMPT_RESPONSE_C_QUESTIONREFID + " text, " + 
								QUIZ_ATTEMPT_RESPONSE_C_TEXT + " text, " +
								QUIZ_ATTEMPT_RESPONSE_C_SCORE + " real)"; 
		Log.d(TAG, "QuizAttempt sql: " + qar_sql);
		db.execSQL(qar_sql);

	}
	
	// Called whenever newVersion != oldVersion
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //
	// Typically do ALTER TABLE statements, but...we're just in development,
	// so:
		
		// drop Props table
		db.execSQL("drop table if exists " + PROPS_TABLE);
				
		// drop Quiz table
		db.execSQL("drop table if exists " + QUIZ_TABLE);
		
		// drop QuizQuestion table
		db.execSQL("drop table if exists " + QUIZ_QUESTION_TABLE);
		
		// drop QuizQuestionProps table
		db.execSQL("drop table if exists " + QUIZ_QUESTION_PROPS_TABLE);
		
		// drop QuizQuestionResponse table
		db.execSQL("drop table if exists " + QUIZ_QUESTION_RESPONSE_TABLE);
		
		// drop QuizQuestionProps table
		db.execSQL("drop table if exists " + QUIZ_QUESTION_RESPONSE_PROPS_TABLE);
				
		// drop QuizAttempt table
		db.execSQL("drop table if exists " + QUIZ_ATTEMPT_TABLE); 
		
		// drop QuizAttemptResponse table
		db.execSQL("drop table if exists " + QUIZ_ATTEMPT_RESPONSE_TABLE);
		
		 // run onCreate to get new database
		Log.d(TAG, "onUpdated: DB tables dropped");
		onCreate(db);
	}
	
	public Cursor getQuizzes(){
		String order = QUIZ_C_TITLE + " ASC";
		return db.query(QUIZ_TABLE, null, null , null, null, null, order);
	}
	
	public Cursor getQuiz(String qrefid){
		String selection = QUIZ_C_REFID + "= ?";
		String[] selArgs = new String[] {qrefid};
		return db.query(QUIZ_TABLE, null, selection , selArgs, null, null, null);
	}
	
	public Cursor getQuestionsForQuiz(String qrefid){
		String selection = QUIZ_QUESTION_C_QUIZREFID + "= ?";
		String[] selArgs = new String[] {qrefid};
		String order = QUIZ_QUESTION_C_ORDERNO + " ASC";
		return db.query(QUIZ_QUESTION_TABLE, null, selection , selArgs, null, null, order);
	}
	
	public Cursor getResponsesForQuestion(String qrefid, String qqrefid){
		String selection = DbHelper.QUIZ_QUESTION_RESPONSE_C_QUIZREFID + "=? AND " + DbHelper.QUIZ_QUESTION_RESPONSE_C_QUESTIONREFID + "=? ";
		String[] selArgs = new String[] {qrefid, qqrefid};
		String order = DbHelper.QUIZ_QUESTION_RESPONSE_C_ORDERNO + " ASC";
		return db.query(DbHelper.QUIZ_QUESTION_RESPONSE_TABLE, null, selection , selArgs, null, null, order);
	}
	
	public Cursor getUnsubmitted(){
		String criteria = DbHelper.QUIZ_ATTEMPT_C_SUBMITTED + "= 0";
		return db.query(DbHelper.QUIZ_ATTEMPT_TABLE, null, criteria , null, null, null, QUIZ_ATTEMPT_GET_ALL_ORDER_BY_ASC);	
	}

	public Cursor getUnsubmitted(int id){
		String criteria = DbHelper.QUIZ_ATTEMPT_C_SUBMITTED + "= 0 AND " + DbHelper.QUIZ_ATTEMPT_C_ID + "=" + String.valueOf(id);
		return db.query(DbHelper.QUIZ_ATTEMPT_TABLE, null, criteria , null, null, null, QUIZ_ATTEMPT_GET_ALL_ORDER_BY_ASC);	
	}
	
	public Cursor getAttemptResponses(int attemptid){
		String criteria = DbHelper.QUIZ_ATTEMPT_RESPONSE_C_ATTEMPTID + "=" + String.valueOf(attemptid);
		return db.query(DbHelper.QUIZ_ATTEMPT_RESPONSE_TABLE, null, criteria , null, null, null, null);
	}
	
	public void clearSubmitted(){
		// first get the records which will be removed
		String criteria = DbHelper.QUIZ_ATTEMPT_C_SUBMITTED + "= 1";
		Cursor c = db.query(DbHelper.QUIZ_ATTEMPT_TABLE, null, criteria , null, null, null, null);
		
		// remove all the attempt responses associated with this 
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			String s = DbHelper.QUIZ_ATTEMPT_RESPONSE_C_ATTEMPTID+"=?";
			String[] args = new String[] {c.getString(c.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_ID))};
			db.delete(DbHelper.QUIZ_ATTEMPT_RESPONSE_TABLE, s, args);
			
			//now remove the attempt
			s = DbHelper.QUIZ_ATTEMPT_C_ID+"=?";
			args = new String[] {c.getString(c.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_ID))};
			db.delete(DbHelper.QUIZ_ATTEMPT_TABLE, s, args);
			
			c.moveToNext();
		}
		c.close();
	}
	
	public Cursor getSavedResults(){
		String myQry = "SELECT a." + DbHelper.QUIZ_ATTEMPT_C_ID + ", " +
				"strftime('%H:%M %d-%m-%Y',datetime(a." + DbHelper.QUIZ_ATTEMPT_C_QUIZDATE + ",'unixepoch', 'localtime')) as QuizDateStr, " +
				"a."+ DbHelper.QUIZ_ATTEMPT_C_QUIZREFID + ", " +
				"b." + DbHelper.QUIZ_C_TITLE + ", " +
				"(a."+ DbHelper.QUIZ_ATTEMPT_C_USERSCORE +"*100/a."+ DbHelper.QUIZ_ATTEMPT_C_MAXSCORE +") || '%' as Score" +
				" FROM " + DbHelper.QUIZ_ATTEMPT_TABLE +" a " +
				" INNER JOIN " + DbHelper.QUIZ_TABLE + " b ON a."+ DbHelper.QUIZ_ATTEMPT_C_QUIZREFID + " = b." + DbHelper.QUIZ_C_REFID +
				" ORDER BY a."+ DbHelper.QUIZ_ATTEMPT_GET_ALL_ORDER_BY_DESC;
		
		return db.rawQuery(myQry, null);
	}
	
	public void setSubmitted(int id){
		ContentValues updateValues = new ContentValues();
		updateValues.put(DbHelper.QUIZ_ATTEMPT_C_SUBMITTED, true);
		db.update(DbHelper.QUIZ_ATTEMPT_TABLE, updateValues, DbHelper.QUIZ_ATTEMPT_C_ID + "=" + id, null); 
		db.close();
	}
	
	// remove quiz from database
	public void clearQuiz(String refid){
		// remove from QuizAttemptResponse
		db.delete(QUIZ_ATTEMPT_RESPONSE_TABLE, QUIZ_ATTEMPT_RESPONSE_C_QUIZREFID+"=?", new String[] {refid});
		
		// remove from QuizAttempt
		db.delete(QUIZ_ATTEMPT_TABLE, QUIZ_ATTEMPT_C_QUIZREFID+"=?", new String[] {refid});
		
		// remove from QuizQuestionResponse
		db.delete(QUIZ_QUESTION_RESPONSE_TABLE, QUIZ_QUESTION_RESPONSE_C_QUIZREFID+"=?", new String[] {refid});
		
		// remove from QuizQuestion
		db.delete(QUIZ_QUESTION_TABLE, QUIZ_QUESTION_C_QUIZREFID+"=?", new String[] {refid});
		
		// remove from Quiz
		db.delete(QUIZ_TABLE, QUIZ_C_REFID+"=?", new String[] {refid});
		
	}
	
	public boolean insertQuiz(JSONObject json){
		try {
			String quizRefId = (String) json.get("refid");
			String title = (String) json.get("title");
			int maxscore = Integer.parseInt((String) json.get("maxscore"));
			
			//check is already installed
			Cursor isInstalled = getQuiz(quizRefId);
			if(isInstalled.getCount() > 0){
				Log.d(TAG,"Quiz already installed");
				isInstalled.close();
				return true;
			}
			isInstalled.close();
			clearQuiz(quizRefId);
			
			// add to Quiz table
			ContentValues values = new ContentValues();
			values.put(DbHelper.QUIZ_C_REFID, quizRefId);
			values.put(DbHelper.QUIZ_C_TITLE, title);
			values.put(DbHelper.QUIZ_C_MAXSCORE, maxscore);
			db.insertOrThrow(DbHelper.QUIZ_TABLE, null, values);
			
			// load questions and add
			JSONArray questions = (JSONArray) json.get("q");
			
			for (int i=0; i<questions.length(); i++){
				JSONObject q = (JSONObject) questions.get(i);
				String questionRefId = (String) q.get("refid");
				int qorderno = Integer.parseInt((String) q.get("orderno"));
				String qtext = (String) q.get("text");
				String qhint = (String) q.optString("hint");
				String type = (String) q.optString("type");
				
				// add to QuizQuestion table
				ContentValues qvalues = new ContentValues();
				qvalues.put(DbHelper.QUIZ_QUESTION_C_REFID, questionRefId);
				qvalues.put(DbHelper.QUIZ_QUESTION_C_QUIZREFID, quizRefId);
				qvalues.put(DbHelper.QUIZ_QUESTION_C_ORDERNO, qorderno);
				qvalues.put(DbHelper.QUIZ_QUESTION_C_TEXT, qtext);
				qvalues.put(DbHelper.QUIZ_QUESTION_C_HINT, qhint);
				qvalues.put(DbHelper.QUIZ_QUESTION_C_TYPE, type);
				db.insertOrThrow(DbHelper.QUIZ_QUESTION_TABLE, null, qvalues);
				
				// add to the QuizQuestionProps
				JSONObject questionProps = (JSONObject) q.get("props");
				
				for (int k = 0; k < questionProps.names().length(); k++) {
					ContentValues qPropValues = new ContentValues();
					qPropValues.put(DbHelper.QUIZ_QUESTION_PROPS_C_QUESTIONREFID, questionRefId);
					qPropValues.put(DbHelper.QUIZ_QUESTION_PROPS_C_KEY, questionProps.names().getString(k));
					qPropValues.put(DbHelper.QUIZ_QUESTION_PROPS_C_VALUE, questionProps.getString(questionProps.names().getString(k)));
					db.insertOrThrow(DbHelper.QUIZ_QUESTION_PROPS_TABLE, null, qPropValues);
				}
				
				JSONArray responses = (JSONArray) q.get("r");
				for (int j=0; j<responses.length(); j++){
					JSONObject r = (JSONObject) responses.get(j);
					String responseRefId = (String) r.get("refid");
					int rorderno = Integer.parseInt((String) r.get("orderno"));
					String rtext = (String) r.get("text");
					int rscore = Integer.parseInt((String) r.get("score"));
					
					// add to QuizQuestionResponse table
					ContentValues rvalues = new ContentValues();
					rvalues.put(DbHelper.QUIZ_QUESTION_RESPONSE_C_REFID, responseRefId);
					rvalues.put(DbHelper.QUIZ_QUESTION_RESPONSE_C_QUIZREFID, quizRefId);
					rvalues.put(DbHelper.QUIZ_QUESTION_RESPONSE_C_QUESTIONREFID, questionRefId);
					rvalues.put(DbHelper.QUIZ_QUESTION_RESPONSE_C_ORDERNO, rorderno);
					rvalues.put(DbHelper.QUIZ_QUESTION_RESPONSE_C_TEXT, rtext);
					rvalues.put(DbHelper.QUIZ_QUESTION_RESPONSE_C_SCORE, rscore);
					db.insertOrThrow(DbHelper.QUIZ_QUESTION_RESPONSE_TABLE, null, rvalues);
					
					// add to the QuizQuestionResponseProps
					JSONObject responseProps = (JSONObject) r.get("props");
					if(responseProps.names() != null){
						for (int m = 0; m < responseProps.names().length(); m++) {
							ContentValues rPropValues = new ContentValues();
							rPropValues.put(DbHelper.QUIZ_QUESTION_RESPONSE_PROPS_C_RESPONSEREFID, responseRefId);
							rPropValues.put(DbHelper.QUIZ_QUESTION_RESPONSE_PROPS_C_KEY, responseProps.names().getString(m));
							rPropValues.put(DbHelper.QUIZ_QUESTION_RESPONSE_PROPS_C_VALUE, responseProps.getString(responseProps.names().getString(m)));
							db.insertOrThrow(DbHelper.QUIZ_QUESTION_RESPONSE_PROPS_TABLE, null, rPropValues);
						}
					}
					
				}
			}
			return true;
		} catch (JSONException e){
			e.printStackTrace();
			db.close();
			return false;
		}
	}
	
	public long saveQuizAttempt(ContentValues cVals){
		return db.insertOrThrow(DbHelper.QUIZ_ATTEMPT_TABLE, null, cVals);
	}
	
	public void saveQuizAttemptResponse(ContentValues cVals){
		db.insertOrThrow(DbHelper.QUIZ_ATTEMPT_RESPONSE_TABLE, null, cVals);
	}

	public HashMap<String,String> getQuestionProps(String questionRefId){
		String selection = QUIZ_QUESTION_PROPS_C_QUESTIONREFID + "= ?";
		String[] selArgs = new String[] {questionRefId};
		Cursor c = db.query(QUIZ_QUESTION_PROPS_TABLE, null, selection , selArgs, null, null, null);
		HashMap<String,String> props = new HashMap<String,String>();
		c.moveToFirst();
		//Log.d(TAG,"Adding props:"+ questionRefId);
		while(c.isAfterLast() == false){
			//Log.d(TAG,"key:"+c.getString(c.getColumnIndex(DbHelper.QUIZ_QUESTION_PROPS_C_KEY)));
			//Log.d(TAG,"value:"+c.getString(c.getColumnIndex(DbHelper.QUIZ_QUESTION_PROPS_C_VALUE)));
			props.put(c.getString(c.getColumnIndex(DbHelper.QUIZ_QUESTION_PROPS_C_KEY)), c.getString(c.getColumnIndex(DbHelper.QUIZ_QUESTION_PROPS_C_VALUE)));
			c.moveToNext();
		}
		return props;
	}
	
	public HashMap<String,String> getResponseProps(String responseRefId){
		String selection = QUIZ_QUESTION_RESPONSE_PROPS_C_RESPONSEREFID + "= ?";
		String[] selArgs = new String[] {responseRefId};
		Cursor c = db.query(QUIZ_QUESTION_RESPONSE_PROPS_TABLE, null, selection , selArgs, null, null, null);
		HashMap<String,String> props = new HashMap<String,String>();
		c.moveToFirst();
		//Log.d(TAG,"Adding props:"+ questionRefId);
		while(c.isAfterLast() == false){
			//Log.d(TAG,"response prop key:"+c.getString(c.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_PROPS_C_KEY)));
			//Log.d(TAG,"response prop value:"+c.getString(c.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_PROPS_C_VALUE)));
			props.put(c.getString(c.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_PROPS_C_KEY)), c.getString(c.getColumnIndex(DbHelper.QUIZ_QUESTION_RESPONSE_PROPS_C_VALUE)));
			c.moveToNext();
		}
		return props;
	}
	
	public boolean runAutoDownload(int interval){
		boolean resp = true;
		if(interval == 0){
			return resp;
		}
		String criteria = DbHelper.PROPS_C_NAME + "='lastautodownload'";
		Cursor cur = db.query(DbHelper.PROPS_TABLE, null, criteria , null, null, null, null);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dnow = new Date();
		
		cur.moveToFirst();
		while (cur.isAfterLast() == false) {
			try{
				Date lastdl = df.parse(cur.getString(cur.getColumnIndex(PROPS_C_VALUE)));
				Long lastdltime = lastdl.getTime();
				lastdltime +=(interval*1000);
				if(dnow.getTime() < lastdltime){
					resp= false;
				}
				
			} catch (ParseException pe){
				pe.printStackTrace();
			}
			cur.moveToNext();
			
		}
		if (resp){
			//add record to prop
			ContentValues rvalues = new ContentValues();
			rvalues.put(DbHelper.PROPS_C_NAME, "lastautodownload");
			rvalues.put(DbHelper.PROPS_C_VALUE, df.format(dnow));
			db.insertOrThrow(DbHelper.PROPS_TABLE, null, rvalues);
		} 
		return resp;
	}
	
	public String createSubmitResponseObject(Cursor cur){
		JSONObject json = new JSONObject();
		try{
			// general quiz overview
			json.put("username", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_USERNAME)));
			json.put("quizid", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_QUIZREFID)));
			json.put("quizdate", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_QUIZDATE)));
			json.put("userscore", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_USERSCORE)));
			json.put("maxscore", cur.getString(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_MAXSCORE)));
	
			// individual quiz responses
			JSONArray responses = new JSONArray();
			Cursor attCur = this.getAttemptResponses(cur.getInt(cur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_C_ID)));
			attCur.moveToFirst();
			while (attCur.isAfterLast() == false) {
				JSONObject r = new JSONObject();
				r.put("qid",attCur.getString(attCur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_QUESTIONREFID)));
				r.put("score",attCur.getFloat(attCur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_SCORE)));
				r.put("qrtext",attCur.getString(attCur.getColumnIndex(DbHelper.QUIZ_ATTEMPT_RESPONSE_C_TEXT)));
				responses.put(r);
				attCur.moveToNext();
			}
			attCur.close();
			json.put("responses", responses);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
}
