package com.hitoncloud.near.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.hitoncloud.near.homepage.Pullable;

/**
 * 带滑动监听的scrollview
 */
public class GradationScrollView extends ScrollView implements Pullable {

    private ScrollViewListener scrollViewListener = null;

    public GradationScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    public boolean canPullDown() {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
            return true;
        else
            return false;
    }

    public interface ScrollViewListener {
        void onScrollChanged(GradationScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}