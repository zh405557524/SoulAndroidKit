package com.soul.common.module.system;

import android.content.Context;
import android.media.AudioManager;


import com.soul.common.Global;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;


/**
 * Description: 系统资源管理
 * Author: 祝明
 * CreateDate: 2019-07-03 14:08
 * UpdateUser:
 * UpdateDate: 2019-07-03 14:08
 * UpdateRemark:
 */
public class SystemManager implements ISystemManger {


    /**
     * 固定延时释放焦点的时长，单位毫秒
     */
    private static final long delayedMillis = 3 * 1000;

    /**
     * 封装：STREAM_类型
     */
    public final static int TYPE_MUSIC = AudioManager.STREAM_MUSIC;
    public final static int TYPE_ALARM = AudioManager.STREAM_ALARM;
    public final static int TYPE_RING = AudioManager.STREAM_RING;

    @IntDef({TYPE_MUSIC, TYPE_ALARM, TYPE_RING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {
    }

    private static ISystemManger sSystemManger;

    /**
     * 音频管理
     */
    private final AudioManager mAudioManager;

    private int NOW_AUDIO_TYPE = TYPE_MUSIC;

    public static ISystemManger getInstance() {
        if (sSystemManger == null) {
            synchronized (SystemManager.class) {
                if (sSystemManger == null) {
                    sSystemManger = new SystemManager();
                }
            }
        }
        return sSystemManger;
    }

    private SystemManager() {
        mAudioManager = (AudioManager) Global.getContext().getSystemService(Context.AUDIO_SERVICE);
    }


    @Override
    public int requestAudioFocus() {
        if (mAudioManager == null) {
            return 0;
        }
        return mAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void abandonAudioFocus() {
        if (mAudioManager == null) {
            return;
        }
        if (mAudioManager.abandonAudioFocus(mAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
        } else {
            //            LogUtil.w("Tag", "abandonAudioFocus failed");
        }

    }

    @Override
    public void abandonAudioFocus(long millis) {
        if (abandonAudioFocusTask != null) {
            Global.getMainThreadHandler().removeCallbacks(abandonAudioFocusTask);
        }
        Global.postDelayedTaskSafely(abandonAudioFocusTask, millis);
    }

    @Override
    public void delayedAbandonAudioFocus() {
        abandonAudioFocus(delayedMillis);
    }

    @Override
    public int setAudioVolume(int volume) {
        final int currentVolume = get100CurrentVolume();
        int a = (int) Math.ceil((volume) * getSystemMaxVolume() * 0.01);
        a = a <= 0 ? 0 : a;
        a = a >= 100 ? 100 : a;
        mAudioManager.setStreamVolume(NOW_AUDIO_TYPE, a, 0);
        return currentVolume;
    }

    @Override
    public void mute() {
        mAudioManager.setStreamMute(TYPE_MUSIC, true);
        mAudioManager.setStreamMute(TYPE_ALARM, true);
        mAudioManager.setStreamMute(TYPE_RING, true);
    }

    @Override
    public void cancelMute() {
        mAudioManager.setStreamMute(TYPE_MUSIC, false);
        mAudioManager.setStreamMute(TYPE_ALARM, false);
        mAudioManager.setStreamMute(TYPE_RING, false);
    }


    /**
     * 以0-100为范围，获取当前的音量值
     *
     * @return 获取当前的音量值
     */
    public int get100CurrentVolume() {
        return 100 * getSystemCurrentVolume() / getSystemMaxVolume();
    }

    public int getSystemCurrentVolume() {
        return mAudioManager.getStreamVolume(NOW_AUDIO_TYPE);
    }


    private int getSystemMaxVolume() {
        return mAudioManager.getStreamMaxVolume(NOW_AUDIO_TYPE);
    }


    private Runnable abandonAudioFocusTask = new Runnable() {
        @Override
        public void run() {
            abandonAudioFocus();
        }
    };

    /**
     * 音频焦点回调
     */
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener
            = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    };


}
