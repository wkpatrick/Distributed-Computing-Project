import javafx.application.Application
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import kotlinx.coroutines.experimental.runBlocking
import tornadofx.*
import java.net.InetAddress


fun main(args: Array<String>) = runBlocking<Unit> {
    //val server = launch(CommonPool) { launchTextServer() }
    //val client = launch(CommonPool) { launchTextClient() }
    //client.join()
    //server.join()

    Application.launch(DistributedComputingProject::class.java, *args)

    /**
    val localhost = Inet4Address.getLocalHost() as Inet4Address

    var clientPort = 5555
    var serverPort = 4444
    var routerPort = 6666
    val testCS = ClientServer(clientPort, serverPort, localhost, routerPort)
                                                        localhost is the router ip

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
    delay(1000L)

    content = odyssey.readLines()
    testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)
    delay(1000L)

    content = aeneid.readLines()
    testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)
    delay(1000L)

    content = inferno.readLines()
    testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)
    delay(1000L)

    content = combo.readLines()
    testPacket = RouterPacket(localhost, Pair(localhost,recieveServerPort), content, Operation.MESSAGE)
    testPacket.startTime = System.currentTimeMillis()
    testCS.sendMessage(testPacket)
    delay(1000L)




    //testCS.sendMessage(testPacket)

    while(true)
    {

    }

     **/
}

class DistributedComputingProject : App(ProjectView::class, Styles::class){
}

class Styles : Stylesheet() {
    init {
        label {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
            //backgroundColor += c("#cecece")
        }
    }
}







