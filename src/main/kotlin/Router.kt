import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Inet4Address
import java.net.ServerSocket
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.sync.Mutex


class Router(val portNumber: Int)
//Need to get some sort of statistics set up.
{
    init {
        val localhost = Inet4Address.getLocalHost()
        val serverSocket = ServerSocket(portNumber)

        //The router will store each packet it recieves, this way we can get the send/receive stats from it when we construct it for later.
        val routerHistory: ArrayList<RouterPacket> = ArrayList<RouterPacket>()
        val historyMutex = Mutex()

        while (true) {
            val job = launch(CommonPool)
            {
                //Accepts the socket and starts doing its work
                val clientSocket = serverSocket.accept()
                val inputStream = ObjectInputStream(clientSocket.getInputStream())
                val outputStream = ObjectOutputStream(clientSocket.getOutputStream())

                //Recieve the message
                //TODO: Ensure this doesn't create junk data when there is nothing trying to connect, check the SeverSocket.accept() docs? (It should block until a connection is made)
                val tempData: RouterPacket = inputStream.readObject() as RouterPacket
                //Send the message
                outputStream.writeObject(tempData)
                //Record the message
                historyMutex.lock()
                try {
                    routerHistory.add(tempData)
                } finally {
                    historyMutex.unlock()
                }
            }
            //job.join()
        }
    }
}
