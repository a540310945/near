package com.hitoncloud.near.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;


//管理所有Activity，以便与随时关闭Activity
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity)
    {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity)
    {
        activities.remove(activity);
    }

    public static void finishAllActivity()
    {
        for(Activity activity:activities)
        {
            if(!activity.isFinishing())
            activity.finish();
        }
    }


}
