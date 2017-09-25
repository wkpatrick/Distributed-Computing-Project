import java.net.Inet4Address

data class RouterPacket(val sourceAddress: Inet4Address,
                        val destAddress: Inet4Address,
                        val sourcePort: Int,
                        val destPort: Int,
                        val Message: String)