package com.echo.util;

import com.echo.api.hk.HCNetSDK;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class HkSDKUtil {

    public static HCNetSDK hkSDKInstance = SDKLoadUtil.loadHkSDKLibrary();


    /**
     * 初始化SDK
     *
     * @return
     */
    public static Boolean init() {
        // 初始化SDK
        boolean b = hkSDKInstance.NET_DVR_Init();
        if (!b) {
            log.error("初始化失败 错误码为: {}", hkSDKInstance.NET_DVR_GetLastError());
        }
        return b;
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
        int userID = hkSDKInstance.NET_DVR_Login_V40(loginInfo, deviceInfo);
        if (userID == -1) {
            log.error("登录失败 错误码为: {}", hkSDKInstance.NET_DVR_GetLastError());
        } else {
            log.info("{} 设备登录成功！", ip);
            // read()后，结构体中才有对应的数据 可以获取登录信息进行进一步处理
            deviceInfo.read();
        }
        // 返回登录结果
        return userID;
    }

    /**
     * @Description： 退出登录
     **/
    public static void logout(Integer lUserID) {
        if (Objects.nonNull(hkSDKInstance) && Objects.nonNull(lUserID)) {
            // 退出登录
            hkSDKInstance.NET_DVR_Logout(lUserID);
            // 清理SDK资源
            hkSDKInstance.NET_DVR_Cleanup();
        }
    }

    /**
     * 获取海康设备在线状态
     *
     * @param ip       ip地址
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return 返回在线设备的通道号以及 是否在线 true 在线 false 不在线的map对象
     */
    public static Map<Integer, Boolean> getOnlineDeviceByDVR(String ip, Integer port, String username, String password) {
        // 初始化设备在线状态记录的Map
        Map<Integer, Boolean> onlineDeviceMap = new HashMap<>();

        //初始化海康设备类 登录成功后信息回封装到此类 但是需要调用read()方法
        HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        // 登录硬盘录像机
        int userId = hkSDKInstance.NET_DVR_Login_V30(
                ip, port.shortValue(), username, password, m_strDeviceInfo
        );
        // 登录失败
        if (userId == -1) {
            log.error("登录失败，错误码为: {}", hkSDKInstance.NET_DVR_GetLastError());
            throw new RuntimeException("设备登录失败，错误码为: " + hkSDKInstance.NET_DVR_GetLastError());
        }
        // 把信息读入到java类 后续才能获取信息
        m_strDeviceInfo.read();
        // 获取设备数组通道总数
        int maxIpChannelNum;
        if (m_strDeviceInfo.byHighDChanNum == 0) {
            maxIpChannelNum = m_strDeviceInfo.byIPChanNum & 0xff;
        } else {
            maxIpChannelNum = ((m_strDeviceInfo.byHighDChanNum & 0xff) << 8);
        }
        log.info("设备数组通道总数：{}", maxIpChannelNum);
        // 分组  64一组
        int group = maxIpChannelNum / 64 <= 0 ? 0 : maxIpChannelNum / 64 - 1;

        for (int i = 0; i <= group; i++) {
            IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
            boolean bRet;

            HCNetSDK.NET_DVR_IPPARACFG_V40 ipparacfgV40 = new HCNetSDK.NET_DVR_IPPARACFG_V40();
            ipparacfgV40.write();
            //lpIpParaConfig 接收数据的缓冲指针
            Pointer lpIpParaConfig = ipparacfgV40.getPointer();

            bRet = hkSDKInstance.NET_DVR_GetDVRConfig(userId, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, i, lpIpParaConfig, ipparacfgV40.size(), ibrBytesReturned);
            ipparacfgV40.read();

            if (!bRet) {
                //设备不支持,则表示没有IP通道
                log.error("设备不支持,没有IP通道");
                return Collections.emptyMap();
            } else {
                //设备支持IP通道
                for (int iChannum = 0; iChannum < ((maxIpChannelNum <= 32) ? 32 : 64); iChannum++) {
                    ipparacfgV40.struStreamMode[iChannum].read();
                    if (ipparacfgV40.struStreamMode[iChannum].byGetStreamType == 0) {

                        ipparacfgV40.struStreamMode[iChannum].uGetStream.setType(HCNetSDK.NET_DVR_IPCHANINFO.class);
                        ipparacfgV40.struStreamMode[iChannum].uGetStream.struChanInfo.read();
                        if (ipparacfgV40.struStreamMode[iChannum].uGetStream.struChanInfo.byEnable == 1) {
                            log.info("IPCamera_{}_在线", (iChannum + 64 * i + m_strDeviceInfo.byStartDChan));
                            onlineDeviceMap.put(iChannum + 64 * i + m_strDeviceInfo.byStartDChan, true);
                        } else {
                            log.info("IPCamera_{}_离线", (iChannum + 64 * i + m_strDeviceInfo.byStartDChan));
                            onlineDeviceMap.put(iChannum + 64 * i + m_strDeviceInfo.byStartDChan, false);
                        }
                    }
                }
            }
        }
        // 登出
        logout(userId);
        return onlineDeviceMap;
    }


    /**
     * @Description： 云台控制操作(不用启动图象预览)
     * dwPTZCommand：云台控制命令云台控制命令[21、上仰，22、下俯，23、左转，24、右转，
     * 25 上左，0 上右，27 下左，28 下右，29 左右自动扫描  11放大 12缩小]
     * dwStop：云台停止动作或开始动作：0－开始，1－停止
     * dwSpeed：云台控制的速度，用户按不同解码器的速度控制值设置。取值范围[1,7]
     **/
    public static void setPTZControlOther(String ip, Integer port, String username, String password, Integer lChannel, Integer dwPTZCommand, Integer dwStop, Integer dwSpeed) {
        Integer lUserID = null;
        try {
            // 初始化
            init();
            // 登录
            lUserID = login(ip, port, username, password);

            if (lUserID != -1) {
                boolean status = false;
                log.info("云台控制操作：lUserID：{}，lChannel：{}，dwPTZPresetCmd：{}，dwPresetIndex：{}，dwSpeed：{}", lUserID, lChannel, dwPTZCommand, dwStop, dwSpeed);
                if (Objects.isNull(dwSpeed)) {
                    status = hkSDKInstance.NET_DVR_PTZControl_Other(lUserID, lChannel, dwPTZCommand, dwStop);
                } else {
                    // 带速度
                    status = hkSDKInstance.NET_DVR_PTZControlWithSpeed_Other(lUserID, lChannel, dwPTZCommand, dwStop, dwSpeed);
                }
                if (status) {
                    log.info("云台控制操作成功");
                } else {
                    log.error("云台控制操作失败，错误码：{}", hkSDKInstance.NET_DVR_GetLastError());
                }

            } else {
                log.error("登录失败，错误码为: {}", hkSDKInstance.NET_DVR_GetLastError());
                throw new RuntimeException("设备登录失败，错误码为: " + hkSDKInstance.NET_DVR_GetLastError());
            }
        } catch (Exception e) {
            log.error("云台控制操异常：{}", e.getMessage());
        } finally {
            logout(lUserID);
        }
    }
}
