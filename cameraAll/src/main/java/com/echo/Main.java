package com.echo;

import com.echo.api.hk.HCNetSDK;
import com.echo.api.ys.NetDEVSDKLib;
import com.echo.util.HkSDKUtil;
import com.echo.util.SDKLoadUtil;

public class Main {
    public static void main(String[] args) throws Exception {

        String ip = "192.168.31.78";
        int port = 8000;
        String username = "admin";
        String password = "Hxxc1309";
        Integer channel = 1;
        Integer command = 11;
        Integer deStop = 0;
        Integer speed = 2;
        HkSDKUtil.setPTZControlOther(ip, port, username, password, channel, command, deStop, speed);

        Thread.sleep(5000);
        command = 12;
        HkSDKUtil.setPTZControlOther(ip, port, username, password, channel, command, deStop, speed);

        Thread.sleep(5000);
        deStop = 1;
        HkSDKUtil.setPTZControlOther(ip, port, username, password, channel, command, deStop, speed);

    }
}