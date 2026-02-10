package com.ryxon.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Utility class for handling IP addresses.
 */
public final class IpUtil {

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    private IpUtil() {}  // Prevent instantiation

    public static boolean isValidIPv4(String ip) {
        if (ip == null) return false;
        var matcher = IPV4_PATTERN.matcher(ip);
        if (!matcher.matches()) return false;
        for (int i = 1; i <= 4; i++) {
            int octet = Integer.parseInt(matcher.group(i));
            if (octet < 0 || octet > 255) return false;
        }
        return true;
    }

    public static boolean isLoopback(String ip) {
        try {
            return InetAddress.getByName(ip).isLoopbackAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static boolean equalsIp(String ip1, String ip2) {
        try {
            return InetAddress.getByName(ip1).equals(InetAddress.getByName(ip2));
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static String normalizeIp(String ip) {
        try {
            return InetAddress.getByName(ip).getHostAddress();
        } catch (UnknownHostException e) {
            return ip;
        }
    }
}