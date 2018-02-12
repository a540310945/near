package com.hitoncloud.near.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hitoncloud.near.R;
import com.hitoncloud.near.task.ReleaseTaskFragment;
import com.hitoncloud.near.task.AcceptTaskFragment;

import static java.lang.Math.abs;

/*主页-任务*/
public class TaskFragment extends Fragment{

    private View view;
    public ImageView img1;
    public ImageView img2;
    private TextView txt1;
    private TextView txt2;
    private FrameLayout frame1;
    private FrameLayout frame2;
    public static final String tag1 = "tag1";
    public static final String tag2 = "tag2";


    //标识当前fragment
    public int mark = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_task, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        img1 = (ImageView) view.findViewById(R.id.leftbtn1);
        img2 = (ImageView) view.findViewById(R.id.rightbtn1);
        txt1 = (TextView) view.findViewById(R.id.ltxt1);
        txt2 = (TextView) view.findViewById(R.id.rtxt1);
        frame1 = (FrameLayout) view.findViewById(R.id.frameLayout1);
        frame2 = (FrameLayout) view.findViewById(R.id.frameLayout2);
        frame1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getoraccept(1);
            }
        });
        frame2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getoraccept(2);
            }
        });
        getoraccept(1);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void getoraccept(int i) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment1 = fm.findFragmentByTag(tag1);
        Fragment fragment2 = fm.findFragmentByTag(tag2);
        if (fragment1 != null) {
            ft.hide(fragment1);
        }
        if (fragment2 != null) {
            ft.hide(fragment2);
        }
        switch (i) {
            case 1:
                if (fragment1 == null) {
                    fragment1 = new ReleaseTaskFragment();
                    ft.add(R.id.taskarea, fragment1, tag1);
                } else {
                    ft.show(fragment1);
                }
                break;
            case 2:
                if (fragment2 == null) {
                    fragment2 = new AcceptTaskFragment();
                    ft.add(R.id.taskarea, fragment2, tag2);
                } else {
                    ft.show(fragment2);
                }
                break;
            default:
                break;
        }
        ft.commit();
        switch (i)
        {
            case 1:
                img1.setBackgroundResource(R.drawable.lbtnon);
                txt1.setTextColor(Color.parseColor("#ffffff"));
                img2.setBackgroundResource(R.drawable.rbtnoff);
                txt2.setTextColor(Color.parseColor("#000000"));
                mark=1;
                break;
            case 2:
                img1.setBackgroundResource(R.drawable.lbtnoff);
                txt1.setTextColor(Color.parseColor("#000000"));
                img2.setBackgroundResource(R.drawable.rbtnon);
                txt2.setTextColor(Color.parseColor("#ffffff"));
                mark=2;
                break;
            default:
                break;
        }
    }





}
