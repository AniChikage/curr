package com.hyphenate.chatuidemo.Evaluation;

import android.app.Activity;
import android.content.Context;
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
public class User extends Activity {

    //region ID
    private SeekBar eva_user_taidu;
    private SeekBar eva_user_xiaoguo;
    private TextView eva_user_taidu_num;
    private TextView eva_user_xiaoguo_num;
    private TextView eva_user_ordernum;
    private TextView eva_user_ordertime;
    private TextView eva_user_price;
    private TextView eva_user_cname;
    private TextView eva_user_queren;
    private ImageView img;
    private ImageView back;
    private Context mContext;
    private String appoint_oid;
    private int taidu=0;
    private int xiaoguo=0;
    private EditText eva_user_advice;
    //endregion

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.eva_user);
        mContext = User.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            appoint_oid = bundle.getString("appoint_oid");
            try{
                initID();
                initData();
            }
            catch (Exception ex){
                Log.e("sdfsdf",ex.toString());
            }
        }
    }

    private void initID(){
        eva_user_taidu = (SeekBar)findViewById(R.id.eva_user_taidu);
        eva_user_xiaoguo = (SeekBar)findViewById(R.id.eva_user_xiaoguo);
        eva_user_taidu_num = (TextView)findViewById(R.id.eva_user_taidu_num);
        eva_user_xiaoguo_num = (TextView)findViewById(R.id.eva_user_xiaoguo_num);
        eva_user_ordernum = (TextView)findViewById(R.id.eva_user_ordernum);
        eva_user_ordertime = (TextView)findViewById(R.id.eva_user_ordertime);
        eva_user_price = (TextView)findViewById(R.id.eva_user_price);
        eva_user_cname = (TextView)findViewById(R.id.eva_user_cname);
        eva_user_queren = (TextView)findViewById(R.id.eva_user_queren);
        img = (ImageView)findViewById(R.id.img);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        eva_user_advice = (EditText) findViewById(R.id.eva_user_advice);
        eva_user_queren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        try{
                            ConnNet operaton=new ConnNet();
                            String result=operaton.uAltOrder(appoint_oid,String.valueOf(taidu),String.valueOf(xiaoguo),eva_user_advice.getText().toString(),"1");
                            Message msg=new Message();
                            msg.obj=result;
                            ulhandler.sendMessage(msg);
                        }
                        catch (Exception ex){
                            Log.e("get orders","获取预约失败");
                        }

                    }
                }).start();
            }
        });
        eva_user_taidu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                eva_user_taidu_num.setText(progress + "/9 ");
                taidu = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(mContext, "触碰SeekBar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               // Toast.makeText(mContext, "放开SeekBar", Toast.LENGTH_SHORT).show();
            }
        });

        eva_user_xiaoguo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                eva_user_xiaoguo_num.setText(progress + "/9 ");
                xiaoguo = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(mContext, "触碰SeekBar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               // Toast.makeText(mContext, "放开SeekBar", Toast.LENGTH_SHORT).show();
            }
        });
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

    Handler ulhandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            Log.e("alter pingjia ",string);
            try {
                String sta = new JSONObject(string).getString("altOrder");
                if(sta.equals("1")){
                    Toast.makeText(User.this,"评价完成！",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(User.this,"评价失败！",Toast.LENGTH_SHORT).show();
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
                img.setBackgroundDrawable(drawable);
                String price = jsonObjc.getString("rid");
                String cid = jsonObjc.getString("id");
                String peroid = jsonObj.getString("period");
                String starttime = jsonObj.getString("starttime");
                eva_user_ordernum.setText(""+oid);
                eva_user_ordertime.setText(""+createtime);
                eva_user_price.setText(perprice+"元/单位咨询时间");
                eva_user_cname.setText(consellor_name);

            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
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
