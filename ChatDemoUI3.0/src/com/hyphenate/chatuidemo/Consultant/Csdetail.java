package com.hyphenate.chatuidemo.Consultant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.Adapter.CsOtherInfoAdapter;
import com.hyphenate.chatuidemo.Adapter.JrywListViewAdapter;
import com.hyphenate.chatuidemo.Appointment.AppointConfirm;
import com.hyphenate.chatuidemo.Base64.BASE64Decoder;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;
import com.hyphenate.chatuidemo.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * Created by AniChikage on 2016/7/29.
 */
public class Csdetail extends Activity {

    private ImageView img,csdtback;
    private TextView text, yuyue, quxiao, queding, first_day_date, second_day_date,third_day_date,forth_day_date;
    private TextView first_day_hint, second_day_hint, third_day_hint, forth_day_hint;
    private TextView csabname,csablevel,csabgender,csabyear,csabtime,csabprice,csabzhuanchang,csabbuzu;
    private ImageView img_first,img_second,img_third,img_forth;
    private String csnoid, user_id, sid="";
    private LinearLayout date_select;
    private LinearLayout llup;
    private RelativeLayout lldown;
    private Context context;
    private View csdt_header_View;
    private LinearLayout csdt_header_ll;
    private ListView csdt_listview;
    private List<Map<String, Object>> csotList;
    private CsOtherInfoAdapter csOtherInfoAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.csdetail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏
        context = this.getApplicationContext();
        initId();
        initDate();
        initOnClick();
        initAdapter();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            csnoid = bundle.getString("csnoid");
            user_id = bundle.getString("user_id");
            Log.e("tag", csnoid);
            if(user_id==null) {
                Log.e("userid","null");
                yuyue.setBackgroundColor(Color.GRAY);
            }
            else{
                yuyue.setOnClickListener(onClickListener);
                Log.e("userid",user_id);
            }

            initGetSingleCs();
        }
    }

    private void initId() {

        //text = (TextView) findViewById(R.id.csdttext);
        yuyue = (TextView) findViewById(R.id.csdtyuyue);
        quxiao = (TextView)findViewById(R.id.quxiao);
        queding = (TextView)findViewById(R.id.queding);

        first_day_date = (TextView)findViewById(R.id.first_day_date);
        first_day_hint = (TextView)findViewById(R.id.first_day_hint);
        second_day_date = (TextView)findViewById(R.id.second_day_date);
        second_day_hint = (TextView)findViewById(R.id.second_day_hint);
        third_day_date = (TextView)findViewById(R.id.third_day_date);
        third_day_hint = (TextView)findViewById(R.id.third_day_hint);
        forth_day_date = (TextView)findViewById(R.id.forth_day_date);
        forth_day_hint = (TextView)findViewById(R.id.forth_day_hint);
        img_first = (ImageView)findViewById(R.id.img_first);
        img_second = (ImageView)findViewById(R.id.img_second);
        img_third = (ImageView)findViewById(R.id.img_third);
        img_forth = (ImageView)findViewById(R.id.img_forth);
        csdtback = (ImageView)findViewById(R.id.csdtback);
        date_select = (LinearLayout)findViewById(R.id.date_select);
        llup = (LinearLayout)findViewById(R.id.llup);
        lldown = (RelativeLayout)findViewById(R.id.lldown);

        //listview
        csdt_listview = (ListView) findViewById(R.id.cslist);
        csdt_header_View = LayoutInflater.from(this).inflate(R.layout.csdt_header, null,false);
        csdt_header_ll = (LinearLayout) csdt_header_View.findViewById(R.id.csdt_header_ll);
        img = (ImageView) csdt_header_View.findViewById(R.id.csdtimg);
        csabname = (TextView)csdt_header_View.findViewById(R.id.csabname);
        csablevel = (TextView)csdt_header_View.findViewById(R.id.csablevel);
        csabgender = (TextView)csdt_header_View.findViewById(R.id.csabgender);
        csabyear = (TextView)csdt_header_View.findViewById(R.id.csabyear);
        csabtime = (TextView)csdt_header_View.findViewById(R.id.csabtime);
        csabprice = (TextView)csdt_header_View.findViewById(R.id.csabprice);
        csabzhuanchang = (TextView)csdt_header_View.findViewById(R.id.csabzhuanchang);
        csabbuzu = (TextView)csdt_header_View.findViewById(R.id.csabbuzu);
        WindowManager wm1 = this.getWindowManager();
        int height1 = wm1.getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams lp1 =csdt_header_ll.getLayoutParams();
        lp1.width=lp1.MATCH_PARENT;
        lp1.height=height1*1300/1679;
        csdt_header_ll.setLayoutParams(lp1);

    }

    private void initDate(){
        java.text.SimpleDateFormat   df=new   java.text.SimpleDateFormat( "MM/dd ");
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        first_day_date.setText(df.format(calendar.getTime()));
        calendar.roll(java.util.Calendar.DAY_OF_YEAR,1);
        second_day_date.setText(df.format(calendar.getTime()));

        calendar.roll(java.util.Calendar.DAY_OF_YEAR,1);
        third_day_date.setText(df.format(calendar.getTime()));
        third_day_hint.setText(getWeek(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK))));

        calendar.roll(java.util.Calendar.DAY_OF_YEAR,1);
        forth_day_date.setText(df.format(calendar.getTime()));
        forth_day_hint.setText(getWeek(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK))));
    }

    private void initOnClick(){
        llup.setOnClickListener(onClickListener);
        csdtback.setOnClickListener(onClickListener);
        quxiao.setOnClickListener(onClickListener);
        queding.setOnClickListener(onClickListener);
        first_day_date.setOnClickListener(onClickListener);
        first_day_hint.setOnClickListener(onClickListener);
        second_day_date.setOnClickListener(onClickListener);
        second_day_hint.setOnClickListener(onClickListener);
        third_day_date.setOnClickListener(onClickListener);
        third_day_hint.setOnClickListener(onClickListener);
        forth_day_date.setOnClickListener(onClickListener);
        forth_day_hint.setOnClickListener(onClickListener);
    }

    private void initAdapter(){
        date_select.setVisibility(View.INVISIBLE);
    }

    private void initGetSingleCs() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ConnNet operaton = new ConnNet();
                    String result = operaton.getUrlgetConsellor(csnoid);
                    Message msg = new Message();
                    msg.obj = result;
                    cshandler.sendMessage(msg);
                } catch (Exception ex) {
                    Toast.makeText(Csdetail.this, "咨询师获取失败", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.csdtyuyue:
                    if(user_id!=null){
                        date_select.setVisibility(View.VISIBLE);
                        setBackground(0);
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    java.text.SimpleDateFormat   df=new   java.text.SimpleDateFormat( "yyyy-MM-dd ");
                                    Calendar calendar= Calendar.getInstance();
                                    calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                                    String sdate = String.valueOf(df.format(calendar.getTime()));
                                    sdate = sdate.replaceAll(" ","");
                                    ConnNet operaton = new ConnNet();
                                    String result = operaton.getPerSchedule(csnoid,sdate);
                                    Log.e("csnoid",csnoid);
                                    Log.e("date",df.format(calendar.getTime()));
                                    Message msg = new Message();
                                    msg.obj = result;
                                    hschedule.sendMessage(msg);
                                } catch (Exception ex) {
                                    Toast.makeText(Csdetail.this, "预约失败", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).start();
                    }
                    else{
                        Toast.makeText(Csdetail.this,"您还未登陆！", Toast.LENGTH_LONG).show();
                    }

                    break;
                case R.id.llup:
                    date_select.setVisibility(View.INVISIBLE);
                    break;
                case R.id.quxiao:
                    date_select.setVisibility(View.INVISIBLE);
                    break;
                case R.id.queding:
                    judgeSelect();
                    if(!sid.equals("")) {
                        //deal_blms();
                        jumpToConfirm();
                    }
                    break;
                case R.id.first_day_date:
                    freshSchedule(0);
                    setBackground(0);
                    break;
                case R.id.first_day_hint:
                    freshSchedule(0);
                    setBackground(0);
                    break;
                case R.id.second_day_date:
                    freshSchedule(1);
                    setBackground(1);
                    break;
                case R.id.second_day_hint:
                    freshSchedule(1);
                    setBackground(1);
                    break;
                case R.id.third_day_date:
                    freshSchedule(2);
                    setBackground(2);
                    break;
                case R.id.third_day_hint:
                    freshSchedule(2);
                    setBackground(2);
                    break;
                case R.id.forth_day_date:
                    freshSchedule(3);
                    setBackground(3);
                    break;
                case R.id.forth_day_hint:
                    freshSchedule(3);
                    setBackground(3);
                    break;
                case R.id.csdtback:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void judgeSelect(){
        try{
            for (int i = 0; i < lldown.getChildCount(); i++)
            {
                View vi=lldown.getChildAt(i);
                if (vi instanceof TextView)
                {
                    if(vi.getTag().equals("1"))
                        sid = String.valueOf (((TextView) vi).getId());
                }
            }
            if(sid.equals(""))
                Toast.makeText(Csdetail.this,"还未选择", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex){
            Toast.makeText(Csdetail.this,"您还未选择日期！", Toast.LENGTH_LONG).show();
        }
    }

    Handler hdialog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            //Log.e("addneed str", string);
            super.handleMessage(msg);
        }
    };

    Handler haddorder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            Log.e("addorder",string);
            super.handleMessage(msg);
        }
    };

    private void setBackground(int selectedId){
        ImageView[] imageView = {img_first,img_second,img_third,img_forth};
        TextView[] tv_date = {first_day_date,second_day_date,third_day_date,forth_day_date};
        TextView[] tv_hint = {first_day_hint,second_day_hint,third_day_hint,forth_day_hint};
        for(int i=0;i<4;i++){
            if(i==selectedId){
                imageView[i].setBackgroundResource(R.drawable.under);
                tv_date[i].setTextColor(getResources().getColor(R.color.solid_red));
                tv_hint[i].setTextColor(getResources().getColor(R.color.solid_red));
            }
            else{
                imageView[i].setBackgroundResource(R.color.solid_white);
                tv_date[i].setTextColor(getResources().getColor(R.color.hint));
                tv_hint[i].setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    //病例描述
    private void deal_blms() {

        final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Csdetail.this);	// 系统默认Dialog没有输入框
        final View alertDialogView = View.inflate(Csdetail.this, R.layout.csdetail_dialog, null);
        final EditText et_blms = (EditText) alertDialogView.findViewById(R.id.binglimiaoshu);

        Button blms_queding = (Button) alertDialogView.findViewById(R.id.blms_queding);
        Button blms_quxiao = (Button) alertDialogView.findViewById(R.id.blms_quxiao);
        final AlertDialog tempDialog = alertDialog.create();
        tempDialog.setView(alertDialogView, 0, 0, 0, 0);

        tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_blms, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        tempDialog.show();
        blms_queding.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            ConnNet operaton = new ConnNet();
                            String result = operaton.addNeed(user_id,et_blms.getText().toString());
                            Message msg = new Message();
                            msg.obj = result;
                            hdialog.sendMessage(msg);
                            tempDialog.dismiss();
                        } catch (Exception ex) {
                            Toast.makeText(Csdetail.this, "添加病例描述失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
        });

        blms_quxiao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tempDialog.dismiss();
            }
        });
    }

    //jump to confirm
    private void jumpToConfirm(){
        Intent intent = new Intent(Csdetail.this, AppointConfirm.class);
        Bundle bundle = new Bundle();
        bundle.putString("sid",sid);
        bundle.putString("user_id",user_id);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void jumpToAppoint() {

        final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Csdetail.this);	// 系统默认Dialog没有输入框
        final View alertDialogView = View.inflate(Csdetail.this, R.layout.csdetail_jump, null);
        Button jump_queding = (Button) alertDialogView.findViewById(R.id.jump_queding);
        Button jump_quxiao = (Button) alertDialogView.findViewById(R.id.jump_quxiao);
        final AlertDialog tempDialog = alertDialog.create();
        tempDialog.setView(alertDialogView, 0, 0, 0, 0);
        tempDialog.show();
        jump_queding.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Csdetail.this, AppointConfirm.class);
                startActivity(intent);
            }
        });

        jump_quxiao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tempDialog.dismiss();
            }
        });


    }

    //
    private void freshSchedule(final int flag){
        new Thread(new Runnable() {
            public void run() {
                try {
                    java.text.SimpleDateFormat   df=new   java.text.SimpleDateFormat( "yyyy-MM-dd");
                    Calendar calendar= Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                    calendar.roll(java.util.Calendar.DAY_OF_YEAR,flag);
                    String sdate = String.valueOf(df.format(calendar.getTime()));
                    sdate = sdate.replaceAll(" ","");
                    ConnNet operaton = new ConnNet();
                    String result = operaton.getPerSchedule(csnoid,sdate);
                    Log.e("csnoid",csnoid);
                    Log.e("date",df.format(calendar.getTime()));
                    Message msg = new Message();
                    msg.obj = result;
                    hschedule.sendMessage(msg);
                } catch (Exception ex) {
                    Toast.makeText(Csdetail.this, "咨询师获取失败", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    Handler cshandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string = (String) msg.obj;
            Log.e("single str", string);
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            //saveFile(string);
            try {
                JSONObject jsonObjs = new JSONObject(string).getJSONObject("consellor");
                int id = jsonObjs.getInt("id");
                String username = jsonObjs.getString("username");
                String realname = jsonObjs.getString("realname");
                String gender = jsonObjs.getString("gender");
                String authen = jsonObjs.getString("authen");
                String career = jsonObjs.getString("career");
                String pro1,pro2,pro3;
                try{
                    pro1 = jsonObjs.getJSONObject("pro1").getString("field");
                }
                catch (Exception e){
                    pro1 = "";
                }
                try{
                    pro2 = jsonObjs.getJSONObject("pro2").getString("field");
                }
                catch (Exception e){
                    pro2 = "";
                }
                try{
                    pro3 = jsonObjs.getJSONObject("pro3").getString("field");
                }
                catch (Exception e){
                    pro3 = "";
                }
                String price = jsonObjs.getJSONObject("rate").getString("price");
                String asdf = jsonObjs.getString("bust");
                String recruit = jsonObjs.getString("recruit");
                String description = jsonObjs.getString("description");
                Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(asdf, Base64.DEFAULT)));
                img.setBackgroundDrawable(drawable);
                csabname.setText(realname);
                csablevel.setText(authen);
                csabgender.setText(gender);
                if(Integer.valueOf(career)>1000){
                    csabtime.setText(">1000小时");
                }
                else if(Integer.valueOf(career)>100&&Integer.valueOf(career)<=1000){
                    csabtime.setText(">100小时");
                }
                else{
                    csabtime.setText("<100小时");
                }
                csabprice.setText(price+"元/单位咨询时长");
                csabzhuanchang.setText(pro1+" "+pro2+" "+pro3);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("otherInfo", getStringFromBASE64(description));
                listItems.add(map);
                //csabqita.setText(getStringFromBASE64(description));
                try{
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar= Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                    Date startTime = df.parse(recruit);
                    Date endTime = df.parse(String.valueOf(df.format(calendar.getTime())).replaceAll(" ",""));
                    long diff = endTime.getTime() - startTime.getTime();//这样得到的差值是微秒级别
                    long years = diff / (1000 * 60 * 60 * 24);
                    csabyear.setText(">"+ String.valueOf(years/365)+"年");
                }
                catch (Exception ex){
                    Log.e("",ex.toString());
                }

            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            csdt_listview.addHeaderView(csdt_header_View);
            csotList = listItems;
            csOtherInfoAdapter = new CsOtherInfoAdapter(context, csotList);
            csdt_listview.setAdapter(csOtherInfoAdapter);
            super.handleMessage(msg);
        }

    };

    Handler hschedule = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lldown.removeAllViews();
            String string = (String) msg.obj;
            Log.e("single str", string);
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("schedules");
                Log.e("sf", String.valueOf(jsonObjs.length()));
                int yTop=0;
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    String startTime = jsonObj.getString("start");
                    String endTime = jsonObj.getString("end");
                    String sid = jsonObj.getString("sid");
                    startTime = startTime.substring(startTime.indexOf(" "),startTime.length()-3);
                    endTime = endTime.substring(endTime.indexOf(" "),endTime.length()-3);

                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    int numPerRow = 4;
                    int realw = (width-50)/numPerRow;
                    int realh = realw*119/227;
                    RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams (realw,realh);
                    if(i%numPerRow==0) yTop++;
                    btParams.topMargin = 20 + (realh+10)*(yTop-1);   //纵坐标定位
                    btParams.leftMargin = 10 + ((width-50)/numPerRow+10)*(i%numPerRow);   //横坐标定位
                    final TextView tv = new TextView(context);
                    tv.setText(startTime+"-"+endTime);
                    tv.setId(Integer.valueOf(sid));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundResource(R.drawable.csunborder);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < lldown.getChildCount(); i++)
                            {
                                View vi=lldown.getChildAt(i);
                                if (vi instanceof TextView)
                                {
                                    //vi.setBackgroundColor(Color.GRAY);
                                    vi.setBackgroundResource(R.drawable.csunborder);
                                    ((TextView) vi).setTextColor(Color.BLACK);
                                    vi.setTag("0");
                                }
                            }
                            //tv.setBackgroundColor(Color.BLUE);
                            tv.setBackgroundResource(R.drawable.csborder);
                            tv.setTextColor(Color.RED);
                            tv.setTag("1");
                            //Toast.makeText(Csdetail.this,String.valueOf(tv.getId()),Toast.LENGTH_LONG).show();
                        }
                    });
                    lldown.addView(tv,btParams);
                }
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

    private String getWeek(String mWay){
        if("1".equals(mWay)){
            mWay ="日";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return "周"+mWay;
    }

    public static void saveFile(String str) {
        String filePath = null;
        filePath = Environment.getExternalStorageDirectory().toString() + "/hello.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("按下了back键   onBackPressed()");
        MainActivity.testPrint();
    }

}
