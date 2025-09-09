package com.soul.common.module.media;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


import com.soul.lib.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:播放器管理类
 * Author: 祝明
 * CreateDate: 2023/10/30 14:09
 * UpdateUser:
 * UpdateDate: 2023/10/30 14:09
 * UpdateRemark:
 */
public class MediaPlayManger {


    private static final String TAG = "PlayManger";
    private final IPlayer mPlayer;

    protected final Handler mMainHandler;
    protected final Handler mPlayHandler;


    /**
     * 当前播放状态
     */
    private PlayState mPlayState = PlayState.STOP;

    private final List<OnPlayStateChangeListener> mOnPlayStateChangeListeners = new ArrayList<>();

    public PlayState getPlayState() {
        return mPlayState;
    }


    /**
     * 播放状态
     */
    public enum PlayState {
        PLAY,//开始播放
        BUFFING,//缓冲中
        PLAYING,//播放中
        PAUSE, //暂停
        COMPLETE, //完成
        STOP//停止
    }

    /**
     * 播放模式
     */
    public enum PlayModel {
        Single,//单曲循环
        List,//列表循环
        SINGLE_ONCE//单曲播放
    }

    /**
     * 播放状态改变监听
     */
    public interface OnPlayStateChangeListener {
        /**
         * 播放状态改变
         *
         * @param playState 当前播放状态 {@link PlayState}
         */
        void onPlayStateChange(PlayState playState);

        /**
         * 播放进度改变
         *
         * @param progress 当前播放进度
         * @param duration 总时长
         */
        void onProgress(long progress, long duration);

    }

    public static MediaPlayManger getInstance() {
        return PlayMangerHolder.INSTANCE;
    }

    public MediaPlayManger() {
        mPlayer = new MediaPlayer();
        mMainHandler = new Handler(Looper.getMainLooper());
        HandlerThread playTask = new HandlerThread("playTask");
        playTask.start();
        mPlayHandler = new Handler(playTask.getLooper());
        initListener();

    }


    public static class PlayMangerHolder {
        private static final MediaPlayManger INSTANCE = new MediaPlayManger();
    }

    public long getDuration() {
        return mPlayer.getDuration();
    }

    Runnable outTimeTask = new Runnable() {
        @Override
        public void run() {
            LogUtil.i(TAG, "加载数据超时");
            onPlayComplete();
        }
    };

    private void initListener() {
        mPlayer.addListener(new Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                LogUtil.i(TAG, "playbackState：" + playbackState);
                switch (playbackState) {
                    case MediaPlayer.STATE_BUFFERING://缓冲中
                        LogUtil.i(TAG, "缓冲中");
                        mPlayState = PlayState.BUFFING;
                        mMainHandler.postDelayed(outTimeTask, 5 * 1000);
                        break;
                    case MediaPlayer.STATE_READY://准备好
                        LogUtil.i(TAG, "准备好");
                        if (mPlayState != PlayState.STOP) {
                            mPlayState = PlayState.PLAYING;
                            mMainHandler.removeCallbacks(outTimeTask);
                            play();
                        }

                        break;
                    case MediaPlayer.STATE_ENDED://播放完毕
                        LogUtil.i(TAG, "播放完毕：mPlayState:" + mPlayState);
                        try {
                            mPlayState = PlayState.COMPLETE;
                            LogUtil.i(TAG, "开始播放完成回调");
                            onPlayComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case MediaPlayer.STATE_IDLE://空闲中
                        LogUtil.i(TAG, "空闲中");
                        break;
                }
                updatePlayState(mPlayState);
            }
        });
    }


    public void addOnPlayStateChangeListener(OnPlayStateChangeListener onPlayStateChangeListener) {
        if (!mOnPlayStateChangeListeners.contains(onPlayStateChangeListener)) {
            mOnPlayStateChangeListeners.add(onPlayStateChangeListener);
        }
        LogUtil.i(TAG, "addOnPlayStateChangeListener:" + mOnPlayStateChangeListeners.size());
    }

    public void removeOnPlayStateChangeListener(OnPlayStateChangeListener onPlayStateChangeListener) {
        mOnPlayStateChangeListeners.remove(onPlayStateChangeListener);
        LogUtil.i(TAG, "removeOnPlayStateChangeListener:" + mOnPlayStateChangeListeners.size());
    }

    public boolean isPlaying() {

        return mPlayState == PlayState.PLAY || mPlayState == PlayState.PLAYING || mPlayState == PlayState.BUFFING;
    }

    /**
     * 播放完成
     */
    protected void onPlayComplete() {

    }

    /**
     * 更新播放状态
     *
     * @param playState 播放状态
     */
    protected void updatePlayState(PlayState playState) {

        mMainHandler.post(() ->
                {
                    LogUtil.i(TAG, "updatePlayState:" + mOnPlayStateChangeListeners.size());
                    try {
                        for (OnPlayStateChangeListener onPlayStateChangeListener : mOnPlayStateChangeListeners) {
                            if (onPlayStateChangeListener != null) {

                                onPlayStateChangeListener.onPlayStateChange(playState);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

    }


    public void play(String playUrl) {
        mPlayHandler.post(() -> {
            try {
                mPlayer.setMediaSource(playUrl);
                mPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
                onPlayComplete();
            }
        });
    }

    public void play() {
        LogUtil.i(TAG, "开始播放");
        mPlayHandler.post(() -> {
            if (mPlayState == PlayState.PAUSE) {
                mPlayState = PlayState.PLAYING;
            } else {
                mPlayState = PlayState.PLAY;
            }
            try {
                mPlayer.play();
                updatePlayState(mPlayState);
            } catch (Exception e) {
                e.printStackTrace();
                mPlayState = PlayState.STOP;
                updatePlayState(mPlayState);
            }
        });

    }

    public void pause() {
        mPlayHandler.post(() -> {
            mPlayState = PlayState.PAUSE;
            try {
                mPlayer.pause();
                updatePlayState(mPlayState);
            } catch (Exception e) {
                e.printStackTrace();
                mPlayState = PlayState.STOP;
                updatePlayState(mPlayState);
            }
        });

    }

    public void stop() {
        mPlayHandler.post(() -> {
            if (mPlayState != PlayState.STOP) {
                mPlayState = PlayState.STOP;
                try {
                    mPlayer.stop();
                    updatePlayState(mPlayState);
                } catch (Exception e) {
                    e.printStackTrace();
                    updatePlayState(mPlayState);
                }
            }
        });
    }

    /**
     * 设置音量
     *
     * @param volume 0~1.0f
     */
    public void setVolume(float volume) {
        mPlayHandler.post(() -> {
            try {
                mPlayer.setVolume(volume, volume);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
