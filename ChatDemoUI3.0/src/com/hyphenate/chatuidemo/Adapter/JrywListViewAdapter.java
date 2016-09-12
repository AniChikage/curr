package com.hyphenate.chatuidemo.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;

import java.util.List;
import java.util.Map;


/**
 * Created by AniChikage on 2016/7/15.
 */
public class JrywListViewAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private Handler hcontext;
    private List<Map<String, Object>> listItems;    //咨询师信息集合
    private LayoutInflater listContainer;           //视图容器
    public final class ListItemView{                //自定义控件集合
        public ImageView jrywlvimg;
        public TextView jrywlvinfo;
        public TextView jrywnoid;
    }
    public JrywListViewAdapter(Context context, List<Map<String, Object>> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return listItems.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return listItems.get(arg0);
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void clickEvent(int clickID){
        new AlertDialog.Builder(context)
                .setTitle("物品详情：" + listItems.get(clickID).get("jrywlvinfo"))
                .setMessage("")
                .setPositiveButton("确定", null)
                .show();
    }

    /**
     * ListView Item设置
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Log.e("method", "getView");
        final int selectID = position;
        //自定义视图
        ListItemView  listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.from(context).inflate(R.layout.jinriyaowenlistview, null);
            //获取控件对象
            listItemView.jrywlvimg = (ImageView)convertView.findViewById(R.id.jrywlvimg);
            listItemView.jrywlvinfo = (TextView)convertView.findViewById(R.id.jrywlvinfo);
            listItemView.jrywnoid = (TextView)convertView.findViewById(R.id.jrywnoid);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }
        listItemView.jrywlvimg.setBackgroundDrawable((Drawable) listItems.get(position).get("jrywlvimg"));
        listItemView.jrywlvinfo.setText((String) listItems.get(position).get("jrywlvinfo"));
        listItemView.jrywnoid.setText((String) listItems.get(position).get("jrywnoid"));

        return convertView;
    }
}
