package com.hitoncloud.near;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/*主页-首页-每日推荐*/
public class Recommend extends Activity implements OnClickListener {

    ImageView img[] = new ImageView[4];
    private int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.recommend, android.R.layout.simple_dropdown_item_1line);
        ListView list_test = (ListView) findViewById(R.id.recommend_list);
        list_test.setAdapter(adapter);
        img[0] = (ImageView) findViewById(R.id.recommend_back);
        for(i=0;i<1;i++)
            img[i].setOnClickListener(this);


    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.recommend_back:
                finish();
                break;
            default:
                break;
        }
    }

}
