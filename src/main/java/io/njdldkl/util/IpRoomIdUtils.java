package io.njdldkl.util;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP地址和房间号之间的转换工具类
 */
public class IpRoomIdUtils {

    private static final String BASE36_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 36;

    public static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("无法获取本机Ip地址", e);
        }
    }

    /**
     * 将IPv4或IPv6地址转换为36进制字符串
     *
     * @param ipAddress IP地址字符串
     * @return 36进制表示的IP地址
     */
    public static String ipToRoomId(String ipAddress) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException("非法的ip地址：" + ipAddress, e);
        }
        byte[] bytes = inetAddress.getAddress();
        BigInteger bigInt = new BigInteger(1, bytes);
        return encodeBase36(bigInt);
    }

    /**
     * 将36进制字符串转换回IPv4或IPv6地址
     *
     * @param base36Str 36进制字符串
     * @return 原始IP地址字符串
     */
    public static String roomIdToIp(String base36Str) {
        BigInteger bigInt = decodeBase36(base36Str);
        byte[] bytes = bigInt.toByteArray();

        // 处理BigInteger可能添加的前导零字节
        if (bytes[0] == 0) {
            byte[] temp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, temp, 0, temp.length);
            bytes = temp;
        }

        // 根据长度确定是IPv4还是IPv6
        try {
            if (bytes.length <= 4) {
                // IPv4: 确保是4字节
                byte[] ipv4Bytes = new byte[4];
                System.arraycopy(bytes, 0, ipv4Bytes, ipv4Bytes.length - bytes.length, bytes.length);
                return Inet4Address.getByAddress(ipv4Bytes).getHostAddress();
            } else {
                // IPv6: 确保是16字节
                byte[] ipv6Bytes = new byte[16];
                System.arraycopy(bytes, 0, ipv6Bytes, ipv6Bytes.length - bytes.length, bytes.length);
                return Inet6Address.getByAddress(ipv6Bytes).getHostAddress();
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将大整数编码为36进制字符串
     *
     * @param number 要编码的大整数
     * @return 36进制字符串
     */
    private static String encodeBase36(BigInteger number) {
        if (number.equals(BigInteger.ZERO)) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = number.divideAndRemainder(BigInteger.valueOf(BASE));
            sb.insert(0, BASE36_CHARS.charAt(divRem[1].intValue()));
            number = divRem[0];
        }
        return sb.toString();
    }

    /**
     * 将36进制字符串解码为大整数
     *
     * @param base36Str 36进制字符串
     * @return 对应的大整数
     * @throws IllegalArgumentException 如果字符串包含非法字符
     */
    private static BigInteger decodeBase36(String base36Str) {
        base36Str = base36Str.toUpperCase();
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < base36Str.length(); i++) {
            char c = base36Str.charAt(i);
            int digit = BASE36_CHARS.indexOf(c);
            if (digit == -1) {
                throw new IllegalArgumentException("非法的36进制字符: " + c);
            }
            result = result.multiply(BigInteger.valueOf(BASE)).add(BigInteger.valueOf(digit));
        }

        return result;
    }
}