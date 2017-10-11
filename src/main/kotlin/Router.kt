import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Inet4Address
import java.net.ServerSocket
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import java.net.Socket
import java.net.SocketException


class Router(val portNumber: Int)
//Need to get some sort of statistics set up.
{
    fun launchRouter() = runBlocking<Unit> {
        val serverSocket = ServerSocket(portNumber)
        val router = launch(CommonPool) { runRouter(serverSocket) }
        //router.join()
    }

    suspend fun runRouter(serverSocket: ServerSocket) {
        //The router will store each packet it recieves, this way we can get the send/receive stats from it when we construct it for later.
        val routerHistory: ArrayList<RouterPacket> = ArrayList<RouterPacket>()
        //The client List keeps track of all the registered clients. We use <String, Int> because there was an issue with having two servers on the same IP registering, and they would overwrite eachother
        val clientList = ArrayList<Pair<Inet4Address, Int>>()

        while (true) {
            //Recieve the message
            try {
                val clientServerSocket = serverSocket.accept()

                //println("A client has connected to the router!")

                //You print from the output, and read from the input (send -> output, receive -> input)
                val inputStream = ObjectInputStream(clientServerSocket.getInputStream())
                //val outputStream = ObjectOutputStream(clientServerSocket.getOutputStream())
                val tempData = inputStream.readObject() as RouterPacket

                when (tempData.type) {
                //A Register Packet includes the int inside the message field, this represents the port number that the source is listening for connections on
                    Operation.REGISTER -> {

                        clientList.add(Pair(tempData.sourceAddress, tempData.message as Int))
                        println("A client has registered with the router!")
                        println("----------------------------------------")
                        println("Addr: " + tempData.sourceAddress.toString() + " Port: " + tempData.message as Int)
                        println()
                    }
                //Removes the server from the list of connected servers, need to include the port number in the DataPacket
                    Operation.DISCONNECT -> {
                        clientList.remove(Pair(tempData.sourceAddress, tempData.message as Int))
                    }
                //A stats Packet includes the time that the client finished getting the packet
                //TODO: Cant this be done clientside?
                    Operation.STATS -> println("Stats")

                //A Messgae Packet is just something to forward to the destination
                    Operation.MESSAGE -> {
                        if (clientList.contains(tempData.destination)) {
                            sendMessage(tempData)
                        } else {
                            println("There is no client with that IP registered!")
                        }
                    }
                }

                clientServerSocket.close()
                //We can probably add the transfer time in here

                routerHistory.add(tempData)

            } catch (e: SocketException) { //TODO: Find out a way to keep this from popping :|
            }

        }

    }

    suspend fun sendMessage(message: RouterPacket) {
        println("Router starting to send message")
        val destAddr = message.destination.first
        val port = message.destination.second
        val socket = Socket(destAddr, port)
        //val inputStream = ObjectInputStream(socket.getInputStream())  Probably should never be used
        val outputStream = ObjectOutputStream(socket.getOutputStream())
        outputStream.writeObject(message)
        socket.close()
    }
}
