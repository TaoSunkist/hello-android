package com.zhimeng.battery.app;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class TatameFileManager {

    public static void profileExternalFiles(Context context) {
        final File root = context.getExternalFilesDir("");
        final ArrayList<File> queue = new ArrayList<>();
        queue.add(root);

        long fileSizeTotal = 0;

        while (!queue.isEmpty()) {
            final File curr = queue.remove(queue.size() - 1);
            if (curr.isDirectory()) {
                /* A folder */
                for (File f : curr.listFiles()) {
                    queue.add(f);
                }
            } else {
                /* A Single File */
                fileSizeTotal += curr.length();
            }
        }
    }

    /**
     * Temporary file for uploading. 临时文件，专门用来上传.
     *
     * @param suffix optional name suffix
     * @return temp file
     */
    public static File getTempImageInputFile(Context context, final String suffix) {
        return new File(
                context.getCacheDir().getAbsoluteFile() + File.separator + "input" + suffix + ".JPG");
    }

    public static File getDownloadFolderImageOutputFile(final String suffix) {
        /* TODO Android Q 里面这种方法deprecated了，所以之后得换一种方式*/
        return new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsoluteFile()
                        + File.separator
                        + "Tatame"
                        + suffix
                        + ".JPG");
    }

    public static File getDownloadFolderVideoOutputFile(final String suffix) {
        /* TODO Android Q 里面这种方法deprecated了，所以之后得换一种方式*/
        return new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsoluteFile()
                        + File.separator
                        + "Tatame"
                        + suffix
                        + ".mp4");
    }

    /**
     * Temporary file for uploading. 临时文件，专门用来上传.
     *
     * @return temp file
     */
    public static File getTempImageInputFile(Context context) {
        return getTempImageInputFile(context, "");
    }

    /**
     * 返回一个文件可以临时存放下载的音频，暂时不做音频的缓存，只能临时下载临时播放.
     *
     * @return
     */
    public static File getTempVoiceFile(Context context) {
        return new File(context.getCacheDir().getAbsoluteFile() + File.separator + "voice");
    }
}
