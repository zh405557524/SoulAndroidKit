package com.soul.common.module.frameanimation;

/**
 * Description: 动画行为接口
 * Author: 祝明
 * CreateDate: 2021/9/6 14:15
 * UpdateUser:
 * UpdateDate: 2021/9/6 14:15
 * UpdateRemark:
 */
public interface IAnimator {

    /**
     * 打开动画
     */
    void openAnimator();

    /**
     * 开启进入动画
     */
    void startInAnimator();

    /**
     * 开启退出动画
     */
    void startOutAnimator();

    /**
     * 关闭动画
     */
    void closeAnimator();
}
