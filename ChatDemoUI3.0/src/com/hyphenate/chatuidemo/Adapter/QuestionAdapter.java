package com.hyphenate.chatuidemo.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;

import java.util.List;
import java.util.Map;


/**
 * Created by AniChikage on 2016/7/15.
 */
public class QuestionAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private Handler hcontext;
    public Integer length;
    private Integer sumScore = 0;
    public Integer[] tagList = new Integer[100];
    private List<Map<String, Object>> listItems;    //咨询师信息集合
    private LayoutInflater listContainer;           //视图容器
    public final class ListItemView{                //自定义控件集合
        public TextView content;
        public RadioButton radioButton1;
        public RadioButton radioButton2;
        public RadioButton radioButton3;
        public RadioButton radioButton4;
        public RadioButton radioButton5;
    }
    public QuestionAdapter(Context context, List<Map<String, Object>> listItems) {
        super();
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
        length = listItems.size();
        int i;
        for (i=0;i<=99 ;i++){
            tagList[i] = 0;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Log.e("method", "getView");
        final int selectID = position;
        //自定义视图
        ListItemView  listItemView = null;
        if (true) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.from(context).inflate(R.layout.question_item, null);
            //获取控件对象
            listItemView.content = (TextView)convertView.findViewById(R.id.question_content);
            listItemView.radioButton1 = (RadioButton) convertView.findViewById(R.id.question_radioButton);
            listItemView.radioButton2 = (RadioButton) convertView.findViewById(R.id.question_radioButton2);
            listItemView.radioButton3 = (RadioButton) convertView.findViewById(R.id.question_radioButton3);
            listItemView.radioButton4 = (RadioButton) convertView.findViewById(R.id.question_radioButton4);
            listItemView.radioButton5 = (RadioButton) convertView.findViewById(R.id.question_radioButton5);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        listItemView.radioButton1.destroyDrawingCache();

        listItemView.content.setText( (String)listItems.get(position).get("content"));
        listItemView.radioButton1.setText((String)listItems.get(position).get("c1"));
        listItemView.radioButton2.setText((String)listItems.get(position).get("c2"));
        listItemView.radioButton3.setText((String)listItems.get(position).get("c3"));
        listItemView.radioButton4.setText((String)listItems.get(position).get("c4"));
        listItemView.radioButton5.setText((String)listItems.get(position).get("c5"));

        listItemView.radioButton1.setTag(1);
        listItemView.radioButton2.setTag(2);
        listItemView.radioButton3.setTag(3);
        listItemView.radioButton4.setTag(4);
        listItemView.radioButton5.setTag(5);

        View.OnClickListener listener = new View.OnClickListener(){

            public void onClick(View v){
                tagList[position] = (Integer) v.getTag();
            }
        };
        listItemView.radioButton1.setOnClickListener(listener);
        listItemView.radioButton2.setOnClickListener(listener);
        listItemView.radioButton3.setOnClickListener(listener);
        listItemView.radioButton4.setOnClickListener(listener);
        listItemView.radioButton5.setOnClickListener(listener);
//        listItemView.radioButton1.setChecked(false);
//        listItemView.radioButton2.setChecked(false);
//        listItemView.radioButton3.setChecked(false);
//        listItemView.radioButton4.setChecked(false);
//        listItemView.radioButton5.setChecked(false);
        Log.e("bullshit", ((Integer)position).toString());
        Log.e("bullshit", tagList[position].toString());
        if(tagList[position]==1)
            listItemView.radioButton1.setChecked(true);
        else
            listItemView.radioButton1.setChecked(false);
        if(tagList[position]==2)
            listItemView.radioButton2.setChecked(true);
        else
            listItemView.radioButton2.setChecked(false);
        if(tagList[position]==3)
            listItemView.radioButton3.setChecked(true);
        else
            listItemView.radioButton3.setChecked(false);
        if(tagList[position]==4)
            listItemView.radioButton4.setChecked(true);
        else
            listItemView.radioButton4.setChecked(false);
        if(tagList[position]==5)
            listItemView.radioButton5.setChecked(true);
        else
            listItemView.radioButton5.setChecked(false);

        //        listItemView.radioButton1.setChecked(false);
//        listItemView.radioButton2.setChecked(false);
//        listItemView.radioButton3.setChecked(false);
//        listItemView.radioButton4.setChecked(false);
//        listItemView.radioButton5.setChecked(false);


        return convertView;
    }
}