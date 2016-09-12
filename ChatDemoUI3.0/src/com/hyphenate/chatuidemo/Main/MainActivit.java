package com.hyphenate.chatuidemo.Main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivit extends Activity {

    SlideShowView slideshow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        slideshow= (SlideShowView) findViewById(R.id.slideshow);
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        double i=width*0.5;
        int a= (int) i;
        ViewGroup.LayoutParams lp =slideshow.getLayoutParams();
        lp.width=lp.MATCH_PARENT;
        lp.height=a;
        slideshow.setLayoutParams(lp);
        initSysnArticals();
    }

    private void initSysnArticals(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getArticalAds();
                    Message msg=new Message();
                    msg.obj=result;
                    jrywhandler.sendMessage(msg);
                }
                catch (Exception ex){

                }

            }
        }).start();
    }

    Handler jrywhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;

            ArrayList<String> adimg = new ArrayList<String>();
            ArrayList<String> adhint = new ArrayList<String>();
            ArrayList<String> adid = new ArrayList<String>();
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("article_ads");
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    String id = jsonObj.getString("aid");
                    String title = jsonObj.getString("title");
                    String cover = jsonObj.getString("cover");
                    adimg.add(cover);
                    adhint.add(title);
                    adid.add(id);
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            slideshow.init(adhint,adimg,adid);
            super.handleMessage(msg);
        }
    };

    private Bitmap getBitmapFromByte(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }

}
