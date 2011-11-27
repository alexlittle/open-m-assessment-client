package org.digitalcampus.assessment;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class ManageQuizActivity extends TabActivity{

	private static final String TAG = "ManageQuizActivity";
	private static TextView mTVFF;
    private static TextView mTVDF;

    private static final String LOCAL_TAB = "local_tab";
    private static final String REMOTE_TAB = "remote_tab";
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();
        tabHost.setBackgroundColor(Color.BLACK);
        tabHost.getTabWidget().setBackgroundColor(Color.BLACK);

        Intent local = new Intent(this, QuizLocalList.class);
        tabHost.addTab(tabHost.newTabSpec(LOCAL_TAB).setIndicator("Installed quizzes").setContent(local));

        Intent remote = new Intent(this, QuizRemoteList.class);
        tabHost.addTab(tabHost.newTabSpec(REMOTE_TAB).setIndicator("Get new quizzes").setContent(remote));

        // hack to set font size
        LinearLayout ll = (LinearLayout) tabHost.getChildAt(0);
        TabWidget tw = (TabWidget) ll.getChildAt(0);

        RelativeLayout rllf = (RelativeLayout) tw.getChildAt(0);
        mTVFF = (TextView) rllf.getChildAt(1);
        mTVFF.setPadding(0, 0, 0, 6);

        RelativeLayout rlrf = (RelativeLayout) tw.getChildAt(1);
        mTVDF = (TextView) rlrf.getChildAt(1);
        mTVDF.setPadding(0, 0, 0, 6);
    }
}
