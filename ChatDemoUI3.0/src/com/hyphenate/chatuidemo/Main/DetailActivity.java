package com.hyphenate.chatuidemo.Main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.hyphenate.chatuidemo.R;

/**
 * Created by AniChikage on 2016/7/6.
 */
public class DetailActivity extends Activity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        imageView = (ImageView)findViewById(R.id.detail_image);

        Bundle extras = getIntent().getExtras();
        imageView.setImageResource(extras.getInt("image_id"));
    }

}
