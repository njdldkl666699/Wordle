import io.njdldkl.util.IpRoomIdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpRoomIdUtilsTest {

    @Test
    public void ipv4RoomIdTest() {
        String roomId = IpRoomIdUtils.ipToRoomId("192.168.1.1");
        System.out.println(roomId);
        String address = IpRoomIdUtils.roomIdToIp(roomId);
        Assertions.assertEquals("192.168.1.1", address);
    }

    @Test
    public void ipv6RoomIdTest() throws UnknownHostException {
        Inet6Address address = (Inet6Address) Inet6Address.getByName("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        System.out.println("address = " + address);

        String roomId = IpRoomIdUtils.ipToRoomId(address.getHostAddress());
        System.out.println(roomId);

        String resultStr = IpRoomIdUtils.roomIdToIp(roomId);
        InetAddress resultAddress = Inet6Address.getByName(resultStr);
        System.out.println("resultAddress = " + resultAddress);

        Assertions.assertEquals(resultAddress, address);
    }
}
