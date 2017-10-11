import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.*
import java.net.Inet4Address
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

//TODO: Investigate if clientPort is ever used
//TODO: Does the stuff in the ClientServer constructor get included in the scope of the functions
/**
 * This class is a Client/Server combo which has the ability to send messages and retrive any arriving ones
 *
 * @param clientPort: Never used
 * @param serverPort: The Port that the server part will listen on
 * @param routerAddress: The address of the router
 * @param routerPort: The port of the router
 */
class ClientServer(val clientPort: Int, val serverPort: Int, val routerAddress: Inet4Address, val routerPort: Int) {
    fun launchServer() = runBlocking<Unit> {
        val server = launch(CommonPool) { launchObjectServer(serverPort, routerAddress, routerPort) }
    }

    //Requires the dest to be registered with the router, or else the router wont be able to send it to the correct place
    //We dont need the destAddress because it will be included in the RoterPacket
    suspend fun sendMessage(message: RouterPacket) {
        println("Starting Client to Send Message")
        val localhost: Inet4Address = Inet4Address.getLocalHost() as Inet4Address
        val socket = Socket(routerAddress, routerPort)
        //val socket = Socket(localhost,serverPort)
        //val inputStream = ObjectInputStream(socket.getInputStream())  Probably should never be used
        val outputStream = ObjectOutputStream(socket.getOutputStream())
        outputStream.writeObject(message)
        socket.close()
        println("Closing Client")
    }

    /**
     * This function launchers a server which registers itself to the router and waits for incoming connections
     *
     * @param serverPort: The port that the created server will run on
     * @param routerAddress: The address of the router
     * @param routerPort: The port that the router uses to listen on
     */
    suspend fun launchObjectServer(serverPort: Int, routerAddress: Inet4Address, routerPort: Int) {
        println("Starting Server")
        val localhost = Inet4Address.getLocalHost() as Inet4Address
        val serverSocket = ServerSocket(serverPort)

        val clientSocket = Socket(routerAddress,routerPort) //For registering the client with the server
        val outPutStream = ObjectOutputStream(clientSocket.getOutputStream())
        outPutStream.writeObject(createRegisterPacket(localhost,serverPort,routerAddress,routerPort))
        clientSocket.close()

        while (true) {
            try {
                val clientServerSocket = serverSocket.accept()
                println("Connection Accepted!")

                //You print from the output, and read from the input (send -> output, receive -> input)
                val inputStream = ObjectInputStream(clientServerSocket.getInputStream())
                //val outputStream = ObjectOutputStream(clientServerSocket.getOutputStream())


                val tempData = inputStream.readObject() as RouterPacket
                tempData.endTime = System.currentTimeMillis()
                val message = tempData.message as ArrayList<String>
                for(line in message)
                {
                    println(line)
                }
                try
                {
                    val fileWriter = FileWriter("data.dat", true)
                    val buffWriter = BufferedWriter(fileWriter)
                    val printWriter = PrintWriter(buffWriter)
                    printWriter.println(tempData.startTime.toString() + "," + tempData.endTime.toString())
                    printWriter.close()
                }
                catch (error: IOException)
                {

                }


                println()
                println()



                println("Closing Server")

                clientServerSocket.close()
            } catch (e: SocketException) {
                //println("Socket Exceptin in the Object Server")
            }
        }
    }

    /**
     * This function returns a registration RouterPacket
     * @param sourceAddress: The source address of the server that is registering with the router
     * @param serverPort: The port that the server will listen on after it has registered with the router
     * @param routerAddress: The address of the router TODO: Im pretty sure this is useless
     * @param routerPort: The port that the router is listenng on
     *
     * @return A RouterPacket containing the nescessary info and Registration operation
     */
    fun createRegisterPacket(sourceAddress: Inet4Address, serverPort: Int, routerAddress: Inet4Address, routerPort: Int):RouterPacket
    {
        val retPacket = RouterPacket(sourceAddress, Pair(routerAddress, routerPort), serverPort, Operation.REGISTER)
        return retPacket
    }
}