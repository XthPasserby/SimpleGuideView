package com.xthpasserby.guide_view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class SimpleGuideView extends View {
    private static final int DEFAULT_BACKGROUND_COLOR = 0x88000000;
    private static final int RECTANGLE_RADIUS = 4; // dp

    private ViewGroup mDecorView;
    private Paint mEraserPaint;
    private Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private List<Target> targets = new ArrayList<>();
    private int[] curLoc = new int[2];
    private int[] loc = new int[2];
    private int rectangleRadius;
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private DismissTarget dismissTarget;

    public SimpleGuideView(Activity activity) {
        super(activity);
        init(activity);
    }

    private void init(Activity activity) {
        mEraserPaint = new Paint();
        mEraserPaint.setColor(Color.TRANSPARENT);
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        rectangleRadius = dp2px(RECTANGLE_RADIUS);
        mDecorView = (ViewGroup) activity.getWindow().getDecorView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (null == mEraserBitmap) {
            mEraserBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        }
        if (null == mEraserCanvas) {
            mEraserCanvas = new Canvas(mEraserBitmap);
        }
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

            // add padding
            target.targetRect.left -= target.padding;
            target.targetRect.top -= target.padding;
            target.targetRect.right += target.padding;
            target.targetRect.bottom += target.padding;
            target.radius += target.padding;

            calculateGuideRect(target);
        }
        if (null != dismissTarget) {
            if (null != dismissTarget.targetView) {
                dismissTarget.targetView.getLocationInWindow(loc);
                dismissTarget.targetRect.left = loc[0] - curLoc[0];
                dismissTarget.targetRect.top = loc[1] - curLoc[1];
                dismissTarget.targetRect.right = dismissTarget.targetRect.left + dismissTarget.targetView.getWidth();
                dismissTarget.targetRect.bottom = dismissTarget.targetRect.top + dismissTarget.targetView.getHeight();
            } else {
                dismissTarget.targetRect.left = curLoc[0];
                dismissTarget.targetRect.top = curLoc[1];
                dismissTarget.targetRect.right = dismissTarget.targetRect.left + getWidth();
                dismissTarget.targetRect.bottom = dismissTarget.targetRect.top + getHeight();
            }
            calculateDismissRect();
        }
    }

    private void calculateGuideRect(Target target) {
        float left, right, top, bottom;
        switch (target.shape) {
            case RECTANGLE:
            case RECTANGLE_ROUND:
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
                target.guideRect.left = left - (target.guideBitmap.getWidth() - target.targetView.getWidth()) / 2;
                target.guideRect.top = top - target.guideBitmap.getHeight();
                break;
            case LEFT:
                target.guideRect.left = left - target.guideBitmap.getWidth();
                target.guideRect.top = top - (target.guideBitmap.getHeight() - target.targetView.getHeight()) / 2;
                break;
            case RIGHT:
                target.guideRect.left = right;
                target.guideRect.top = top - (target.guideBitmap.getHeight() - target.targetView.getHeight()) / 2;
                break;
            case BOTTOM:
                target.guideRect.left = left - (target.guideBitmap.getWidth() - target.targetView.getWidth()) / 2;
                target.guideRect.top = bottom;
                break;
            case CENTER:
                target.guideRect.left = left - (target.guideBitmap.getWidth() - target.targetView.getWidth()) / 2;
                target.guideRect.top = top - (target.guideBitmap.getHeight() - target.targetView.getHeight()) / 2;
                break;
        }
        // add offset
        target.guideRect.left += target.guideOffsetX;
        target.guideRect.top += target.guideOffsetY;
    }

    private void calculateDismissRect() {
        float left, right, top, bottom;
        left = dismissTarget.targetRect.left;
        right = dismissTarget.targetRect.right;
        top = dismissTarget.targetRect.top;
        bottom = dismissTarget.targetRect.bottom;

        switch (dismissTarget.gravity) {
            case TOP:
                dismissTarget.dismissRect.left = left - (dismissTarget.dismissBitmap.getWidth() - (right - left)) / 2;
                // 当基于父布局定位时，top = top
                dismissTarget.dismissRect.top = dismissTarget.targetView == null ? top : top - dismissTarget.dismissBitmap.getHeight();
                break;
            case LEFT:
                // 当基于父布局定位时，left = left
                dismissTarget.dismissRect.left = dismissTarget.targetView == null ? left : left - dismissTarget.dismissBitmap.getWidth();
                dismissTarget.dismissRect.top = top - (dismissTarget.dismissBitmap.getHeight() - (bottom - top)) / 2;
                break;
            case RIGHT:
                // 当基于父布局定位时，left = right - dismissBitmap.getWidth()
                dismissTarget.dismissRect.left = dismissTarget.targetView == null ? right - dismissTarget.dismissBitmap.getWidth() : right;
                dismissTarget.dismissRect.top = top - (dismissTarget.dismissBitmap.getHeight() - (bottom - top)) / 2;
                break;
            case BOTTOM:
                dismissTarget.dismissRect.left = left - (dismissTarget.dismissBitmap.getWidth() - (right - left)) / 2;
                // 当基于父布局定位时，top = bottom - dismissBitmap.getHeight()
                dismissTarget.dismissRect.top = dismissTarget.targetView == null ? bottom - dismissTarget.dismissBitmap.getHeight() : bottom;
                break;
            case CENTER:
                dismissTarget.dismissRect.left = left - (dismissTarget.dismissBitmap.getWidth() - (right - left)) / 2;
                dismissTarget.dismissRect.top = top - (dismissTarget.dismissBitmap.getHeight() - (bottom - top)) / 2;
                break;
        }
        // add offset
        dismissTarget.dismissRect.left += dismissTarget.offsetX;
        dismissTarget.dismissRect.top += dismissTarget.offsetY;

        dismissTarget.dismissRect.right = dismissTarget.dismissRect.left + dismissTarget.dismissBitmap.getWidth();
        dismissTarget.dismissRect.bottom =  dismissTarget.dismissRect.top + dismissTarget.dismissBitmap.getHeight();
    }

    private boolean isDismissClick(MotionEvent event) {
        return  (null != dismissTarget && dismissTarget.dismissRect.left <= event.getX()
                && dismissTarget.dismissRect.right >= event.getX()
                && dismissTarget.dismissRect.top <= event.getY()
                && dismissTarget.dismissRect.bottom >= event.getY());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && isDismissClick(event)) {
            dismissTarget.clickListener.onDismiss();
            return true;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mEraserBitmap.eraseColor(Color.TRANSPARENT);
        mEraserCanvas.drawColor(backgroundColor);
        drawTarget();
        drawDismissTarget();
        canvas.drawBitmap(mEraserBitmap, 0, 0, null);
    }

    private void drawDismissTarget() {
        if (null == dismissTarget) return;
        mEraserCanvas.drawBitmap(dismissTarget.dismissBitmap, dismissTarget.dismissRect.left, dismissTarget.dismissRect.top, null);
    }

    private void drawTarget() {
        for (Target target : targets) {
            switch (target.shape) {
                case RECTANGLE:
                    mEraserCanvas.drawRect(target.targetRect, mEraserPaint);
                    break;
                case RECTANGLE_ROUND:
                    mEraserCanvas.drawRoundRect(target.targetRect, rectangleRadius, rectangleRadius, mEraserPaint);
                    break;
                case CIRCLE:
                default:
                    mEraserCanvas.drawCircle(target.targetRect.centerX(), target.targetRect.centerY(), target.targetRect.width() / 2, mEraserPaint);
                    break;
            }
            mEraserCanvas.drawBitmap(target.guideBitmap, target.guideRect.left, target.guideRect.top, null);
        }
    }

    public void setDismissTarget(DismissTarget target) {
        this.dismissTarget = target;
    }

    public void addTarget(Target target) {
        if (null == target) return;
        targets.add(target);
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    public void show() {
        mDecorView.post(new Runnable() {
            @Override
            public void run() {
                mDecorView.addView(SimpleGuideView.this);
            }
        });
    }

    public void hide() {
        mDecorView.post(new Runnable() {
            @Override
            public void run() {
                mDecorView.removeView(SimpleGuideView.this);
            }
        });
    }

    private int dp2px(final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public interface OnDismissClickListener {
        void onDismiss();
    }

    public static class DismissTarget {
        private RectF targetRect = new RectF();
        private RectF dismissRect = new RectF();
        private View targetView;
        private OnDismissClickListener clickListener;
        private Bitmap dismissBitmap;
        private TargetGravity gravity;
        private int offsetX = 0;
        private int offsetY = 0;

        public DismissTarget(Bitmap dismissBitmap, OnDismissClickListener clickListener) {
            this(dismissBitmap, clickListener, TargetGravity.CENTER);
        }

        public DismissTarget(Bitmap dismissBitmap, OnDismissClickListener clickListener, TargetGravity gravity) {
            this(null, dismissBitmap, clickListener, gravity);
        }

        public DismissTarget(View targetView, Bitmap dismissBitmap, OnDismissClickListener clickListener) {
            this(targetView, dismissBitmap, clickListener, TargetGravity.CENTER);
        }

        public DismissTarget(View targetView, Bitmap dismissBitmap, OnDismissClickListener clickListener, TargetGravity gravity) {
            this.targetView = targetView;
            this.clickListener = clickListener;
            this.dismissBitmap = dismissBitmap;
            this.gravity = gravity;
        }

        public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
        }

        public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }
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
        private int padding = 0;

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

        public void setPadding(int padding) {
            this.padding = padding;
        }
    }

    public enum TargetGravity {
        TOP,
        LEFT,
        RIGHT,
        BOTTOM,
        CENTER
    }

    public enum TargetShape {
        CIRCLE,
        RECTANGLE_ROUND,
        RECTANGLE
    }
}
