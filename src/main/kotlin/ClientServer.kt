import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Inet4Address
import java.net.ServerSocket
import java.net.Socket

//TODO: Investigate if clientPort is ever used
class ClientServer(val clientPort: Int, val serverPort: Int, val routerAddress: Inet4Address, val routerPort: Int) {
    fun launchBoth() = runBlocking<Unit> {
        val localhost = Inet4Address.getLocalHost() as Inet4Address
        val server = launch(CommonPool) { launchObjectServer(4444) }
        val client = launch(CommonPool) { launchObjectClient(localhost, localhost) }
        client.join()
        server.join()
    }

    //We only need the address of the router and the address of w/e we want to send the message to,  router handles dealing with the destPort
    suspend fun launchObjectClient(routerAddress: Inet4Address, destAddress: Inet4Address) {
        println("Starting Client")
        val localhost:Inet4Address = Inet4Address.getLocalHost() as Inet4Address
        delay(1000L)
        //val socket = Socket(routerAddress, routerPort)
        val socket = Socket(destAddress, serverPort)
        //val inputStream = ObjectInputStream(socket.getInputStream())
        val outputStream = ObjectOutputStream(socket.getOutputStream())

        val tempMsg = RouterPacket(localhost,destAddress,routerPort, "Hello World!", Operation.MESSAGE)
        outputStream.writeObject(tempMsg)
        socket.close()
        println("Closing Client")
    }

    suspend fun launchObjectServer(serverPort: Int) {
        println("Starting Server")
        val localhost = Inet4Address.getLocalHost()
        val serverSocket = ServerSocket(serverPort)
        val clientServerSocket = serverSocket.accept()
        println("Connection Accepted!")

        //You print from the output, and read from the input (send -> output, receive -> input)
        val inputStream = ObjectInputStream(clientServerSocket.getInputStream())
        val outputStream = ObjectOutputStream(clientServerSocket.getOutputStream())


        val tempData = inputStream.readObject() as RouterPacket
        println(tempData.message)


        println("Closing Server")

        serverSocket.close()
    }
}