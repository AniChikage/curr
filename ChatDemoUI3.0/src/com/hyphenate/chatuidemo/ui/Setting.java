package com.hyphenate.chatuidemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.Help.createSDFile;
import com.hyphenate.chatuidemo.R;

/**
 * Created by AniChikage on 2016/10/4.
 */
public class Setting extends Activity {

    private Button btn_logout;
    private createSDFile mycreateSDFile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        initId();
        initClick();
    }

    private void initId(){
        btn_logout = (Button)findViewById(R.id.btn_logout);
        mycreateSDFile = new createSDFile(getBaseContext());
    }

    private void initClick(){
        btn_logout.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logout();
        }
    };

    private void logout(){
        mycreateSDFile.deleteSDFile("cache");
        mycreateSDFile.deleteSDFile("cachetype");
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
                        MainActivity.finishThis();
                        startActivity(new Intent(Setting.this, LoginActivity.class));

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
