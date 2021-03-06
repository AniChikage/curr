/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.chatuidemo.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentHostCallback;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.easemob.redpacketui.RedPacketConstant;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.Adapter.AppointAdapter;
import com.hyphenate.chatuidemo.Adapter.CsabListViewAdapter;
import com.hyphenate.chatuidemo.Adapter.JrywListViewAdapter;
import com.hyphenate.chatuidemo.Appointment.AppointDetail;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.Consultant.Csdetail;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.Help.createSDFile;
import com.hyphenate.chatuidemo.Main.ArticleDetail;
import com.hyphenate.chatuidemo.Main.Category;
import com.hyphenate.chatuidemo.Main.MainActivit;
import com.hyphenate.chatuidemo.Main.QuestionActivity;
import com.hyphenate.chatuidemo.Main.SettingActivity;
import com.hyphenate.chatuidemo.Main.SlideShowView;
import com.hyphenate.chatuidemo.Main.SlideShowViewCs;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.db.InviteMessgeDao;
import com.hyphenate.chatuidemo.db.UserDao;
import com.hyphenate.chatuidemo.netapp.ConnNet;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsResultAction;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity{

	//region 变量
	protected static final String TAG = "MainActivity";
	private static Drawable user_drawable;
	// textview for unread message count
	private TextView unreadLabel;
	private static final int REFRESH_COMPLETE = 0X110;
	private static String user_id=null;
	private static String user_id_s;
	private static String isAccept;
	private static String firstChecked="0";
	private createSDFile mycreateSDFile;
	// textview for unread event message
	private TextView unreadAddressLable;
	private TextView paixu;
	private static TextView app_username;
	private static ImageView app_portrait;
	private TextView btn_recmd;
	private TextView btn_default;
	private TextView tologin;
	private String user_email;
	private static ImageView dochuzhen;
	private static ImageView mp_setting;
	private PopupWindow popupwindow;

	private Button[] mTabs;
	private ContactListFragment contactListFragment;
	private Context hcontext;
	private static Context in_hcontext;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	// user logged into another device
	public boolean isConflict = false;
	// user account was removed
	private boolean isCurrentAccountRemoved = false;
	//
	private boolean isMainpageRefreshing = false;
//	private boolean isSysnJRYWOver = false;
//	private boolean isMainpageRefreshing = false;

	private ListView mainpage_mp_listview;
	private ListView mainpage_cs_listview;
	private static ListView mainpage_mine_listview;
	private ListView[] mainpage_listview;

	private SlideShowView slideshow;
	private SlideShowViewCs slideShowViewCs;
	private LinearLayout mainpage_mp_header_ll;
	private LinearLayout mainpage_cs_header_ll;
	private static LinearLayout mainpage_mine_header_ll;
	private static View mainpage_mp_header_view;
	private static View mainpage_cs_header_view;
	private static View mainpage_mine_header_view;
	private JrywListViewAdapter jrywListViewAdapter;
	private CsabListViewAdapter csabListViewAdapter;
	private static AppointAdapter appointAdapter;
	private TextView anli,kepu,xuzhi;
	private ImageView iv_anli,iv_kepu,iv_xuzhi;
	private XProgressDialog xdialog;
	private XProgressDialog sysncs_xpd;
	private static XProgressDialog sysnmine_xpd;

	private List<Map<String, Object>> csabLista,jrywList;
	private static List<Map<String, Object>> minelist;

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private ProgressDialog pd_update=null;
	private boolean isAccountRemovedDialogShow;
	private BroadcastReceiver internalDebugReceiver;
	private ConversationListFragment conversationListFragment;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;
	private String useremail;
	public static MainActivity instance = null;
	private ProgressDialog pd=null;
	private boolean isCsFetched;
	private boolean isMineFetched;
	private boolean isJRYWover;
	private boolean isJRYWADover;
	private boolean isCSover;
	private boolean isCSADover;
	private static SwipeRefreshLayout mSwipeLayout;
	private static boolean isMineFetchingOver = false;
	private SwipeRefreshLayout mainpage_srl;
	private XProgressDialog fetching_cs_list;
	private ImageView cs_paixu;
	//endregion

	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
		hcontext=this.getApplicationContext();
		mycreateSDFile = new createSDFile(hcontext);
		setContentView(R.layout.em_activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
		in_hcontext = hcontext;

		//region 定义下拉菜单
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		//mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.holo_blue_bright,
				R.color.holo_green_light,
				R.color.holo_orange_light,
				R.color.holo_red_light);
		//mSwipeLayout.setProgressBackgroundColorSchemeResource(R.color.holo_green_light);
		mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isMineFetchingOver = true;
				mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
			}
		});
		//endregion

		//region 定义下拉菜单:mainpage
		mainpage_srl = (SwipeRefreshLayout) findViewById(R.id.mainpage_srl);
		//mainpage_srl.setOnRefreshListener(this);
		mainpage_srl.setColorSchemeResources(R.color.holo_blue_bright,
				R.color.holo_green_light,
				R.color.holo_orange_light,
				R.color.holo_red_light);
		//mSwipeLayout.setProgressBackgroundColorSchemeResource(R.color.holo_green_light);
		mainpage_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isMainpageRefreshing = true;
				mainpage_Handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
			}
		});
		//endregion

		pd = new ProgressDialog(MainActivity.this);
		pd_update = new ProgressDialog(MainActivity.this);


		//region 判断是否已经登录,得到useremail
		instance = this;
		if(mycreateSDFile.hasFile("cache")){
			useremail = mycreateSDFile.readSDFile("cache");
			Log.e(TAG,"has cache");
		}
		else{
			useremail = "您还未登录";
			Log.e(TAG,"has not cache");
		}
		Log.e(TAG,useremail);
		//endregion

		//region 垃圾
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		    String packageName = getPackageName();
		    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
		        Intent intent = new Intent();
		        intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
		        intent.setData(Uri.parse("package:" + packageName));
		        startActivity(intent);
		    }
		}
		//make sure activity will not in background if user is logged into another device or removed
		if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
		    DemoHelper.getInstance().logout(false,null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		requestPermissions();
		//endregion

		//region 初始化view和data
		try{
			initView();
			initData();
		}
		catch (Exception ex){
			Log.e("error",ex.toString());
		}
		//endregion

		//region 获取用户
		if(mycreateSDFile.hasFile("cache")) {
			//pd_update.setMessage("更新数据中......");
			//pd_update.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						ConnNet operaton = new ConnNet();
						String result = operaton.getUser(useremail);
						Message msg = new Message();
						msg.obj = result;
						hgetuser.sendMessage(msg);
					} catch (Exception ex) {
						Toast.makeText(MainActivity.this, "咨询师获取失败", Toast.LENGTH_SHORT).show();
					}
				}
			}).start();
			Log.e("hetuser","getuser");
		}
		//endregion

		//region 垃圾
		MobclickAgent.updateOnlineConfig(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);

		if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}

		//获取
		Log.e(TAG,EMClient.getInstance().getCurrentUser());

		inviteMessgeDao = new InviteMessgeDao(this);
		//endregion
	}

	//region 垃圾
	@TargetApi(23)
	private void requestPermissions() {
		PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
			@Override
			public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDenied(String permission) {
				//Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
			}
		});
	}
	//endregion

	//region handler获取用户
	//first step: get user
	Handler hgetuser = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			Log.e(".......",string);
			try {
				JSONObject jsonObjs = new JSONObject(string).getJSONObject("user");
				user_id = jsonObjs.getString("id");
				user_id_s = user_id;
				try{
					String portrait = jsonObjs.getString("portrait");
					user_drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait,Base64.DEFAULT)));
				}
				catch (Exception ex){
					Log.e("noUserPortrait",ex.toString());
				}
				Log.e(TAG,user_id);
				try{
					//initView();
					//initData();
					initFirstVisit();
				}
				catch (Exception ex){
					Toast.makeText(MainActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				System.out.println("获取用户失败");
				e.printStackTrace();
			}
			super.handleMessage(msg);
		}
	};
	//endregion

	//region 初始化view
	private void initView() {
		isCsFetched = false;
		isMineFetched = false;
		isJRYWover = false;
		isJRYWADover = false;
		isCSover = false;
		isCSADover = false;
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
		tologin = (TextView)findViewById(R.id.tologin);
		tologin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,LoginActivity.class));
			}
		});
		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_conversation);
		mTabs[1] = (Button) findViewById(R.id.btn_address_list);
		mTabs[2] = (Button) findViewById(R.id.btn_setting);
		mainpage_mp_listview = (ListView) findViewById(R.id.mainpage_mp_listview);
		mainpage_cs_listview = (ListView) findViewById(R.id.mainpage_cs_listview);
//		mainpage_cs_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//			}
//
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//								 int visibleItemCount, int totalItemCount) {
//				boolean enable = false;
//				if(mainpage_cs_listview != null && mainpage_cs_listview.getChildCount() > 0){
//					// check if the first item of the list is visible
//					boolean firstItemVisible = mainpage_cs_listview.getFirstVisiblePosition() == 0;
//					// check if the top of the first item is visible
//					boolean topOfFirstItemVisible = mainpage_cs_listview.getChildAt(0).getTop() == 0;
//					// enabling or disabling the refresh layout
//					enable = firstItemVisible && topOfFirstItemVisible;
//				}
//				mSwipeLayout.setEnabled(enable);
//			}});
		mainpage_mine_listview = (ListView) findViewById(R.id.mainpage_mine_listview);
		mainpage_listview = new ListView[]{mainpage_mp_listview,mainpage_cs_listview,mainpage_mine_listview};
		// select first tab
		mTabs[0].setSelected(true);
	}
	//endregion

	private void initData(){
		xdialog = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_HEART_PROGRESS);
		xdialog.show();
		xdialog.setCanceledOnTouchOutside(false);

		initMainpage();
		//initConsellor();
		//initMine();
	}

	private static void initFirstVisit(){
		new Thread(new Runnable() {
			public void run() {
				try {
					ConnNet operaton = new ConnNet();
					String result = operaton.checkFirstVisit("1",user_id);
					Message msg = new Message();
					msg.obj = result;
					hCheckFirstVisit.sendMessage(msg);
				} catch (Exception ex) {
					Toast.makeText(in_hcontext, "咨询师获取失败", Toast.LENGTH_SHORT).show();
				}
			}
		}).start();
	}

	static Handler hCheckFirstVisit=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			Log.e("hCheckFirstVisit",string);
			try {
				firstChecked = new JSONObject(string).getString("1st");
				Log.e("firstchecked",firstChecked);
				initMine();
			} catch (JSONException e) {
				System.out.println("Jsons parse error !");
				e.printStackTrace();
			}
			super.handleMessage(msg);
		}
	};


	//region initMainPage
	private void initMainpage(){
		//mainpage begin
		mainpage_mp_header_view = LayoutInflater.from(this).inflate(R.layout.mainpage_header, null);
		mainpage_mp_header_ll = (LinearLayout) mainpage_mp_header_view.findViewById(R.id.header_ll);
		WindowManager wm1 = this.getWindowManager();
		int height1 = wm1.getDefaultDisplay().getHeight();
		//int a1= (int) height1*936/1679;
		int a1= (int) height1*880/1679;
		ViewGroup.LayoutParams lp1 =mainpage_mp_header_ll.getLayoutParams();
		lp1.width=lp1.MATCH_PARENT;
		lp1.height=a1;
		mainpage_mp_header_ll.setLayoutParams(lp1);

		SysnJRYW();  //同步今日要闻
		slideshow= (SlideShowView)mainpage_mp_header_view.findViewById(R.id.slideshow);
		anli = (TextView)mainpage_mp_header_view.findViewById(R.id.anli);
		kepu = (TextView)mainpage_mp_header_view.findViewById(R.id.kepu);
		xuzhi = (TextView)mainpage_mp_header_view.findViewById(R.id.xuzhi);
		iv_anli = (ImageView)mainpage_mp_header_view.findViewById(R.id.iv_anli);
		iv_kepu = (ImageView)mainpage_mp_header_view.findViewById(R.id.iv_kepu);
		iv_xuzhi = (ImageView)mainpage_mp_header_view.findViewById(R.id.iv_xuzhi);
		anli.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Category.class);
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
				Intent intent = new Intent(MainActivity.this, Category.class);
				Bundle bundle = new Bundle();
				bundle.putString("category_name","案例");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		kepu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Category.class);
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
				Intent intent = new Intent(MainActivity.this, Category.class);
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
				Intent intent = new Intent(MainActivity.this, Category.class);
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
				Intent intent = new Intent(MainActivity.this, Category.class);
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
	}
	//endregion

	//region initConsellor
	private void initConsellor(){
		sysncs_xpd = new XProgressDialog(this, "正在加载..", XProgressDialog.THEME_HEART_PROGRESS);
		sysncs_xpd.show();
		sysncs_xpd.setCanceledOnTouchOutside(false);
		//mainpage_cs_listview = (ListView)findViewById(R.id.mainpage_cs_listview);
		mainpage_cs_header_view = LayoutInflater.from(this).inflate(R.layout.mainpage_cs_header, null);
		SysnConsellors();
		SysnConsellorsAds();
	}
	//endregion

	//region initMine
	private static void initMine(){
		mainpage_mine_header_view = LayoutInflater.from(in_hcontext).inflate(R.layout.mainpage_mine_header, null);
		mainpage_mine_header_ll = (LinearLayout)mainpage_mine_header_view.findViewById(R.id.mine_header_ll);
		app_username = (TextView)mainpage_mine_header_view.findViewById(R.id.app_username);
		app_portrait = (ImageView) mainpage_mine_header_view.findViewById(R.id.portrait);
		app_username.setText(EMClient.getInstance().getCurrentUser().toString());
		try{
			app_portrait.setImageDrawable(user_drawable);
		}
		catch (Exception ex){
			Log.e("noUserPortrait",ex.toString());
		}
		Log.e("","lllllllllllll");
		dochuzhen = (ImageView) mainpage_mine_header_view.findViewById(R.id.dochuzhen);
		if(firstChecked.equals("0")){
			dochuzhen.setImageResource(R.drawable.quesundo);
		}
		else{
			dochuzhen.setImageResource(R.drawable.quesdone);
		}
		dochuzhen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(firstChecked.equals("0")){
					Intent intent = new Intent(in_hcontext,QuestionActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle bundle = new Bundle();
					bundle.putString("user_id",user_id);
					intent.putExtras(bundle);
					mainpage_mine_listview.removeHeaderView(mainpage_mine_header_view);
					in_hcontext.startActivity(intent);
				}
				else{
					Toast.makeText(in_hcontext,"您已经完成初诊",Toast.LENGTH_SHORT).show();
				}
			}
		});
		mp_setting = (ImageView) mainpage_mine_header_view.findViewById(R.id.mp_setting);
		mp_setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//System.exit(0);
				Intent intent = new Intent(in_hcontext,Setting.class);
				//Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//intent.setClass(in_hcontext,Setting.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
				//startActivity(intent);
				in_hcontext.startActivity(intent);
				//logout();
				//Toast.makeText(MainActivity.this,"开发中",Toast.LENGTH_SHORT).show();
			}
		});

		DisplayMetrics dm = new DisplayMetrics();
		dm = in_hcontext.getApplicationContext().getResources().getDisplayMetrics();
		//WindowManager wmmine = in_hcontext.getApplicationContext().getResources().getWindowManager();
		//int allheight = wmmine.getDefaultDisplay().getHeight();
		int allheight = dm.heightPixels;
		int upmineh= (int) allheight*795/1905;
		ViewGroup.LayoutParams lpmine = mainpage_mine_header_ll.getLayoutParams();
		lpmine.width=lpmine.MATCH_PARENT;
		lpmine.height=upmineh;
		mainpage_mine_header_ll.setLayoutParams(lpmine);
		SysnMyAppoint();
	}
	//endregion

	private static void SysnMyAppoint(){
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

	static Handler myAppointHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			try {
				JSONArray jsonObjs = new JSONObject(string).getJSONArray("orders");
				Log.e("orders len",String.valueOf(jsonObjs.length()));
				for(int i = 0; i < jsonObjs.length() ; i++){
					JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
					String sid = "";
					try{
						sid = jsonObj.getString("sid");
					}
					catch (Exception ex){
					}
					String oid = jsonObj.getString("oid");
					String paid = jsonObj.getString("paid");

					try{
						isAccept = jsonObj.getString("refuse");
					}
					catch (Exception ex){
						isAccept = "notSet";
					}
					String delivery = "0";
					try{
						delivery = jsonObj.getString("delivery");
					}
					catch (Exception ex){
						delivery = "0";
					}
					String evau = "0";
					try{
						evau = jsonObj.getString("evau");
					}
					catch (Exception ex){
						evau = "0";
					}
					String starttime = "0";
					try{
						starttime = jsonObj.getString("starttime");
					}
					catch (Exception ex){
						starttime = "0";
					}
					String str_schedule = "0";
//					try{
					str_schedule = jsonObj.getString("schedule");
//					}
//					catch (Exception ex){
//						str_schedule = "0";
//					}
					JSONObject jsonObjsc = new JSONObject(str_schedule).getJSONObject("consellor");
					String portrait = jsonObjsc.getString("portrait");
					String consellor_name = jsonObjsc.getString("realname");
					Drawable drawable = new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait,Base64.DEFAULT)));
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("appoint_img", drawable);
					if(isAccept.equals("notSet")){
						map.put("appoint_hint", "未审核");
					}
					else if(isAccept.equals("0")){
						if(paid.equals("1")){
							map.put("appoint_hint", "已支付");
							if(delivery.equals("1")){
								map.put("appoint_hint", "已完成");
							}
							if(evau.equals("1")){
								map.put("appoint_hint", "已评价");
							}
						}
						else{
							map.put("appoint_hint", "未支付");
						}

					}
					else{
						map.put("appoint_hint", "未通过");
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

			Log.e("lsv",String.valueOf(mainpage_mine_listview.getChildCount()));
			mainpage_mine_listview.addHeaderView(mainpage_mine_header_view);
			minelist = listItems;
			appointAdapter = new AppointAdapter(in_hcontext, minelist);
			mainpage_mine_listview.setAdapter(appointAdapter);
			mainpage_mine_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
					try{
						ListView lv = (ListView)parent;
						HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
						if(person!=null){
							//Toast.makeText(getApplicationContext(),person.get("appoint_oid").toString(), Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(in_hcontext, AppointDetail.class);
							Bundle bundle = new Bundle();
							bundle.putString("appoint_oid",person.get("appoint_oid").toString());
							intent.putExtras(bundle);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							in_hcontext.startActivity(intent);
						}
						else{

						}
						//Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT).show();
					}
					catch (Exception ex){
						Toast.makeText(in_hcontext,ex.toString() ,Toast.LENGTH_SHORT).show();
						Log.e("get error",ex.toString());
					}
				}
			});
			try{
				if(isMineFetchingOver){
					mSwipeLayout.setRefreshing(false);
					isMineFetchingOver = false;
				}
			}
			catch (Exception ex){
				Log.e("mineFetching",ex.toString());
			}
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
					Toast.makeText(MainActivity.this,"咨询师获取失败",Toast.LENGTH_SHORT).show();
				}

			}
		}).start();
	}

	//咨询师信息
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			Log.e(TAG,string);
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
					Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(asdf,Base64.DEFAULT)));
					//Drawable drawable =new BitmapDrawable(getBitmapFromByte(jsonObj.getString("portrait").getBytes()));
					map.put("csabimg", drawable);               //咨询师头像
					map.put("csabid",String.valueOf(id));
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
			mainpage_cs_listview.addHeaderView(mainpage_cs_header_view);
			csabLista = listItems;
			csabListViewAdapter = new CsabListViewAdapter(hcontext, csabLista);
			mainpage_cs_listview.setAdapter(csabListViewAdapter);
			mainpage_cs_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
					try{
						ListView lv = (ListView)parent;
						HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
						if(person!=null){
							//Toast.makeText(getApplicationContext(), person.get("csabid").toString(), Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(MainActivity.this, Csdetail.class);
							Bundle bundle = new Bundle();
							bundle.putString("csnoid",person.get("csabid").toString());
							bundle.putString("user_id",user_id);
							Log.e("put csdetail uid","null");
							intent.putExtras(bundle);
							startActivity(intent);
							//finish();
						}
						else
							Toast.makeText(getApplicationContext(),"获取咨询师列表失败", Toast.LENGTH_SHORT).show();
					}
					catch (Exception ex){
						Toast.makeText(MainActivity.this,ex.toString() ,Toast.LENGTH_SHORT).show();
						Log.e("get error",ex.toString());
					}
				}
			});
			//Log.e("TAG" ,string);
			//Log.e("TAG" ,String.valueOf(string.length()));
			try{
				isCSover = true;
				if(isCSover && isCSADover){
					sysncs_xpd.dismiss();
				}
			}
			catch (Exception ex){
				Log.e("同步咨询师列表出错",ex.toString());
			}
			super.handleMessage(msg);
		}
	};

	private void SysnConsellorsAds(){
		mainpage_cs_header_ll = (LinearLayout) mainpage_cs_header_view.findViewById(R.id.mpcs_header_ll);
		paixu = (TextView) mainpage_cs_header_view.findViewById(R.id.paixu);
		btn_recmd = (TextView) mainpage_cs_header_view.findViewById(R.id.btn_recmd);
		btn_default = (TextView) mainpage_cs_header_view.findViewById(R.id.btn_default);
		cs_paixu = (ImageView)mainpage_cs_header_view.findViewById(R.id.cs_paixu);
		paixu.setOnClickListener(new click_paixu());
		cs_paixu.setOnClickListener(new click_paixu());
		btn_recmd.setOnClickListener(new click_recmd());
		btn_default.setOnClickListener(new click_default());
		WindowManager wm1 = this.getWindowManager();
		int height1 = wm1.getDefaultDisplay().getHeight();
		int a1= (int) height1*552/1679;
		ViewGroup.LayoutParams lp1 = mainpage_cs_header_ll.getLayoutParams();
		lp1.width=lp1.MATCH_PARENT;
		lp1.height=a1;
		mainpage_cs_header_ll.setLayoutParams(lp1);
		slideShowViewCs = (SlideShowViewCs)mainpage_cs_header_view.findViewById(R.id.mpcsslideshow);
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
			slideShowViewCs.init(adhint,adimg,adid,user_id);
			slideShowViewCs.setOnTouchListener(new View.OnTouchListener() {
				             @Override
				             public boolean onTouch(View v, MotionEvent event) {
					                 switch (event.getAction()) {
						                     case MotionEvent.ACTION_MOVE:
							                         mSwipeLayout.setEnabled(false);
							                         break;
						                     case MotionEvent.ACTION_UP:
							                     case MotionEvent.ACTION_CANCEL:
													 mSwipeLayout.setEnabled(true);
							                         break;
						                 }
					                 return false;
					             }
				         });
			try{
				isCSADover = true;
				if(isCSover && isCSADover){
					sysncs_xpd.dismiss();
				}
			}
			catch (Exception ex){
				Log.e("同步咨询师header出错",ex.toString());
			}
			super.handleMessage(msg);
		}
	};

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
				case R.id.cs_paixu:
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

	class click_recmd implements View.OnClickListener{
		@Override
		public void onClick(View v) {
//			pd.setMessage("获取中……");
//			pd.show();
			try{
				fetching_cs_list = new XProgressDialog(MainActivity.this, "正在加载..",XProgressDialog.THEME_HEART_PROGRESS);
				fetching_cs_list.show();
				fetching_cs_list.setCanceledOnTouchOutside(false);
			}
			catch (Exception ex){
			}
			new Thread(new Runnable(){
				@Override
				public void run() {
					try{
						ConnNet operaton=new ConnNet();
						String result=operaton.getConsellorRecmd();
						Message msg=new Message();
						msg.obj=result;
						recmdhandler.sendMessage(msg);
					}
					catch (Exception ex){

					}
				}
			}).start();
		}
	}

	class click_default implements View.OnClickListener{
		@Override
		public void onClick(View v) {
//			pd.setMessage("获取中……");
//			pd.show();
			try{
				fetching_cs_list = new XProgressDialog(MainActivity.this, "正在加载..",XProgressDialog.THEME_HEART_PROGRESS);
				fetching_cs_list.show();
				fetching_cs_list.setCanceledOnTouchOutside(false);
			}
			catch (Exception ex){
			}
			new Thread(new Runnable(){
				@Override
				public void run() {
					try{
						ConnNet operaton=new ConnNet();
						String result=operaton.getConsellors();
						Message msg=new Message();
						msg.obj=result;
						recmdhandler.sendMessage(msg);
					}
					catch (Exception ex){

					}
				}
			}).start();
		}
	}

	//咨询师信息
	Handler recmdhandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			Log.e(TAG+"recmd",string);
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
					Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(asdf,Base64.DEFAULT)));
					//Drawable drawable =new BitmapDrawable(getBitmapFromByte(jsonObj.getString("portrait").getBytes()));
					map.put("csabimg", drawable);               //咨询师头像
					map.put("csabid",String.valueOf(id));
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

			//mainpage_cs_listview.addHeaderView(mainpage_cs_header_view);
			csabLista = listItems;
			csabListViewAdapter = new CsabListViewAdapter(hcontext, csabLista);
			mainpage_cs_listview.setAdapter(csabListViewAdapter);
			mainpage_cs_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
					try{
						ListView lv = (ListView)parent;
						HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
						if(person!=null){
							//Toast.makeText(getApplicationContext(), person.get("csabid").toString(), Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(MainActivity.this, Csdetail.class);
							Bundle bundle = new Bundle();
							bundle.putString("csnoid",person.get("csabid").toString());
							bundle.putString("user_id",user_id);
							intent.putExtras(bundle);
							startActivity(intent);
							//finish();
						}
						else
							Toast.makeText(getApplicationContext(),"获取咨询师列表失败", Toast.LENGTH_SHORT).show();
					}
					catch (Exception ex){
						Toast.makeText(MainActivity.this,ex.toString() ,Toast.LENGTH_SHORT).show();
						Log.e("get error",ex.toString());
					}
				}
			});
			//Log.e("TAG" ,string);
			//Log.e("TAG" ,String.valueOf(string.length()));
			try{
				fetching_cs_list.dismiss();
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
			super.handleMessage(msg);
		}
	};

	public void initmPopupWindowView() {

		// // 获取自定义布局文件pop.xml的视图
		View customView = getLayoutInflater().inflate(R.layout.popview_item,
				null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;     // 屏幕宽度（像素）
		popupwindow = new PopupWindow(customView, width/3,width*2/9 );
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
//				pd.setMessage("获取中……");
//				pd.show();
				try{
					fetching_cs_list = new XProgressDialog(MainActivity.this, "正在加载..",XProgressDialog.THEME_HEART_PROGRESS);
					fetching_cs_list.show();
					fetching_cs_list.setCanceledOnTouchOutside(false);
				}
				catch (Exception ex){
				}
				new Thread(new Runnable() {
					public void run() {
						try {
							ConnNet operaton = new ConnNet();
							String result = operaton.getConsellorGoodat();
							Message msg = new Message();
							msg.obj = result;
							recmdhandler.sendMessage(msg);
						} catch (Exception ex) {
							Toast.makeText(in_hcontext, "咨询师获取失败", Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
				if (popupwindow != null&&popupwindow.isShowing()) {
					popupwindow.dismiss();
				}
			}
		});
		btton3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				paixu.setText("价格");
//				pd.setMessage("获取中……");
//				pd.show();
				try{
					fetching_cs_list = new XProgressDialog(MainActivity.this, "正在加载..",XProgressDialog.THEME_HEART_PROGRESS);
					fetching_cs_list.show();
					fetching_cs_list.setCanceledOnTouchOutside(false);
				}
				catch (Exception ex){
				}
				new Thread(new Runnable() {
					public void run() {
						try {
							ConnNet operaton = new ConnNet();
							String result = operaton.getConsellorPrice();
							Message msg = new Message();
							msg.obj = result;
							recmdhandler.sendMessage(msg);
						} catch (Exception ex) {
							Toast.makeText(in_hcontext, "咨询师获取失败", Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
				if (popupwindow != null&&popupwindow.isShowing()) {
					popupwindow.dismiss();
				}
			}
		});
		/*
		btton4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				paixu.setText("可预约");
				if (popupwindow != null&&popupwindow.isShowing()) {
					popupwindow.dismiss();
				}
			}
		});
		*/
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
					Toast.makeText(MainActivity.this,"今日要闻获取失败",Toast.LENGTH_SHORT).show();
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
					Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(thumbnail,Base64.DEFAULT)));
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
				mainpage_mp_listview.addHeaderView(mainpage_mp_header_view);
				jrywList = listItems;
				jrywListViewAdapter = new JrywListViewAdapter(hcontext, jrywList);
				mainpage_mp_listview.setAdapter(jrywListViewAdapter);
				mainpage_mp_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
						try{
							ListView lv = (ListView)parent;
							HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
							if(person!=null){
								//Toast.makeText(getApplicationContext(), person.get("csabid").toString(), Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(MainActivity.this, ArticleDetail.class);
								Bundle bundle = new Bundle();
								bundle.putString("msgid",person.get("jrywnoid").toString());
								intent.putExtras(bundle);
								startActivity(intent);
								//finish();
							}
							else
								Toast.makeText(getApplicationContext(),"kong", Toast.LENGTH_SHORT).show();
						}
						catch (Exception ex){
							Toast.makeText(MainActivity.this,ex.toString() ,Toast.LENGTH_SHORT).show();
							Log.e("get error",ex.toString());
						}
					}
				});
			}
			try{
				isJRYWover = true;
				if(isJRYWADover && isJRYWover){
					xdialog.dismiss();
					if(isMainpageRefreshing){
						mainpage_srl.setRefreshing(false);
						isMainpageRefreshing = false;
					}
				}
			}
			catch (Exception ex){
				Log.e("xlog",ex.toString());
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
			try{
				isJRYWADover = true;
				if(isJRYWADover && isJRYWover){
					xdialog.dismiss();
					if(isMainpageRefreshing){
						mainpage_srl.setRefreshing(false);
						isMainpageRefreshing = false;
					}
				}
			}
			catch (Exception ex){
				Log.e("xlog",ex.toString());
			}
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

	/**
	 * on tab clicked
	 *
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_conversation:
			index = 0;
			mSwipeLayout.setVisibility(View.INVISIBLE);
			mainpage_srl.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_address_list:
			index = 1;
			mSwipeLayout.setVisibility(View.INVISIBLE);
			mainpage_srl.setVisibility(View.INVISIBLE);
			if(!isCsFetched){
				initConsellor();
				isCsFetched = true;
			}
			break;
		case R.id.btn_setting:
			index = 2;
			mSwipeLayout.setVisibility(View.VISIBLE);
			mainpage_srl.setVisibility(View.INVISIBLE);
			break;
		}

		if (currentTabIndex != index) {
			mainpage_listview[index].setVisibility(View.VISIBLE);
			if(index == 2 && !mycreateSDFile.hasFile("cache")){
				tologin.setVisibility(View.VISIBLE);
			}
			else{
				tologin.setVisibility(View.INVISIBLE);
			}
			mainpage_listview[currentTabIndex].setVisibility(View.INVISIBLE);
		}
		mTabs[currentTabIndex].setSelected(false);
		// set current tab selected
		mTabs[index].setSelected(true);
		currentTabIndex = index;

	}

	EMMessageListener messageListener = new EMMessageListener() {

		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			// notify new message
		    for (EMMessage message : messages) {
		        DemoHelper.getInstance().getNotifier().onNewMsg(message);
		    }
			refreshUIWithMessage();
		}

		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
			//red packet code : 处理红包回执透传消息
			for (EMMessage message : messages) {
				EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
				final String action = cmdMsgBody.action();//获取自定义action
				if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
					RedPacketUtil.receiveRedPacketAckMessage(message);
				}
			}
			//end of red packet code
			refreshUIWithMessage();
		}

		@Override
		public void onMessageReadAckReceived(List<EMMessage> messages) {
		}

		@Override
		public void onMessageDeliveryAckReceived(List<EMMessage> message) {
		}

		@Override
		public void onMessageChanged(EMMessage message, Object change) {}
	};

	private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// refresh unread count
				//updateUnreadLabel();
				if (currentTabIndex == 0) {
					// refresh conversation list
					if (conversationListFragment != null) {
						conversationListFragment.refresh();
					}
				}
			}
		});
	}

	@Override
	public void back(View view) {
		super.back(view);
	}

	private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
		intentFilter.addAction(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //updateUnreadLabel();
                //updateUnreadAddressLable();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                } else if (currentTabIndex == 1) {
                    if(contactListFragment != null) {
                        contactListFragment.refresh();
                    }
                }
                String action = intent.getAction();
                if(action.equals(Constant.ACTION_GROUP_CHANAGED)){
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
				//red packet code : 处理红包回执透传消息
				if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)){
					if (conversationListFragment != null){
						conversationListFragment.refresh();
					}
				}
				//end of red packet code
			}
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

	public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {}
        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
					if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
							username.equals(ChatActivity.activityInstance.toChatUsername)) {
					    String st10 = getResources().getString(R.string.have_you_removed);
					    Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_SHORT)
					    .show();
					    ChatActivity.activityInstance.finish();
					}
                }
            });
        }
        @Override
        public void onContactInvited(String username, String reason) {}
        @Override
        public void onContactAgreed(String username) {}
        @Override
        public void onContactRefused(String username) {}
	}

	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
		//unregisterBroadcastReceiver();

		try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }

	}

	/**
	 * update unread message count

	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}
	*/
	/**
	 * update the total unread count 

	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}
	*/
	/**
	 * get unread event notification count, including application, accepted, etc
	 *
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
		return unreadAddressCountTotal;
	}

	/**
	 * get unread message count
	 *
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
		for(EMConversation conversation:EMClient.getInstance().chatManager().getAllConversations().values()){
			if(conversation.getType() == EMConversationType.ChatRoom)
			chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}

	private InviteMessgeDao inviteMessgeDao;

	@Override
	protected void onResume() {
		super.onResume();

		if (!isConflict && !isCurrentAccountRemoved) {
			//updateUnreadLabel();
			//updateUnreadAddressLable();
		}

		// unregister this event listener when this activity enters the
		// background
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.pushActivity(this);

		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

	@Override
	protected void onStop() {
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
*/


	/**
	 * show the dialog when user logged into another device
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						finish();
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * show the dialog if user account is removed
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						finish();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
			}

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	/**
	 * debug purpose only, you can ignore this
	 */
	private void registerInternalDebugReceiver() {
	    internalDebugReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(false,new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                finish();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {}

                    @Override
                    public void onError(int code, String message) {}
                });
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
	}

	void logout() {
		String st = getResources().getString(R.string.Are_logged_out);
		DemoHelper.getInstance().logout(false,new EMCallBack() {

			@Override
			public void onSuccess() {
				startActivity(new Intent(MainActivity.this, LoginActivity.class));
				finish();
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {

			}
		});
	}

	public static void testPrint(){
		Log.e("callback","yes");
	}

	public static void freshMine(){
		Log.e("callback","yes");
		initFirstVisit();
	}

	public static void initFirstVisitFresh(){
		new Thread(new Runnable() {
			public void run() {
				try {
					ConnNet operaton = new ConnNet();
					String result = operaton.checkFirstVisit("1",user_id_s);
					Message msg = new Message();
					msg.obj = result;
					hCheckFirstVisitFresh.sendMessage(msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	static String firstChecked1;
	static Handler hCheckFirstVisitFresh=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			Log.e("hCheckFirstVisitFresh",string);
			try {
				firstChecked1 = new JSONObject(string).getString("1st");
				Log.e("firstcheckedFresh",firstChecked1);
				//if(!firstChecked1.equals("0")){
					freshMinea();
				//}

			} catch (JSONException e) {
				System.out.println("Jsons parse error !");
				e.printStackTrace();
			}
			super.handleMessage(msg);
		}
	};

	static View mainpage_mine_header_view1;
	static LinearLayout mainpage_mine_header_ll1;
	static ImageView dochuzhen1;
	public static void freshMinea(){
		mainpage_mine_header_view1 = LayoutInflater.from(in_hcontext).inflate(R.layout.mainpage_mine_header, null);
		mainpage_mine_header_ll1 = (LinearLayout)mainpage_mine_header_view1.findViewById(R.id.mine_header_ll);
		dochuzhen1 = (ImageView) mainpage_mine_header_view1.findViewById(R.id.dochuzhen);
		if(firstChecked1.equals("0")){
			dochuzhen1.setImageResource(R.drawable.quesundo);
		}
		else{
			dochuzhen1.setImageResource(R.drawable.quesdone);
		}
		dochuzhen1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(firstChecked1.equals("0")){
					Intent intent = new Intent(in_hcontext,QuestionActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("user_id",user_id_s);
					intent.putExtras(bundle);
					in_hcontext.startActivity(intent);
				}
				else{
					Toast.makeText(in_hcontext,"您已经完成初诊",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public static void finishThis(){
		Log.e(TAG,"mainay end");
		MainActivity.instance.finish();
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			exitBy2Click(); //调用双击退出函数
		}
		return false;
	}
	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}

	//region “我”的信息下拉刷新
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
				case REFRESH_COMPLETE:
//					mDatas.addAll(Arrays.asList("Lucene", "Canvas", "Bitmap"));
//					mAdapter.notifyDataSetChanged();
					try{
						mainpage_mine_listview.removeHeaderView(mainpage_mine_header_view);
						mainpage_mine_listview.setAdapter(null);
						initMine();
					}
					catch (Exception ex){
						Log.e("下拉刷新",ex.toString());
					}
					//mSwipeLayout.setRefreshing(false);
					break;
			}
		};
	};

	//下拉刷新
	public void onRefresh()
	{
		// Log.e("xxx", Thread.currentThread().getName());
		// UI Thread
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
	}

	//endregion

	//region “主页(mainpage)”的信息下拉刷新
	private Handler mainpage_Handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
				case REFRESH_COMPLETE:
//					mDatas.addAll(Arrays.asList("Lucene", "Canvas", "Bitmap"));
//					mAdapter.notifyDataSetChanged();
					try{
						mainpage_mp_listview.removeHeaderView(mainpage_mp_header_view);
						mainpage_mp_listview.setAdapter(null);
						initMainpage();
					}
					catch (Exception ex){
						Log.e("mainpage下拉刷新",ex.toString());
					}
					break;
			}
		};
	};

	//下拉刷新
//	public void onRefresh()
//	{
//		// Log.e("xxx", Thread.currentThread().getName());
//		// UI Thread
//		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
//	}

	//endregion

}
