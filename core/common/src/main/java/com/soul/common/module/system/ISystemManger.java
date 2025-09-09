package com.soul.common.module.system;

/**
 * Description:系统资源管理接口
 * Author: 祝明
 * CreateDate: 2019-07-03 14:23
 * UpdateUser:
 * UpdateDate: 2019-07-03 14:23
 * UpdateRemark:
 */
public interface ISystemManger {

    /**
     * 请求焦点
     *
     * @return 不为 0 时，则为成功
     */
    int requestAudioFocus();

    /**
     * 释放焦点
     */
    void abandonAudioFocus();

    /**
     * 固定延时释放焦点 3秒后释放
     */
    void delayedAbandonAudioFocus();

    /**
     * 延时释放焦点
     *
     * @param millis 时间，单位:毫秒
     */
    void abandonAudioFocus(long millis);

    /**
     * 设置音量
     *
     * @param volume 音量大小 0~100
     * @return 修改之前的音量大小  0~100
     */
    int setAudioVolume(int volume);

    /**
     * 静音
     */
    void mute();

    /**
     * 打开声音
     */
    void cancelMute();

}
