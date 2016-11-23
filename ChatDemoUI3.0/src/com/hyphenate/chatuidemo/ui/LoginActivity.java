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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.Consellor.ConsellorPage;
import com.hyphenate.chatuidemo.DemoApplication;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.Help.createSDFile;
import com.hyphenate.chatuidemo.Main.Mainpage;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.netapp.ConnNet;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Login screen
 * 
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;

	private TextView log_scan;

	private createSDFile mycreateSDFile;

	private RadioButton user_role;
	private RadioButton consellor_role;

	private boolean progressShow;
	private boolean autoLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// enter the main activity if already logged in
		/*
		if (DemoHelper.getInstance().isLoggedIn()) {
			autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			return;
		}
		*/
		setContentView(R.layout.em_activity_login);

		mycreateSDFile = new createSDFile(getBaseContext());

		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		user_role = (RadioButton) findViewById(R.id.user_role);
		consellor_role = (RadioButton) findViewById(R.id.consellor_role);
		log_scan = (TextView)findViewById(R.id.log_scan);
		log_scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				finish();
			}
		});

		// if user changed, clear the password
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		if (DemoHelper.getInstance().getCurrentUsernName() != null) {
			usernameEditText.setText(DemoHelper.getInstance().getCurrentUsernName());
		}
	}

	/**
	 * login
	 * 
	 * @param view
	 */
	public void login(View view) {
		if(!user_role.isChecked()&&!consellor_role.isChecked()){
			Toast.makeText(this,"请先选择登陆角色！",Toast.LENGTH_LONG).show();
		}
		else{
			if (!EaseCommonUtils.isNetWorkConnected(this)) {
				Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
				return;
			}
			String currentUsername = usernameEditText.getText().toString().trim();
			String currentPassword = passwordEditText.getText().toString().trim();

			if (TextUtils.isEmpty(currentUsername)) {
				Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(currentPassword)) {
				Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
				return;
			}

			progressShow = true;
			final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					Log.d(TAG, "EMClient.getInstance().onCancel");
					progressShow = false;
				}
			});
			pd.setMessage(getString(R.string.Is_landing));
			pd.show();

			// After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
			// close it before login to make sure DemoDB not overlap
			DemoDBManager.getInstance().closeDB();

			// reset current user name before login
			DemoHelper.getInstance().setCurrentUserName(currentUsername);

			final long start = System.currentTimeMillis();
			// call login method
			Log.d(TAG, "EMClient.getInstance().login");
			EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

				@Override
				public void onSuccess() {
					Log.d(TAG, "login: onSuccess");


					// ** manually load all local groups and conversation
					EMClient.getInstance().groupManager().loadAllGroups();
					EMClient.getInstance().chatManager().loadAllConversations();

					// update current user's display name for APNs
					boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
							DemoApplication.currentUserNick.trim());
					if (!updatenick) {
						Log.e("LoginActivity", "update current user nick fail");
					}

					if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
						pd.dismiss();
					}
					// get user's info (this should be get from App's server or 3rd party service)
					DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

					if(user_role.isChecked()){
						try{
							mycreateSDFile.deleteSDFile("cache");
							mycreateSDFile.deleteSDFile("cachetype");
							mycreateSDFile.createSDFile("cache");
							mycreateSDFile.createSDFile("cachetype");
							mycreateSDFile.writeSDFile(usernameEditText.getText().toString(),"cache");
							mycreateSDFile.writeSDFile("user","cachetype");
						}
						catch (Exception ex){
							ex.printStackTrace();
						}

						new Thread(new Runnable() {
							public void run() {
								try{
									ConnNet operaton=new ConnNet();
									//String result=operaton.doLogin(usernameEditText.getText().toString(),passwordEditText.getText().toString());
									String resul=operaton.getConn("http://www.clr-vision.com:18080/Therapista/user/login",usernameEditText.getText().toString(),passwordEditText.getText().toString(),"111");
									Message msg=new Message();
									msg.obj=resul;
									handler.sendMessage(msg);
								}
								catch (Exception ex){
									Toast.makeText(LoginActivity.this,"用户名或者密码不能为空",Toast.LENGTH_LONG).show();
								}

							}
						}).start();
					}
					else if(consellor_role.isChecked()){
						try{
							mycreateSDFile.deleteSDFile("cache");
							mycreateSDFile.deleteSDFile("cachetype");
							mycreateSDFile.createSDFile("cache");
							mycreateSDFile.createSDFile("cachetype");
							mycreateSDFile.writeSDFile(usernameEditText.getText().toString(),"cache");
							mycreateSDFile.writeSDFile("consellor","cachetype");
						}
						catch (Exception ex){
							ex.printStackTrace();
						}
						new Thread(new Runnable() {
							public void run() {
								try{
									ConnNet operaton=new ConnNet();
									String result = operaton.consellorLogin(usernameEditText.getText().toString().trim(),passwordEditText.getText().toString().trim());
									Message msg=new Message();
									msg.obj=result;
									cshandler.sendMessage(msg);
								}
								catch (Exception ex){
									Toast.makeText(LoginActivity.this,"用户名或者密码不能为空",Toast.LENGTH_LONG).show();
								}

							}
						}).start();
					}
					else{
						Toast.makeText(LoginActivity.this,"请先选择登陆角色",Toast.LENGTH_LONG).show();
					}



				/*
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);

				finish();*/
				}

				@Override
				public void onProgress(int progress, String status) {
					Log.d(TAG, "login: onProgress");
				}

				@Override
				public void onError(final int code, final String message) {
					Log.d(TAG, "login: onError: " + code);
					if (!progressShow) {
						return;
					}
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
		}

	}

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			Log.e("dologin" ,string);
			//Toast.makeText(Login.this, String.valueOf(string.indexOf("login")), Toast.LENGTH_SHORT).show();
			if(string.indexOf("token")>0) {
				Intent intent =new Intent(LoginActivity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("app_username",usernameEditText.getText().toString());
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
				//Toast.makeText(Login.this, "登陆成功", Toast.LENGTH_SHORT).show();
			}
            /*
            else if(string.indexOf("unregistered")>0) {
                Toast.makeText(Login.this,"该账号尚未注册！",Toast.LENGTH_LONG);
            }*/
			else{
				Toast.makeText(LoginActivity.this, "用户登录失败", Toast.LENGTH_SHORT).show();
			}
			// Toast.makeText(Login.this, string, Toast.LENGTH_SHORT).show();
			Log.e(string,string);
			super.handleMessage(msg);
		}
	};

	Handler cshandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String string=(String) msg.obj;
			Log.e("csdologin" ,string);
			if(string.indexOf("token")>0) {
				Intent intent =new Intent(LoginActivity.this, ConsellorPage.class);
				startActivity(intent);
				finish();
			}
			else{
				Toast.makeText(LoginActivity.this, "咨询师登录失败", Toast.LENGTH_SHORT).show();
			}
			Log.e("sdsdf",string);
			super.handleMessage(msg);
		}
	};


	/**
	 * register
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}



	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
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
}
