package com.hyphenate.chatuidemo.Main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.Adapter.ArticleTextAdapter;
import com.hyphenate.chatuidemo.Base64.BASE64Decoder;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by AniChikage on 2016/8/2.
 */
public class ArticleDetail extends Activity {
    private TextView testid,title,time,articletext;
    private ImageView headerimg;
    private List article_list;
    private ArticleTextAdapter articleTextAdapter;
    private ListView listview;
    private Context hcontext;
    private View header_View;
    private LinearLayout header_ll;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.article_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏
        hcontext=this.getApplicationContext();
        try{
            initId();
        }
        catch (Exception ex){
            Log.e("init_error",ex.toString());
        }
        try {
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                String data = bundle.getString("msgid");
                testid.setText(data);
                SysnArticleDetail(data);
            }
        }
        catch (Exception ex){
            Log.e("tag",ex.toString());
        }
    }


    private void initId(){
        testid=(TextView)findViewById(R.id.testid);
        listview = (ListView) findViewById(R.id.article_detail_listview);
        header_View = LayoutInflater.from(this).inflate(R.layout.article_detail_header, null);
        header_ll = (LinearLayout) header_View.findViewById(R.id.articledetail_header_ll);
        headerimg = (ImageView)header_View.findViewById(R.id.articledetail_img);
        title = (TextView)header_View.findViewById(R.id.articledetail_title);
        time = (TextView)header_View.findViewById(R.id.articledetail_time);
        WindowManager wm1 = this.getWindowManager();
        int height1 = wm1.getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams lp1 =header_ll.getLayoutParams();
        lp1.width=lp1.MATCH_PARENT;
        lp1.height=height1*900/1932;
        header_ll.setLayoutParams(lp1);
    }

    private void SysnArticleDetail(String data){
        final String aid=data;
        new Thread(new Runnable() {
            public void run() {
                try{
                    ConnNet operaton=new ConnNet();
                    String result=operaton.getArticle(aid);
                    Message msg=new Message();
                    msg.obj=result;
                    handler.sendMessage(msg);
                }
                catch (Exception ex){
                    Toast.makeText(ArticleDetail.this,"文章获取失败", Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            listview.addHeaderView(header_View);
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try{
                JSONObject jsonObjs = new JSONObject(string).getJSONObject("article");
                String jcover = jsonObjs.getString("cover");
                String jtitle = jsonObjs.getString("title");
                String jtime = jsonObjs.getString("ptime");
                String jsubstance = jsonObjs.getString("substance");
                headerimg.setImageBitmap(getBitmapFromByte(Base64.decode(jcover, Base64.DEFAULT)));
                title.setText(jtitle);
                time.setText(jtime);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("articletext", getStringFromBASE64(jsubstance));
                Log.e("tag",getStringFromBASE64(jsubstance));
                listItems.add(map);
                article_list = listItems;
                articleTextAdapter = new ArticleTextAdapter(hcontext, article_list);
                listview.setAdapter(articleTextAdapter);
            }
            catch (Exception ex){
                Log.e("jsonErr",ex.toString());
            }

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
}
