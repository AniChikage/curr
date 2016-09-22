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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentHostCallback;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hyphenate.chatuidemo.Main.ArticleDetail;
import com.hyphenate.chatuidemo.Main.Category;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {

	protected static final String TAG = "MainActivity";
	// textview for unread message count
	private TextView unreadLabel;
	private String user_id;
	private String firstChecked="0";
	// textview for unread event message
	private TextView unreadAddressLable;
	private TextView paixu;
	private TextView app_username;
	private ImageView dochuzhen;
	private ImageView mp_setting;
	private PopupWindow popupwindow;

	private Button[] mTabs;
	private ContactListFragment contactListFragment;
	private Context hcontext;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	// user logged into another device
	public boolean isConflict = false;
	// user account was removed
	private boolean isCurrentAccountRemoved = false;

	private ListView mainpage_mp_listview;
	private ListView mainpage_cs_listview;
	private ListView mainpage_mine_listview;
	private ListView[] mainpage_listview;

	private SlideShowView slideshow;
	private SlideShowViewCs slideShowViewCs;
	private LinearLayout mainpage_mp_header_ll;
	private LinearLayout mainpage_cs_header_ll;
	private LinearLayout mainpage_mine_header_ll;
	private View mainpage_mp_header_view;
	private View mainpage_cs_header_view;
	private View mainpage_mine_header_view;
	private JrywListViewAdapter jrywListViewAdapter;
	private CsabListViewAdapter csabListViewAdapter;
	private AppointAdapter appointAdapter;
	private TextView anli,kepu,xuzhi;
	private ImageView iv_anli,iv_kepu,iv_xuzhi;

	private List<Map<String, Object>> csabLista,jrywList,minelist;

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	private BroadcastReceiver internalDebugReceiver;
	private ConversationListFragment conversationListFragment;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;

	/**
	 * check if current user account was remove
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
		super.onCreate(savedInstanceState);
		hcontext=this.getApplicationContext();
		
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
		setContentView(R.layout.em_activity_main);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏
		// runtime permission for android 6.0, just require all permissions here for simple
		requestPermissions();


		new Thread(new Runnable() {
			public void run() {
				try {
					ConnNet operaton = new ConnNet();
					String result = operaton.getUser(EMClient.getInstance().getCurrentUser());
					Message msg = new Message();
					msg.obj = result;
					hgetuser.sendMessage(msg);
				} catch (Exception ex) {
					Toast.makeText(MainActivity.this, "咨询师获取失败", Toast.LENGTH_LONG).show();
				}
			}
		}).start();


		//umeng api
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
		//Log.e(TAG,inviteMessgeDao.COLUMN_NAME_FROM);
		/*
		UserDao userDao = new UserDao(this);
		conversationListFragment = new ConversationListFragment();
		contactListFragment = new ContactListFragment();
		SettingsFragment settingFragment = new SettingsFragment();
		fragments = new Fragment[] { conversationListFragment, contactListFragment, settingFragment};

		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
				.add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
				.commit();

		//register broadcast receiver to receive the change of group from DemoHelper
		registerBroadcastReceiver();
		
		
		EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
		//debug purpose only
        registerInternalDebugReceiver();
        */
	}

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

	//first step: get user
	Handler hgetuser = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			try {
				JSONObject jsonObjs = new JSONObject(string).getJSONObject("user");
				user_id = jsonObjs.getString("id");
				Log.e(TAG,user_id);
				try{
					initView();
					initData();
					initFirstVisit();
				}
				catch (Exception ex){
					Toast.makeText(MainActivity.this,"获取失败",Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				System.out.println("获取用户失败");
				e.printStackTrace();
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * init views
	 */
	private void initView() {
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_conversation);
		mTabs[1] = (Button) findViewById(R.id.btn_address_list);
		mTabs[2] = (Button) findViewById(R.id.btn_setting);
		mainpage_mp_listview = (ListView) findViewById(R.id.mainpage_mp_listview);
		mainpage_cs_listview = (ListView) findViewById(R.id.mainpage_cs_listview);
		mainpage_mine_listview = (ListView) findViewById(R.id.mainpage_mine_listview);
		mainpage_listview = new ListView[]{mainpage_mp_listview,mainpage_cs_listview,mainpage_mine_listview};
		// select first tab
		mTabs[0].setSelected(true);
	}

	private void initData(){
		initMainpage();
		initConsellor();
		//initMine();
	}

	private void initFirstVisit(){
		new Thread(new Runnable() {
			public void run() {
				try {
					ConnNet operaton = new ConnNet();
					String result = operaton.checkFirstVisit("1",user_id);
					Message msg = new Message();
					msg.obj = result;
					hCheckFirstVisit.sendMessage(msg);
				} catch (Exception ex) {
					Toast.makeText(MainActivity.this, "咨询师获取失败", Toast.LENGTH_LONG).show();
				}
			}
		}).start();
	}

	Handler hCheckFirstVisit=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;

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

	private void initConsellor(){
		//mainpage_cs_listview = (ListView)findViewById(R.id.mainpage_cs_listview);
		mainpage_cs_header_view = LayoutInflater.from(this).inflate(R.layout.mainpage_cs_header, null);
		SysnConsellors();
		SysnConsellorsAds();
	}

	private void initMine(){
		mainpage_mine_header_view = LayoutInflater.from(this).inflate(R.layout.mainpage_mine_header, null);
		mainpage_mine_header_ll = (LinearLayout)mainpage_mine_header_view.findViewById(R.id.mine_header_ll);
		app_username = (TextView)mainpage_mine_header_view.findViewById(R.id.app_username);
		app_username.setText(EMClient.getInstance().getCurrentUser().toString());
		dochuzhen = (ImageView) mainpage_mine_header_view.findViewById(R.id.dochuzhen);
		if(firstChecked!="0"){
			dochuzhen.setImageResource(R.drawable.quesdone);
		}
		dochuzhen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(firstChecked=="0"){
					Intent intent = new Intent(MainActivity.this,QuestionActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("user_id",user_id);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				else{
					Toast.makeText(MainActivity.this,"您已经完成初诊",Toast.LENGTH_LONG).show();
				}
			}
		});
		mp_setting = (ImageView) mainpage_mine_header_view.findViewById(R.id.mp_setting);
		mp_setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,SettingActivity.class));
				//logout();
				//Toast.makeText(MainActivity.this,"开发中",Toast.LENGTH_LONG).show();
			}
		});

		WindowManager wmmine = this.getWindowManager();
		int allheight = wmmine.getDefaultDisplay().getHeight();
		int upmineh= (int) allheight*795/1905;
		ViewGroup.LayoutParams lpmine = mainpage_mine_header_ll.getLayoutParams();
		lpmine.width=lpmine.MATCH_PARENT;
		lpmine.height=upmineh;
		mainpage_mine_header_ll.setLayoutParams(lpmine);
		SysnMyAppoint();
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
				Log.e("orders len",String.valueOf(jsonObjs.length()));
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
					Drawable drawable = new BitmapDrawable(getBitmapFromByte(Base64.decode(portrait,Base64.DEFAULT)));
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

			mainpage_mine_listview.addHeaderView(mainpage_mine_header_view);
			minelist = listItems;
			appointAdapter = new AppointAdapter(hcontext, minelist);
			mainpage_mine_listview.setAdapter(appointAdapter);
			mainpage_mine_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
					try{
						ListView lv = (ListView)parent;
						HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
						if(person!=null){
							//Toast.makeText(getApplicationContext(),person.get("appoint_oid").toString(), Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(MainActivity.this, AppointDetail.class);
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
						Toast.makeText(MainActivity.this,ex.toString() ,Toast.LENGTH_SHORT).show();
						Log.e("get error",ex.toString());
					}
				}
			});
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
					Toast.makeText(MainActivity.this,"咨询师获取失败",Toast.LENGTH_LONG).show();
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
		Button btton4 = (Button) customView.findViewById(R.id.keyuyue);
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
		btton4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				paixu.setText("可预约");
				if (popupwindow != null&&popupwindow.isShowing()) {
					popupwindow.dismiss();
				}
			}
		});
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
					Toast.makeText(MainActivity.this,"今日要闻获取失败",Toast.LENGTH_LONG).show();
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

	private Bitmap getBitmapFromByte(byte[] temp){
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
			break;
		case R.id.btn_address_list:
			index = 1;
			break;
		case R.id.btn_setting:
			index = 2;
			break;
		}

		if (currentTabIndex != index) {
			mainpage_listview[index].setVisibility(View.VISIBLE);
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
					    Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



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
}
