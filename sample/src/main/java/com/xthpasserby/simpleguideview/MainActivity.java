package com.xthpasserby.simpleguideview;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;

import com.xthpasserby.guide_view.SimpleGuideView;

public class MainActivity extends AppCompatActivity {
    private View button1, button2, button3;
    private SimpleGuideView guideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        // 这里可以先判断是否需要显示引导界面
        ViewStub stub = findViewById(R.id.guide_stub);
        stub.inflate();

        guideView = findViewById(R.id.guide_view);
        SimpleGuideView.Target target1 = new SimpleGuideView.Target(button1,
                BitmapFactory.decodeResource(getResources(), R.drawable.free_gift_guide),
                SimpleGuideView.TargetGravity.BOTTOM, SimpleGuideView.TargetShape.RECTANGLE);
        guideView.addTarget(target1);
        SimpleGuideView.Target target2 = new SimpleGuideView.Target(button2,
                BitmapFactory.decodeResource(getResources(), R.drawable.recharge_guide));
        target2.setGuideOffsetX(-dp2px(40));
        guideView.addTarget(target2);
        SimpleGuideView.Target target3 = new SimpleGuideView.Target(button3,
                BitmapFactory.decodeResource(getResources(), R.drawable.my_game_guide),
                SimpleGuideView.TargetGravity.TOP);
        target3.setGuideOffsetX(dp2px(40));
        guideView.addTarget(target3);

        guideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideView.setVisibility(View.GONE);
            }
        });
    }

    private int dp2px(final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
