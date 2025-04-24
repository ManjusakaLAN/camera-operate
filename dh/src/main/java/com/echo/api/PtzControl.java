package com.echo.api;
import com.netsdk.lib.NetSDKLib;

public class PtzControl {
    public static NetSDKLib netsdk = NetSDKLib.NETSDK_INSTANCE;

    public static boolean ptzControlStart(NetSDKLib.LLong m_hLoginHandle, Integer NET_PTZ_ControlType) {
        return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, 0, NET_PTZ_ControlType, 2, 4, 0, 0);
    }

    public static boolean ptzControlEnd(NetSDKLib.LLong m_hLoginHandle, Integer NET_PTZ_ControlType) {
        return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, 0, NET_PTZ_ControlType, 0, 0, 0, 1);
    }

    /**
     * 向上
     */
    public static boolean ptzControlUpEnd(NetSDKLib.LLong m_hLoginHandle) {
        return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, 0, NetSDKLib.NET_PTZ_ControlType.NET_PTZ_UP_CONTROL, 0, 0, 0, 1);
    }

    /**
     * 向下
     */

    public static boolean ptzControlDownEnd(NetSDKLib.LLong m_hLoginHandle) {
        return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, 0, NetSDKLib.NET_PTZ_ControlType.NET_PTZ_DOWN_CONTROL, 0, 0, 0, 1);
    }

    /**
     * 向左
     */

    public static boolean ptzControlLeftEnd(NetSDKLib.LLong m_hLoginHandle) {
        return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, 0, NetSDKLib.NET_PTZ_ControlType.NET_PTZ_LEFT_CONTROL, 0, 0, 0, 1);
    }

    /**
     * 向右
     */

    public static boolean ptzControlRightEnd(NetSDKLib.LLong m_hLoginHandle) {
        return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, 0, NetSDKLib.NET_PTZ_ControlType.NET_PTZ_RIGHT_CONTROL, 0, 0, 0, 1);
    }

    public static boolean ptzControl(NetSDKLib.LLong m_hLoginHandle, Integer NET_PTZ_ControlType) {
        boolean b = netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, 0, NET_PTZ_ControlType, 0, 0, 0, 1);
        return b;
    }
}
