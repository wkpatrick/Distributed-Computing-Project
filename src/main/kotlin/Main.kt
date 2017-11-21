import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.TerminalBuilder
import org.codetome.zircon.api.component.builder.*
import org.codetome.zircon.api.resource.CP437TilesetResource
import java.io.File
import java.net.Inet4Address

fun main(args: Array<String>) = runBlocking<Unit> {
    //val server = launch(CommonPool) { launchTextServer() }
    //val client = launch(CommonPool) { launchTextClient() }
    //client.join()
    //server.join()

    val columnSize = 68
    val rowSize = 36

    //TODO: do something like panelColumnSize = columnSize - panel2ColumnSize - some constant (2?) for the borders
    val panelColumnSize = columnSize - 25
    val panelRowSize = rowSize - 1 //We allow 1 for the drop shadow

    val panel2ColumnSize = columnSize - panelColumnSize - 2
    val panel2RowSize = panelRowSize

    val terminal = TerminalBuilder.newBuilder()
            .initialTerminalSize(Size.of(columnSize, rowSize))
            .font(CP437TilesetResource.WANDERLUST_16X16.toFont())
            .title("Distributed Computing Project")
            .build()

    val screen = TerminalBuilder.createScreenFor(terminal)

    //Set up the left side of the program
    val panel = PanelBuilder.newBuilder()
            .wrapInBox()
            .title("Client/Server")
            .addShadow()
            .size(Size.of(panelColumnSize, panelRowSize))
            .position(Position.OFFSET_1x1)
            .build()

    val routerLabel = LabelBuilder.newBuilder()
            .position(Position.OFFSET_1x1)
            .text("Router Address: ")
            .build()

    val routerIPInputBox = TextBoxBuilder.newBuilder()
            .position(Position.of(0, 0)
                    .relativeToRightOf(routerLabel))
            .size(Size.of(16, 1))
            .build()

    val connectButton = ButtonBuilder.newBuilder()
            .position(Position.of(0,0)
                    .relativeToBottomOf(routerLabel))
            .text("Connect")
            .build()





    //Set up the Right Side of the label
    val panel2 = PanelBuilder.newBuilder()
            .wrapInBox()
            .title("Router")
            .addShadow()
            .size(Size.of(panel2ColumnSize, panel2RowSize))
            .position(Position.of(0, 0)
                    .relativeToRightOf(panel))
            .build()

    val routerEnableCheckBox = CheckBoxBuilder.newBuilder()
            .text("Enable Router")
            .position(Position.OFFSET_1x1)
            .build()
    val routerConnectListLabel = LabelBuilder.newBuilder()
            .text("-Connected Servers-")
            .position(Position.of(0,0)
                    .relativeToBottomOf(routerEnableCheckBox))
            .build()

    val testRouterConnectList = TextBoxBuilder()
            .position(Position.of(0,0)
                    .relativeToBottomOf(routerConnectListLabel))
            .size(Size.of(16,1))
            .text("255.255.255.255")
            .build()



    panel.addComponent(routerLabel)
    panel.addComponent(routerIPInputBox)
    panel.addComponent(connectButton)

    panel2.addComponent(routerEnableCheckBox)
    panel2.addComponent(routerConnectListLabel)
    panel2.addComponent(testRouterConnectList)

    screen.addComponent(panel)
    screen.addComponent(panel2)

    screen.display()



    terminal.flush()


    /**
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



