package com.soul.common.module.queue;

import android.os.SystemClock;

/**
 * Description: 字节数组读写
 * Author: 祝明
 * CreateDate: 2021/10/11 14:33
 * UpdateUser:
 * UpdateDate: 2021/10/11 14:33
 * UpdateRemark:
 */
public class ByteArrayQueue {
    private final SafeQueue<byte[]> mSafeQueue;
    private int mStartPos;
    private byte[] mFront;

    private int maxSize = 50;
    private int mLength;

    public ByteArrayQueue() {
        mSafeQueue = new SafeQueue<>();
    }

    /**
     * 读取数据
     *
     * @param bytes
     * @param size
     */
    public int read(byte[] bytes, int size) {
        if (mStartPos == 0 || mFront == null) {
            mFront = mSafeQueue.front();
        }
        if (mFront == null || mFront.length == 0) {
            SystemClock.sleep(1);
            return 0;
        }
        mLength = mFront.length - mStartPos;
        if (mLength <= 0) {
            mStartPos = 0;
            return 0;
        }
        if (mStartPos < 0) {
            mStartPos = 0;
        }
        if (size < mLength) {
            mLength = size;
            System.arraycopy(mFront, mStartPos, bytes, 0, mLength);
            mStartPos += mLength;
        } else {
            System.arraycopy(mFront, mStartPos, bytes, 0, mLength);
            mStartPos = 0;
        }
        return mLength;
    }


    /**
     * 设置最大缓存数量
     *
     * @param size
     */
    public void setMaxCacheSize(int size) {
        maxSize = size;
    }

    public int size() {
        return mSafeQueue.size();
    }

    /**
     * 写入数据
     *
     * @param bytes
     * @param size
     */
    public void write(byte[] bytes, int size) {
        while (mSafeQueue.size() > maxSize) {
            mSafeQueue.front();
        }
        Queue.Element<byte[]> obtain = mSafeQueue.obtain();
        byte[] temp = obtain.obj;
        if (temp == null || temp.length < size) {
            temp = new byte[size];
            obtain.obj = temp;
        }
        System.arraycopy(bytes, 0, temp, 0, size);
        mSafeQueue.push(obtain);
    }


}
