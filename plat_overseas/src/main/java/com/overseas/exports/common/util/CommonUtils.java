package com.overseas.exports.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.overseas.exports.common.InstallationID;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommonUtils {

    public static final String SDCARD_NAME = "game_sdk";
    private static CommonUtils instance;
    private static long lastShowTime;
    public static boolean isArea = false;
    public static String szImei = "";
    public static String szDevIDShort = "";
    public static String szAndroidID = "";
    public static String szWLANMAC = "";
    public static String szBTMAC = "";

    public static int simType = -1;                // 0.移动  1.联通  2.电信
    public static String phoneNum = null;
    public static String pkName = null;
    public static String versionName = null;
    public static String szAppKey = "";
    protected static boolean debugType = false;
    private static String szUniqueDeviceIdID = null;

    public static CommonUtils getInstance() {
        if (instance == null) {
            synchronized (CommonUtils.class) {
                if (instance == null) {
                    instance = new CommonUtils();
                }
            }
        }
        return instance;
    }

    public void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public boolean checkNet(Context context) {
        if (hasConnectedNetwork(context))
            return true;
        if (System.currentTimeMillis() - lastShowTime > 3000) {
            toast(context, "无网络连接...");
            lastShowTime = System.currentTimeMillis();
        }
        return false;
    }

    /**
     * 网络判断
     */
    public boolean hasConnectedNetwork(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = null;
            if ((mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE)) == null) {
                return false;
            }
            return mConnectivityManager.getActiveNetworkInfo() != null;
        } else {
            return false;
        }
    }


    /**
     * 返回版本名字
     * 对应build.gradle中的versionName
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 返回版本号
     * 对应build.gradle中的versionCode
     */
    public static String getVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packInfo.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取Android 版本（4.4、5.0、5.1 ...）
     */
    public String getDeviceversion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取设备型号
     */
    public String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    public String getDeviceId() {
        return szUniqueDeviceIdID;
    }

    @SuppressLint("MissingPermission")
    public String getOldDeviceAccount(Context context) {
        String uid = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            uid = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uid == null || uid.equals("")) {
            try {
                WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                uid = info.getMacAddress().replaceAll(":", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (uid == null || uid.equals("")) {
            uid = szUniqueDeviceIdID;
        }
        return uid;
    }

    /**
     * 获取设备的唯一标识，deviceId
     */
    public String getDeviceId(Context context) {
        StringBuilder deviceId = new StringBuilder();
        deviceId.append("device_");
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if (imei != null && !"".equals(imei)) {
                deviceId.append(imei);
                return deviceId.toString();
            }
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if (sn != null && !"".equals(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            String uuid = InstallationID.getUUID(context);
            if (uuid != null && !"".equals(uuid)) {
                deviceId.append("uuid");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("uuid").append(InstallationID.getUUID(context));
        }
        return deviceId.toString();

    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public void prepareDeviceInfo(Context ctx) {

        try {
            android.content.SharedPreferences sp = ctx.getSharedPreferences("d2eam_sdk", Activity.MODE_PRIVATE);
            szAndroidID = sp.getString("szAndroidID", "");
            szImei = sp.getString("szImei", "");
            szBTMAC = sp.getString("szBTMAC", "");
            szWLANMAC = sp.getString("szWLANMAC", "");
            szDevIDShort = sp.getString("szDevIDShort", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (szDevIDShort == null || szDevIDShort.equals("")) {
            // 新设备ID
            try {
                // Pseudo-Unique ID
                szDevIDShort = "35" + Build.BOARD.length() % 10
                        + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
                        + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
                        + Build.HOST.length() % 10 + Build.ID.length() % 10
                        + Build.MANUFACTURER.length() % 10 + Build.MODEL.length()
                        % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length()
                        % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10;
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                // IMEI
                TelephonyManager TelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                if (TelephonyMgr != null) {
                    szImei = TelephonyMgr.getDeviceId();
                    if (szImei == null) {
                        szImei = "null";
                    }
                }
            } catch (Exception e) {
                szImei = "null";
                e.printStackTrace();
            }

            try {
                // The Android ID
                szAndroidID = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (szAndroidID == null) {
                    szAndroidID = "null";
                }
            } catch (Exception e) {
                szAndroidID = "null";
                e.printStackTrace();
            }

            try {
                // The WLAN MAC Address string
                WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                if (wm != null) {
                    szWLANMAC = wm.getConnectionInfo().getMacAddress();
                    if (szWLANMAC == null) {
                        szWLANMAC = "null";
                    }
                }
            } catch (Exception e) {
                szWLANMAC = "null";
                e.printStackTrace();
            }

            try {
                // The BT MAC Address string
                BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (m_BluetoothAdapter != null) {
                    szBTMAC = m_BluetoothAdapter.getAddress();
                    if (szBTMAC == null) {
                        szBTMAC = "null";
                    }
                }
            } catch (Exception e) {
                szBTMAC = "null";
                e.printStackTrace();
            }

            try {
                android.content.SharedPreferences sp = ctx.getSharedPreferences("d2eam_sdk", Activity.MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = sp.edit();
                editor.putString("szAndroidID", szAndroidID);
                editor.putString("szImei", szImei);
                editor.putString("szBTMAC", szBTMAC);
                editor.putString("szWLANMAC", szWLANMAC);
                editor.putString("szDevIDShort", szDevIDShort);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            String m_szLongID = szAndroidID + szImei + szBTMAC + szWLANMAC + szDevIDShort;

            // compute md5
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());

            // get md5 bytes
            byte p_md5Data[] = m.digest();

            // create a hex string
            String m_szUniqueID = new String();
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);
                // if it is a single digit, make sure it have 0 in front (proper padding)
                if (b <= 0xF)
                    m_szUniqueID += "0";
                // add number to string
                m_szUniqueID += Integer.toHexString(b);
            }

            // hex string to uppercase
            szUniqueDeviceIdID = m_szUniqueID.toUpperCase();
        } catch (Exception e) {
            szUniqueDeviceIdID = "null";
            e.printStackTrace();
        }

        // 电话
        try {
            TelephonyManager telManager = (TelephonyManager) ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);
            phoneNum = telManager.getLine1Number();
        } catch (Exception e) {
            phoneNum = "null";
            e.printStackTrace();
        }

        // 运营商
        try {
            TelephonyManager telManager = (TelephonyManager) ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String operator = telManager.getSimOperator();
            if (operator != null && !operator.isEmpty()) {
                if (operator.equals("46020") || operator.equals("46000")
                        || operator.equals("46002") || operator.equals("46007")) {
                    simType = 0;
                } else if (operator.equals("46001") || operator.equals("46006")
                        || operator.equals("46010")) {
                    simType = 1;
                } else if (operator.equals("46003") || operator.equals("46005")
                        || operator.equals("46011")) {
                    simType = 2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (simType == -1) {
            String strPhoneNum = phoneNum;
            if (strPhoneNum == null) {
                return;
            }

            String strPreNum = null;
            if (strPhoneNum.trim().length() == 11) {
                strPreNum = strPhoneNum.substring(0, 3);

                int simMobile[] = new int[]{134, 135, 136, 137, 138, 139,
                        147, 150, 151, 152, 157, 158, 159, 182, 183, 184, 187,
                        188};
                int simUnicom[] = new int[]{130, 131, 132, 145, 155, 156,
                        185, 186};
                int simTelecom[] = new int[]{133, 153, 180, 181, 189};

                int phoneNumHeader = Integer.parseInt(strPreNum);
                if (Arrays.binarySearch(simMobile, phoneNumHeader) != -1)
                    simType = 0;
                else if (Arrays.binarySearch(simUnicom, phoneNumHeader) != -1)
                    simType = 1;
                else if (Arrays.binarySearch(simTelecom, phoneNumHeader) != -1)
                    simType = 2;
            }
        }
    }

    /**
     * SD卡判断,创建文件夹
     */
    public String getSDPath() {
        File sdDir = null;
        File file = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            file = new File(sdDir + "/" + SDCARD_NAME);
        } else {
            return null;
        }
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file.toString();
    }

    /**
     * 获取AndroidManifest.xml里 Meta的值
     *
     * @param context 上下文
     * @param name    Meta-key
     * @return Meta-value
     */
    public static String getMetaData(Context context, String name) {
        String value = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取MAC地址
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public String getLocalMacAddressFromIp(Context context) {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress
                    .getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac_s;
    }

    public String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    /**
     * 获取Ip地址
     *
     * @return
     */
    public String getLocalIpAddress() {
        try {
            String ipv4;
            List<NetworkInterface> nilist = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                List<InetAddress> ialist = Collections.list(ni
                        .getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ipv4 = address
                            .getHostAddress())) {
                        return ipv4;
                    }
                }
            }

        } catch (SocketException ex) {
            ex.toString();
        }
        return null;
    }

    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
