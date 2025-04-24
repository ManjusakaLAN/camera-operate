package com.echo;

import com.echo.api.DeviceLoginControl;
import com.echo.api.PtzControl;
import com.echo.util.CommonUtil;
import com.netsdk.lib.LibraryLoad;
import com.netsdk.lib.NetSDKLib;
import com.sun.jna.Library;
import com.sun.jna.Native;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {

        if (CommonUtil.getOSAndArch().equals("win")) {
            log.info("运行在 win");
            LibraryLoad.setExtractPath("E:\\cameraSDK\\cameraJavaDemo\\dh\\src\\main\\resources\\lib\\win64");
        } else if (CommonUtil.getOSAndArch().equals("mac")) {
            log.info("运行在 mac");
            LibraryLoad.setExtractPath("/usr/share/dh");
        } else if (CommonUtil.getOSAndArch().equals("linux")) {
            log.info("运行在 linux");
            LibraryLoad.setExtractPath("/usr/share/dh");
        } else {
            log.error("运行在未知平台");
        }

        DeviceLoginControl deviceLoginControl = new DeviceLoginControl();
        // 登录
        boolean login = deviceLoginControl.login("192.168.32.25", 8000, "admin", "admin123");
        // 控制摄像头向上
        PtzControl.ptzControlUpEnd(deviceLoginControl.getM_hLoginHandle());
    }
}