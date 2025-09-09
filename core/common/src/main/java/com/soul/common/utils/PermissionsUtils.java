package com.soul.common.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import com.soul.common.Global;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * * @author soul
 *
 * @项目名:Compilations
 * @包名: com.soul.library.utils
 * @作者：祝明
 * @描述：TODO
 * @创建时间：2017/2/22 13:23
 */

public class PermissionsUtils {
    //申请权限的回调
    private static final int requestCode = 1;

    public static void lacksPermissions(Activity activity, String... permissions) {
        lacksPermissions(activity, requestCode, permissions);
    }

    public static void lacksPermissions(Activity activity, int requestCode, String... permissions) {
        ArrayList<String> strings = new ArrayList<>();
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                strings.add(permission);
            }
        }
        String[] strings1 = new String[strings.size()];
        String[] strings2 = strings.toArray(strings1);
        openPermissions(activity, strings2, requestCode);

    }

    /**
     * 权限是否齐全
     *
     * @param permissions 权限
     * @return true 所有权限齐全； false 有缺陷缺失
     */
    public static boolean checkPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(Global.getContext(), permission) == PackageManager.PERMISSION_DENIED;
    }


    private static void openPermissions(Activity context, String[] strings, int requestCode) {
        if (strings != null && strings.length > 0) {
            ActivityCompat.requestPermissions(context, strings, requestCode);
        }
    }

}
