package com.xthpasserby.simpleguideview;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.xthpasserby.guide_view.SimpleGuideView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private View button1, button2;
    private SimpleGuideView guideView1, guideView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        guideView1 = new SimpleGuideView(this);
        SimpleGuideView.Target target1 = new SimpleGuideView.Target(button1,
                BitmapFactory.decodeResource(getResources(), R.drawable.recharge_guide),
                SimpleGuideView.TargetGravity.TOP, SimpleGuideView.TargetShape.RECTANGLE_ROUND);
        target1.setPadding(20);
        guideView1.addTarget(target1);
        guideView1.setOnClickListener(this);

        SimpleGuideView.DismissTarget dismissTarget = new SimpleGuideView.DismissTarget(button1,
                BitmapFactory.decodeResource(getResources(), R.drawable.guide_next), new SimpleGuideView.OnDismissClickListener() {
            @Override
            public void onDismiss() {
                guideView1.hide();

                guideView2 = new SimpleGuideView(MainActivity.this);
                SimpleGuideView.Target target2 = new SimpleGuideView.Target(button2,
                        BitmapFactory.decodeResource(getResources(), R.drawable.recharge_guide));
                target2.setGuideOffsetX(-dp2px(40));
                guideView2.addTarget(target2);

                SimpleGuideView.DismissTarget dismissTarget = new SimpleGuideView.DismissTarget(
                        BitmapFactory.decodeResource(getResources(), R.drawable.guide_next), new SimpleGuideView.OnDismissClickListener() {
                    @Override
                    public void onDismiss() {
                        guideView2.hide();
                    }
                }, SimpleGuideView.TargetGravity.CENTER);
                guideView2.setDismissTarget(dismissTarget);
                guideView2.show();
            }
        }, SimpleGuideView.TargetGravity.BOTTOM);
        dismissTarget.setOffsetY(dp2px(20));
        guideView1.setDismissTarget(dismissTarget);
        guideView1.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            Toast.makeText(this, "button1 click", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.button2) {
            Toast.makeText(this, "button2 click", Toast.LENGTH_SHORT).show();
        }
    }

    private int dp2px(final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
