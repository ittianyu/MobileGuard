package com.ittianyu.mobileguard.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;

import com.ittianyu.mobileguard.R;

/**
 * GridView with separation line
 */
public class SeparationLineGridView extends GridView {
    private float lineMarginLeft;
    private float lineMarginRight;
    private float lineMarginTop;
    private float lineMarginBottom;
    private float lineSize = 0.5f;
    private int lineColor = Color.WHITE;


    public SeparationLineGridView(Context context) {
        super(context);
        initAttrs(null, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeparationLineGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs, defStyleAttr);
    }

    public SeparationLineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
    }

    public SeparationLineGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs, defStyleAttr);
    }

    /**
     * init attrs of TextView
     */
    private void initAttrs(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SeparationLineGridView, defStyle, 0);

        lineMarginLeft = a.getDimension(R.styleable.SeparationLineGridView_lineMarginLeft, 0);
        lineMarginRight = a.getDimension(R.styleable.SeparationLineGridView_lineMarginRight, 0);
        lineMarginTop = a.getDimension(R.styleable.SeparationLineGridView_lineMarginTop, 0);
        lineMarginBottom = a.getDimension(R.styleable.SeparationLineGridView_lineMarginBottom, 0);
        lineSize = a.getDimension(R.styleable.SeparationLineGridView_lineSize, 0.5f);
        lineColor = a.getColor(R.styleable.SeparationLineGridView_lineColor, Color.WHITE);

        a.recycle();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int col = getNumColumns();
        int row = (getCount() + col - 1) / col;
        int unitHeight = getHeight() / row;
        int unitWidth = getWidth() / col;
//        int hSpace = getHSpacing(col);
//        int vSpace = getVSpacing(row);
//
//        hSpace = vSpace = 0;

//        System.out.println(String.format("col=%d, row=%d unitHeight=%d, unitWidth=%d, hSpace=%d, vSpace=%d",
//                col, row, unitHeight, unitWidth, hSpace, vSpace));

        Paint paint = new Paint();
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineSize);

//        System.out.println("lineSize=" + lineSize);

        // horizontal line
        for (int i = 1; i < row; i++) {
//            System.out.println(String.format("(%d,%d) - (%d,%d)",
//                    (int)(lineMarginLeft + 1), i * unitHeight - vSpace / 2,
//                    (int)(getWidth() - lineMarginRight), i * unitHeight - vSpace / 2));
            canvas.drawLine(lineMarginLeft + 1, i * unitHeight,
                    getWidth() - lineMarginRight, i * unitHeight, paint);
        }

        // vertical line
        for (int i = 1; i < col; i++) {
//            System.out.println(String.format("(%d,%d) - (%d,%d)",
//                    i * unitWidth - hSpace / 2, (int)(lineMarginTop + 1),
//                    i * unitWidth - hSpace / 2, (int)(getHeight() - lineMarginBottom)));
            canvas.drawLine(i * unitWidth, lineMarginTop + 1,
                    i * unitWidth, getHeight() - lineMarginBottom, paint);
        }

//        canvas.drawRect(0, 0, 100, 100, paint);

    }

//    /**
//     * @param col the count of column
//     * @return horizontal spacing
//     */
//    private int getHSpacing(int col) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            return getHorizontalSpacing();
//        }
//        // if api less than 16, need calculate
//        int width = getWidth();
//
//        for (int i = 0; i < col; i++) {
//            width -= getChildAt(i).getWidth();
//        }
//        width -= getPaddingLeft();
//        width -= getPaddingRight();
//
//        return width / (col - 1);
//    }
//
//
//    /**
//     * @param row the count of row
//     * @return vertical spacing
//     */
//    private int getVSpacing(int row) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            return getVerticalSpacing();
//        }
//        // if api less than 16, need calculate
//        int height = getHeight();
//
//        for (int i = 0; i < row; i++) {
//            height -= getChildAt(i).getHeight();
//        }
//        height -= getPaddingTop();
//        height -= getPaddingBottom();
//
//        return height / (row - 1);
//    }

}
