package com.soul.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import com.soul.common.utils.FileHelp;
import com.soul.common.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint({"SimpleDateFormat", "SdCardPath"})
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    private UncaughtExceptionHandler mDefaultHandler;

    //CrashHandler实例
    private static CrashHandler instance;

    //程序的Context对象
    private Context context;

    private Map<String, String> infos = new HashMap<String, String>();

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null)
            instance = new CrashHandler();
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        File file = new File(Global.getExternalCacheDir() + "crash");
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            LogUtil.i(TAG, "kdirs:" + mkdirs);
        }
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        restart();
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    private void restart() {
        Intent intent = Global.getContext().getPackageManager().getLaunchIntentForPackage(Global.getContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Global.getContext().startActivity(intent);
        }

    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        LogUtil.e(TAG, "uncaughtException:" + ex);

        //收集设备参数信息
        collectDeviceInfo(context);
        //write failed: ENOSPC (No space left on device)
        long availableSize = FileHelp.getSDAvailableSize();
        if (availableSize < 1024 * 1024) {
            return false;
        }


        //保存日志文件
        saveCatchInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
                String processName = Global.getProcessName(Global.getContext());
                if (!TextUtils.isEmpty(processName)) {
                    infos.put("processName", processName);
                }


            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private String saveCatchInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Global.getExternalCacheDir() + "crash" + File.separator;
                deleteFile(path);

                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
                File file = new File(path + fileName);

                LogUtil.i("Tag", "file_path:" + file.getPath());
                FileOutputStream fos = new FileOutputStream(file);

                fos.write(sb.toString().getBytes());

                sendCrashLog2PM(path + fileName);
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            //System.exit(0);
        }

        return null;
    }

    private void sendCrashLog2PM(String fileName) {
        if (!new File(fileName).exists()) {
            //	Toast.makeText(context, "日志文件不存在！", Toast.LENGTH_SHORT).show();
            return;
        }
        FileInputStream fis = null;
        BufferedReader reader = null;
        String s = null;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
            while (true) {
                s = reader.readLine();
                if (s == null)
                    break;

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void deleteFile(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null || file.length() <= 0) {
            Log.e("error", "空目录");
            return;
        }
        //将需要的子文件信息存入到FileInfo里面
        ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(files));
        Collections.sort(fileList, new FileComparator());//通过重写Comparator的实现类FileComparator来实现按文件创建时间排序。
        if (fileList.size() > 99) {
            fileList.get(0).delete();
        }
    }


}

class FileComparator implements Comparator<File> {
    public int compare(File file1, File file2) {
        boolean lInValid = (file1 == null || !file1.exists());
        boolean rInValid = (file2 == null || !file2.exists());
        boolean bothInValid = lInValid && rInValid;
        if (bothInValid) {
            return 0;
        }

        if (lInValid) {
            return -1;
        }

        if (rInValid) {
            return 1;
        }
        if (file1.lastModified() < file2.lastModified()) {
            return -1;
        } else {
            return 1;
        }
    }
}


