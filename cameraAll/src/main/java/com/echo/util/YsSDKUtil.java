package com.echo.util;

import com.echo.api.hk.HCNetSDK;
import com.echo.api.ys.NetDEVSDKLib;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YsSDKUtil {

    // 加载宇视动态库
    public static NetDEVSDKLib ysLibInstance = SDKLoadUtil.loadYsSDKLibrary();

    /**
     * 初始化 SDK
     */
    public static Boolean init() {
        // 初始化
        boolean b = ysLibInstance.NETDEV_Init();
        if (!b){
            log.error("初始化失败 错误码为: {}", ysLibInstance.NETDEV_GetLastError());
        }
        return b;
    }

    /**
     * 登出
     * @param lUserID 登录句柄
     */
    public static void logout(Pointer lUserID) {
        // 登出
        boolean b = ysLibInstance.NETDEV_Logout(lUserID);
        if (!b){
            log.error("登出失败 错误码为: {}", ysLibInstance.NETDEV_GetLastError());
        }
        // 清理SDK
        ysLibInstance.NETDEV_Cleanup();
    }

}
