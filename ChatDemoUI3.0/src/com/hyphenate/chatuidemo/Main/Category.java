package com.hyphenate.chatuidemo.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chatuidemo.Adapter.JrywListViewAdapter;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.netapp.ConnNet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AniChikage on 2016/8/1.
 */
public class Category extends Activity {

    private ListView category_listview;
    private Context hcontext;
    private View category_header_View;
    private LinearLayout category_header_ll;
    private SlideShowViewCate slideShowView;
    private List<Map<String, Object>> jrywList;
    private JrywListViewAdapter jrywListViewAdapter;
    private TextView category_name;
    private String data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
        setContentView(R.layout.category);
        hcontext=this.getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //透明导航栏

        //接收信息
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            data = bundle.getString("category_name");
            Log.e("category_name",data);


            category_listview = (ListView) findViewById(R.id.category_listview);
            category_header_View = LayoutInflater.from(this).inflate(R.layout.category_header, null,false);
            category_header_ll = (LinearLayout) category_header_View.findViewById(R.id.category_header_ll);
            WindowManager wm1 = this.getWindowManager();
            int height1 = wm1.getDefaultDisplay().getHeight();
            ViewGroup.LayoutParams lp1 =category_header_ll.getLayoutParams();
            lp1.width=lp1.MATCH_PARENT;
            //lp1.height=height1*693/1679;
            lp1.height=height1*560/1679;
            category_header_ll.setLayoutParams(lp1);

            SysnJRYW();

            slideShowView= (SlideShowViewCate) category_header_View.findViewById(R.id.slideshow);
            category_name=(TextView)category_header_View.findViewById(R.id.category_name);
            category_name.setText(data);

            WindowManager wm = this.getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            ViewGroup.LayoutParams lp =slideShowView.getLayoutParams();
            lp.width=lp.MATCH_PARENT;
            lp.height=height*526/1679;
            slideShowView.setLayoutParams(lp);

            SysnAds();  //同步
        }

        // header
    }

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
                    Toast.makeText(Category.this,"今日要闻获取失败", Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    Handler jrywhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            try {
                JSONArray jsonObjs = new JSONObject(string).getJSONArray("articles");
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = (JSONObject)jsonObjs.get(i);
                    String id = jsonObj.getString("aid");
                    String title = jsonObj.getString("title");
                    String thumbnail = jsonObj.getString("thumbnail");
                    String typ = jsonObj.getString("typ");
                    Map<String, Object> map = new HashMap<String, Object>();
                    Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(thumbnail, Base64.DEFAULT)));
                    if(typ.equals(data)){
                        map.put("jrywlvimg", drawable);               //咨询师头像
                        map.put("jrywlvinfo",title);
                        map.put("jrywnoid", id);              //姓名
                        listItems.add(map);
                    }
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }

            category_listview.addHeaderView(category_header_View);
            jrywList = listItems;
            jrywListViewAdapter = new JrywListViewAdapter(hcontext, jrywList);
            category_listview.setAdapter(jrywListViewAdapter);
            category_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        ListView lv = (ListView)parent;
                        HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
                        if(person!=null){
                            //Toast.makeText(getApplicationContext(), person.get("csabid").toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Category.this, ArticleDetail.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("msgid",person.get("jrywnoid").toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                           // finish();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"kong", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(Category.this,ex.toString() , Toast.LENGTH_SHORT).show();
                        Log.e("get error",ex.toString());
                    }
                }
            });
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
                    //Drawable drawable =new BitmapDrawable(getBitmapFromByte(Base64.decode(cover,Base64.DEFAULT)));
                    adimg.add(cover);
                    adhint.add(title);
                    adid.add(id);
                }
            } catch (JSONException e) {
                System.out.println("Jsons parse error !");
                e.printStackTrace();
            }
            slideShowView.init(adhint,adimg,adid);

            super.handleMessage(msg);
        }
    };

    //back
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下键盘上返回按钮
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(Category.this, Mainpage.class);
            startActivity(intent);
            finish();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);

        }
    }
    */
}
