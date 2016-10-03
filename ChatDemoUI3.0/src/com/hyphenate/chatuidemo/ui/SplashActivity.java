package com.hyphenate.chatuidemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.Consellor.ConsellorPage;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.Help.createSDFile;
import com.hyphenate.chatuidemo.R;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {

	private createSDFile mycreateSDFile;
	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
		setContentView(R.layout.em_activity_splash);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏
		super.onCreate(arg0);

		RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_root);

		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);

	}

	@Override
	protected void onStart() {
		super.onStart();
		mycreateSDFile = new createSDFile(getBaseContext());
		try{
			mycreateSDFile.deleteSDFile("cache");
			mycreateSDFile.deleteSDFile("cachetype");
			mycreateSDFile.createSDFile("cache");
			mycreateSDFile.createSDFile("cachetype");
			mycreateSDFile.writeSDFile("ctest1","cache");
			mycreateSDFile.writeSDFile("consellor","cachetype");
			Log.e("fdg",mycreateSDFile.readSDFile("cache"));
			Log.e("fdg",mycreateSDFile.readSDFile("cachetype"));

			if(mycreateSDFile.readSDFile("cachetype").trim().equals("consellor")){
				if(mycreateSDFile.readSDFile("cache").trim().equals("")){
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
				else{
					long start = System.currentTimeMillis();
					long costTime = System.currentTimeMillis() - start;
					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//enter main screen
					startActivity(new Intent(SplashActivity.this, ConsellorPage.class));
					finish();
				}
			}
			else if(mycreateSDFile.readSDFile("cachetype").trim().equals("user")){

			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
/*
		new Thread(new Runnable() {
			public void run() {
				if (DemoHelper.getInstance().isLoggedIn()) {
					// auto login mode, make sure all group and conversation is loaed before enter the main screen
					long start = System.currentTimeMillis();
					EMClient.getInstance().groupManager().loadAllGroups();
					EMClient.getInstance().chatManager().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//enter main screen
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();
*/
	}
	
	/**
	 * get sdk version

	private String getVersion() {
	    return EMClient.getInstance().getChatConfig().getVersion();
	}
	 */
}
