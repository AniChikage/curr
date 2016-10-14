package com.hyphenate.chatuidemo.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.Adapter.AppointAdapter;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;
import com.hyphenate.chatuidemo.ui.VideoCallActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AniChikage on 2016/8/18.
 */
public class MyAppoint extends Activity {
    private Context hcontext;
    private ListView listview;
    private List<Map<String, Object>> list;
    private AppointAdapter appointAdapter;
    private View header_view;
    private String user_email="";
    private TextView user_nickname;
    private Drawable drawable;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myappoint);
        hcontext=this.getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            user_email = bundle.getString("user_email");
            Log.e("user_email",user_email);
            InitId();
            InitData();
            SysnOrders();
        }
    }

    private void InitId(){
        listview = (ListView)findViewById(R.id.listview);
        header_view = LayoutInflater.from(this).inflate(R.layout.myappoint_header, null);
        user_nickname = (TextView)header_view.findViewById(R.id.myappoint_name);
    }

    private void InitData(){
        user_nickname.setText(user_email);
    }

    private void SysnOrders(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getOrders();
                    Message msg=new Message();
                    msg.obj=result;
                    handler.sendMessage(msg);
                }
                catch (Exception ex){
                    Log.e("get orders","获取预约失败");
                }

            }
        }).start();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("orders");
                Log.e("orders len", String.valueOf(jsonObjs.length()));
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    String sid = jsonObj.getString("sid");
                    String oid = jsonObj.getString("oid");
                    String starttime = jsonObj.getString("starttime");
                    String str_schedule = jsonObj.getString("schedule");
                    JSONObject jsonObjsc = new JSONObject(str_schedule).getJSONObject("consellor");
                    String portrait = jsonObjsc.getString("portrait");
                    String consellor_name = jsonObjsc.getString("realname");
                    drawable = new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait, Base64.DEFAULT)));
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("appoint_img", drawable);
                    map.put("appoint_hint", getResources().getDrawable(R.drawable.nopay));
                    map.put("appoint_oid",oid);
                    map.put("appoint_cname",consellor_name);
                    map.put("appoint_time", starttime);              //姓名
                    listItems.add(map);
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }

            listview.addHeaderView(header_view);
            list = listItems;
            appointAdapter = new AppointAdapter(hcontext, list);
            listview.setAdapter(appointAdapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        ListView lv = (ListView)parent;
                        HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
                        if(person!=null){
                            //Toast.makeText(getApplicationContext(),person.get("appoint_oid").toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MyAppoint.this, AppointDetail.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("appoint_oid",person.get("appoint_oid").toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(getApplicationContext(),"kong", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(MyAppoint.this,ex.toString() , Toast.LENGTH_SHORT).show();
                        Log.e("get error",ex.toString());
                    }
                }
            });
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
