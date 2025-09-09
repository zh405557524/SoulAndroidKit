package com.soul.common.module.frameanimation;

import android.graphics.Canvas;

import java.lang.ref.WeakReference;

/**
 * Description: 绘制动画播放
 * Author: 祝明
 * CreateDate: 2022/2/7 14:17
 * UpdateUser:
 * UpdateDate: 2022/2/7 14:17
 * UpdateRemark:
 */
public class DrawAnimator implements IAnimation {

    private boolean isStart;

    private final PlayAnimationView mPlayAnimationView;

    protected int wight;

    protected int height;

    public DrawAnimator(WeakReference<PlayAnimationView> playAnimationView) {
        mPlayAnimationView = playAnimationView.get();
        mPlayAnimationView.attachAnimationView(this);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        wight = w;
        height = h;
    }


    public void start() {
        if (!isStart) {
            mPlayAnimationView.start();
            isStart = true;
        }
    }

    public void stop() {
        isStart = false;

    }

    @Override
    public boolean onDraw(Canvas canvas) {
        if (isStart) {
            onDrawUI(canvas);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDrawDefault(Canvas canvas) {
        return false;
    }

    @Override
    public void onDestroyed() {
        isStart = false;
    }

    protected void onDrawUI(Canvas canvas) {

    }


}
