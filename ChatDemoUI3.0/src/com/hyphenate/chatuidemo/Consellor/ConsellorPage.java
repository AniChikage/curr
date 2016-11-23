package com.hyphenate.chatuidemo.Consellor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AniChikage on 2016/9/27.
 */


public class ConsellorPage extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    //region ID
    private createSDFile mycreateSDFile;
    private ListView listView;
    private String cid;
    private Context hcontext;
    private long[] mHits = new long[2];
    private int isAdd = 0;
    private List<Map<String, Object>> list;
    private CsAppointAdapter csAppointAdapter;
    private View header_view;
    private LinearLayout header_ll;
    private TextView app_username;
    private String consellor_email;
    private static MaterialCalendarView mcv;
    private ImageView dochuzhen;
    private ImageView mp_setting;
    private int index;
    private int currentTabIndex;
    public static ConsellorPage instance = null;
    private ProgressDialog pd=null;
    private ProgressDialog pd1=null;
    private ProgressDialog pd2=null;
    private ProgressDialog pd_del=null;
    private Button[] mTabs;
    private TextView unreadLabel;
    private TextView unreadAddressLable;
    private RelativeLayout[] relativeLayouts;
    private RelativeLayout richeng_page;
    private RelativeLayout wode_page;
    private MaterialCalendarView materialCalendarView;
    private RelativeLayout ll_down;
    private double difDay;

    private static final String TIME_PATTERN = "HH:mm";
    private TextView lblDate;
    private TextView lblTime;
    private Calendar calendar1;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    private TextView txt_starttime;
    private TextView txt_endtime;
    private TextView btn_starttime;
    private TextView btn_endtime;
    private TextView toAdd_add;
    private TextView toAdd_cancel;
    private LinearLayout ll_add;
    private LinearLayout ll_add_up;
    private String select_year="";
    private String select_month="";
    private String select_day="";
    private String select_time="";

    private int which_one=0;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.consellorpage);

        calendar1 = Calendar.getInstance();
        onTimeSetListener = this;
        /*
        try{
            dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
            timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
            //update();
            TimePickerDialog.newInstance(this, calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
        }
        catch (Exception ex){
            Log.e("timeerr",ex.toString());
        }
*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏
        hcontext = this.getApplicationContext();
        mycreateSDFile = new createSDFile(hcontext);
        consellor_email = mycreateSDFile.readSDFile("cache");
        instance = this;
        pd = new ProgressDialog(ConsellorPage.this);
        pd1 = new ProgressDialog(ConsellorPage.this);
        pd2 = new ProgressDialog(ConsellorPage.this);
        pd_del = new ProgressDialog(ConsellorPage.this);
        try{
            initId();
            pd.setMessage("获取中……");
            pd.show();
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

        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
        mTabs = new Button[2];
        mTabs[0] = (Button) findViewById(R.id.btn_conversation);
        mTabs[1] = (Button) findViewById(R.id.btn_address_list);
        mTabs[0].setSelected(true);

        txt_starttime = (TextView)findViewById(R.id.txt_starttime);
        txt_endtime = (TextView)findViewById(R.id.txt_endtime);
        btn_endtime = (TextView)findViewById(R.id.btn_endtime);
        btn_endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                    timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
                    //update();
                    which_one = 1;
                    TimePickerDialog.newInstance(onTimeSetListener, calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
                }
                catch (Exception ex){
                    Log.e("timeerr",ex.toString());
                }
            }
        });
        btn_starttime = (TextView)findViewById(R.id.btn_starttime);
        btn_starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                    timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
                    //update();
                    which_one = 0;
                    TimePickerDialog.newInstance(onTimeSetListener, calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
                }
                catch (Exception ex){
                    Log.e("timeerr",ex.toString());
                }
            }
        });
        toAdd_add = (TextView)findViewById(R.id.toAdd_add);
        toAdd_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd2.setMessage("添加中……");
                pd2.show();
                isAdd = 1;
                if(select_time!=""&&txt_starttime.getText().toString()!=""&&txt_endtime.getText().toString()!=""){
                    new Thread(new Runnable() {
                        public void run() {

                            try {
                                ConnNet operaton = new ConnNet();
                                String adddate = select_time;
                                String addstarttime = adddate+" "+txt_starttime.getText().toString()+":00";
                                String addendtime = adddate+" "+txt_endtime.getText().toString()+":00";
                                Log.e("adddate",adddate);
                                Log.e("addstarttime",addstarttime);
                                Log.e("addendtime",addendtime);
                                String result = operaton.addSchedule(cid,adddate,addstarttime,addendtime);
                                Message msg = new Message();
                                msg.obj = result;
                                hAddSchedule.sendMessage(msg);
                            }catch (Exception ex) {
                                Toast.makeText(hcontext, "添加日程失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();
            }
            }
        });
        toAdd_cancel = (TextView)findViewById(R.id.toAdd_cancel);
        toAdd_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_add.setVisibility(View.INVISIBLE);
            }
        });
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        ll_add_up = (LinearLayout) findViewById(R.id.ll_add_up);
        ll_add_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_add.setVisibility(View.INVISIBLE);
            }
        });

        richeng_page = (RelativeLayout)findViewById(R.id.richeng_page);
        wode_page = (RelativeLayout)findViewById(R.id.wode_page);
        relativeLayouts = new RelativeLayout[]{richeng_page,wode_page};
        relativeLayouts[0].setVisibility(View.VISIBLE);
        relativeLayouts[1].setVisibility(View.INVISIBLE);
        ll_add.setVisibility(View.INVISIBLE);

        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        materialCalendarView.setOnDateChangedListener(onDateSelectedListener);

        ll_down = (RelativeLayout)findViewById(R.id.ll_down);

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

    Handler hAddSchedule = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            Log.e("hAddSchedule",string);
            try {
                String status = new JSONObject(string).getString("addSchedule");
                if(status.equals("1")){

                    refreshSche();
                }
                else{
                    Toast.makeText(ConsellorPage.this,"添加日程失败！",Toast.LENGTH_LONG).show();
                    refreshSche();
                }
            } catch (Exception e) {
                System.out.println("获取用户失败");
                e.printStackTrace();
            }
            //pd2.dismiss();
            super.handleMessage(msg);
        }
    };

    public void refreshSche(){
        ll_down.removeAllViews();
        new Thread(new Runnable() {
            public void run() {
                try {
                    java.text.SimpleDateFormat   df= new java.text.SimpleDateFormat( "yyyy-MM-dd ");
                    Calendar calendar= Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                    String sdate = String.valueOf(df.format(calendar.getTime()));
                    sdate = sdate.replaceAll(" ","");
                    difDay = mcv.getSelectedDate().getDay() - calendar.getTime().getDay();
                    String selectedDate = mcv.getSelectedDate().getYear()+"-"+String.valueOf(Integer.valueOf(mcv.getSelectedDate().getMonth())+1)+"-"+mcv.getSelectedDate().getDay();
                    Log.e("时间差",String.valueOf(difDay));
                    difDay -= 20;
                    Log.e("时间差",String.valueOf(difDay));
                    select_time = selectedDate;
                    if(difDay>=0&&difDay<=6){
                        ConnNet operaton = new ConnNet();
                        String result = operaton.getPerSchedule(cid,selectedDate);
                        Log.e("csnoid",cid);
                        Log.e("sdate",sdate);
                        Log.e("selectdate",selectedDate);
                        Log.e("date",df.format(calendar.getTime()));
                        Message msg = new Message();
                        msg.obj = result;
                        hschedule.sendMessage(msg);
                    }
                    else{
                        Message msg = new Message();
                        msg.obj = "请选择七天之内的时间";
                        hupdate.sendMessage(msg);
                        try{
                            ll_down.removeAllViews();
                        }
                        catch (Exception ex){
                            Log.e("kak",ex.toString());
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(hcontext, "预约失败", Toast.LENGTH_LONG).show();
                }
            }
        }).start();

    }

    OnDateSelectedListener onDateSelectedListener = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            //Toast.makeText(ConsellorPage.this,widget.getSelectedDate().toString(),Toast.LENGTH_LONG).show();
            pd1.setMessage("获取中……");
            pd1.show();
            String role = "${inputCount}*0.04*383+${saleCount}*15";
            Pattern p = Pattern.compile("\\{.*?\\}");// 查找规则公式中大括号以内的字符
            Matcher m = p.matcher(widget.getSelectedDate().toString());
            while (m.find()) {// 遍历找到的所有大括号
                String param = m.group().replaceAll("\\{\\}", "");// 去掉括号
                Log.e("param",param);
            }
            mcv = widget;
            select_year = mcv.getSelectedDate().getYear()+"";
            select_month = String.valueOf(Integer.valueOf(mcv.getSelectedDate().getMonth())+1);
            select_day = mcv.getSelectedDate().getDate()+"";
            new Thread(new Runnable() {
                public void run() {
                    try {
                        java.text.SimpleDateFormat   df= new java.text.SimpleDateFormat( "yyyy-MM-dd ");
                        Calendar calendar= Calendar.getInstance();
                        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                        String sdate = String.valueOf(df.format(calendar.getTime()));
                        sdate = sdate.replaceAll(" ","");
                        difDay = mcv.getSelectedDate().getDay() - calendar.getTime().getDay();
                        String selectedDate = mcv.getSelectedDate().getYear()+"-"+String.valueOf(Integer.valueOf(mcv.getSelectedDate().getMonth())+1)+"-"+mcv.getSelectedDate().getDay();
                        Log.e("时间差",String.valueOf(difDay));
                        difDay -= 20;
                        Log.e("时间差",String.valueOf(difDay));
                        select_time = selectedDate;
                        if(difDay>=0&&difDay<=6){
                            ConnNet operaton = new ConnNet();
                            String result = operaton.getPerSchedule(cid,selectedDate);
                            Log.e("csnoid",cid);
                            Log.e("sdate",sdate);
                            Log.e("selectdate",selectedDate);
                            Log.e("date",df.format(calendar.getTime()));
                            Message msg = new Message();
                            msg.obj = result;
                            hschedule.sendMessage(msg);
                        }
                        else{
                            Message msg = new Message();
                            msg.obj = "请选择七天之内的时间";
                            pd1.dismiss();
                            hupdate.sendMessage(msg);
                            try{
                                ll_down.removeAllViews();
                            }
                            catch (Exception ex){
                                Log.e("kak",ex.toString());
                            }
                        }
                    } catch (Exception ex) {
                        Toast.makeText(hcontext, "预约失败", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
    };

    Handler hupdate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(hcontext, "请选择七天之内的时间",Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }
    };

    Handler hschedule = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_down.removeAllViews();
            String string = (String) msg.obj;
            Log.e("single str", string);
            int i;
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("schedules");
                Log.e("sf", String.valueOf(jsonObjs.length()));
                int yTop=0;
                for(i = 0; i < jsonObjs.length() ; i++){
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
                    final TextView tv = new TextView(hcontext);
                    tv.setText(startTime+"-"+endTime);
                    tv.setId(Integer.valueOf(sid));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundResource(R.drawable.csunborder);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < ll_down.getChildCount(); i++)
                            {
                                View vi=ll_down.getChildAt(i);
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
                            /////double click
                            System.out.println("" + mHits.length);

                            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                            mHits[mHits.length - 1] = SystemClock.uptimeMillis(); // 系统开机时间
                            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                                //Toast.makeText(ConsellorPage.this, "这就是传说中的双击事件", Toast.LENGTH_LONG).show();
                                new AlertDialog.Builder(ConsellorPage.this).setTitle("确定删除该日程吗？")
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(hcontext, "yes", Toast.LENGTH_LONG).show();
                                                // 点击“确认”后的操作
                                                pd_del.setMessage("删除中......");
                                                pd_del.show();
                                                new Thread(new Runnable() {
                                                    public void run() {
                                                        try {
                                                            ConnNet operaton = new ConnNet();
                                                            String result = operaton.delSchedule(String.valueOf(tv.getId()));
                                                            Message msg = new Message();
                                                            msg.obj = result;
                                                            hdelschedule.sendMessage(msg);

                                                        } catch (Exception ex) {
                                                            Toast.makeText(hcontext, "删除日程失败", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }).start();

                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 点击“返回”后的操作,这里不设置没有任何操作
                                            }
                                        }).show();
                            }
                            //Toast.makeText(Csdetail.this,String.valueOf(tv.getId()),Toast.LENGTH_LONG).show();
                        }
                    });
                    ll_down.addView(tv,btParams);
                }
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
                final TextView tv = new TextView(hcontext);
                tv.setText("添加");
                //tv.setId(Integer.valueOf(sid));
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundResource(R.drawable.csunborder);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < ll_down.getChildCount(); i++)
                        {
                            View vi=ll_down.getChildAt(i);
                            if (vi instanceof TextView)
                            {
                                //vi.setBackgroundColor(Color.GRAY);
                                vi.setBackgroundResource(R.drawable.csunborder);
                                ((TextView) vi).setTextColor(Color.BLACK);
                                vi.setTag("0");

                            }
                        }
                        Log.e("kjkhjkhkh","hjgjgjh");
                        ll_add.setVisibility(View.VISIBLE);
                        //tv.setBackgroundColor(Color.BLUE);
                        tv.setBackgroundResource(R.drawable.csborder);
                        tv.setTextColor(Color.RED);
                        tv.setTag("1");
                        //Toast.makeText(Csdetail.this,String.valueOf(tv.getId()),Toast.LENGTH_LONG).show();
                    }
                });
                /*
                tv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
                */
                ll_down.addView(tv,btParams);
                try{
                    pd1.dismiss();
                    pd2.dismiss();
                    if(isAdd == 1)
                        Toast.makeText(ConsellorPage.this,"添加日程成功！",Toast.LENGTH_LONG).show();
                    isAdd = 0;
                    ll_add.setVisibility(View.INVISIBLE);
                }
                catch (Exception ex){
                    Log.e("err","err");
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
                try{
                    pd1.dismiss();
                    pd2.dismiss();
                }
                catch (Exception ex){
                    Log.e("err","err");
                }
            }
            super.handleMessage(msg);
        }

    };

    //
    Handler hdelschedule = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            Log.e("sd",string);
            try{
                String del_status = new JSONObject(string).getString("delSchedule");
                if(del_status.equals("1")){
                    Toast.makeText(ConsellorPage.this,"删除日程成功！",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ConsellorPage.this,"删除日程失败！",Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception ex){
                Log.e("msg","del error");
            }
            refreshSche();
            pd_del.dismiss();
            super.handleMessage(msg);
        }
    };
    /**
     * on tab clicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_address_list:
                index = 1;
                break;
        }

        if (currentTabIndex != index) {
            relativeLayouts[index].setVisibility(View.VISIBLE);
            relativeLayouts[currentTabIndex].setVisibility(View.INVISIBLE);
            ll_add.setVisibility(View.INVISIBLE);
        }
        mTabs[currentTabIndex].setSelected(false);
        // set current tab selected
        mTabs[index].setSelected(true);
        currentTabIndex = index;
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
                    String isAccept = "";
                    try{
                        isAccept = jsonObj.getString("refuse");
                    }
                    catch(Exception ex){
                        isAccept = "notSet";
                    }
                    Log.e("isaccept",isAccept);
                    JSONObject jsonObjsc = new JSONObject(str_schedule).getJSONObject("consellor");
                    //String portrait = jsonObjsc.getString("portrait");
                    String portrait = jsonObj.getJSONObject("user").getString("portrait");
                    String consellor_name = jsonObjsc.getString("realname");
                    Drawable drawable = new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait,Base64.DEFAULT)));
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("appoint_img", drawable);
                    if(isAccept.equals("0")){
                        if(paid.equals("1")){
                            map.put("appoint_hint", "已支付");
                        }
                        else{
                            map.put("appoint_hint", "未支付");
                        }
                    }
                    else if(isAccept.equals("1")){
                        map.put("appoint_hint", "已拒绝");
                    }
                    else{
                        map.put("appoint_hint", "还未审核");
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
            pd.dismiss();
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

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar1.set(year, monthOfYear, dayOfMonth);
        update();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar1.set(Calendar.MINUTE, minute);
        update();
    }

    private void update() {
        if(which_one == 0)
            txt_starttime.setText(timeFormat.format(calendar1.getTime()));
        else
            txt_endtime.setText(timeFormat.format(calendar1.getTime()));
        //Toast.makeText(ConsellorPage.this,timeFormat.format(calendar1.getTime()),Toast.LENGTH_LONG ).show();
    }
}
