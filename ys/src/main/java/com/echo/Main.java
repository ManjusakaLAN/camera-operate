package com.echo;

import com.echo.api.NetDEVSDKLib;

public class Main {
    public static void main(String[] args) {
        NetDEVSDKLib netsdk = NetDEVSDKLib.NETDEVSDK_INSTANCE;
        netsdk.NETDEV_Init();
    }
}

