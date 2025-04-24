package com.echo.util;


import com.echo.api.HCNetSDK;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;

/**
 * @ClassName： HCNetSDKUtil.java
 * @Description： 海康 SDK
 **/
@Slf4j
public class HCNetSDKUtil {

    public static HCNetSDK hCNetSDK = null;
    static FExceptionCallBack_Imp fExceptionCallBack;

    static Boolean initFlag = false;

    static class FExceptionCallBack_Imp implements HCNetSDK.FExceptionCallBack {
        public void invoke(int dwType, int lUserID, int lHandle, Pointer pUser) {
            log.error("异常事件类型：{}", dwType);
            return;
        }
    }

    /**
     * @Description： SDK初始化
     **/
    public static void init() {

        // 动态库加载
        if (hCNetSDK == null) {
            synchronized (HCNetSDK.class) {
                String strDllPath = "";
                try {
                    if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                        strDllPath = System.getProperty("user.dir") + "/docker/jarResource/echo-file/libhcnetsdk.so";
                    } else {
//                        strDllPath = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("")).getPath() + "lib\\win64\\HCNetSDK.dll";
                        File file = new File(ClassLoader.getSystemClassLoader().getResource("").getFile() + "lib/win64/HCNetSDK.dll");
                        strDllPath = file.getAbsolutePath();
                    }
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
                } catch (Exception ex) {
                    log.error("加载SDK失败，loadLibrary: {}，Error: {}", strDllPath, ex.getMessage());
                    return;
                }
            }
        }

        // SDK初始化，一个程序只需要调用一次
        if (!initFlag) {
            boolean initSuc = hCNetSDK.NET_DVR_Init();
            log.info("SDK初始化: {}", initSuc);
            initFlag = true;
        }

        // 异常消息回调
        if (fExceptionCallBack == null) {
            fExceptionCallBack = new FExceptionCallBack_Imp();
        }

        Pointer pUser = null;
        if (!hCNetSDK.NET_DVR_SetExceptionCallBack_V30(0, 0, fExceptionCallBack, pUser)) {
            return;
        }
        // 启动SDK写日志
        hCNetSDK.NET_DVR_SetLogToFile(3, "./sdkLog", false);
    }


    /**
     * @param ip   设备IP
     * @param port 设备端口
     * @param user 用户名
     * @param psw  密码
     * @Description： 登录摄像头
     */
    public static Integer login(String ip, Integer port, String user, String psw) {
        // 创建设备登录信息和设备信息对象
        HCNetSDK.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
        HCNetSDK.NET_DVR_DEVICEINFO_V40 deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();

        // 设置设备IP地址
        byte[] deviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        byte[] ipBytes = ip.getBytes();
        System.arraycopy(ipBytes, 0, deviceAddress, 0, Math.min(ipBytes.length, deviceAddress.length));
        loginInfo.sDeviceAddress = deviceAddress;

        // 设置用户名和密码
        byte[] userName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        byte[] password = psw.getBytes();
        System.arraycopy(user.getBytes(), 0, userName, 0, Math.min(user.length(), userName.length));
        System.arraycopy(password, 0, loginInfo.sPassword, 0, Math.min(password.length, loginInfo.sPassword.length));
        loginInfo.sUserName = userName;

        // 设置端口和登录模式
        loginInfo.wPort = port.shortValue();
        loginInfo.bUseAsynLogin = false; // 同步登录
        loginInfo.byLoginMode = 0; // 使用SDK私有协议

        // 执行登录操作
        int userID = hCNetSDK.NET_DVR_Login_V40(loginInfo, deviceInfo);
        if (userID == -1) {
            log.error("登录失败，错误码为: {}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            log.info("{} 设备登录成功！", ip);

            // read()后，结构体中才有对应的数据
            deviceInfo.read();

            // 处理通道号逻辑
            int startDChan = deviceInfo.struDeviceV30.byStartDChan;
            log.info("预览起始通道号: {}", startDChan);
        }

        // 返回登录结果
        return userID;
    }

    /**
     * @Description： 退出登录
     **/
    public static void logout(Integer lUserID) {
        if (Objects.nonNull(hCNetSDK) && Objects.nonNull(lUserID)) {
            hCNetSDK.NET_DVR_Logout(lUserID);
            hCNetSDK.NET_DVR_Cleanup();
        }
    }

    /**
     * @Description： 设置球机预置点(不用启动图象预览)
     * dwPTZPresetCmd：8、设置预置点，9、清除预置点，39、转到预置点
     * dwPresetIndex：预置点的序号（从1开始），最多支持300个预置点
     **/
    public static void setPTZPresetOther(String ip, Integer port, String username, String password, Integer lChannel, Integer dwPTZPresetCmd, Integer dwPresetIndex) {
        Integer lUserID = null;
        try {
            init();
            lUserID = login(ip, port, username, password);

            // 设置球机预置点
            if (lUserID != -1) {
                log.info("设置预置点开始：lUserID：{}，lChannel：{}，dwPTZPresetCmd：{}，dwPresetIndex：{}", lUserID, lChannel, dwPTZPresetCmd, dwPresetIndex);
                boolean status = hCNetSDK.NET_DVR_PTZPreset_Other(lUserID, lChannel, dwPTZPresetCmd, dwPresetIndex);
                if (status) {
                    log.info("设置预置点成功");
                } else {
                    log.error("设置预置点失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
                }
            }
        } catch (Exception e) {
            log.error("设置预置点异常：{}", e.getMessage());
        } finally {
            logout(lUserID);
        }
    }

    /**
     * @Description： 云台控制操作(不用启动图象预览)
     * dwPTZCommand：云台控制命令【21、上仰，22、下俯，23、左转，24、右转】
     * dwStop：云台停止动作或开始动作：0－开始，1－停止
     * dwSpeed：云台控制的速度，用户按不同解码器的速度控制值设置。取值范围[1,7]
     **/
    public static void setPTZControlOther(String ip, Integer port, String username, String password, Integer lChannel, Integer dwPTZCommand, Integer dwStop, Integer dwSpeed) {
        Integer lUserID = null;
        try {
            init();
            lUserID = login(ip, port, username, password);
            log.info("云台控制操作开始：lUserID：{}", lUserID);
            if (lUserID != -1) {
                log.info("云台控制操作：lUserID：{}，lChannel：{}，dwPTZPresetCmd：{}，dwPresetIndex：{}，dwSpeed：{}", lUserID, lChannel, dwPTZCommand, dwStop, dwSpeed);
                boolean status = false;
                if (Objects.isNull(dwSpeed)) {
                    status = hCNetSDK.NET_DVR_PTZControl_Other(lUserID, lChannel, dwPTZCommand, dwStop);
                } else {
                    // 带速度
                    status = hCNetSDK.NET_DVR_PTZControlWithSpeed_Other(lUserID, lChannel, dwPTZCommand, dwStop, dwSpeed);
                }

                if (status) {
                    log.info("云台控制操作成功");
                } else {
                    log.error("云台控制操作失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
                }
            }
        } catch (Exception e) {
            log.error("云台控制操异常：{}", e.getMessage());
        } finally {
            logout(lUserID);
        }
    }
}