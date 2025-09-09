package com.soul.common.utils;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;

/**
 * Description: 日期
 * Author: 祝明
 * CreateDate: 2024/6/13 11:06
 * UpdateUser:
 * UpdateDate: 2024/6/13 11:06
 * UpdateRemark:
 */
public class DateUtils {

    public static String changeTimeToDay(String originalDateTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return changeDate(originalDateTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
        }
        return originalDateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String changeDate(String originalDateTime, String format, String changeFormat) {

        try {
            // 定义输入的日期时间格式
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(format);
            // 解析日期时间字符串
            LocalDateTime dateTime = LocalDateTime.parse(originalDateTime, inputFormatter);

            // 定义输出的日期格式
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(changeFormat);
            // 转换成只有日期的字符串
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return originalDateTime;
    }
}
