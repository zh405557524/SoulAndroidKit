package com.soul.common.module.media;


/**
 * Description: 播放器接口
 * Author: 祝明
 * CreateDate: 2023/10/19 16:51
 * UpdateUser:
 * UpdateDate: 2023/10/19 16:51
 * UpdateRemark:
 */
public interface IPlayer {


    void addListener(Listener listener);

    void setMediaSource(String mediaSource);

    void prepare();

    void play();

    void seekTo(long l);

    void pause();

    void stop();

    long getCurrentPosition();

    long getDuration();
     void setVolume(float v, float v1);


}
