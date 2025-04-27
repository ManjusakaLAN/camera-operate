package com.echo.util;

import com.echo.api.hk.HCNetSDK;
import com.echo.api.ys.NetDEVSDKLib;
import com.sun.jna.Native;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SDKLoadUtil {



    // 获取操作平台信息
    public static String getOS() {
        String osName = System.getProperty("os.name");
        String osType;
        if (osName.toLowerCase().startsWith("linux")) {
            osType = "linux";
        } else if (!osName.toLowerCase().startsWith("mac") && !osName.toLowerCase().startsWith("darwin")) {
            if (osName.toLowerCase().startsWith("windows")) {
                osType = "win";
            } else {
                osType = "";
            }
        } else {
            osType = "mac";
        }
        return osType;
    }

    /**
     * 获取加载宇视SDK库
     * mac和linux 默认放置在 /usr/share/ys/
     *
     * @return 返回宇视SDK库路径
     */
    public static NetDEVSDKLib loadYsSDKLibrary() {
        String loadLibrary = "";
        String os = getOS();

        if (os.equals("win")) {
            loadLibrary = System.getProperty("user.dir") + "\\ys\\src\\main\\resources\\lib\\win64\\";
        } else if (os.equals("mac")) {
            loadLibrary = "/usr/share/ys/";
        } else if (os.equals("linux")) {
            loadLibrary = "/usr/share/ys/";
        } else {
            log.error("运行在未知平台");
        }
        String loadSDKLibrary = loadLibrary + "NetDEVSDK";
        log.info("Load YS SDKLibrary Path : {}", loadSDKLibrary);
        return (NetDEVSDKLib) Native.loadLibrary(loadSDKLibrary, NetDEVSDKLib.class);
//        ysLibInstance.NETDEV_Init();

    }

    /**
     * 获取加载海康SDK库
     * mac和linux 默认放置在 /usr/share/hk/
     *
     * @return 返回宇视SDK库路径
     */
    public static HCNetSDK loadHkSDKLibrary() {
        String loadLibrary = "";
        String os = getOS();

        if (os.equals("win")) {
            loadLibrary = System.getProperty("user.dir") + "\\hk\\src\\main\\resources\\lib\\win64\\";
        } else if (os.equals("mac")) {
            loadLibrary = "/usr/share/hk/";
        } else if (os.equals("linux")) {
            loadLibrary = "/usr/share/hk/";
        } else {
            log.error("运行在未知平台");
        }
        String loadSDKLibrary = loadLibrary + "HCNetSDK.dll";
        log.info("Load HK SDKLibrary Path : {}", loadSDKLibrary);
        return (HCNetSDK) Native.loadLibrary(loadSDKLibrary, HCNetSDK.class);
    }
}
