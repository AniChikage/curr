package com.hyphenate.chatuidemo.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

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
    private ImageView ai_portrait;
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
        ai_portrait = (ImageView)findViewById(R.id.ai_portrait);
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
        ai_portrait.setOnClickListener(onClickListener);
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
                case R.id.ai_portrait:
                    changePortrait();
                    break;
                default:
                    break;
            }
        }
    };
    //endregion

    //region 点击头像
    private void changePortrait(){
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                ImageView imageView = (ImageView) findViewById(R.id.ai_portrait);
                /* 将Bitmap设定到ImageView */
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
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
                    String minePortrait;
                    minePortrait = bitmaptoString(((BitmapDrawable)ai_portrait.getDrawable()).getBitmap());
                    if(mycreateSDFile.readSDFile("cachetype").equals("user")){
                        resul=operaton.AlterUserInfo(mycreateSDFile.readSDFile("cache"),ai_nickname.getText().toString(),ai_password.getText().toString(),
                                ai_phone.getText().toString(),ai_address.getText().toString(),ai_region.getText().toString(),
                                ai_hometown.getText().toString(),ai_hunyin.getText().toString(),ai_realname.getText().toString(),
                                ai_gender.getText().toString(),ai_birthday.getText().toString(),ai_telephone.getText().toString(),minePortrait);
                    }
                    else{
                        resul=operaton.AlterConsellorInfo(csid,ai_nickname.getText().toString(),ai_password.getText().toString(),
                                ai_address.getText().toString(),ai_region.getText().toString(),
                                ai_hometown.getText().toString(),ai_hunyin.getText().toString(),ai_realname.getText().toString(),
                                ai_gender.getText().toString(),ai_birthday.getText().toString(),ai_telephone.getText().toString(),minePortrait);
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
                try{
                    String por = new JSONObject(string).getJSONObject("user").getString("portrait");
                    ai_portrait.setImageDrawable(new BitmapDrawable(getBitmapFromByte(Base64.decode(por,Base64.DEFAULT))));
                }
                catch (Exception ex){

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

    //region base64转bitmap
    private static Bitmap getBitmapFromByte(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }
    //endregion

    //region bitmap转base64
    /**
     * 通过Base32将Bitmap转换成Base64字符串
     * @param bitmap
     * @return
     */
    public static String bitmaptoString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }
    //endregion


}
