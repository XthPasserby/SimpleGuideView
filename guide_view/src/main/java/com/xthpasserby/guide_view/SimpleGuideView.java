package com.xthpasserby.guide_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


import java.util.ArrayList;
import java.util.List;


public class SimpleGuideView extends View {
    private static final int RECTANGLE_RADIUS = 4; // dp
    private Paint mEraserPaint;
    private Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private List<Target> targets = new ArrayList<>();
    private int[] curLoc = new int[2];
    private int[] loc = new int[2];
    private int rectangleRadius;

    public SimpleGuideView(Context context) {
        this(context, null);
    }

    public SimpleGuideView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleGuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mEraserBitmap = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_4444);
        mEraserCanvas = new Canvas(mEraserBitmap);

        mEraserPaint = new Paint();
        mEraserPaint.setColor(Color.TRANSPARENT);
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        rectangleRadius = dp2px(RECTANGLE_RADIUS);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.getLocationInWindow(curLoc);
        for (Target target : targets) {
            target.targetView.getLocationInWindow(loc);
            target.targetRect.left = loc[0] - curLoc[0];
            target.targetRect.top = loc[1] - curLoc[1];
            target.targetRect.right = target.targetRect.left + target.targetView.getWidth();
            target.targetRect.bottom = target.targetRect.top + target.targetView.getHeight();
            target.radius = Math.max(target.targetView.getWidth(), target.targetView.getHeight()) / 2;
            calculateGuideRect(target);
        }
    }

    private void calculateGuideRect(Target target) {
        float left, right, top, bottom;
        switch (target.shape) {
            case RECTANGLE:
                left = target.targetRect.left;
                right = target.targetRect.right;
                top = target.targetRect.top;
                bottom = target.targetRect.bottom;
                break;
            case CIRCLE:
            default:
                left = target.targetRect.centerX() - target.radius;
                right = target.targetRect.centerX() + target.radius;
                top = target.targetRect.centerY() - target.radius;
                bottom = target.targetRect.centerY() + target.radius;
                break;

        }
        switch (target.gravity) {
            case TOP:
                target.guideRect.left = left - (target.guideBitmap.getHeight() - target.targetView.getWidth()) / 2;
                target.guideRect.top = top - target.guideBitmap.getHeight();
                break;
            case LEFT:
                target.guideRect.left = left - target.guideBitmap.getHeight();
                target.guideRect.top = top + target.guideBitmap.getHeight();
                break;
            case RIGHT:
                target.guideRect.left = right;
                target.guideRect.top = top - target.guideBitmap.getHeight();
                break;
            case BOTTOM:
                target.guideRect.left = left - (target.guideBitmap.getHeight() - target.targetView.getWidth()) / 2;
                target.guideRect.top = bottom;
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mEraserBitmap.eraseColor(Color.TRANSPARENT);
        mEraserCanvas.drawColor(0xD8000000);
        drawTarget();
        canvas.drawBitmap(mEraserBitmap, 0, 0, null);
    }

    private void drawTarget() {
        for (Target target : targets) {
            switch (target.shape) {
                case RECTANGLE:
                    mEraserCanvas.drawRoundRect(target.targetRect, rectangleRadius, rectangleRadius, mEraserPaint);
                    break;
                case CIRCLE:
                default:
                    mEraserCanvas.drawCircle(target.targetRect.centerX(), target.targetRect.centerY(), target.targetRect.width() / 2, mEraserPaint);
                    break;
            }
            mEraserCanvas.drawBitmap(target.guideBitmap, target.guideRect.left + target.guideOffsetX, target.guideRect.top + target.guideOffsetY, null);
        }
    }

    public void addTarget(Target target) {
        if (null == target) return;
        targets.add(target);
    }

    private int dp2px(final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public static class Target {
        private RectF targetRect = new RectF();
        private RectF guideRect = new RectF();
        private View targetView;
        private Bitmap guideBitmap;
        private TargetGravity gravity;
        private TargetShape shape;
        private int radius;
        private int guideOffsetX = 0;
        private int guideOffsetY = 0;

        public Target(View targetView, Bitmap guideBitmap) {
            this(targetView, guideBitmap, TargetGravity.TOP);
        }

        public Target(View targetView, Bitmap guideBitmap, TargetGravity gravity) {
            this(targetView, guideBitmap, gravity, TargetShape.CIRCLE);
        }

        public Target(View targetView, Bitmap guideBitmap, TargetGravity gravity, TargetShape shape) {
            this.targetView = targetView;
            this.guideBitmap = guideBitmap;
            this.gravity = gravity;
            this.shape = shape;
        }

        public void setGuideOffsetX(int guideOffsetX) {
            this.guideOffsetX = guideOffsetX;
        }

        public void setGuideOffsetY(int guideOffsetY) {
            this.guideOffsetY = guideOffsetY;
        }
    }

    public enum TargetGravity {
        TOP,
        LEFT,
        RIGHT,
        BOTTOM
    }

    public enum TargetShape {
        CIRCLE,
        RECTANGLE
    }
}
