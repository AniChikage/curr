package com.hyphenate.chatuidemo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chatuidemo.Consellor.ConsellorPage;
import com.hyphenate.chatuidemo.Help.createSDFile;
import com.hyphenate.chatuidemo.R;


/**
 * Created by AniChikage on 2016/7/4.
 */
public class Welcome extends Activity{

    private static long showTime = 3000;
    private createSDFile mycreateSDFile;
    String TAG="welcome";
    Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.welcome);
        Log.d(TAG,"welcome");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏


        handler.postDelayed(new Runnable(){
            public void run(){
                /*
                Intent intent = new Intent(Welcome.this, LoginActivity.class);
                startActivity(intent);
                finish();
                */
                init();
            }
        }, showTime);

    }
    private void init(){
        mycreateSDFile = new createSDFile(getBaseContext());
        try{
            //mycreateSDFile.writeSDFile("test2","cache");
            //mycreateSDFile.writeSDFile("user","cachetype");
            Log.e("fdg",mycreateSDFile.readSDFile("cache"));
            Log.e("fdg",mycreateSDFile.readSDFile("cachetype"));

            if(mycreateSDFile.readSDFile("cachetype").trim().equals("consellor")){
                if(mycreateSDFile.readSDFile("cache").trim().equals("")){
                    startActivity(new Intent(Welcome.this, LoginActivity.class));
                    finish();
                }
                else{
                    //enter main screen
                    startActivity(new Intent(Welcome.this, ConsellorPage.class));
                    finish();
                }
            }
            else if(mycreateSDFile.readSDFile("cachetype").trim().equals("user")){
                if(mycreateSDFile.readSDFile("cache").trim().equals("")){
                    startActivity(new Intent(Welcome.this, LoginActivity.class));
                    finish();
                }
                else if(!mycreateSDFile.readSDFile("cache").trim().equals("")){
                    //enter main screen
                    startActivity(new Intent(Welcome.this, MainActivity.class));
                    finish();
                }
            }
            else{
                try {
                    Thread.sleep(showTime);
                } catch (InterruptedException e) {
                }
                startActivity(new Intent(Welcome.this, LoginActivity.class));
                finish();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
    /*
    private void init(){
        mycreateSDFile = new createSDFile(getBaseContext());
        try{
            //mycreateSDFile.writeSDFile("test2","cache");
            //mycreateSDFile.writeSDFile("user","cachetype");
            Log.e("fdg",mycreateSDFile.readSDFile("cache"));
            Log.e("fdg",mycreateSDFile.readSDFile("cachetype"));

            if(mycreateSDFile.readSDFile("cachetype").trim().equals("consellor")){
                if(mycreateSDFile.readSDFile("cache").trim().equals("")){
                    try {
                        Thread.sleep(showTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(Welcome.this, LoginActivity.class));
                    finish();
                }
                else{
                    long start = System.currentTimeMillis();
                    long costTime = System.currentTimeMillis() - start;
                    //wait
                    if (showTime - costTime > 0) {
                        try {
                            Thread.sleep(showTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //enter main screen
                    startActivity(new Intent(Welcome.this, ConsellorPage.class));
                    finish();
                }
            }
            else if(mycreateSDFile.readSDFile("cachetype").trim().equals("user")){
                if(mycreateSDFile.readSDFile("cache").trim().equals("")){
                    try {
                        Thread.sleep(showTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(Welcome.this, LoginActivity.class));
                    finish();
                }
                else if(!mycreateSDFile.readSDFile("cache").trim().equals("")){
                    long start = System.currentTimeMillis();
                    long costTime = System.currentTimeMillis() - start;
                    //wait
                    if (showTime - costTime > 0) {
                        try {
                            Thread.sleep(showTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //enter main screen
                    startActivity(new Intent(Welcome.this, MainActivity.class));
                    finish();
                }
            }
            else{
                try {
                    Thread.sleep(showTime);
                } catch (InterruptedException e) {
                }
                startActivity(new Intent(Welcome.this, LoginActivity.class));
                finish();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
    */
}
