import java.io.*
import java.net.*

class ClientServer(val clientPort: Int, val routerAddress: Inet4Address, val routherPort: Int) {

    val localhost = Inet4Address.getLocalHost();

    fun sendMessage(serverAddress: Inet4Address, serverPort: Int) {
        val serverSocket = Socket(routerAddress, routherPort);
        //Output is what gets sent ot the server, input was what the client gets back
        val inputStream = ObjectInputStream(serverSocket.getInputStream());
        val outputStream = ObjectOutputStream(serverSocket.getOutputStream());

        val dataToSend = RouterPacket(localhost as Inet4Address, serverAddress, clientPort, serverPort, "Test");
        outputStream.writeObject(dataToSend);
    }

}