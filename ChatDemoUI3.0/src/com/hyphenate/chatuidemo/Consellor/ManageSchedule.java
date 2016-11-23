package com.hyphenate.chatuidemo.Consellor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.hyphenate.chatuidemo.R;

/**
 * Created by AniChikage on 2016/11/9.
 */
public class ManageSchedule extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.manageschedule);
    }
}
