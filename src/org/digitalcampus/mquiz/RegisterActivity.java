package org.digitalcampus.mquiz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class RegisterActivity extends Activity {

	private static final String TAG = "RegisterActivity";
	SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
	}


}