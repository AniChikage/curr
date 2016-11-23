package com.hyphenate.chatuidemo.Appointment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;

import org.json.JSONObject;

/**
 * Created by AniChikage on 2016/8/13.
 */
public class AppointConfirm extends Activity {

    private TextView addOrder,tv_realname,tv_starttime,tv_time,tv_danjia,tv_total;
    private ImageView btn_plus,btn_minus;
    private EditText blms;
    private Context context;
    private String user_id, sid, nid, starttime,danjia;
    private ImageView acback,iv_portrait;
    private ProgressDialog pd=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.appointconfirm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏
        context = getApplication();
        pd = new ProgressDialog(AppointConfirm.this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sid = bundle.getString("sid");
            user_id = bundle.getString("user_id");
            Log.e(sid,sid);
            InitId();
            InitData();
            InitClickEvent();
        }
    }

    private void InitId(){
        addOrder = (TextView)findViewById(R.id.addOrder);
        tv_realname = (TextView)findViewById(R.id.tv_realname);
        tv_starttime = (TextView)findViewById(R.id.tv_starttime);
        tv_time = (TextView)findViewById(R.id.tv_time);
        tv_danjia = (TextView)findViewById(R.id.tv_danjia);
        tv_total = (TextView)findViewById(R.id.tv_total);
        btn_plus = (ImageView) findViewById(R.id.btn_plus);
        btn_minus = (ImageView)findViewById(R.id.btn_minus);
        blms = (EditText)findViewById(R.id.binglimiaoshu);
        acback = (ImageView)findViewById(R.id.acback);
        iv_portrait = (ImageView)findViewById(R.id.iv_portrait);
    }

    private void InitData(){
        //need id
        /*
        new Thread(new Runnable() {
            public void run() {
                try {
                    ConnNet operaton = new ConnNet();
                    String result = operaton.getNeeds();
                    Message msg = new Message();
                    msg.obj = result;
                    needhandler.sendMessage(msg);
                } catch (Exception ex) {
                    Toast.makeText(AppointConfirm.this, "get needs fails", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
        */
        new Thread(new Runnable() {
            public void run() {
                try {
                    ConnNet operaton = new ConnNet();
                    String result = operaton.getSchedule(sid);
                    Message msg = new Message();
                    msg.obj = result;
                    hGetSchedule.sendMessage(msg);
                } catch (Exception ex) {
                    Toast.makeText(AppointConfirm.this, "get needs fails", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private void InitClickEvent(){
        acback.setOnClickListener(onClickListener);
        btn_plus.setOnClickListener(onClickListener);
        btn_minus.setOnClickListener(onClickListener);
        addOrder.setOnClickListener(onClickListener);
        tv_time.addTextChangedListener(fieldValidatorTextWatcher);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.acback:
                    finish();
                    break;
                case R.id.btn_plus:
                    if(((Double.valueOf(tv_time.getText().toString())-2)!=0)){
                        tv_time.setText(String.valueOf(Double.valueOf(tv_time.getText().toString())+0.5));
                    }
                    refeshHint();
                    break;
                case R.id.btn_minus:
                    if(Double.valueOf(tv_time.getText().toString())!=1){
                        tv_time.setText(String.valueOf(Double.valueOf(tv_time.getText().toString())-0.5));
                    }
                    refeshHint();
                    break;
                case R.id.addOrder:
                    addOrder();
                default:
                    break;
            }
        }
    };

    public void addOrder(){
        pd.setMessage("提交中......");
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                if(!blms.getText().toString().equals("")){
                    try {
                        ConnNet operaton = new ConnNet();
                        String result = operaton.addNeed(user_id,blms.getText().toString());
                        Message msg = new Message();
                        msg.obj = result;
                        haddneed.sendMessage(msg);
                    } catch (Exception ex) {
                        Toast.makeText(AppointConfirm.this, "添加病例描述失败！", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(AppointConfirm.this,"您需要添加病例描述", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    public void refeshHint(){
        if(((Double.valueOf(tv_time.getText().toString())-2)==0)){
            btn_plus.setImageResource(R.drawable.plusgray);
        }
        else if(Double.valueOf(tv_time.getText().toString())==1){
            btn_minus.setImageResource(R.drawable.minusgray);
        }
        else{
            btn_plus.setImageResource(R.drawable.plusred);
            btn_minus.setImageResource(R.drawable.minusred);
        }
    }

    Handler haddneed = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            Log.e("add need",string);
            try {
                JSONObject jsonObject = new JSONObject(string);
                nid = jsonObject.getString("addNeed");
                Log.e("nid",nid);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            ConnNet operaton = new ConnNet();
                            String result = operaton.getSchedule(sid);
                            Message msg = new Message();
                            msg.obj = result;
                            haddorder.sendMessage(msg);
                        }
                        catch (Exception ex) {
                            Toast.makeText(AppointConfirm.this, "添加病例描述失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
            catch (Exception ex){

            }
            super.handleMessage(msg);
        }
    };

    Handler hGetSchedule = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            Log.e("get sid",string);
            try {
                JSONObject jsonObj = new JSONObject(string).getJSONObject("schedule");
                String portrait = jsonObj.getJSONObject("consellor").getString("portrait");
                String realname = jsonObj.getJSONObject("consellor").getString("realname");
                danjia = jsonObj.getJSONObject("consellor").getJSONObject("rate").getString("price");
                starttime = jsonObj.getString("start");
                Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait, Base64.DEFAULT)));
                iv_portrait.setBackgroundDrawable(drawable);
                tv_realname.setText(realname);
                tv_starttime.setText(starttime);
                tv_danjia.setText(danjia+"元/小时");
                Double fee = Double.valueOf(danjia)* Double.valueOf(tv_time.getText().toString());
                tv_total.setText(fee.toString()+"元");
            }
            catch (Exception ex){
                Log.e("hGetSchedule_error",ex.toString());
            }
            super.handleMessage(msg);
        }
    };

    Handler haddorder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            Log.e("add order",string);
            try {
                JSONObject jsonObjs = new JSONObject(string).getJSONObject("schedule");
                starttime = jsonObjs.getString("start");
                Log.e("uid",user_id);
                Log.e("nid",nid);
                Log.e("sid",sid);
                Log.e("starttime",starttime);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            ConnNet operaton = new ConnNet();
                            String result = operaton.addOrder(user_id,nid,sid,starttime,tv_time.getText().toString());
                            Message msg = new Message();
                            msg.obj = result;
                            haddorderdone.sendMessage(msg);
                        }
                        catch (Exception ex) {
                            Toast.makeText(AppointConfirm.this, "添加病例描述失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
            catch (Exception ex){

            }
            super.handleMessage(msg);
        }
    };

    Handler haddorderdone = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            Log.e("add order done",string);
            try {
                String jsonObjs = new JSONObject(string).getString("addOrder");
                if(jsonObjs!=""){
                    Toast.makeText(AppointConfirm.this,"提交成功！",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                else{
                    Toast.makeText(AppointConfirm.this,"提交失败！",Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception ex){

            }
            super.handleMessage(msg);
        }
    };

    private Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }

    TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {//表示最终内容
            Log.d("afterTextChanged", s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start/*开始的位置*/, int count/*被改变的旧内容数*/, int after/*改变后的内容数量*/) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start/*开始位置*/, int before/*改变前的内容数量*/, int count/*新增数*/) {
            Double fee = Double.valueOf(danjia)* Double.valueOf(tv_time.getText().toString());
            tv_total.setText(fee.toString()+"元");
        }
    };
}
