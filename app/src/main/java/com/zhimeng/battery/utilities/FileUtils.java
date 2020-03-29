package com.zhimeng.battery.utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

public class FileUtils {

    public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
    /**
     * app 所有文件根目录
     */
    public static final File ROOT_PATH = new File(FILE_SDCARD + File.separator + "tatame" + File.separator);
    /**
     * app 所有用户文件目录
     */
    public static final String USER_DATA_PATH = File.separator + "User" + File.separator;
    /**
     * app 所有用户图片文件目录
     */
    public static final String USER_DATA_PATH_IMG = File.separator + "Image" + File.separator;
    public static final File TATAME_FILE_FOLDER = new File(ROOT_PATH + USER_DATA_PATH + USER_DATA_PATH_IMG);

    public static File getAvatarDisplayImageFile(Context context) {
        return new File(context.getFilesDir().getAbsolutePath() + "avatar.jpg");
    }

    /**
     * 解压一个压缩文档 到指定位置
     *
     * @param zipFileString 压缩包的名字
     * @param outPathString 指定的路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString,
                                   String outPathString) throws Exception {
        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFileString));
        java.util.zip.ZipEntry zipEntry;
        String szName = "";

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                file.createNewFile();
                java.io.FileOutputStream out = new java.io.FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }//end of while

        inZip.close();

    }

    // 正在推出的时候删除用户的所有数据;
    public static String getAlohaImgPath() {

        String path = TATAME_FILE_FOLDER.getAbsolutePath();
        if (TATAME_FILE_FOLDER.exists()) {
            return path;
        }
        if (createFolder(path)) {
            return path;
        } else {
            return null;
        }
    }

    /**
     * 创建目录
     *
     * @return 如果目录已经存在，或者目录创建成功，返回true；如果目录创建失败，返回false
     */
    public static boolean createFolder(String folderPath) {
        boolean success = false;
        try {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                success = true;
            } else {
                success = folder.mkdirs();
            }
        } catch (SecurityException e) {
            Log.e("FILE", "createFolder: ", e);
            e.printStackTrace();
        }
        return success;
    }

    public static long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
