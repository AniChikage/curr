package com.hyphenate.chatuidemo.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.Consellor.ConsellorPage;
import com.hyphenate.chatuidemo.Help.createSDFile;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;

import org.json.JSONObject;

/**
 * Created by AniChikage on 2016/12/5.
 */
public class AlterInfo extends Activity {

    //region 定义ID
    private Context context;
    private String csid="";
    private String access_type="";
    private createSDFile mycreateSDFile;
    private ProgressDialog pd=null;
    private ProgressDialog pd1=null;
    private TextView ai_nickname;
    private TextView ai_password;
    private TextView ai_phone;
    private TextView ai_address;
    private TextView ai_address_tv;
    private TextView ai_region;
    private TextView ai_region_tv;
    private TextView ai_hometown;
    private TextView ai_hometown_tv;
    private TextView ai_hunyin;
    private TextView ai_hunyin_tv;
    private TextView ai_realname;
    private TextView ai_gender;
    private TextView ai_birthday;
    private TextView ai_telephone;
    private TextView ai_queren;
    private TextView ai_phone_tv;
    private ImageView ai_back;
    private LinearLayout ll1,ll2,ll3,ll4,ll5;
    //endregion

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.alterinfo);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        context = getApplication();
        pd = new ProgressDialog(AlterInfo.this);
        pd1 = new ProgressDialog(AlterInfo.this);
        mycreateSDFile = new createSDFile(getBaseContext());

        try{
            initID();
            initData();
            initOnClick();
        }
        catch(Exception ex){
            Log.e("AlterInfo",ex.toString());
        }
    }

    //region 初始化ID
    private void initID(){
        ai_nickname = (TextView)findViewById(R.id.ai_nickname);
        ai_password = (TextView)findViewById(R.id.ai_password);
        ai_phone = (TextView)findViewById(R.id.ai_phone);
        ai_address = (TextView)findViewById(R.id.ai_address);
        ai_region = (TextView)findViewById(R.id.ai_region);
        ai_hometown = (TextView)findViewById(R.id.ai_hometown);
        ai_hometown_tv = (TextView)findViewById(R.id.ai_hometown_tv);
        ai_hunyin = (TextView)findViewById(R.id.ai_hunyin);
        ai_realname = (TextView)findViewById(R.id.ai_realname);
        ai_gender = (TextView)findViewById(R.id.ai_gender);
        ai_birthday = (TextView)findViewById(R.id.ai_birthday);
        ai_telephone = (TextView)findViewById(R.id.ai_telephone);
        ai_queren = (TextView)findViewById(R.id.ai_queren);
        ai_phone_tv = (TextView)findViewById(R.id.ai_phone_tv);
        ai_address_tv = (TextView)findViewById(R.id.ai_address_tv);
        ai_hunyin_tv = (TextView)findViewById(R.id.ai_hunyin_tv);
        ai_region_tv = (TextView)findViewById(R.id.ai_region_tv);
        ai_address_tv = (TextView)findViewById(R.id.ai_address_tv);
        ai_back = (ImageView)findViewById(R.id.ai_back);
        ll1 = (LinearLayout)findViewById(R.id.ll1);
        ll2 = (LinearLayout)findViewById(R.id.ll2);
        ll3 = (LinearLayout)findViewById(R.id.ll3);
        ll4 = (LinearLayout)findViewById(R.id.ll4);
        ll5 = (LinearLayout)findViewById(R.id.ll5);
    }
    //endregion

    //region 初始化点击事件
    private void initOnClick(){
        ai_queren.setOnClickListener(onClickListener);
        ai_back.setOnClickListener(onClickListener);
    }
    //endregion

    //region 点击事件
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ai_queren:
                    if(!ai_password.equals("")&&ai_password.getText().toString().length()>=6)
                        queren();
                    else{
                        Toast.makeText(AlterInfo.this,"密码不能低于6位",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.ai_back:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    //endregion

    //region 确认按钮，更新个人信息
    private void queren(){

        new Thread(new Runnable() {
            public void run() {
                try{
                    //pd1.setMessage("更新中");
                    //pd1.show();
                    ConnNet operaton=new ConnNet();
                    String resul="";
                    if(mycreateSDFile.readSDFile("cachetype").equals("user")){
                        resul=operaton.AlterUserInfo(mycreateSDFile.readSDFile("cache"),ai_nickname.getText().toString(),ai_password.getText().toString(),
                                ai_phone.getText().toString(),ai_address.getText().toString(),ai_region.getText().toString(),
                                ai_hometown.getText().toString(),ai_hunyin.getText().toString(),ai_realname.getText().toString(),
                                ai_gender.getText().toString(),ai_birthday.getText().toString(),ai_telephone.getText().toString());
                    }
                    else{
                        resul=operaton.AlterConsellorInfo(csid,ai_nickname.getText().toString(),ai_password.getText().toString(),
                                ai_address.getText().toString(),ai_region.getText().toString(),
                                ai_hometown.getText().toString(),ai_hunyin.getText().toString(),ai_realname.getText().toString(),
                                ai_gender.getText().toString(),ai_birthday.getText().toString(),ai_telephone.getText().toString());
                    }
                    Message msg=new Message();
                    msg.obj=resul;
                    halterinfo.sendMessage(msg);
                }
                catch (Exception ex){
                    Toast.makeText(AlterInfo.this,"更改信息失败",Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }
    //endregion

    private void initData(){

        //region 咨询师无紧急联系人，设为隐藏
        if(mycreateSDFile.readSDFile("cachetype").equals("consellor")) {
            //Log.e("getuser", string);
            ai_phone.setVisibility(View.INVISIBLE);
            ai_phone_tv.setVisibility(View.INVISIBLE);
            ai_address.setVisibility(View.INVISIBLE);
            ai_address_tv.setVisibility(View.INVISIBLE);
            ai_hunyin.setVisibility(View.INVISIBLE);
            ai_hunyin_tv.setVisibility(View.INVISIBLE);
            ai_region.setVisibility(View.INVISIBLE);
            ai_region_tv.setVisibility(View.INVISIBLE);
            ai_hometown.setVisibility(View.INVISIBLE);
            ai_hometown_tv.setVisibility(View.INVISIBLE);
            ll1.setVisibility(View.INVISIBLE);
            ll2.setVisibility(View.INVISIBLE);
            ll3.setVisibility(View.INVISIBLE);
            ll4.setVisibility(View.INVISIBLE);
            ll5.setVisibility(View.INVISIBLE);
        }
        else{
            ai_phone.setVisibility(View.INVISIBLE);
            ai_phone_tv.setVisibility(View.INVISIBLE);
        }
        //endregion

        //region 初始化用户（user、consellor）信息
        new Thread(new Runnable() {
            public void run() {
                try{
                    //pd.setMessage("更新中");
                    //pd.show();
                    ConnNet operaton=new ConnNet();
                    String resul="";
                    if(mycreateSDFile.readSDFile("cachetype").equals("user")){
                        resul=operaton.getUser(mycreateSDFile.readSDFile("cache"));
                    }
                    else{
                        resul=operaton.getConsellor(mycreateSDFile.readSDFile("cache"));
                    }
                    Message msg=new Message();
                    msg.obj=resul;
                    hinitdata.sendMessage(msg);
                }
                catch (Exception ex){
                    Toast.makeText(AlterInfo.this,"获取用户信息失败",Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        //endregion
    }

    Handler halterinfo=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            String string=(String) msg.obj;
            Log.e("alteruserinfo" ,string);
            try{
                String alterFlag;
                if(mycreateSDFile.readSDFile("cachetype").equals("user")){
                    alterFlag = new JSONObject(string).getString("altUser");
                    if(alterFlag.equals("1")){
                        Toast.makeText(AlterInfo.this,"更新成功！",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(AlterInfo.this,"更新失败！",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    alterFlag = new JSONObject(string).getString("altConsellor");
                    if(alterFlag.equals("1")){
                        Toast.makeText(AlterInfo.this,"更新成功！",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(AlterInfo.this,"更新失败！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (Exception ex){
                Log.e("alterinfoerror",ex.toString());
            }
            //pd1.dismiss();
            super.handleMessage(msg);
        }
    };

    Handler hinitdata=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            String string=(String) msg.obj;

            if(mycreateSDFile.readSDFile("cachetype").equals("user")){
                Log.e("getuser" ,string);
                try{
                    ai_nickname.setText(new JSONObject(string).getJSONObject("user").getString("nickname"));
                }
                catch (Exception ex){
                    ai_nickname.setText("");
                }
                try{
                    ai_phone.setText(new JSONObject(string).getJSONObject("user").getString("emergency"));
                }
                catch (Exception ex){
                    ai_phone.setText("");
                }
                try{
                    ai_address.setText(new JSONObject(string).getJSONObject("user").getString("address"));
                }
                catch (Exception ex){
                    ai_address.setText("");
                }
                try{
                    ai_region.setText(new JSONObject(string).getJSONObject("user").getString("religion"));
                }
                catch (Exception ex){
                    ai_region.setText("");
                }
                try{
                    ai_hometown.setText(new JSONObject(string).getJSONObject("user").getString("homeland"));
                }
                catch (Exception ex){
                    ai_hometown.setText("");
                }
                try{
                    ai_hunyin.setText(new JSONObject(string).getJSONObject("user").getString("marriage"));
                }
                catch (Exception ex){
                    ai_hunyin.setText("");
                }
                try{
                    ai_realname.setText(new JSONObject(string).getJSONObject("user").getString("realname"));
                }
                catch (Exception ex){
                    ai_realname.setText("");
                }
                try{
                    ai_gender.setText(new JSONObject(string).getJSONObject("user").getString("gender"));
                }
                catch (Exception ex){
                    ai_gender.setText("");
                }
                try{
                    ai_birthday.setText(new JSONObject(string).getJSONObject("user").getString("birth"));
                }
                catch (Exception ex){
                    ai_birthday.setText("");
                }
                try{
                    ai_telephone.setText(new JSONObject(string).getJSONObject("user").getString("tel"));
                }
                catch (Exception ex){
                    ai_telephone.setText("");
                }
            }
            else if(mycreateSDFile.readSDFile("cachetype").equals("consellor")){
                if(mycreateSDFile.readSDFile("cachetype").equals("consellor")){
                    Log.e("getconsellor" ,string);
                    try{
                        ai_nickname.setText(new JSONObject(string).getJSONObject("consellor").getString("username"));
                    }
                    catch (Exception ex){
                        ai_nickname.setText("");
                    }
                    try{
                        ai_phone.setText(new JSONObject(string).getJSONObject("consellor").getString("emergency"));
                    }
                    catch (Exception ex){
                        ai_phone.setText("");
                    }
                    try{
                        ai_address.setText(new JSONObject(string).getJSONObject("consellor").getString("address"));
                    }
                    catch (Exception ex){
                        ai_address.setText("");
                    }
                    try{
                        ai_region.setText(new JSONObject(string).getJSONObject("consellor").getString("religion"));
                    }
                    catch (Exception ex){
                        ai_region.setText("");
                    }
                    try{
                        ai_hometown.setText(new JSONObject(string).getJSONObject("consellor").getString("homeland"));
                    }
                    catch (Exception ex){
                        ai_hometown.setText("");
                    }
                    try{
                        csid = new JSONObject(string).getJSONObject("consellor").getString("id");
                        Log.e("csid",csid);
                    }
                    catch (Exception ex){
                        ai_hometown.setText("");
                    }
                    try{
                        ai_hunyin.setText(new JSONObject(string).getJSONObject("consellor").getString("marriage"));
                    }
                    catch (Exception ex){
                        ai_hunyin.setText("");
                    }
                    try{
                        ai_realname.setText(new JSONObject(string).getJSONObject("consellor").getString("realname"));
                    }
                    catch (Exception ex){
                        ai_realname.setText("");
                    }
                    try{
                        ai_gender.setText(new JSONObject(string).getJSONObject("consellor").getString("gender"));
                    }
                    catch (Exception ex){
                        ai_gender.setText("");
                    }
                    try{
                        ai_birthday.setText(new JSONObject(string).getJSONObject("consellor").getString("birth"));
                    }
                    catch (Exception ex){
                        ai_birthday.setText("");
                    }
                    try{
                        ai_telephone.setText(new JSONObject(string).getJSONObject("consellor").getString("tel"));
                    }
                    catch (Exception ex){
                        ai_telephone.setText("");
                    }
                }
            }
            else{
                Log.e("sdad","sdfsdf");
            }
            //pd.dismiss();
            super.handleMessage(msg);
        }
    };
}
