package com.echo;

import com.echo.util.HCNetSDKUtil;

import java.io.File;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        String ip = "192.168.31.78";
        Integer port = 8000;
        String username = "admin";
        String password = "Hxxc1309";
        Integer channel = 1;
        Integer command = 27;
        Integer deStop = 0;
        Integer speed = 2;
        operateHKCamera(ip, port, username, password, channel, command, deStop, speed);
    }

    /**
     * @description: 指定摄像头云台控制
     * @param ip       192.168.31.78
     * @param port     8000
     * @param username admin
     * @param password Hxxc1309
     * @param channel  1
     * @param command  云台控制命令【21、上仰，22、下俯，23、左转，24、右转，25 上左，26 上右，27 下左，28 下右，29 左右自动扫描  11放大 12缩小】
     * @param speed    云台控制的速度，用户按不同解码器的速度控制值设置。取值范围[1,7]
     * @param deStop   0－开始，1－停止
     */
    static void operateHKCamera(String ip, Integer port, String username, String password, Integer channel, Integer command, Integer deStop, Integer speed) {
        HCNetSDKUtil.setPTZControlOther(ip, port, username, password, channel, command, deStop, speed);
    }
}