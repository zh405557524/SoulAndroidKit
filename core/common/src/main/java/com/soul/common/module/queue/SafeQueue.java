package com.soul.common.module.queue;

/**
 * Description: 安全的队列
 * Author: 祝明
 * CreateDate: 2021/10/9 17:51
 * UpdateUser:
 * UpdateDate: 2021/10/9 17:51
 * UpdateRemark:
 */
public class SafeQueue<T> extends Queue<T> {

    /**
     * 对象锁
     *
     * @hide
     */
    private final Object sSafeQueueSync = new Object();

    @Override
    public void push(T t) {
        synchronized (sSafeQueueSync) {
            super.push(t);
        }
    }

    @Override
    public T front() {
        synchronized (sSafeQueueSync) {
            return super.front();
        }
    }

    @Override
    public int size() {
        synchronized (sSafeQueueSync) {
            return super.size();
        }
    }

    @Override
    public void clear() {
        synchronized (sSafeQueueSync) {
            super.clear();
        }
    }
}
