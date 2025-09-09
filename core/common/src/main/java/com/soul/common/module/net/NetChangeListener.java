package com.soul.common.module.net;

/**
 * Description:网络监听
 * Author: 祝明
 * CreateDate: 2019-07-02 17:54
 * UpdateUser:
 * UpdateDate: 2019-07-02 17:54
 * UpdateRemark:
 */
public interface NetChangeListener {

    /**
     * 网络状态发生改变
     *
     * @param netType {@link INetManager.NetType} 网络状态
     */
    void onNetStateChange(int netType);

}
