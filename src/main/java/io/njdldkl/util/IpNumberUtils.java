package io.njdldkl.util;

import java.net.InetAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP地址和数字之间的转换工具类
 */
public class IpNumberUtils {

    // 用于存储房间ID到IP的映射关系
    private static final Map<Integer, String> roomIdToAddressMap = new ConcurrentHashMap<>();

    private static final Random random = new Random();

    /**
     * 获取本地IP地址
     */
    public static String getLocalHostAddress(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException("获取本地IP地址失败", e);
        }
    }

    /**
     * 将IP地址转换为房间ID
     */
    public static String addressToRoomId(String address) {
        // 如果已经存在该IP地址的房间ID，则直接返回
        for (Map.Entry<Integer, String> entry : roomIdToAddressMap.entrySet()) {
            if (entry.getValue().equals(address)) {
                return String.valueOf(entry.getKey());
            }
        }

        // 否则生成一个新的房间ID
        int roomId = random.nextInt(10000, 100000);
        // 确保生成的房间ID唯一
        while (roomIdToAddressMap.containsKey(roomId)) {
            roomId = random.nextInt(10000, 100000);
        }
        roomIdToAddressMap.put(roomId, address);
        return String.valueOf(roomId);
    }

    /**
     * 将房间ID转换为IP地址
     */
    public static String roomIdToAddress(String roomId) {
        return roomIdToAddressMap.get(Integer.parseInt(roomId));
    }
}
