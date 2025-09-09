package com.soul.common.module.queue;

/**
 * Description: 队列 先进先出
 * Author: 祝明
 * CreateDate: 2021/9/30 14:32
 * UpdateUser:
 * UpdateDate: 2021/9/30 14:32
 * UpdateRemark:
 */
public class Queue<T> {

    private Element<T> mTElement;
    /**
     * 元素池，废弃的对象放入元素池，需要时取出来
     */
    private Element<T> elementPool;

    private int size;

    /**
     * 入列
     *
     * @param t
     */
    public void push(T t) {
        Element<T> element = obtain();
        element.obj = t;
        push(element);
    }

    public void push(Element<T> element) {
        Element<T> p = mTElement;
        if (p == null) {
            element.next = p;
            mTElement = element;
        } else {
            Element<T> pre;
            do {
                pre = p;
                p = p.next;
            } while (p != null);
            pre.next = element;
        }
        size++;
    }

    /**
     * 出列
     *
     * @return
     */
    public T front() {
        if (mTElement == null) {
            return null;
        }
        Element<T> e = mTElement;
        mTElement = e.next;
        size--;
        T obj = e.obj;
        recycle(e);
        return obj;
    }

    /**
     * 队列大小
     *
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * 释放所有资源
     */
    public void clear() {
        Element<T> next;
        synchronized (sPoolSync) {//清空元素池
            Element<T> p = elementPool;
            do {
                if (p == null) {
                    break;
                }
                next = p.next;
                p.next = null;
                p = next;
            } while (p != null);
        }

        Element<T> p = mTElement;

        do {
            if (p == null) {
                break;
            }
            next = p.next;
            p.next = null;
            p = next;
        } while (p != null);
    }

    /**
     * 元素池的最大值
     */
    private static final int MAX_POOL_SIZE = 100;

    /**
     * 元素池大小
     */
    private static int sPoolSize = 0;

    /**
     * 对象锁
     *
     * @hide
     */
    private final Object sPoolSync = new Object();

    /**
     * 从元素池获取元素对象
     *
     * @return
     */
    public Element<T> obtain() {
        synchronized (sPoolSync) {
            if (elementPool != null) {
                Element<T> element = elementPool;
                elementPool = element.next;
                element.next = null;
                sPoolSize--;
                return element;
            }
        }
        return new Element<>();
    }

    /**
     * 将废弃元素放入元素池中
     *
     * @param element
     */
    private void recycle(Element<T> element) {
        synchronized (sPoolSync) {//锁
            if (sPoolSize < MAX_POOL_SIZE) {
                element.next = elementPool;
                elementPool = element;
                sPoolSize++;
            }
        }
    }


    public static class Element<T> {

        /**
         * 下一个数据
         */
        Element<T> next;

        /**
         * 具体数据
         */
        public T obj;

    }

}
