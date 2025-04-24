package com.echo.util;

public class CommonUtil {
    public static String getOSAndArch(){
       return "OS Type:" + getOS() + " Arch Type:" + getArch();
    }

    public static String getOS(){
        String osName = System.getProperty("os.name");
        String osType;
        if (osName.toLowerCase().startsWith("linux")) {
            osType = "linux";
        } else if (!osName.toLowerCase().startsWith("mac") && !osName.toLowerCase().startsWith("darwin")) {
            if (osName.toLowerCase().startsWith("windows")) {
                osType = "win";
            } else {
                osType = "";
            }
        } else {
            osType = "mac";
        }
        return osType;
    }

    public static String getArch(){
        String arch = System.getProperty("os.arch");
        arch = arch.toLowerCase().trim();
        if (!"i386".equals(arch) && !"i686".equals(arch) && !"x86".equals(arch)) {
            if (!"x86_64".equals(arch) && !"amd64".equals(arch)) {
                if (arch.startsWith("arm")) {
                    arch = "ARM";
                }
            } else {
                arch = "64";
            }
        } else {
            arch = "32";
        }
        return arch;
    }
}
