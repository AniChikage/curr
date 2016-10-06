package com.hyphenate.chatuidemo.Consellor;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.Adapter.CsAppointAdapter;
import com.hyphenate.chatuidemo.Adapter.CsabListViewAdapter;
import com.hyphenate.chatuidemo.Appointment.AppointDetail;
import com.hyphenate.chatuidemo.Appointment.CdAppointDetail;
import com.hyphenate.chatuidemo.Appointment.MyAppoint;
import com.hyphenate.chatuidemo.Consultant.Csdetail;
import com.hyphenate.chatuidemo.Help.createSDFile;
import com.hyphenate.chatuidemo.Main.QuestionActivity;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;
import com.hyphenate.chatuidemo.ui.CcSetting;
import com.hyphenate.chatuidemo.ui.Setting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AniChikage on 2016/9/27.
 */
public class ConsellorPage extends Activity {

    private createSDFile mycreateSDFile;
    private ListView listView;
    private String cid;
    private Context hcontext;
    private List<Map<String, Object>> list;
    private CsAppointAdapter csAppointAdapter;
    private View header_view;
    private LinearLayout header_ll;
    private TextView app_username;
    private String consellor_email;
    private ImageView dochuzhen;
    private ImageView mp_setting;
    public static ConsellorPage instance = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consellorpage);
        hcontext = this.getApplicationContext();
        mycreateSDFile = new createSDFile(hcontext);
        consellor_email = mycreateSDFile.readSDFile("cache");
        instance = this;
        try{
            initId();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ConnNet operaton = new ConnNet();
                        String result = operaton.getConsellor(consellor_email);
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
        header_view = LayoutInflater.from(hcontext).inflate(R.layout.mainpage_mine_header, null);

        header_ll = (LinearLayout)header_view.findViewById(R.id.mine_header_ll);
        app_username = (TextView)header_view.findViewById(R.id.app_username);
        app_username.setText(consellor_email);
        dochuzhen = (ImageView)header_view.findViewById(R.id.dochuzhen);
        dochuzhen.setVisibility(View.INVISIBLE);
        mp_setting = (ImageView)header_view.findViewById(R.id.mp_setting);
        mp_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hcontext,Setting.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                hcontext.startActivity(intent);
            }
        });

        DisplayMetrics dm = hcontext.getApplicationContext().getResources().getDisplayMetrics();
        int allheight = dm.heightPixels;
        int upmineh= (int) allheight*795/1905;
        ViewGroup.LayoutParams lpmine = header_ll.getLayoutParams();
        lpmine.width=lpmine.MATCH_PARENT;
        lpmine.height=upmineh;
        header_ll.setLayoutParams(lpmine);
        listView.addHeaderView(header_view);
    }

    //first step: get Consellor id
    Handler hGetConsellor = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            Log.e("get cs email",string);
            try {
                JSONObject jsonObject = new JSONObject(string).getJSONObject("consellor");
                cid = jsonObject.getString("id");
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            ConnNet operaton = new ConnNet();
                            String result = operaton.getOrderByCid(cid);
                            Message msg = new Message();
                            msg.obj = result;
                            hAdapt.sendMessage(msg);
                        } catch (Exception ex) {
                            Toast.makeText(ConsellorPage.this, "咨询师获取失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            } catch (Exception e) {
                System.out.println("获取用户失败");
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };

    Handler hAdapt = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("orders");
                Log.e("orders len",String.valueOf(jsonObjs.length()));
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    String sid = jsonObj.getString("sid");
                    String oid = jsonObj.getString("oid");
                    String paid = jsonObj.getString("paid");
                    String starttime = jsonObj.getString("starttime");
                    String username = jsonObj.getJSONObject("user").getString("nickname");
                    String str_schedule = jsonObj.getString("schedule");
                    JSONObject jsonObjsc = new JSONObject(str_schedule).getJSONObject("consellor");
                    String portrait = jsonObjsc.getString("portrait");
                    String consellor_name = jsonObjsc.getString("realname");
                    Drawable drawable = new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait,Base64.DEFAULT)));
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("appoint_img", drawable);
                    if(paid.equals("1")){
                        map.put("appoint_hint", "已支付");
                    }
                    else{
                        map.put("appoint_hint", "未支付");
                    }
                    map.put("appoint_oid",oid);
                    map.put("appoint_cname",consellor_name);
                    map.put("appoint_time", starttime);              //姓名
                    map.put("appoint_username", username);              //姓名
                    listItems.add(map);
                }
            } catch (Exception e) {
                System.out.println("通过cid获取的预约失败！");
                e.printStackTrace();
            }

            list = listItems;
            //listView.addHeaderView(header_view);
            csAppointAdapter = new CsAppointAdapter(hcontext, list);
            listView.setAdapter(csAppointAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        ListView lv = (ListView)parent;
                        HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
                        if(person!=null){
                            //Toast.makeText(getApplicationContext(), person.get("csabid").toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ConsellorPage.this, CdAppointDetail.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("appoint_oid",person.get("appoint_oid").toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //finish();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"获取咨询师列表失败", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(ConsellorPage.this,ex.toString() ,Toast.LENGTH_SHORT).show();
                        Log.e("get error",ex.toString());
                    }
                }
            });

            super.handleMessage(msg);
        }
    };

    private static Bitmap getBitmapFromByte(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }

    public static void finishThis(){
        ConsellorPage.instance.finish();
    }
}
