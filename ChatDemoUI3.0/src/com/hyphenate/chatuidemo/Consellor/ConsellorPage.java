package com.hyphenate.chatuidemo.Consellor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AniChikage on 2016/9/27.
 */
public class ConsellorPage extends Activity {

    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consellorpage);

        try{
            initId();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ConnNet operaton = new ConnNet();
                        String result = operaton.getConsellor(EMClient.getInstance().getCurrentUser().toString());
                        Message msg = new Message();
                        msg.obj = result;
                        hGetConsellor.sendMessage(msg);
                    } catch (Exception ex) {
                        Toast.makeText(ConsellorPage.this, "咨询师获取失败", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void initId(){
        listView = (ListView)findViewById(R.id.listview);
    }

    //first step: get user
    Handler hGetConsellor = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            Log.e("get cs email",string);
            try {

            } catch (Exception e) {
                System.out.println("获取用户失败");
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };
}
