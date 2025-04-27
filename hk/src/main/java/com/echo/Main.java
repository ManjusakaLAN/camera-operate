package com.echo;

import com.echo.api.HCNetSDK;
import com.echo.util.HCNetSDKUtil;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static com.echo.util.HCNetSDKUtil.login;

@Slf4j
public class Main {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String ip = "192.168.31.78";
        Integer port = 8000;
        String username = "admin";
        String password = "Hxxc1309";
        Integer channel = 1;
        Integer command = 22;
        Integer deStop = 0;
        Integer speed = 2;
//        operateHKCamera(ip, port, username, password, channel, command, deStop, speed);
        HCNetSDKUtil.init();

        int lUserID = login(ip, port, username, password);
        System.out.println("userId:" + lUserID);

//        Integer userId = HCNetSDKUtil.login(ip, port, username, password);

        HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        int userId = HCNetSDKUtil.hCNetSDK.NET_DVR_Login_V30(
                ip, port.shortValue(), username, password, m_strDeviceInfo
        );

        if (userId == -1){
            System.out.println("登录失败，错误码为: " + HCNetSDKUtil.hCNetSDK.NET_DVR_GetLastError());
        }

        m_strDeviceInfo.read();
        int maxIpChannelNum;
        if (m_strDeviceInfo.byHighDChanNum == 0) {
            maxIpChannelNum = m_strDeviceInfo.byIPChanNum & 0xff;
            System.out.println("设备数组通道总数：" + maxIpChannelNum);
        } else {
            maxIpChannelNum = (int) ((m_strDeviceInfo.byHighDChanNum & 0xff) << 8);
            System.out.println("设备数组通道总数：" + maxIpChannelNum);
        }
        int group = maxIpChannelNum / 64 <= 0 ? 0 : maxIpChannelNum / 64 - 1;
        System.out.println(group);
        for (int i = 0; i <= group; i++) {
            IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
            boolean bRet;

            HCNetSDK.NET_DVR_IPPARACFG_V40 m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG_V40();
            m_strIpparaCfg.write();
            //lpIpParaConfig 接收数据的缓冲指针
            Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();

            bRet = HCNetSDKUtil.hCNetSDK.NET_DVR_GetDVRConfig(userId, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, i, lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
            m_strIpparaCfg.read();

            if (!bRet) {
                //设备不支持,则表示没有IP通道
            } else {
                //设备支持IP通道
                for (int iChannum = 0; iChannum < ((maxIpChannelNum <= 32) ? 32 : 64); iChannum++) {
                    m_strIpparaCfg.struStreamMode[iChannum].read();
                    if (m_strIpparaCfg.struStreamMode[iChannum].byGetStreamType == 0) {

                        m_strIpparaCfg.struStreamMode[iChannum].uGetStream.setType(HCNetSDK.NET_DVR_IPCHANINFO.class);
                        m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.read();
                        if (m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byEnable == 1) {
                            System.out.println("IPCamera_" + (iChannum + 64 * i + m_strDeviceInfo.byStartDChan) + "_在线");
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("IPCamera_" + (iChannum + 64 * i + m_strDeviceInfo.byStartDChan) + "_在线");
                        } else {
                            System.out.println("IPCamera_" + (iChannum + 64 * i + m_strDeviceInfo.byStartDChan) + "_离线");
                        }
                    }
                }
            }
        }
    }

    /**
     * @param ip       192.168.31.78
     * @param port     8000
     * @param username admin
     * @param password Hxxc1309
     * @param channel  1
     * @param command  云台控制命令【21、上仰，22、下俯，23、左转，24、右转，25 上左，0 上右，27 下左，28 下右，29 左右自动扫描  11放大 12缩小】
     * @param speed    云台控制的速度，用户按不同解码器的速度控制值设置。取值范围[1,7]
     * @param deStop   0－开始，1－停止
     * @description: 指定摄像头云台控制
     */
    static void operateHKCamera(String ip, Integer port, String username, String password, Integer channel, Integer command, Integer deStop, Integer speed) {
        HCNetSDKUtil.setPTZControlOther(ip, port, username, password, channel, command, deStop, speed);
    }
}