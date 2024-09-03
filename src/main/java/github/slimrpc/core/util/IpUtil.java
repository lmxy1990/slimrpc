package github.slimrpc.core.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class IpUtil {

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static final Pattern PORT_PATTERN = Pattern.compile("^:(\\d{1,5})$");


    public static boolean isIP(String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }

    public static boolean isPort(String port) {
        return PORT_PATTERN.matcher(port).matches();
    }

    public static boolean isIPAndPort(String ipPort) {
        if (!StringUtils.hasText(ipPort)) {
            return false;
        }
        String[] split = ipPort.split(":");
        if (split.length != 2) {
            return false;
        }
        return isIP(split[0]) && isPort(split[1]);
    }


    public static String getIp(String ipPort) {
        if (!StringUtils.hasText(ipPort)) {
            return null;
        }
        String[] split = ipPort.split(":");
        return split[0];
    }

    public static int getPort(String ipPort) {
        if (!StringUtils.hasText(ipPort)) {
            return 0;
        }
        String[] split = ipPort.split(":");
        return Integer.parseInt(split[1]);
    }


}
