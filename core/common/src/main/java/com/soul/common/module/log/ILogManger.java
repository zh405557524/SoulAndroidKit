package com.soul.common.module.log;

/**
 * Description:
 * Author: 祝明
 * CreateDate: 2020/1/9 14:38
 * UpdateUser:
 * UpdateDate: 2020/1/9 14:38
 * UpdateRemark:
 */
public interface ILogManger {
    void init();

    /**
     * 初始化log管理
     *
     * @param logPath log文件保存地址
     */
    void init(String logPath);

    /**
     * 设置配置
     *
     * @param isSaveLog 是否保存日志
     */
    void setConfig(boolean isSaveLog);

    /**
     * 保存日志信息
     *
     * @param msg 日志内容
     */
    void saveLog(String msg);
}
