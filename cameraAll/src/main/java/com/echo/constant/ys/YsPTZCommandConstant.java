package com.echo.constant.ys;

import com.echo.api.ys.NetDEVSDKLib;

/**
 * 宇视 云台指令枚举
 */
public class YsPTZCommandConstant {

    /**
     * 向上
     */
    public static final int UP = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_TILTUP;


    /**
     * 向下
     */
    public static final int DOWN = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_TILTDOWN;

    /**
     * 向左
     */
    public static final int LEFT = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_PANLEFT;

    /**
     * 向右
     */
    public static final int RIGHT = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_PANRIGHT;

    /**
     * 向左上
     */
    public static final int LEFT_UP = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_LEFTUP;

    /**
     * 向右上
     */
    public static final int RIGHT_UP = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_RIGHTUP;

    /**
     * 向左下
     */
    public static final int LEFT_DOWN = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_LEFTDOWN;

    /**
     * 向右下
     */
    public static final int RIGHT_DOWN = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_RIGHTDOWN;

    /**
     * 放大
     */
    public static final int ZOOM_IN = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_ZOOMTELE;

    /**
     * 缩小
     */
    public static final int ZOOM_OUT = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_ZOOMWIDE;

    /**
     * 近聚焦
     */
    public static final int FOCUS_NEAR = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_FOCUSNEAR;

    /**
     * 远聚焦
     */
    public static final int FOCUS_FAR = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_FOCUSFAR;

    /**
     * 停止
     */
    public static final int STOP = NetDEVSDKLib.NETDEV_PTZ_E.NETDEV_PTZ_ALLSTOP;

}
