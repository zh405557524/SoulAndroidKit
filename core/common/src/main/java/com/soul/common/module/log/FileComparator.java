package com.soul.common.module.log;

import java.io.File;
import java.util.Comparator;

/**
 * Description:
 * Author: 祝明
 * CreateDate: 2020/1/9 15:49
 * UpdateUser:
 * UpdateDate: 2020/1/9 15:49
 * UpdateRemark:
 */
public class FileComparator implements Comparator<File> {

    public int compare(File file1, File file2) {
        if (file1.lastModified() < file2.lastModified()) {
            return -1;
        } else {
            return 1;
        }
    }
}
