package com.hyphenate.chatuidemo.Evaluation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.R;

/**
 * Created by AniChikage on 2016/12/7.
 */
public class User extends Activity {

    //region ID
    private SeekBar eva_user_taidu;
    private SeekBar eva_user_xiaoguo;
    private TextView eva_user_taidu_num;
    private TextView eva_user_xiaoguo_num;
    private Context mContext;
    //endregion

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.eva_user);
        mContext = User.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏

        try{
            initID();
        }
        catch (Exception ex){
            Log.e("sdfsdf",ex.toString());
        }
    }

    private void initID(){
        eva_user_taidu = (SeekBar)findViewById(R.id.eva_user_taidu);
        eva_user_xiaoguo = (SeekBar)findViewById(R.id.eva_user_xiaoguo);
        eva_user_taidu_num = (TextView)findViewById(R.id.eva_user_taidu_num);
        eva_user_xiaoguo_num = (TextView)findViewById(R.id.eva_user_xiaoguo_num);

        eva_user_taidu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                eva_user_taidu_num.setText(progress + "/9 ");
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
}
