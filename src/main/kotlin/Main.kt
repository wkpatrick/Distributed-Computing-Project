import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.net.Inet4Address

fun main(args: Array<String>) = runBlocking<Unit> {
    //val server = launch(CommonPool) { launchTextServer() }
    //val client = launch(CommonPool) { launchTextClient() }
    //client.join()
    //server.join()

    val localhost = Inet4Address.getLocalHost() as Inet4Address

    var clientPort = 5555
    var serverPort = 4444
    var routerPort = 6666
    val testCS = ClientServer(clientPort, serverPort, localhost, routerPort)

    val recieveClientPort = 7777
    val recieveServerPort = 8888
    val testReciever = ClientServer(clientPort, recieveServerPort, localhost, routerPort)

    val router = Router(routerPort)
    router.launchRouter()
    testCS.launchServer()
    testReciever.launchServer()


    delay(1000L)
    //val testPacket=RouterPacket(localhost,localhost,"Hello World", Operation.MESSAGE)

    val illiad = File("texts/illiad.txt")
    val odyssey = File("texts/odyssey.txt")
    val aeneid = File("texts/aeneid.txt")
    val inferno = File("texts/inferno.txt")
    val combo = File("texts/combo.txt")

    var content = illiad.readLines()
    var testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)

    content = odyssey.readLines()
    testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)

    content = aeneid.readLines()
    testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)

    content = inferno.readLines()
    testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)

    content = combo.readLines()
    testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)




    //testCS.sendMessage(testPacket)

    while(true)
    {

    }
}



