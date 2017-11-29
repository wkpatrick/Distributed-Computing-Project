import java.io.Serializable
import java.net.Inet4Address

//Time is an arrayList of the time it takes to do the following:
//      Get time for the confirmation the remote server exists (t2 - t1)
//      Get time for how long it takes to send the text (t3-t2)
data class RouterPacket(val sourceAddress: Inet4Address,
                        var destination: Pair<Inet4Address, Int>,
                        val message: Any,
                        val type: Operation,
                        var time: ArrayList<Long> = ArrayList()) : Serializable

//The different Operations and their uses are described in the Router class file
enum class Operation {
    REGISTER, DISCONNECT, STATS, MESSAGE, LIST, BOUNCE, CHECK
}


