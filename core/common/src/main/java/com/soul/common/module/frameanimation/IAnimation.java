package com.soul.common.module.frameanimation;

import android.graphics.Canvas;

/**
 * Description:
 * Author: 祝明
 * CreateDate: 2022/1/14 15:52
 * UpdateUser:
 * UpdateDate: 2022/1/14 15:52
 * UpdateRemark:
 */
public interface IAnimation {

     void onSizeChanged(int w, int h, int oldw, int oldh);

     boolean onDraw(Canvas canvas);

     /**
      * 设置默认界面
      */
     boolean onDrawDefault(Canvas canvas);

     void onDestroyed();
}
