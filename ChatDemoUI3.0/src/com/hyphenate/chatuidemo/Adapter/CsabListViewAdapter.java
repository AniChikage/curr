package com.hyphenate.chatuidemo.Adapter;

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
public class CsabListViewAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private Handler hcontext;
    private List<Map<String, Object>> listItems;    //咨询师信息集合
    private LayoutInflater listContainer;           //视图容器
    public final class ListItemView{                //自定义控件集合
        public ImageView csabimg;
        public TextView csabid;
        public TextView csabname;
        public TextView csabgender;
        public TextView csablevel;
        public TextView csabtime;
        public TextView csabgoodat;
        public TextView csabprice;
    }
    public CsabListViewAdapter(Context context, List<Map<String, Object>> listItems) {
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
            convertView = listContainer.inflate(R.layout.cslvabstract, null);
            //获取控件对象
            listItemView.csabimg = (ImageView)convertView.findViewById(R.id.csabimg);
            listItemView.csabid = (TextView)convertView.findViewById(R.id.csabid);
            listItemView.csabname = (TextView)convertView.findViewById(R.id.csabname);
            listItemView.csabgender = (TextView)convertView.findViewById(R.id.csabgender);
            listItemView.csablevel= (TextView)convertView.findViewById(R.id.csablevel);
            //listItemView.csabtime = (TextView)convertView.findViewById(R.id.csabtime);
            listItemView.csabgoodat = (TextView)convertView.findViewById(R.id.csabgoodat);
            listItemView.csabprice = (TextView)convertView.findViewById(R.id.csabprice);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }
//      Log.e("image", (String) listItems.get(position).get("title"));  //测试
//      Log.e("image", (String) listItems.get(position).get("info"));
       // listItemView.csabimg.setBackgroundResource((Integer) listItems.get(position).get("csabimg"));
        listItemView.csabimg.setBackgroundDrawable((Drawable)listItems.get(position).get("csabimg"));
        listItemView.csabid.setText("uytu"+(String) listItems.get(position).get("csabid"));
        listItemView.csabname.setText(""+(String) listItems.get(position)
                .get("csabname"));
        listItemView.csabgender.setText(""+(String) listItems.get(position)
                .get("csabgender"));
        listItemView.csablevel.setText(""+(String) listItems.get(position)
                .get("csablevel"));
        //listItemView.csabtime.setText(""+(String) listItems.get(position)
        //        .get("csabtime"));
        listItemView.csabgoodat.setText("专长："+(String) listItems.get(position)
                .get("csabgoodat"));
        listItemView.csabprice.setText(""+(String) listItems.get(position)
                .get("csabprice")+"元/单位咨询时长");

        return convertView;
    }

}
