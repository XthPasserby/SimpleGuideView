# SimpleGuideView
简单的遮罩引导控件

### 截图

![image](https://github.com/XthPasserby/SimpleGuideView/blob/master/screenshot.png)

### 如何使用
直接拷贝SimpleGuideView.java到项目中在需要添加引导的布局中加入SimpleGuideView，最后在代码中添加如下代码即可：
```java
    guideView1 = new SimpleGuideView(this);
    SimpleGuideView.Target target1 = new SimpleGuideView.Target(button1,
            BitmapFactory.decodeResource(getResources(), R.drawable.recharge_guide),
            SimpleGuideView.TargetGravity.TOP, SimpleGuideView.TargetShape.RECTANGLE_ROUND);
    target1.setPadding(20);
    guideView1.addTarget(target1);
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
```
