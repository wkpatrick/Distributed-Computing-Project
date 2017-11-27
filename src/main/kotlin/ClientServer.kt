import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.*
import java.nio.file.*
import java.net.Inet4Address
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

//TODO: Does the stuff in the ClientServer constructor get included in the scope of the functions
/**
 * This class is a Client/Server combo which has the ability to send messages and retrive any arriving ones
 *
 * @param serverPort: The Port that the server part will listen on
 * @param routerAddress: The address of the router
 * @param routerPort: The port of the router
 */

class ClientServer(val serverPort: Int, val routerAddress: Inet4Address, val routerPort: Int) {

    //The msg queue is so that we can check with the routers that the recipient exists, they send confirmation back and then we send the msg
    var msgQueue = ArrayList<RouterPacket>()

    fun launchServer() = runBlocking<Unit> {
        val server = launch(CommonPool) { launchObjectServer(serverPort, routerAddress, routerPort) }
    }

    //Requires the dest to be registered with the router, or else the router wont be able to send it to the correct place
    suspend fun sendMessage(message: RouterPacket) {
        println("Starting Client to Send Message")
        //We change this to sending it direct to the destination as part of phase 2
        //val socket = Socket(routerAddress, routerPort)
        val socket = Socket(message.destination.first, message.destination.second)
        //val socket = Socket(localhost,serverPort)
        //val inputStream = ObjectInputStream(socket.getInputStream())  Probably should never be used
        val outputStream = ObjectOutputStream(socket.getOutputStream())
        outputStream.writeObject(message)
        socket.close()
        println("Closing Client")
    }

    /**
     * This function that sends the provided object to the destination, it handles sending it to the router and adding the msg to the queue
     */
    suspend fun sendMessageToClient(message: RouterPacket) {
        msgQueue.add(message)
        val localhost = Inet4Address.getLocalHost()
        //The destination field is the addr we are looking to see if its connected, we manually set it to the router
        val checkMsg = RouterPacket(
                sourceAddress = localhost as Inet4Address,
                destination = message.destination,
                message = serverPort,
                type = Operation.CHECK)
        val socket = Socket(routerAddress, routerPort)
        val outputStream = ObjectOutputStream(socket.getOutputStream())
        outputStream.writeObject(checkMsg)
        socket.close()
        println("Closing Client")
    }

    /**
     * This function launches a server which registers itself to the router and waits for incoming connections, it also handles sending messages in the msg queue once their destination has been confirmed
     *
     * @param serverPort: The port that the created server will run on
     * @param routerAddress: The address of the router
     * @param routerPort: The port that the router uses to listen on
     */
    suspend fun launchObjectServer(serverPort: Int, routerAddress: Inet4Address,routerPort: Int) {
        println("Starting Server")
        val localhost = Inet4Address.getLocalHost() as Inet4Address
        val serverSocket = ServerSocket(serverPort)

        val clientSocket = Socket(routerAddress, routerPort) //For registering the client with the server
        val outPutStream = ObjectOutputStream(clientSocket.getOutputStream())
        outPutStream.writeObject(createRegisterPacket(localhost, serverPort, routerAddress, routerPort))
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
                println("Type: " + tempData.type.toString())
                //Here we test to see if its a message we want, or a returned check message
                if (tempData.type == Operation.MESSAGE) {

                    val message = tempData.message as ArrayList<String>


                    for (line in message) {
                        println(line)
                    }

                    try {
                        val fileWriter = FileWriter("data.dat", true)
                        val buffWriter = BufferedWriter(fileWriter)
                        val printWriter = PrintWriter(buffWriter)
                        printWriter.println(tempData.startTime.toString() + "," + tempData.endTime.toString())
                        printWriter.close()
                    } catch (error: IOException) {

                    }

                    println()
                    println()
                    println("Closing Server")
                } else if (tempData.type == Operation.CHECK) {

                    val copyQueue = msgQueue.toMutableList()
                    for (message in copyQueue) {
                        val dest = tempData.message as Pair<Inet4Address,Int>
                        if (dest.second.toString() == message.destination.second.toString()) {
                            tempData.destination = dest
                            sendMessage(message)
                            msgQueue.remove(message)
                        }
                    }
                }
                clientServerSocket.close()
            } catch (e: SocketException) {
                //println("Socket Exception in the Object Server")
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
    fun createRegisterPacket(sourceAddress: Inet4Address, serverPort: Int, routerAddress: Inet4Address, routerPort: Int): RouterPacket {
        val retPacket = RouterPacket(sourceAddress, Pair(routerAddress, routerPort), serverPort, Operation.REGISTER)
        return retPacket
    }
}