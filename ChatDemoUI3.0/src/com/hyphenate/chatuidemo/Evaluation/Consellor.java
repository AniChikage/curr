package com.hyphenate.chatuidemo.Evaluation;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.Base64.BASE64Decoder;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AniChikage on 2016/12/7.
 */
public class Consellor extends Activity {

    //region ID
    private SeekBar eva_user_taidu;
    private SeekBar eva_user_xiaoguo;
    private TextView eva_user_taidu_num;
    private TextView eva_user_xiaoguo_num;
    private TextView username;
    private TextView tvprice;
    private TextView adyuyuehao;
    private TextView adordertime;
    private TextView confirm;
    private Context mContext;
    private ImageView back;
    private ImageView user_img;
    private String appoint_oid;
    private EditText impressb;
    private EditText impresse;
    private EditText motive;
    private EditText cooperation;
    private EditText expectation;
    private EditText profession;
    private EditText neutrality;
    private EditText acceptation;
    private EditText achievement;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.eva_consellor);
        mContext = Consellor.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            appoint_oid = bundle.getString("appoint_oid");
            try{
                initID();
                initOnClick();
                initData();
            }
            catch (Exception ex){
                Log.e("sdfsdf",ex.toString());
            }
        }
    }

    private void initID(){
        back = (ImageView)findViewById(R.id.back);
        user_img = (ImageView)findViewById(R.id.user_img);
        username = (TextView)findViewById(R.id.username);
        tvprice = (TextView)findViewById(R.id.price);
        adyuyuehao = (TextView)findViewById(R.id.adyuyuehao);
        adordertime = (TextView)findViewById(R.id.adordertime);
        impressb = (EditText) findViewById(R.id.impressb);
        impresse = (EditText)findViewById(R.id.impresse);
        motive = (EditText)findViewById(R.id.motive);
        cooperation = (EditText)findViewById(R.id.cooperation);
        expectation = (EditText)findViewById(R.id.expectation);
        profession = (EditText)findViewById(R.id.profession);
        neutrality = (EditText)findViewById(R.id.neutrality);
        acceptation = (EditText)findViewById(R.id.acceptation);
        achievement = (EditText)findViewById(R.id.achievement);
        confirm = (TextView)findViewById(R.id.confirm);
    }

    private void initOnClick(){
        back.setOnClickListener(onClickListener);
        confirm.setOnClickListener(onClickListener);
    }

    private void initData(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getOrder(appoint_oid);
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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    finish();
                    break;
                case R.id.confirm:
                    updatePingjia();
                    break;
                default:
                    break;
            }
        }
    };

    private void updatePingjia(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.csAltOrderPingjia(appoint_oid,
                            impressb.getText().toString(),
                    impresse.getText().toString(),
                    motive.getText().toString(),
                    cooperation.getText().toString(),
                    expectation.getText().toString(),
                    profession.getText().toString(),
                    neutrality.getText().toString(),
                    acceptation.getText().toString(),
                    achievement.getText().toString(),"1");
                    Message msg=new Message();
                    msg.obj=result;
                    alhandler.sendMessage(msg);
                }
                catch (Exception ex){
                    Log.e("get orders","获取预约失败");
                }

            }
        }).start();
    }

    Handler alhandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            Log.e("alter pingjia ",string);
            try {
                String sta = new JSONObject(string).getString("altOrder");
                if(sta.equals("1")){
                    Toast.makeText(Consellor.this,"评价完成！",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(Consellor.this,"评价失败！",Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception ex){

            }
        }
    };

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            try {
                JSONObject jsonObj = new JSONObject(string).getJSONObject("order");
                String str_schedule = jsonObj.getString("schedule");
                String oid = jsonObj.getString("oid");
                String paid = jsonObj.getString("paid");
                String delivery="0";
                try{
                    delivery = jsonObj.getString("delivery");
                }
                catch (Exception ex){
                    Log.e("",ex.toString());
                }
                String user_name = jsonObj.getJSONObject("user").getString("nickname");
                String user_email = jsonObj.getJSONObject("user").getString("email");
                String requirement = jsonObj.getJSONObject("need").getString("requirement");
                String createtime = jsonObj.getString("createtime");
                String totalprice = jsonObj.getString("total");
                String period = jsonObj.getString("period");
                JSONObject jsonObjc = new JSONObject(str_schedule).getJSONObject("consellor");
                String consellor_name = jsonObjc.getString("realname");
                String portrait = jsonObjc.getString("portrait");
                String description = getStringFromBASE64(jsonObjc.getString("description"));
                String perprice = jsonObjc.getJSONObject("rate").getString("price");
                Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait, Base64.DEFAULT)));
                user_img.setBackgroundDrawable(drawable);
                String price = jsonObjc.getString("rid");
                String cid = jsonObjc.getString("id");
                String peroid = jsonObj.getString("period");
                String starttime = jsonObj.getString("starttime");
                adyuyuehao.setText("预约号："+oid);
                adordertime.setText("订单生成时间："+createtime);
                tvprice.setText(perprice+"元/单位咨询时间");
                username.setText(user_name);

            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };

    // 将 BASE64 编码的字符串 s 进行解码
    public static String getStringFromBASE64(String base64string) {
        if (base64string == null) return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(base64string);
            return new String(b,"gbk");
        } catch (Exception e) {
            return null;
        }
    }

    private Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }
}
