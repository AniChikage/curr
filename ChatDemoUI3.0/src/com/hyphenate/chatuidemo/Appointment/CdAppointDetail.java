package com.hyphenate.chatuidemo.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.Base64.BASE64Decoder;
import com.hyphenate.chatuidemo.Evaluation.Consellor;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;
import com.hyphenate.chatuidemo.ui.VideoCallActivity;
import com.hyphenate.exceptions.EMServiceNotReadyException;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by AniChikage on 2016/8/18.
 */
public class CdAppointDetail extends Activity {
    private Context hcontext;
    private Button btn_pay,adcancelorer,btn_reject;
    private TextView tv_appoint_detail,adyuyuehao,adordertime,adtime,adperprice,adtotalprice,adcall,adcsname,adpaid;
    private ImageView adcsimg,adback;
    private String appoint_oid, consellor_id="";
    private String user_email;
    private String isAccept="";
    private LinearLayout ll_shenhe;
    private LinearLayout ll_yiwancheng;
    private LinearLayout ll_pingjia;
    private TextView tv_pingjia;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.csappointdetail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏
        hcontext = this.getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            appoint_oid = bundle.getString("appoint_oid");
            Log.e("appoint_oid",appoint_oid);
            InitId();
            InitClickEvent();
            SysnAppoint();
        }
    }

    private void InitId(){
        adcall = (TextView)findViewById(R.id.adcall);
        adyuyuehao = (TextView)findViewById(R.id.adyuyuehao);
        adordertime = (TextView)findViewById(R.id.adordertime);
        adtime = (TextView)findViewById(R.id.adtime);
        adperprice = (TextView)findViewById(R.id.adperprice);
        adtotalprice = (TextView)findViewById(R.id.adtotalprice);
        adcsname = (TextView)findViewById(R.id.adcsname);
        adpaid = (TextView)findViewById(R.id.adpaid);
        //btn_pay = (Button)findViewById(R.id.btn_pay);
        adcancelorer = (Button)findViewById(R.id.adcancelorder);
        btn_reject = (Button)findViewById(R.id.btn_reject);
        tv_appoint_detail = (TextView)findViewById(R.id.tv_appoint_detail);
        adcsimg = (ImageView)findViewById(R.id.adcsimg);
        adback = (ImageView)findViewById(R.id.adback);
        ll_shenhe = (LinearLayout)findViewById(R.id.ll_shenhe);
        ll_yiwancheng = (LinearLayout)findViewById(R.id.ll_yiwancheng);
        ll_pingjia = (LinearLayout)findViewById(R.id.ll_pingjia);
        tv_pingjia = (TextView)findViewById(R.id.tv_pingjia);
    }

    private void InitClickEvent(){
        adcall.setOnClickListener(onClickListener);
        //btn_pay.setOnClickListener(onClickListener);
        adback.setOnClickListener(onClickListener);
        adcancelorer.setOnClickListener(onClickListener);
        btn_reject.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.adcall:
                    if(!consellor_id.equals("")){
                    try{
                        video(consellor_id);
                    }
                    catch (Exception ex) {
                        Log.e("ex",ex.toString());
                    }
                    }
                    break;
                /*
                case R.id.btn_pay:
                    Toast.makeText(CdAppointDetail.this,"开发中……", Toast.LENGTH_SHORT).show();
                    break;
                    */
                case R.id.adback:
                    finish();
                    break;
                case R.id.adcancelorder:
                    if(isAccept.equals("notSet")){
                        acceptOrder("0");
                    }
                    break;
                case R.id.btn_reject:
                    if(isAccept.equals("notSet")){
                        acceptOrder("1");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //beginregion
    private void acceptOrder(String myrefuse){
        final String refuse = myrefuse;
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.altOrder(appoint_oid,refuse);
                    Message msg=new Message();
                    msg.obj=result;
                    accept_handler.sendMessage(msg);
                }
                catch (Exception ex){
                    Log.e("get orders","通过失败");
                }

            }
        }).start();
    }

    Handler accept_handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            Log.e("alt status",string);
            try {
                String del = new JSONObject(string).getString("altOrder");
                if(del.equals("1")){
                    Toast.makeText(CdAppointDetail.this,"审核通过！", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(CdAppointDetail.this,"审核no！", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };
    //endregion


    private void cancelOrder(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.delOrder(appoint_oid);
                    Message msg=new Message();
                    msg.obj=result;
                    cancel_handler.sendMessage(msg);
                }
                catch (Exception ex){
                    Log.e("get orders","删除失败");
                }

            }
        }).start();
    }

    Handler cancel_handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            Log.e("cancel_status",string);
            try {
                String del = new JSONObject(string).getString("del");
                if(del.equals("1")){
                    Toast.makeText(CdAppointDetail.this,"删除预约成功！", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(CdAppointDetail.this,"删除预约失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };

    private void SysnAppoint(){
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
                String evac = "0";
                try{
                    evac = jsonObj.getString("evac");
                }
                catch (Exception ex){
                    Log.e("",ex.toString());
                }
                String user_name = jsonObj.getJSONObject("user").getString("nickname");
                user_email = jsonObj.getJSONObject("user").getString("email");
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
                adcsimg.setBackgroundDrawable(drawable);
                String price = jsonObjc.getString("rid");
                String cid = jsonObjc.getString("id");
                String peroid = jsonObj.getString("period");
                String starttime = jsonObj.getString("starttime");
                try{
                    isAccept = jsonObj.getString("refuse");
                }
                catch (Exception ex){
                    isAccept = "notSet";
                }

                tv_appoint_detail.setText(requirement);
                consellor_id = user_email;
                adyuyuehao.setText("预约号："+oid);
                adordertime.setText("订单生成时间："+createtime);
                adperprice.setText(perprice+"元/单位咨询时间");
                adtotalprice.setText(totalprice+"元");
                adcsname.setText(user_name);
                adtime.setText(period+"单位咨询时间");
                if(isAccept.equals("1")){
                    adpaid.setText("已拒绝");
                    adcancelorer.setVisibility(View.INVISIBLE);
                    btn_reject.setVisibility(View.INVISIBLE);
                    ll_shenhe.setVisibility(View.INVISIBLE);
                    ll_yiwancheng.setVisibility(View.INVISIBLE);
                    ll_pingjia.setVisibility(View.INVISIBLE);
                }
                else if(isAccept.equals("0")){ //已审核
                    if(paid.equals("0")){
                        adpaid.setText("待付款");
                    }
                    else{
                        adpaid.setText("已支付");
                        if(delivery.equals("0")){
                            ll_yiwancheng.setVisibility(View.VISIBLE);
                            ll_shenhe.setVisibility(View.INVISIBLE);
                            ll_pingjia.setVisibility(View.INVISIBLE);
                            ll_yiwancheng.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            try{
                                                ConnNet operaton=new ConnNet();
                                                String result=operaton.csAltOrder(appoint_oid,"1");
                                                Message msg=new Message();
                                                msg.obj=result;
                                                cs_alter_delivery.sendMessage(msg);
                                            }
                                            catch (Exception ex){
                                                Log.e("get orders","失败");
                                            }

                                        }
                                    }).start();
                                }
                            });
                        }
                        else if(delivery.equals("1")){
                            if(evac.equals("0")){
                                ll_yiwancheng.setVisibility(View.INVISIBLE);
                                ll_shenhe.setVisibility(View.INVISIBLE);
                                ll_pingjia.setVisibility(View.VISIBLE);
                                ll_pingjia.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //go to pingjia
                                        Intent intent = new Intent(CdAppointDetail.this, Consellor.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("appoint_oid",appoint_oid);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });
                            }
                            else{
                                ll_yiwancheng.setVisibility(View.INVISIBLE);
                                ll_shenhe.setVisibility(View.INVISIBLE);
                                ll_pingjia.setVisibility(View.VISIBLE);
                                ll_pingjia.setBackgroundColor(Color.GRAY);
                                tv_pingjia.setBackgroundColor(Color.GRAY);
                                tv_pingjia.setTextColor(Color.BLACK);
                                ll_pingjia.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(CdAppointDetail.this,"您已经完成评价",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                    adcancelorer.setVisibility(View.INVISIBLE);
                    btn_reject.setVisibility(View.INVISIBLE);
                    ll_shenhe.setVisibility(View.INVISIBLE);
                }
                else{
                    //还未审核
                    if(paid.equals("0")){
                        adpaid.setText("待付款");
                    }
                    else{
                        adpaid.setText("已支付");
                    }
                    adcancelorer.setVisibility(View.VISIBLE);
                    btn_reject.setVisibility(View.VISIBLE);
                    ll_shenhe.setVisibility(View.VISIBLE);
                    ll_yiwancheng.setVisibility(View.INVISIBLE);
                    ll_pingjia.setVisibility(View.INVISIBLE);
                }
                //if()
                Log.e("consellor id",consellor_id);
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };

    Handler cs_alter_delivery=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            Log.e("cancel_status",string);
            try {
                String del = new JSONObject(string).getString("altOrder");
                if(del.equals("1")){
                    Toast.makeText(CdAppointDetail.this,"成功！", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(CdAppointDetail.this,"失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };


    private void video(String consellornoid) {
        /**
         * 拨打视频通话
         * @param to
         * @throws EMServiceNotReadyException

        try {
            //EMClient.getInstance().callManager().makeVideoCall(consellornoid);
            Intent intent = new Intent(CdAppointDetail.this, VideoCallActivity.class);
            intent.putExtra("username", consellornoid);
            //intent.putExtra("isComingCall", false);
            startActivity(intent);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", consellornoid)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            //inputMenu.hideExtendMenuContainer();
        }
        /*
        if (!EMChatManager.getInstance().isConnected()) {
            Toast.makeText(AppointDetail.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
        }
        else {
            String toUser=consellornoid;
            if (TextUtils.isEmpty(toUser)){
                Toast.makeText(AppointDetail.this, "请填写接受方账号", Toast.LENGTH_SHORT).show();
                return ;
            }
            Intent intent = new Intent(AppointDetail.this, VideoCallActivity.class);
            intent.putExtra("username", toUser);
            intent.putExtra("isComingCall", false);
            startActivity(intent);
        }
        */
    }

    private Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }

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
}
