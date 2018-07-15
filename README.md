# SlideUnlockView
滑动解锁控件

![more](https://github.com/zjun615/SlideUnlockView/blob/master/imgs/SlideUnlockView.gif) 

## Gradle依赖
> 在工程build.gradle中添加
```xml
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
> 添加依赖
```xml
dependencies {
    implementation 'com.github.zjun615:SlideUnlockView:0.2'
}
```

## 使用
### xml布局
```xml
<com.zjun.widget.SlideUnlockView
    android:id="@+id/slv_unlock"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="5dp"
    app:suv_tips="Right slide to unlock" />
```

### 回调接口
```java
slvUnlock.setOnUnlockListener(new SlideUnlockView.OnUnlockListener() {
            @Override
            public void onUnlocked() {
                Toast.makeText(MainActivity.this, "已解锁", Toast.LENGTH_SHORT).show();
            }
        });
```

## 属性说明
属性名 | 说明 | 默认值
:------ | :------ | :------
suv_bgColor    | 背景颜色 | #FFEEEEEE
suv_strokeColor    | 边框颜色 | #FFFF0000
suv_strokeWidth    | 边框宽度 | 2dp
suv_slideBgColor    | 滑过的背景颜色 | #FFFDE4D8
suv_slideGap    | 滑动按钮与外边的间距 | 2dp
suv_slideDeviation    | 滑动误差值 | 5dp
suv_btnColor    | 滑动按钮颜色 | 同suv_strokeColor
suv_btnRingSize    | 被按下时，按钮的光环大小 | 同suv_slideGap
suv_btnRingColor    | 被按下时，按钮的光环颜色 | #A0ABABAB
suv_arrowSize    | 滑动按钮里箭头大小 | -1，会自动计算大小
suv_arrowLineWidth    | 滑动按钮里箭头线条大小 | 2dp
suv_arrowColor    | 滑动按钮里箭头颜色 | 同suv_bgColor
suv_tips    | 提示文字内容 | 空字符串
suv_tipsSize    | 提示文字字体大小 | 14dp
suv_tipsColor    | 提示文字字体颜色 | #FF888888
suv_tipsBold    | 提示文字是否粗体 | false
suv_backAnimatorEnable    | 是否需要返回动画 | false
suv_backFullDuration    | 返回动画，从终点到起点的总时长。单位：ms | 500


