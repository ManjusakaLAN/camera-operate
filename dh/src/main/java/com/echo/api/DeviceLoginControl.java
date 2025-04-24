package com.echo.api;
import com.netsdk.lib.NetSDKLib;
import com.netsdk.lib.ToolKits;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * 设备登录
 */
@Slf4j
@Data
public class DeviceLoginControl {
    public static NetSDKLib netsdk = NetSDKLib.NETSDK_INSTANCE;
    public NetSDKLib configsdk = NetSDKLib.CONFIG_INSTANCE;
    public NetSDKLib.LLong m_hPlayHandleOne = new NetSDKLib.LLong(0);
    private Vector<String> chnlist = new Vector<String>();
    public NetSDKLib.LLong m_hPlayHandleTwo = new NetSDKLib.LLong(0);
    // 设备信息
    public NetSDKLib.NET_DEVICEINFO_Ex m_stDeviceInfo = new NetSDKLib.NET_DEVICEINFO_Ex();
    private NetSDKLib.LLong lRealHandle;
    private String videoStoragePath;

    private Long cycleTime;
    private Long cleanCycle;
    // 登陆句柄
    public NetSDKLib.LLong m_hLoginHandle = new NetSDKLib.LLong(0);

    private static boolean bInit = false;
    private static boolean bLogopen = false;

    public DeviceLoginControl() {
    }

    public DeviceLoginControl(NetSDKLib.fDisConnect disConnect, NetSDKLib.fHaveReConnect fHaveReConnect) {
        init(disConnect, fHaveReConnect);
    }

    public boolean init(NetSDKLib.fDisConnect disConnect, NetSDKLib.fHaveReConnect haveReConnect) {
        bInit = netsdk.CLIENT_Init(disConnect, null);
        if (!bInit) {
            log.info("Initialize SDK failed");
            return false;
        }
        //打开日志，可选
//        NetSDKLib.LOG_SET_PRINT_INFO setLog = new NetSDKLib.LOG_SET_PRINT_INFO();
//        File path = new File("./sdklog/");
//        if (!path.exists()) {
//            path.mkdir();
//        }
//        String logPath = path.getAbsoluteFile().getParent() + "\\sdklog\\" + ToolKits.getDate() + ".log";
//        setLog.nPrintStrategy = 0;
//        setLog.bSetFilePath = 1;
//        System.arraycopy(logPath.getBytes(), 0, setLog.szLogFilePath, 0, logPath.getBytes().length);
//        System.out.println(logPath);
//        setLog.bSetPrintStrategy = 1;
//        bLogopen = netsdk.CLIENT_LogOpen(setLog);
//        if (!bLogopen) {
//            log.error("Failed to open NetSDK log");
//        }

        // 设置断线重连回调接口，设置过断线重连成功回调函数后，当设备出现断线情况，SDK内部会自动进行重连操作
        // 此操作为可选操作，但建议用户进行设置
        netsdk.CLIENT_SetAutoReconnect(haveReConnect, null);

        //设置登录超时时间和尝试次数，可选
        int waitTime = 5000; //登录请求响应超时时间设置为5S
        int tryTimes = 1;    //登录时尝试建立链接1次
        netsdk.CLIENT_SetConnectTime(waitTime, tryTimes);


        // 设置更多网络参数，NET_PARAM的nWaittime，nConnectTryNum成员与CLIENT_SetConnectTime
        // 接口设置的登录设备超时时间和尝试次数意义相同,可选
        NetSDKLib.NET_PARAM netParam = new NetSDKLib.NET_PARAM();
        netParam.nConnectTime = 10000;      // 登录时尝试建立链接的超时时间
        netParam.nGetConnInfoTime = 3000;   // 设置子连接的超时时间
        netParam.nGetDevInfoTime = 3000;//获取设备信息超时时间，为0默认1000ms
        netsdk.CLIENT_SetNetworkParam(netParam);

        return true;
    }

    public boolean login(String m_strIp, int m_nPort, String m_strUser, String m_strPassword) {
        //IntByReference nError = new IntByReference(0);
        //入参
        NetSDKLib.NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY pstInParam = new NetSDKLib.NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY();
        pstInParam.nPort = m_nPort;
        pstInParam.szIP = m_strIp.getBytes();
        pstInParam.szPassword = m_strPassword.getBytes();
        pstInParam.szUserName = m_strUser.getBytes();
        //出参
        NetSDKLib.NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY pstOutParam = new NetSDKLib.NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY();
        pstOutParam.stuDeviceInfo = m_stDeviceInfo;
        //m_hLoginHandle = netsdk.CLIENT_LoginEx2(m_strIp, m_nPort, m_strUser, m_strPassword, 0, null, m_stDeviceInfo, nError);
        m_hLoginHandle = netsdk.CLIENT_LoginWithHighLevelSecurity(pstInParam, pstOutParam);
        if (m_hLoginHandle.longValue() == 0) {
            System.err.printf("Login Device[%s] Port[%d]Failed. %s\n", m_strIp, m_nPort, ToolKits.getErrorCodePrint());
        } else {
            System.out.println("Login Success [ " + m_strIp + " ]");
        }

        return m_hLoginHandle.longValue() != 0;
    }


    public void realPlayByDataType() {

        // 创建Stin对象 设置码流格式 通道号  预览类型 用户数据 保存文件名称
        NetSDKLib.NET_IN_REALPLAY_BY_DATA_TYPE stIn = new NetSDKLib.NET_IN_REALPLAY_BY_DATA_TYPE();

        stIn.emDataType = NetSDKLib.EM_REAL_DATA_TYPE.EM_REAL_DATA_TYPE_GBPS;
        stIn.nChannelID = 0;
        stIn.rType = NetSDKLib.NET_RealPlayType.NET_RType_Realplay;

        stIn.dwUser = null;
        stIn.szSaveFileName = "D:\\DahuaResult\\result.flv";   // 转换后的裸H264码流文件名

        // 创建Stout对象
        NetSDKLib.NET_OUT_REALPLAY_BY_DATA_TYPE stOut = new NetSDKLib.NET_OUT_REALPLAY_BY_DATA_TYPE();

        // 预览转码流保存
        NetSDKLib.LLong lRealHandle = netsdk.CLIENT_RealPlayByDataType(m_hLoginHandle, stIn, stOut, 5000);
        if (lRealHandle.longValue() != 0) {
            System.out.println("RealPlayByDataType Succeed!");
        } else {
            System.err.printf("RealPlayByDataType Failed!Last Error[0x%x]\n", netsdk.CLIENT_GetLastError());
            return;
        }
        try {
            // 录像时间 单位毫秒
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 停止预览
        netsdk.CLIENT_StopRealPlay(lRealHandle);   // 必须停止拉流后，才会生成文件


    }
    public boolean logout() {
        netsdk.CLIENT_StopRealPlayEx(this.m_hPlayHandleOne);
        netsdk.CLIENT_StopRealPlayEx(this.m_hPlayHandleTwo);
        if (m_hLoginHandle.longValue() == 0) {
            return false;
        }
        boolean bRet = netsdk.CLIENT_Logout(m_hLoginHandle);
        if (bRet) {
            m_hLoginHandle.setValue(0);
        }
        for (int i = 0; i < m_stDeviceInfo.byChanNum; i++) {
            chnlist.clear();
        }
        return true;
    }
}
