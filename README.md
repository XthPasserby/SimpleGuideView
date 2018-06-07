# SimpleGuideView
简单的遮罩引导控件

### 截图

![image](https://github.com/XthPasserby/SimpleGuideView/blob/master/screenshot.png)

### 如何使用
直接拷贝SimpleGuideView.java到项目中在需要添加引导的布局中加入SimpleGuideView，最后在代码中添加如下代码即可：
```java
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
```
