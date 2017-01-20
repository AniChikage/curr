package com.hyphenate.chatuidemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.Consellor.ConsellorPage;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.Help.createSDFile;
import com.hyphenate.chatuidemo.R;

/**
 * Created by AniChikage on 2016/10/4.
 */
public class Setting extends Activity {

    private Button btn_logout;
    private createSDFile mycreateSDFile;
    private String type;
    private TextView tv_clear_cache;
    private TextView tv_alter_info;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        initId();
        initClick();
    }

    private void initId(){
        btn_logout = (Button)findViewById(R.id.btn_logout);
        tv_clear_cache = (TextView)findViewById(R.id.clear_cache);
        tv_alter_info = (TextView)findViewById(R.id.alter_info);
        mycreateSDFile = new createSDFile(getBaseContext());
        type = mycreateSDFile.readSDFile("cachetype");
    }

    private void initClick(){
        btn_logout.setOnClickListener(onClickListener);
        tv_clear_cache.setOnClickListener(onClickListener);
        tv_alter_info.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.clear_cache:
                    clearCache();
                    break;
                case R.id.btn_logout:
                    logout();
                    break;
                case R.id.alter_info:
                    startAlter();
                    break;
            }
        }
    };

    private void clearCache(){
        mycreateSDFile.deleteSDFile("cache");
        mycreateSDFile.deleteSDFile("cachetype");
        Toast.makeText(this,"清除缓存成功！",Toast.LENGTH_SHORT).show();
    }

    private void startAlter(){
        Intent intent = new Intent(Setting.this,AlterInfo.class);
        startActivity(intent);
    }

    private void logout(){

        //EMClient.getInstance().logout(true);
        //startActivity(new Intent(Setting.this, LoginActivity.class));
        //finish();
        DemoHelper.getInstance().logout(false,new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // show login screen
                        finish();
                        if(type.equals("user"))
                        MainActivity.finishThis();
                        else{
                            ConsellorPage.finishThis();
                        }
                        startActivity(new Intent(Setting.this, LoginActivity.class));
                        mycreateSDFile.deleteSDFile("cache");
                        mycreateSDFile.deleteSDFile("cachetype");
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Setting.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("按下了back键   onBackPressed()");
        finish();
        MainActivity.finishThis();
        startActivity(new Intent(Setting.this, LoginActivity.class));
    }
*/

}
