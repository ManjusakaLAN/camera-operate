package com.echo;

import com.echo.api.NetDEVSDKLib;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // 加载库
        NetDEVSDKLib netsdk = NetDEVSDKLib.NETDEVSDK_INSTANCE;
        //初始化
        netsdk.NETDEV_Init();
        //登录

        NetDEVSDKLib.NETDEV_DEVICE_LOGIN_INFO_S stDevLoginInfo = new NetDEVSDKLib.NETDEV_DEVICE_LOGIN_INFO_S();

        String strUserName = "admin";
        String strPassword = "admin";
        String strIPAddr = "192.168.1.100";

        System.arraycopy(strUserName.getBytes(), 0, stDevLoginInfo.szUserName, 0, strUserName.getBytes().length);
        System.arraycopy(strPassword.getBytes(), 0, stDevLoginInfo.szPassword, 0, strPassword.getBytes().length);
        System.arraycopy(strIPAddr.getBytes(), 0, stDevLoginInfo.szIPAddr, 0, strIPAddr.getBytes().length);

        stDevLoginInfo.szUserName = strUserName.getBytes(StandardCharsets.UTF_8);

        stDevLoginInfo.dwPort = 80;


//       设备类型枚举定义
//        typedef enum tagNETDEVDeviceMainType
//        {
//            NETDEV_DTYPE_MAIN_ENCODE = 0, /* 编码设备*/
//            NETDEV_DTYPE_MAIN_DECODE = 1, /* 解码设备*/
//            NETDEV_DTYPE_MAIN_VMS = 2, /* 一体机设备*/
//            NETDEV_DTYPE_MAIN_DA = 3, /* 代理设备*/
//            NETDEV_DTYPE_MAIN_CLOUD = 4, /* 云端编码设备 */
//            NETDEV_DTYPE_MAIN_BAYONET = 5, /* 卡口设备*/
//            NETDEV_DTYPE_MAIN_ACS = 6, /* 门禁主机设备*/
//            NETDEV_DTYPE_MAIN_ALARMHOST = 7, /* 报警主机设备*/
//            NETDEV_DTYPE_MAIN_EXITENTRANCE = 8, /* 出入口设备*/
//            NETDEV_DTYPE_MAIN_LOCK = 9, /* 锁设备*/
//            NETDEV_DTYPE_MAIN_PLAYER_MANAGER = 10, /* 播放盒管理设备*/
//            NETDEV_DTYPE_MAIN_PLAYER = 11, /* 播放盒*/
//            NETDEV_DTYPE_MAIN_UNKNOWN = 0XFF /* 未知设备*/
//        }NETDEV_DEVICE_MAIN_TYPE_E;

        NetDEVSDKLib.NETDEV_SELOG_INFO_S logInfoS = new NetDEVSDKLib.NETDEV_SELOG_INFO_S();
        Pointer userId = netsdk.NETDEV_Login_V30(stDevLoginInfo, logInfoS);

        log.info("userId:{}", userId);
        // 查询组织信息
        Pointer pointer = netsdk.NETDEV_FindDevList(userId, NetDEVSDKLib.NETDEV_DEVICE_MAIN_TYPE_E.NETDEV_DTYPE_MAIN_ENCODE);

        NetDEVSDKLib.NETDEV_UPNP_NAT_STATE_S netdevUpnpNatStateS = new NetDEVSDKLib.NETDEV_UPNP_NAT_STATE_S();
        boolean b = netsdk.NETDEV_GetUpnpNatState(userId, netdevUpnpNatStateS);
        log.info("b:{}", b);
        log.info("netdevUpnpNatStateS:{}", netdevUpnpNatStateS);

        netsdk.NETDEV_SetDiscoveryCallBack(new NetDEVSDKLib.NETDEV_DISCOVERY_CALLBACK_PF() {
            @Override
            public void invoke(NetDEVSDKLib.NETDEV_DISCOVERY_DEVINFO_S pstDevInfo, Pointer lpUserData) {
                log.info("pstDevInfo:{}", pstDevInfo);
            }
        }, pointer);

        // 查询通道信息
        netsdk.NETDEV_Discovery("0.0.0.0", "0.0.0.0");


    }
}

