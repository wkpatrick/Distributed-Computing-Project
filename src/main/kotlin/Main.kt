import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.*
import java.net.Inet4Address
import java.net.ServerSocket
import java.net.Socket

fun main(args: Array<String>) = runBlocking<Unit> {
    //val server = launch(CommonPool) { launchTextServer() }
    //val client = launch(CommonPool) { launchTextClient() }
    //client.join()
    //server.join()
    val localhost = Inet4Address.getLocalHost() as Inet4Address
    val testCS = ClientServer(5555, 4444, localhost, 6666)
    testCS.launchBoth()
}

suspend fun launchTextClient() {
    println("Starting Client")
    val localhost = Inet4Address.getLocalHost()
    delay(1000L)
    val socket = Socket(localhost, 4444)
    val input = BufferedReader(InputStreamReader(socket.getInputStream()))
    val output = PrintWriter(socket.getOutputStream(), true)
    output.println("Hello World!")
    output.println("The Server will Never Close!")
    output.println("Forever!")
    socket.close()
    println("Closing Client")
}

suspend fun launchTextServer() {
    println("Starting Server")
    val localhost = Inet4Address.getLocalHost()
    val serverSocket = ServerSocket(4444)
    val clientServerSocket = serverSocket.accept()
    println("Connection Accepted!: " + clientServerSocket.isConnected)
    val input = BufferedReader(InputStreamReader(clientServerSocket.getInputStream()))

    println("Starting Accepting Server Input")
    for(line in input.lines())
    {
        println(input.readLine())
    }
    println("Closing Server")
}




