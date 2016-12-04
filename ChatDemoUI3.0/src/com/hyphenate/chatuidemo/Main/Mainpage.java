package com.hyphenate.chatuidemo.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.Adapter.AppointAdapter;
import com.hyphenate.chatuidemo.Adapter.CsabListViewAdapter;
import com.hyphenate.chatuidemo.Adapter.JrywListViewAdapter;
import com.hyphenate.chatuidemo.Adapter.ListViewAdapter;
import com.hyphenate.chatuidemo.Appointment.AppointDetail;
import com.hyphenate.chatuidemo.Consultant.Csdetail;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;
import com.hyphenate.chatuidemo.widget.ViewFlow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by AniChikage on 2016/7/6.
 */
@SuppressWarnings("deprecation")
public class Mainpage extends Activity {
    private TabHost tabHost=null;
    private Context hcontext;
    private ViewFlow viewFlow;
    private TextView app_username,moren, tuijian, paixu,anli,kepu,xuzhi;
    private PopupWindow popupwindow;
    private Button test_question;
    private ImageView dochuzhen,mp_setting;
    private ImageView iv_anli,iv_kepu,iv_xuzhi;
    private SlideShowView slideshow;
    private SlideShowViewCs csslideshow;
    private View viewMainpage,viewConsuler,viewQanda,viewPersonal,mainpage_header_view,mainpage_cs_header_view,mine_header_view;
    private String user_email;
    private String user_id;
    private String user_nickname;
    private LinearLayout mainpage_header_ll,mainpage_cs_header_ll,mine_header_ll;

    private ListView listView,listviewCase,cslistviewa,jrywListView,mainpage_listview,mine_listview;

    private ImageButton imgbt_sum;
    private ListViewAdapter listViewAdapter;
    private JrywListViewAdapter listViewAdapterCase;
    private CsabListViewAdapter csListViewAdaptera;
    private JrywListViewAdapter jrywListViewAdapter;
    private AppointAdapter appointAdapter;

    private List<Map<String, Object>> listItems,listCase,csabLista,jrywList,minelist;

    private TabWidget mTabWidget;
    public List<ImageView> imageList = new ArrayList<ImageView>();

    //program entry
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.mainframe);
        hcontext=this.getApplicationContext();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            user_email = bundle.getString("app_username");
            Log.e("user_email",user_email);
            judgeLogin();
        }
    }

    //判断是否登录
    private void judgeLogin(){
        if(!user_email.equals("scan_visitor")){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ConnNet operaton = new ConnNet();
                        String result = operaton.getUser(user_email);
                        Message msg = new Message();
                        msg.obj = result;
                        hgetuser.sendMessage(msg);
                    } catch (Exception ex) {
                        Toast.makeText(Mainpage.this, "咨询师获取失败", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
        else{
            try{
                init();
                SysnConsellors();
                SysnConsellorsAds();
                app_username.setText("您还未登录！");
            }
            catch (Exception ex){
                Toast.makeText(Mainpage.this,"获取失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    //first step: get user
    Handler hgetuser = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            try {
                JSONObject jsonObjs = new JSONObject(string).getJSONObject("user");
                user_id = jsonObjs.getString("id");
                user_nickname = jsonObjs.getString("nickname");
                try{
                    init();
                    SysnConsellors();
                    SysnConsellorsAds();
                    app_username.setText(user_nickname);
                }
                catch (Exception ex){
                    Toast.makeText(Mainpage.this,"获取失败", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                System.out.println("获取用户失败");
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };


    private void SysnConsellorsAds(){
        mainpage_cs_header_ll = (LinearLayout) mainpage_cs_header_view.findViewById(R.id.mpcs_header_ll);
        paixu = (TextView) mainpage_cs_header_view.findViewById(R.id.paixu);
        paixu.setOnClickListener(new click_paixu());
        WindowManager wm1 = this.getWindowManager();
        int height1 = wm1.getDefaultDisplay().getHeight();
        int a1= (int) height1*552/1679;
        ViewGroup.LayoutParams lp1 = mainpage_cs_header_ll.getLayoutParams();
        lp1.width=lp1.MATCH_PARENT;
        lp1.height=a1;
        mainpage_cs_header_ll.setLayoutParams(lp1);
        csslideshow = (SlideShowViewCs)mainpage_cs_header_view.findViewById(R.id.mpcsslideshow);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getConsellorAds();
                    Message msg=new Message();
                    msg.obj=result;
                    mpcs_handler.sendMessage(msg);
                }
                catch (Exception ex){

                }
            }
        }).start();
    }

    Handler mpcs_handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            ArrayList<String> adimg = new ArrayList<String>();
            ArrayList<String> adhint = new ArrayList<String>();
            ArrayList<String> adid = new ArrayList<String>();
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("consellor_ads");
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    String id = jsonObj.getString("id");
                    String realname = jsonObj.getString("realname");
                    String cover = jsonObj.getString("bust");
                    adimg.add(cover);
                    adhint.add(realname);
                    adid.add(id);
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse errorr !");
                e.printStackTrace();
            }
            csslideshow.init(adhint,adimg,adid,user_id);
            super.handleMessage(msg);
        }
    };

    //同步咨询师列表
    private void SysnConsellors(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getConsellors();
                    Message msg=new Message();
                    msg.obj=result;
                    handler.sendMessage(msg);
                }
                catch (Exception ex){
                    Toast.makeText(Mainpage.this,"咨询师获取失败", Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    //同步咨询师列表广告
    private void SysnConsellorAds(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getConsellorAds();
                    Message msg=new Message();
                    msg.obj=result;
                    csadhandler.sendMessage(msg);
                }
                catch (Exception ex){
                    Toast.makeText(Mainpage.this,"咨询师获取失败", Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    /*
    * 初始化
    * */
    private void init(){
        //viewFlow = (ViewFlow)findViewById(R.id.viewflow);

        test_question = (Button)findViewById(R.id.test_question);

        //mine begin
        mine_listview = (ListView)findViewById(R.id.mine_listview);
        mine_header_view = LayoutInflater.from(this).inflate(R.layout.mainpage_mine_header, null);
        mine_header_ll = (LinearLayout)mine_header_view.findViewById(R.id.mine_header_ll);
        app_username = (TextView)mine_header_view.findViewById(R.id.app_username);
        dochuzhen = (ImageView) mine_header_view.findViewById(R.id.dochuzhen);
        dochuzhen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this,QuestionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_id",user_id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mp_setting = (ImageView) mine_header_view.findViewById(R.id.mp_setting);
        mp_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Mainpage.this,"开发中", Toast.LENGTH_LONG).show();
            }
        });

        WindowManager wmmine = this.getWindowManager();
        int allheight = wmmine.getDefaultDisplay().getHeight();
        int upmineh= (int) allheight*795/1905;
        ViewGroup.LayoutParams lpmine = mine_header_ll.getLayoutParams();
        lpmine.width=lpmine.MATCH_PARENT;
        lpmine.height=upmineh;
        mine_header_ll.setLayoutParams(lpmine);
        SysnMyAppoint();

        //mainpage begin
        mainpage_listview = (ListView)findViewById(R.id.mplistview);
        mainpage_header_view = LayoutInflater.from(this).inflate(R.layout.mainpage_header, null);
        mainpage_header_ll = (LinearLayout) mainpage_header_view.findViewById(R.id.header_ll);
        WindowManager wm1 = this.getWindowManager();
        int height1 = wm1.getDefaultDisplay().getHeight();
        //int a1= (int) height1*936/1679;
        int a1= (int) height1*800/1679;
        ViewGroup.LayoutParams lp1 =mainpage_header_ll.getLayoutParams();
        lp1.width=lp1.MATCH_PARENT;
        lp1.height=a1;
        mainpage_header_ll.setLayoutParams(lp1);

        SysnJRYW();  //同步今日要闻
        slideshow= (SlideShowView)mainpage_header_view.findViewById(R.id.slideshow);
        anli = (TextView)mainpage_header_view.findViewById(R.id.anli);
        kepu = (TextView)mainpage_header_view.findViewById(R.id.kepu);
        xuzhi = (TextView)mainpage_header_view.findViewById(R.id.xuzhi);
        iv_anli = (ImageView)mainpage_header_view.findViewById(R.id.iv_anli);
        iv_kepu = (ImageView)mainpage_header_view.findViewById(R.id.iv_kepu);
        iv_xuzhi = (ImageView)mainpage_header_view.findViewById(R.id.iv_xuzhi);
        anli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this, Category.class);
                Bundle bundle = new Bundle();
                bundle.putString("category_name","案例");
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();
            }
        });
        iv_anli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this, Category.class);
                Bundle bundle = new Bundle();
                bundle.putString("category_name","案例");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        kepu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this, Category.class);
                Bundle bundle = new Bundle();
                bundle.putString("category_name","科普");
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();
            }
        });
        xuzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this, Category.class);
                Bundle bundle = new Bundle();
                bundle.putString("category_name","须知");
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();
            }
        });
        iv_kepu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this, Category.class);
                Bundle bundle = new Bundle();
                bundle.putString("category_name","科普");
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();
            }
        });
        iv_xuzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this, Category.class);
                Bundle bundle = new Bundle();
                bundle.putString("category_name","须知");
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();
            }
        });
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams lp =slideshow.getLayoutParams();
        lp.width=lp.MATCH_PARENT;
        lp.height=height*526/1670;
        slideshow.setLayoutParams(lp);
        SysnAds();  //同步header
        //mainpage end

        //mainpage cs begin


        //

        /*
        * 标签栏
        * */
        tabHost = (TabHost) findViewById(R.id.myTabHost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(composeLayout("首页",R.drawable.home_default))
                .setContent(R.id.mainpage));
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(composeLayout("咨询师",R.drawable.zixunshi_default))
                .setContent(R.id.consulter));
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator(composeLayout("问答",R.drawable.question_mark_default))
                .setContent(R.id.qanda));
        tabHost.addTab(tabHost.newTabSpec("tab4")
                .setIndicator(composeLayout("我的",R.drawable.me))
                .setContent(R.id.personal));
        imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.home_slected));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            // TODO Auto-generated method stub
            @Override
            public void onTabChanged(String tabId)
            {
                // 设置所有选项卡的图片为未选中图片
                imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.home_default));
                imageList.get(1).setImageDrawable(getResources().getDrawable(R.drawable.zixunshi_default));
                imageList.get(2).setImageDrawable(getResources().getDrawable(R.drawable.question_mark_default));
                imageList.get(3).setImageDrawable(getResources().getDrawable(R.drawable.me));

                if (tabId.equalsIgnoreCase("tab1")) {
                    imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.home_slected));
                }
                else if (tabId.equalsIgnoreCase("tab2")) {
                    imageList.get(1).setImageDrawable(getResources().getDrawable(R.drawable.zixunshi));
                }
                else if (tabId.equalsIgnoreCase("tab3")) {
                    imageList.get(2).setImageDrawable(getResources().getDrawable(R.drawable.question_mark_slected));
                }
                else if (tabId.equalsIgnoreCase("tab4")) {
                    imageList.get(3).setImageDrawable(getResources().getDrawable(R.drawable.me_slected));
                }


            }
        });

        //标签栏点击事件
        mTabWidget = tabHost.getTabWidget();
        mTabWidget.setDividerDrawable(null);
        viewMainpage = mTabWidget.getChildTabViewAt(0);
        viewConsuler = mTabWidget.getChildTabViewAt(1);
        viewQanda = mTabWidget.getChildTabViewAt(2);
        viewPersonal = mTabWidget.getChildTabViewAt(3);

        //app_username.setText(user_nickname);
        jrywListView = (ListView)findViewById(R.id.mplist);
        cslistviewa = (ListView)findViewById(R.id.cslva);
        mainpage_cs_header_view = LayoutInflater.from(this).inflate(R.layout.mainpage_cs_header, null);

    }

    private void SysnMyAppoint(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getOrderByUid(user_id);
                    Message msg=new Message();
                    msg.obj=result;
                    myAppointHandler.sendMessage(msg);
                }
                catch (Exception ex){
                    Log.e("get orders","获取预约失败");
                }

            }
        }).start();
    }

    Handler myAppointHandler=new Handler(){
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
                    String paid = jsonObj.getString("paid");
                    String starttime = jsonObj.getString("starttime");
                    String str_schedule = jsonObj.getString("schedule");
                    JSONObject jsonObjsc = new JSONObject(str_schedule).getJSONObject("consellor");
                    String portrait = jsonObjsc.getString("portrait");
                    String consellor_name = jsonObjsc.getString("realname");
                    Drawable drawable = new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait, Base64.DEFAULT)));
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
                    listItems.add(map);
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }

            mine_listview.addHeaderView(mine_header_view);
            minelist = listItems;
            appointAdapter = new AppointAdapter(hcontext, minelist);
            mine_listview.setAdapter(appointAdapter);
            mine_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        ListView lv = (ListView)parent;
                        HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
                        if(person!=null){
                            //Toast.makeText(getApplicationContext(),person.get("appoint_oid").toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Mainpage.this, AppointDetail.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("appoint_oid",person.get("appoint_oid").toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else{

                        }
                            //Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(Mainpage.this,ex.toString() , Toast.LENGTH_SHORT).show();
                        Log.e("get error",ex.toString());
                    }
                }
            });
            super.handleMessage(msg);
        }
    };

    //

    class click_paixu implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.paixu:
                    if (popupwindow != null&&popupwindow.isShowing()) {
                        popupwindow.dismiss();
                        return;
                    } else {
                        initmPopupWindowView();
                        popupwindow.showAsDropDown(v, 0, 5);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void initmPopupWindowView() {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popview_item,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        popupwindow = new PopupWindow(customView, width/3,width/3 );
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupwindow.setAnimationStyle(R.style.AnimationFade);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });

        /** 在这里可以实现自定义视图的功能 */
        Button btton2 = (Button) customView.findViewById(R.id.zhuanchang);
        Button btton3 = (Button) customView.findViewById(R.id.jiage);
        //Button btton4 = (Button) customView.findViewById(R.id.keyuyue);
        btton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paixu.setText("专长");
                if (popupwindow != null&&popupwindow.isShowing()) {
                    popupwindow.dismiss();
                }
            }
        });
        btton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paixu.setText("价格");
                if (popupwindow != null&&popupwindow.isShowing()) {
                    popupwindow.dismiss();
                }
            }
        });

    }

    //咨询师信息
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;

            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("consellors");
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    int id = jsonObj.getInt("id");
                    String name = jsonObj.getString("realname");
                    String gender = jsonObj.getString("gender");
                    String level = jsonObj.getString("authen");
                    String time = jsonObj.getString("career");
                    String goodat = jsonObj.getJSONObject("pro1").getString("field");
                    String price = jsonObj.getJSONObject("rate").getString("price");
                    Map<String, Object> map = new HashMap<String, Object>();
                    //Log.e("get: img length",String.valueOf(jsonObj.getString("portrait").length()));
                   // Log.e("get: img string",jsonObj.getString("portrait"));
                    //===============================================================
                    String asdf=jsonObj.getString("portrait");
                    asdf=asdf.replace("/n","/n");

                    //===============================================================
                    Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(asdf, Base64.DEFAULT)));
                    //Drawable drawable =new BitmapDrawable(getBitmapFromByte(jsonObj.getString("portrait").getBytes()));
                    map.put("csabimg", drawable);               //咨询师头像
                    map.put("csabid", String.valueOf(id));
                    map.put("csabname", name);              //姓名
                    map.put("csabgender", gender);     //性别
                    map.put("csablevel", level); //职业等级
                    //map.put("csabtime", time); //案例时长
                    map.put("csabgoodat", goodat); //擅长领域
                    map.put("csabprice", price); //价格
                    listItems.add(map);
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            cslistviewa.addHeaderView(mainpage_cs_header_view);
            csabLista = listItems;
            csListViewAdaptera = new CsabListViewAdapter(hcontext, csabLista);
            cslistviewa.setAdapter(csListViewAdaptera);
            cslistviewa.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        ListView lv = (ListView)parent;
                        HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
                        if(person!=null){
                            //Toast.makeText(getApplicationContext(), person.get("csabid").toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Mainpage.this, Csdetail.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("csnoid",person.get("csabid").toString());
                            bundle.putString("user_id",user_id);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //finish();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"kong", Toast.LENGTH_SHORT).show();
                    }
                   catch (Exception ex){
                       Toast.makeText(Mainpage.this,ex.toString() , Toast.LENGTH_SHORT).show();
                       Log.e("get error",ex.toString());
                   }
                }
            });
            //Log.e("TAG" ,string);
            //Log.e("TAG" ,String.valueOf(string.length()));
            super.handleMessage(msg);
        }
    };

    //咨询师广告信息
    Handler csadhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            Log.e("ads",string);

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

    /**
     * 这个设置Tab标签本身的布局，需要TextView和ImageView不能重合 s:是文本显示的内容 i:是ImageView的图片位置
     */
    public View composeLayout(String s, int i) {
        // 定义一个LinearLayout布局
        LinearLayout layout = new LinearLayout(this);
        // 设置布局垂直显示
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView iv = new ImageView(this);
        imageList.add(iv);
        iv.setImageResource(i);
        //设置图片布局
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 100);
        lp.setMargins(0, 20, 0, 0);
        layout.addView(iv, lp);
        // 定义TextView
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setText(s);
        tv.setTextColor(getResources().getColor(R.color.tab_text_color));
        tv.setTextSize(15);
        //设置Text布局
        layout.addView(tv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return layout;
    }

    //同步今日要闻
    private void SysnJRYW(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getArticals();
                    Message msg=new Message();
                    msg.obj=result;
                    jrywhandler.sendMessage(msg);
                }
                catch (Exception ex){
                    Toast.makeText(Mainpage.this,"今日要闻获取失败", Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    Handler jrywhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            boolean havedata = false;
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("articles");
                if(jsonObjs.length()!=0) havedata=true;
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    String id = jsonObj.getString("aid");
                    String title = jsonObj.getString("title");
                    String thumbnail = jsonObj.getString("thumbnail");
                    Map<String, Object> map = new HashMap<String, Object>();
                    Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(thumbnail, Base64.DEFAULT)));
                    map.put("jrywlvimg", drawable);               //咨询师头像
                    map.put("jrywlvinfo",title);
                    map.put("jrywnoid", id);              //姓名
                    listItems.add(map);
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            if(havedata){
                mainpage_listview.addHeaderView(mainpage_header_view);
                jrywList = listItems;
                jrywListViewAdapter = new JrywListViewAdapter(hcontext, jrywList);
                mainpage_listview.setAdapter(jrywListViewAdapter);
                mainpage_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try{
                            ListView lv = (ListView)parent;
                            HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
                            if(person!=null){
                                //Toast.makeText(getApplicationContext(), person.get("csabid").toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Mainpage.this, ArticleDetail.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("msgid",person.get("jrywnoid").toString());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                            else
                                Toast.makeText(getApplicationContext(),"kong", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception ex){
                            Toast.makeText(Mainpage.this,ex.toString() , Toast.LENGTH_SHORT).show();
                            Log.e("get error",ex.toString());
                        }
                    }
                });
            }

            super.handleMessage(msg);
        }
    };

    private void SysnAds(){
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getArticalAds();
                    Message msg=new Message();
                    msg.obj=result;
                    mainpage_header.sendMessage(msg);
                }
                catch (Exception ex){

                }

            }
        }).start();
    }


    Handler mainpage_header=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;

            ArrayList<String> adimg = new ArrayList<String>();
            ArrayList<String> adhint = new ArrayList<String>();
            ArrayList<String> adid = new ArrayList<String>();
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("article_ads");
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    String id = jsonObj.getString("aid");
                    String title = jsonObj.getString("title");
                    String cover = jsonObj.getString("cover");
                    adimg.add(cover);
                    adhint.add(title);
                    adid.add(id);
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            slideshow.init(adhint,adimg,adid);
            super.handleMessage(msg);
        }
    };

    /**
     * 检测网络是否连接
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

}
