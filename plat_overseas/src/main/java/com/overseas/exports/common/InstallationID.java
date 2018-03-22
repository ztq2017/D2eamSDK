package com.overseas.exports.common;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class InstallationID {

    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String getUUID(Context context) {
        if (sID == null) {

            try {
                String dir = getDirectory();
                File file = new File(dir + File.separator + "sdk.rc");
                if (!file.exists()) {
                    writeInstallationFileToSD(file);
                }
                sID = readInstallationFileFromSD(file);

            } catch (Exception e) {
                getUUIDInstallation(context);
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    public synchronized static String getUUIDInstallation(Context context) {
        if (sID == null) {

            try {
                File installation = new File(context.getFilesDir(),
                        INSTALLATION);

                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation)
            throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation)
            throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();

        out.write(id.getBytes());
        out.close();
    }

    private static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return null;
        }
    }

    private static String getDirectory() {
        String dir = getSDPath() + "/" + "6lapp";
        File file = new File(dir);
        if (!file.exists())
            file.mkdirs();
        return file.toString();
    }

    private static void writeInstallationFileToSD(File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        String id = UUID.randomUUID().toString();

        out.write(id.getBytes());
        out.close();

    }

    private static String readInstallationFileFromSD(File file)
            throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }
}