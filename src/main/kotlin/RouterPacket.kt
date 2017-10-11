import java.io.Serializable
import java.net.Inet4Address

data class RouterPacket(val sourceAddress: Inet4Address,
                        val destination: Pair<Inet4Address, Int>,
                        val message: Any,
                        val type: Operation,
                        var startTime: Long = 0,
                        var endTime: Long = 0): Serializable

enum class Operation {
    REGISTER,DISCONNECT,STATS,MESSAGE
}
