package cn.com.bluemoon.delivery.db.manager;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cn.com.bluemoon.liblog.LogUtils;

/**
 * Created by bm on 2017/7/21.
 */

public class GreenDaoUtil {

    /**
     * 获取本地ip地址
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress mInetAddress = enumIpAddr.nextElement();
                    if (!mInetAddress.isLoopbackAddress() && mInetAddress instanceof Inet4Address) {
                        return mInetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtils.e(ex.getMessage());
        }
        return null;
    }
}
