package com.hitoncloud.near.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.hitoncloud.near.R;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.utils.DensityUtil;

/**
 * Created by 蒋凌 on 2017/10/21.
 */

public class Triangleview extends View{

    private int width = DensityUtil.dip2px(MyApplication.appcontext,20);
    private int height = DensityUtil.dip2px(MyApplication.appcontext,20);

    private Paint whitePaint;
    private Path path;
    private int color;

    public Triangleview(Context context) {
        this(context, null);
    }

    public Triangleview(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Triangleview);
        color = a.getColor(R.styleable.Triangleview_color,getResources().getColor(R.color.theme));
        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setColor(color);
        whitePaint.setStyle(Paint.Style.FILL);

        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measureLength(widthMeasureSpec, width);
        height = measureLength(heightMeasureSpec, height);
        setMeasuredDimension(width, height);
    }

    private int measureLength(int measureSpec, int length) {
        int measureLength = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                //wrap_content时size其实为父控件能给的最大长度,length相关于一个默认值的作用 Math.min
                measureLength = Math.min(size, length);
                break;
            case MeasureSpec.EXACTLY:
                //match_parent或指定如30dp时 测出来多大就多大,考虑的情况是有时候测量出的确没有空间放置此控件了
                measureLength = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                measureLength = length;
                break;
        }
        return measureLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTriangle(canvas);
    }

    private void drawTriangle(Canvas canvas) {
        //用路径来画出这个三角形
        path.moveTo(0, 0);//起始点
        path.lineTo(width, height);//从起始点画一根线到 相应的坐标
        path.lineTo(width, 0);
        canvas.drawPath(path, whitePaint);
    }
}
