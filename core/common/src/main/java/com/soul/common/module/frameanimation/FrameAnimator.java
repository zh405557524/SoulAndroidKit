package com.soul.common.module.frameanimation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;


import com.soul.common.Global;
import com.soul.common.utils.LogUtil;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

/**
 * Description: 帧动画播放
 * Author: 祝明
 * CreateDate: 2022/2/7 14:17
 * UpdateUser:
 * UpdateDate: 2022/2/7 14:17
 * UpdateRemark:
 */
public class FrameAnimator implements IAnimation {

    private boolean isDebug = false;

    public static final String TAG = FrameAnimator.class.getSimpleName();

    /**
     * 启动动画
     */
    private static final int MSG_START = 1;

    private final Rect mResRect;

    private final Rect mDstRectF;

    private final Paint mPaint;

    private final Handler mReadBitmapResHandler;

    private final PlayAnimationView mPlayAnimationView;

    private int wight;

    private int height;

    private int readTimebase;

    private boolean isStart;

    private Bitmap mBitmap;

    private BitmapFactory.Options mOptions;

    private OnAnimatorPlayListener mOnAnimatorPlayListener;

    public interface OnAnimatorPlayListener {
        void onPlayComplete();
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public FrameAnimator(WeakReference<PlayAnimationView> playAnimationView) {
        mResRect = new Rect();
        mDstRectF = new Rect();
        mPlayAnimationView = playAnimationView.get();
        mPlayAnimationView.attachAnimationView(this);
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        HandlerThread readBitmapResTask = new HandlerThread("ReadBitmapResTask");
        readBitmapResTask.start();
        mReadBitmapResHandler = new Handler(readBitmapResTask.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleReadBitmapMessage(msg);
            }
        };
    }

    /**
     * 图片资源
     */
    volatile int[] animatorRes;

    /**
     * 绘制间隔时间
     */
    private int duration = 40;

    /**
     * 设置动画资源
     */
    public void setAnimatorRes(int[] animatorRes) {
        this.animatorRes = animatorRes;
    }

    public void setOnAnimatorPlayListener(OnAnimatorPlayListener onAnimatorPlayListener) {
        mOnAnimatorPlayListener = onAnimatorPlayListener;
    }

    int defaultRes;

    /***
     * 设置默认图片
     */
    public void setDefault() {
        mPlayAnimationView.setDefault();
    }

    public void setDefault(int defaultRes) {
        this.defaultRes = defaultRes;
        mPlayAnimationView.setDefault();
    }

    /**
     * 设置动画间隔时间
     *
     * @param duration 多久刷新一次
     */
    public void setDuration(int duration) {
        this.duration = duration;
        mPlayAnimationView.setDuration(duration);
    }

    /**
     * 启动动画
     */
    public synchronized void start() {
        if (isDebug) {
            LogUtil.i(TAG, "start");
        }
        readTimebase = 0;
        if (!isStart) {
            isStart = true;
            mPlayAnimationView.start();
            mReadBitmapResHandler.sendEmptyMessage(MSG_START);
        }
    }

    /**
     * 是否自动播放
     */
    private boolean isAutoPlay = true;

    /**
     * 设置是否自动播放
     *
     * @param isAutoPlay 是否自动播放
     */
    public void setAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
    }

    public synchronized boolean isStart() {
        return isStart;
    }

    /**
     * 停止动画
     */
    public synchronized void stop() {
        if (isDebug) {
            LogUtil.i(TAG, "stop");
        }
        isStart = false;
    }


    private float alpha;

    private float scaleX;
    private float scaleY;

    /**
     * 设置透明度
     *
     * @param alpha 0 ~ 1.f
     */
    protected void setAlpha(float alpha) {
        this.alpha = alpha;
        mPaint.setAlpha((int) (255 * alpha));
    }

    /**
     * 设置x轴的缩放
     *
     * @param scaleX 0 ~ 1.f
     */
    protected void setScaleX(float scaleX) {
        this.scaleX = scaleX;
        float distanceX = (wight - wight * scaleX) / 2.f;
        mDstRectF.left = (int) distanceX;
        mDstRectF.right = (int) (wight - distanceX);
    }

    /**
     * 设置y轴的缩放
     *
     * @param scaleY 0 ~ 1.f
     */
    protected void setScaleY(float scaleY) {
        this.scaleY = scaleY;
        float distanceY = (height - height * scaleY) / 2.f;//间隔
        mDstRectF.top = (int) distanceY;
        mDstRectF.bottom = (int) (height - distanceY);
    }


    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        wight = w;
        height = h;
        mDstRectF.left = 0;
        mDstRectF.top = 0;
        mDstRectF.right = w;
        mDstRectF.bottom = h;
    }

    @Override
    public boolean onDraw(Canvas canvas) {

        if (isDebug) {
            LogUtil.i(TAG, "do onDraw isStart：" + isStart + "  mBitmap:" + mBitmap);
        }
        if (isStart && mBitmap != null) {
            if (isDebug) {
                LogUtil.i(TAG, "do onDraw");
            }

            if (isCanDraw(readTimebase)) {
                canvas.drawBitmap(mBitmap, mResRect, mDstRectF, mPaint);
            } else {
                onDrawDefault(canvas);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onDrawDefault(Canvas canvas) {
        if (mOptions == null) {
            mOptions = new BitmapFactory.Options();
            mOptions.inMutable = true;//设置为true 复用才生效
            mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            mOptions.inJustDecodeBounds = false;
            mOptions.inSampleSize = Tool.sampleBitmapSize(mOptions, wight, height);
        }
        if (defaultRes != 0) {
            mBitmap = BitmapFactory.decodeResource(Global.getResources(), defaultRes, mOptions);
        }
        if (mBitmap != null) {
            mResRect.left = 0;
            mResRect.top = 0;
            mResRect.right = mBitmap.getWidth();
            mResRect.bottom = mBitmap.getHeight();
            canvas.drawBitmap(mBitmap, mResRect, mDstRectF, mPaint);
        }
        return true;
    }

    @Override
    public void onDestroyed() {
        isStart = false;
    }


    /**
     * 处理读取图片资源的消息
     *
     * @param msg
     */
    private void handleReadBitmapMessage(Message msg) {
        switch (msg.what) {
            case MSG_START:
                readBitmapRes();
                break;
        }
    }


    private void readBitmapRes() {
        while (isStart) {
            SystemClock.sleep(duration);

            if (!isCanDraw(readTimebase)) {
                continue;
            }

            //读取图片资源
            if (readTimebase >= animatorRes.length) {
                readTimebase = 0;
                if (mOnAnimatorPlayListener != null) {
                    mOnAnimatorPlayListener.onPlayComplete();
                }
            }
            int animatorRe = animatorRes[readTimebase];

            if (mOptions == null) {
                mOptions = new BitmapFactory.Options();
                mOptions.inMutable = true;//设置为true 复用才生效
                mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                mOptions.inJustDecodeBounds = false;
                mOptions.inSampleSize = Tool.sampleBitmapSize(mOptions, wight, height);
            }
            if (mBitmap != null) {
                mOptions.inBitmap = mBitmap;
            }
            if (animatorRe != 0) {
                mBitmap = BitmapFactory.decodeResource(Global.getResources(), animatorRe, mOptions);
            }
            if (mBitmap != null) {
                mResRect.left = 0;
                mResRect.top = 0;
                mResRect.right = mBitmap.getWidth();
                mResRect.bottom = mBitmap.getHeight();
            }
            if (isDebug) {
                LogUtil.i(TAG, "do readBitmapRes animatorRe:" + animatorRe + "  mBitmap:" + mBitmap);
            }
            readTimebase++;
        }
    }

    protected boolean isCanDraw(int readTimebase) {
        return true;
    }

}
