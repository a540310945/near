package com.hitoncloud.near.utils;

import android.text.TextUtils;

/**
 * Created by 蒋凌 on 2017/11/19.
 */

public class StringUtils {

    public static String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }

}
