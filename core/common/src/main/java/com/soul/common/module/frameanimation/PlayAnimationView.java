package com.soul.common.module.frameanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.soul.common.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Description: 动画播放View
 * Author: 祝明
 * CreateDate: 2021/9/10 18:32
 * UpdateUser:
 * UpdateDate: 2021/9/10 18:32
 * UpdateRemark:
 */
public class PlayAnimationView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = PlayAnimationView.class.getSimpleName();

    private static boolean isDebug = false;

    /**
     * 启动动画
     */
    private static final int MSG_START = 1;
    /**
     * 画布是否准备好了
     */
    private boolean isPrepare;

    /**
     * 画布宽
     */
    private int wight;
    /**
     * 画布高
     */
    private int height;

    /**
     * 图片资源
     */
    int[] animatorRes;
    /**
     * 绘制间隔时间
     */
    private int duration = 40;
    /**
     * 是否是启动中
     */
    private boolean isStart;

    /**
     * 时间刻度
     */
    private int readTimebase;

    private BitmapFactory.Options mOptions;


    Context context;
    private final SurfaceHolder mHolder;
    private Bitmap mBitmap;
    private final Paint mPaint;
    private final Rect mResRect;
    private final Rect mDstRectF;
    private final PaintFlagsDrawFilter mPfd;
    private final Handler mAnimatorHandler;

    public PlayAnimationView(Context context) {
        this(context, null);
    }

    public PlayAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        HandlerThread animatorTask = new HandlerThread("AnimatorTask");
        animatorTask.start();
        mAnimatorHandler = new Handler(animatorTask.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                handleAnimatorMessage(msg);
            }
        };
        this.context = context;
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        /**------------------------------设置抗锯齿 start--------------------------------*/
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        /**------------------------------设置抗锯齿 end--------------------------------*/
        mResRect = new Rect();
        mDstRectF = new Rect();
        //设置透明背景
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);


    }

    public void setIsDebug(boolean isDebug) {
        PlayAnimationView.isDebug = isDebug;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        wight = w;
        height = h;
        mDstRectF.left = 0;
        mDstRectF.top = 0;
        mDstRectF.right = w;
        mDstRectF.bottom = h;
        for (IAnimation iAnimation : mIAnimation) {
            if (iAnimation != null) {
                iAnimation.onSizeChanged(w, h, oldw, oldh);
            }
        }
    }

    /**
     * 设置动画资源
     */
    public void setAnimatorRes(int[] animatorRes) {
        this.animatorRes = animatorRes;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 设置默认图片
     */
    public void setDefault() {
        //得到一个加锁的画布--->锁一个画布
        Canvas canvas = mHolder.lockCanvas();
        //设置抗锯齿
        do {
            if (canvas == null) {
                break;
            }
            canvas.setDrawFilter(mPfd);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            for (IAnimation iAnimation : mIAnimation) {
                if (iAnimation != null && iAnimation.onDrawDefault(canvas)) {
                    break;
                }
            }
        } while (false);
        if (canvas != null && isPrepare) {
            //释放锁-->解锁一个画布
            try {
                mHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动动画
     */
    public synchronized void start() {
        if (!isStart) {
            isStart = true;
            mAnimatorHandler.sendEmptyMessageDelayed(MSG_START, (long) (duration / 2.f));
        }
    }

    /**
     * 停止动画
     */
    public synchronized void stop() {
        isStart = false;
    }

    private List<IAnimation> mIAnimation = new ArrayList<>();


    /**
     * 绑定外部动画
     *
     * @param iAnimation 外部动画
     */
    public void attachAnimationView(IAnimation iAnimation) {
        if (!mIAnimation.contains(iAnimation)) {
            mIAnimation.add(iAnimation);
        }
        if (iAnimation != null) {
            iAnimation.onSizeChanged(wight, height, 0, 0);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        //创建bottom
        isPrepare = true;
        if (mCallback != null) {
            mCallback.surfaceCreated(holder);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (mCallback != null) {
            mCallback.surfaceChanged(holder, format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        isStart = false;
        synchronized (this) {
            isPrepare = false;
        }

        if (mCallback != null) {
            mCallback.surfaceDestroyed(holder);
        }
        mAnimatorHandler.getLooper().quitSafely();
    }

    /**
     * 处理动画启动的消息
     *
     * @param msg
     */
    private void handleAnimatorMessage(Message msg) {
        switch (msg.what) {
            case MSG_START:
                startBitmapAnimation();
                break;
        }
    }


    /**
     * 启动动画
     */
    private void startBitmapAnimation() {
        while (isStart) {
            //  这个里面就是需要去更新ui操作
            SystemClock.sleep(duration);
            if (isDebug) {
                LogUtil.i(TAG, "do startBitmapAnimation");
            }
            synchronized (this) {
                if (isPrepare) {
                    drawUI();
                }
            }
        }
    }

    /**
     * 所有的绘制操作就放到这个方法里面来
     */
    private void drawUI() {

        //得到一个加锁的画布--->锁一个画布
        Canvas canvas = mHolder.lockCanvas();
        //设置抗锯齿
        do {
            if (canvas == null) {
                break;
            }
            canvas.setDrawFilter(mPfd);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            for (IAnimation iAnimation : mIAnimation) {
                if (iAnimation != null && iAnimation.onDraw(canvas)) {
                    break;
                }
            }
        } while (false);
        Surface surface = mHolder.getSurface();
        if (canvas != null && isPrepare && surface != null && surface.isValid()) {
            //释放锁-->解锁一个画布
            try {
                mHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SurfaceHolder.Callback mCallback;


    public void setCallBack(SurfaceHolder.Callback callback) {
        mCallback = callback;
    }
}
