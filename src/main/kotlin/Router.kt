import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Inet4Address
import java.net.ServerSocket
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread


class Router(val portNumber: Int)
//Need to get some sort of statistics set up.
{
    init{
        val localhost = Inet4Address.getLocalHost()
        val serverSocket = ServerSocket(portNumber)
        val historySemaphore = Semaphore(1)

        //The router will store each packet it recieves, this way we can get the send/receive stats from it when we construct it for later.
        val routerHistory: ArrayList<RouterPacket>


        while(true){
            thread {
                //Accepts the socket and starts doing its work
                val clientSocket = serverSocket.accept()
                val inputStream = ObjectInputStream(clientSocket.getInputStream())
                val outputStream = ObjectOutputStream(clientSocket.getOutputStream())

                //Recieve the message
                //TODO: Ensure this doesn't create junk data when there is nothing trying to connect, check the SeverSocket.accept() docs? (It should block until a connection is made)
                val tempData: RouterPacket = inputStream.readObject() as RouterPacket
                //Send the message
                //Record the message

                historySemaphore.acquire()
                historySemaphore.release()
            }
            //Look through the queue and send any packets that need to be sent.
        }
    }
}
