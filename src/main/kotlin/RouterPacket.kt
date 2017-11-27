import java.io.Serializable
import java.net.Inet4Address

//TODO: replace startTime and endTime with an ArrayList of times, for Start,Router,Bounce(?), End
data class RouterPacket(val sourceAddress: Inet4Address,
                        var destination: Pair<Inet4Address, Int>,
                        val message: Any,
                        val type: Operation,
                        var startTime: Long = 0,
                        var endTime: Long = 0) : Serializable

//The different Operations and their uses are described in the Router class file
//REMOVE is used on the client Server, that way we can remove
enum class Operation {
    REGISTER, DISCONNECT, STATS, MESSAGE, LIST, BOUNCE, CHECK, REMOVE
}


