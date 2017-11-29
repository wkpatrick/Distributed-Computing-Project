import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Inet4Address
import java.net.ServerSocket
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.net.Socket
import java.net.SocketException


class Router()
//Need to get some sort of statistics set up.
{
    var running = false
    val localhost = Inet4Address.getLocalHost()
    var portNum = -1

    fun launchRouter(portNumber: Int) = runBlocking<Unit> {
        val serverSocket = ServerSocket(portNumber)
        portNum = portNumber
        running = true
        val router = launch(CommonPool) { runRouter(serverSocket) }
        //router.join()
    }

    fun cancelRouter() {
        running = false
    }

    suspend fun runRouter(serverSocket: ServerSocket) {
        //The router will store each packet it recieves, this way we can get the send/receive stats from it when we construct it for later.
        val routerHistory: ArrayList<RouterPacket> = ArrayList<RouterPacket>()
        //The client List keeps track of all the registered clients. We use <String, Int> because there was an issue with having two servers on the same IP registering, and they would overwrite eachother
        val clientList = ArrayList<Pair<Inet4Address, Int>>()
        //The router list keeps track of all the routers that have expressed interest in ours,
        val routerList = ArrayList<Pair<Inet4Address, Int>>()

        while (true) {
            //TODO: This might not actually cancel the router, but I think itll work if it has been done after a packet has been sent after the stop button is pressed
            if (running == false) {
                break
            }
            //Recieve the message
            try {
                val clientServerSocket = serverSocket.accept()

                println("A client has connected to the router!")

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
                        println("Addr: " + tempData.sourceAddress.toString() + " Port: " + tempData.message)
                        println()
                    }
                //Removes the server from the list of connected servers, need to include the port number in the DataPacket
                    Operation.DISCONNECT -> {
                        clientList.remove(Pair(tempData.sourceAddress, tempData.message as Int))
                    }
                //A stats Packet includes the time that the client finished getting the packet
                //TODO: Cant this be done clientside?
                    Operation.STATS -> println("Stats")

                //A Message Packet is just something to forward to the destination
                //This should be deprecated, the client should no longer send a Message packet to the router
                    Operation.MESSAGE -> {
                        if (clientList.contains(tempData.destination)) {
                            sendMessage(tempData)
                        } else {
                            //When we dont have the IP, we pass on the message to the routers we know exist, and then they can send
                            for (router in routerList) {

                            }
                            println("There is no client with that IP registered! Trying other routers")
                        }
                    }

                // A List packet adds the router that sent it to the routerList, which contains all the routers we send a message to if we dont hav ethe ip
                // The message contains the port number of the router
                    Operation.LIST -> {
                        routerList.add(Pair(tempData.sourceAddress, tempData.message as Int))
                    }
                //A bounce packet is recieved from other routers that dont have the destination in their list of clients, the router that recieves it just sends it to a client if it is in its list, if not it just
                // The message contains the router port we send it back to, and destination contains the client we are looking for,
                // So we send the client back with it in message?
                    Operation.BOUNCE -> {
                        for (client in clientList) {
                            //TODO: Return this to using normal destination
                            if (tempData.destination.second == client.second) {
                                val checkMessage = RouterPacket(
                                        sourceAddress = tempData.sourceAddress,
                                        destination = Pair(tempData.sourceAddress, tempData.message as Int),
                                        message = tempData.destination,
                                        type = Operation.CHECK
                                )
                                sendMessage(checkMessage)
                            }
                        }
                    }
                // A Check packet is sent from the client to the router, if the router does not the msg recipient as registered, the router sends a bounce packet to others it have registerd. Once a router has the client requested, and it sends it back ot the client, who then send the message
                // The destination field contains what address we are checking for, the message field contains the port, which combined with the source address allows us to send it back to the client
                //One issue is that localhost is 127.x.x.x.x  but for some reason its picking up the 10.xxx as the dest, so I have to force it to juse use the host name in this case
                    Operation.CHECK -> {
                        var isClient = false
                        for (client in clientList) {
                            if (tempData.destination.second.toString() == client.second.toString()) {
                                val checkMessage = RouterPacket(
                                        sourceAddress = tempData.sourceAddress,
                                        destination = Pair(tempData.sourceAddress, tempData.message as Int),
                                        message = tempData.destination,
                                        type = Operation.CHECK
                                )
                                sendMessage(checkMessage)
                                isClient = true
                            }
                        }
                        //If the client is not in our list, we send it to routers
                        if (!isClient) {
                            val bounceMessage = RouterPacket(
                                    sourceAddress = tempData.sourceAddress,
                                    destination = tempData.destination,
                                    message = tempData.message,
                                    type = Operation.BOUNCE
                            )
                            for (router in routerList) {
                                sendRouterMessage(bounceMessage, router.first, router.second)
                            }

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

    suspend fun sendRouterMessage(message: RouterPacket, destAddress: Inet4Address, port: Int) {
        println("Router starting to send message to other router")
        val socket = Socket(destAddress, port)
        val outputStream = ObjectOutputStream(socket.getOutputStream())
        outputStream.writeObject(message)
        socket.close()
    }

    //We manage to make this not suspend by having it launch the job itself, hopefully it works
    fun sendListMessage(destAddress: Inet4Address, port: Int) {
        val message = RouterPacket(
                sourceAddress = localhost as Inet4Address,
                destination = Pair(destAddress, port),
                message = this.portNum,
                type = Operation.LIST
        )
        val send = launch(CommonPool) {
            sendMessage(message)
        }
    }
}
