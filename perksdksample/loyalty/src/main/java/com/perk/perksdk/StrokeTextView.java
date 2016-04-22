package com.perk.perksdk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * <h1>StrokeTextView</h1> Contains a method for drawing colored outlines
 * around text elements.
 *
 * @author P.J. Snavely
 * @version 1.0
 * @since 2015-07-28
 */

public class StrokeTextView extends TextView {

    // fields
    private int mStrokeColor;
    private int mStrokeWidth;
    private TextPaint mStrokePaint;

    // constructors
    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StrokeTextView(Context context) {
        super(context);
    }

    // getters + setters
    public void setStrokeColor(int color) {
        mStrokeColor = color;
    }

    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
    }

    // overridden methods
    @Override
    protected boolean onSetAlpha(int alpha) {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // lazy load
        if (mStrokePaint == null) {
            mStrokePaint = new TextPaint();
        }

        // copy
        TextPaint paint = getPaint();
        mStrokePaint.setTextSize(paint.getTextSize());
        mStrokePaint.setTypeface(paint.getTypeface());
        mStrokePaint.setFlags(paint.getFlags());
        mStrokePaint.setAlpha(paint.getAlpha());

        // custom
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        String text = getText().toString();
        canvas.drawText(text, (getWidth() - mStrokePaint.measureText(text)) / 2, getBaseline(), mStrokePaint);
        super.onDraw(canvas);
    }
}