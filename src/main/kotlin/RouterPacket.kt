import java.io.Serializable
import java.net.Inet4Address

data class RouterPacket(val sourceAddress: Inet4Address,
                        val destAddress: Inet4Address,
                        //val sourcePort: Int,
                        val destPort: Int,  //TODO: Investigate if this can be omitted entirely
                        val message: String,
                        val type: Operation): Serializable

enum class Operation {
    REGISTER,DISCONNECT,STATS,MESSAGE
}
