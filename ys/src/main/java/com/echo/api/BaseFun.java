package com.echo.api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BaseFun {
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

    //获取加载SDK库
    public static String LoadSDKLibrary() {
        String loadLibrary = "";
        String os = getOS();

        if (os.equals("win")) {
            loadLibrary = "E:\\cameraSDK\\cameraJavaDemo\\hk\\src\\main\\resources\\lib\\win64\\";
        } else if (os.equals("mac")) {
            loadLibrary = "/usr/share/ys/";
        } else if (os.equals("linux")) {
            loadLibrary = "/usr/share/ys/";
        } else {
            log.error("运行在未知平台");
        }

        String loadSDKLibrary = loadLibrary + "NetDEVSDK";
        log.info("Load SDKLibrary Path : {}", loadSDKLibrary);
        return loadSDKLibrary;
    }
}
