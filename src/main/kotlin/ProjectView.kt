import javafx.scene.control.Button
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import kotlinx.coroutines.experimental.CommonPool
import tornadofx.*
import java.io.File
import java.net.Inet4Address
import java.net.InetAddress

class ProjectView : View("Project View") {
    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        var routerIPTextBox: TextField by singleAssign()
        var routerPortTextBox: TextField by singleAssign()
        var routerConnectButton: Button by singleAssign()

        var isRouterRunning = false

        var serverIPTextBox: TextField by singleAssign()
        var serverPortTextBox: TextField by singleAssign()
        var serverConnectButton: Button by singleAssign()

        var runRouterPortTextBox: TextField by singleAssign()
        var runRouterLaunchButton: Button by singleAssign()

        var connectRouterIPTextBox: TextField by singleAssign()
        var connectRouterPortTextBox: TextField by singleAssign()
        var connectRouterButton: Button by singleAssign()

        var launchServerPortTextBox: TextField by singleAssign()

        var router = Router()
        var localhost = Inet4Address.getLocalHost().hostAddress
        var clientServer = ClientServer(-1, localhost, -1)



        tab("Client/Server") {
            borderpane {
                center = vbox {
                    form {
                        fieldset("Send Texts") {
                            field("Server IP: ") {
                                serverIPTextBox = textfield().apply {
                                    appendText("127.0.0.1")
                                }
                            }
                            field("Server Port: ") {
                                serverPortTextBox = textfield().apply {
                                    appendText("6666")
                                }
                            }
                            serverConnectButton = button("Send") {
                                action {
                                    val illiad = File("texts/illiad.txt")
                                    val odyssey = File("texts/odyssey.txt")
                                    val aeneid = File("texts/aeneid.txt")
                                    val inferno = File("texts/inferno.txt")
                                    val combo = File("texts/combo.txt")

                                    var content = illiad.readLines()
                                    var testPacket = RouterPacket(Inet4Address.getByName(localhost) as Inet4Address, Pair(Inet4Address.getByName(serverIPTextBox.text) as Inet4Address, Integer.parseInt(serverPortTextBox.text)), content, Operation.MESSAGE)
                                    testPacket.time.add(System.currentTimeMillis())
                                    val send = kotlinx.coroutines.experimental.launch(CommonPool) {
                                        clientServer.sendMessageToClient(testPacket)
                                    }


                                    content = odyssey.readLines()
                                    testPacket = RouterPacket(Inet4Address.getByName(localhost) as Inet4Address, Pair(Inet4Address.getByName(serverIPTextBox.text) as Inet4Address, Integer.parseInt(serverPortTextBox.text)), content, Operation.MESSAGE)
                                    testPacket.time.add(System.currentTimeMillis())
                                    val send2 = kotlinx.coroutines.experimental.launch(CommonPool) {
                                        clientServer.sendMessageToClient(testPacket)
                                    }

                                    content = aeneid.readLines()
                                    testPacket = RouterPacket(Inet4Address.getByName(localhost) as Inet4Address, Pair(Inet4Address.getByName(serverIPTextBox.text) as Inet4Address, Integer.parseInt(serverPortTextBox.text)), content, Operation.MESSAGE)
                                    testPacket.time.add(System.currentTimeMillis())
                                    val send3 = kotlinx.coroutines.experimental.launch(CommonPool) {
                                        clientServer.sendMessageToClient(testPacket)
                                    }


                                    content = inferno.readLines()
                                    testPacket = RouterPacket(Inet4Address.getByName(localhost) as Inet4Address, Pair(Inet4Address.getByName(serverIPTextBox.text) as Inet4Address, Integer.parseInt(serverPortTextBox.text)), content, Operation.MESSAGE)
                                    testPacket.time.add(System.currentTimeMillis())
                                    val send4 = kotlinx.coroutines.experimental.launch(CommonPool) {
                                        clientServer.sendMessageToClient(testPacket)
                                    }


                                    content = combo.readLines()
                                    testPacket = RouterPacket(Inet4Address.getByName(localhost) as Inet4Address, Pair(Inet4Address.getByName(serverIPTextBox.text) as Inet4Address, Integer.parseInt(serverPortTextBox.text)), content, Operation.MESSAGE)
                                    testPacket.time.add(System.currentTimeMillis())
                                    val send5 = kotlinx.coroutines.experimental.launch(CommonPool) {
                                        clientServer.sendMessageToClient(testPacket)
                                    }

                                }
                            }
                        }
                    }
                }
                right = vbox {
                    form {
                        fieldset("Host Server") {
                            field("Server Port") {
                                launchServerPortTextBox = textfield("5555").apply {
                                }
                            }
                            field("Router IP") {
                                routerIPTextBox = textfield("127.0.0.1").apply {
                                }
                            }
                            field("Router Port") {
                                routerPortTextBox = textfield("8888").apply {
                                }
                            }

                            button("Launch") {
                                action {
                                    runAsync {
                                        clientServer = ClientServer(Integer.parseInt(
                                                launchServerPortTextBox.text),
                                                routerIPTextBox.text,
                                                Integer.parseInt(routerPortTextBox.text)
                                        )
                                        val cliServ = kotlinx.coroutines.experimental.launch(CommonPool) {
                                            clientServer.launchObjectServer(
                                                    Integer.parseInt(launchServerPortTextBox.text),
                                                    routerIPTextBox.text,
                                                    Integer.parseInt(routerPortTextBox.text)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                bottom = hbox {
                    val localhost = InetAddress.getLocalHost()
                    label("Local Host: " + localhost.toString())
                }

            }
        }
        tab("Router") {
            borderpane {
                //Left Side For connecting to a router
                left = vbox {

                    form {
                        fieldset("Router") {
                            //radiobutton("Run Local Router")
                            field("Router IP") {
                                runRouterPortTextBox = textfield().apply {
                                    appendText("8888")
                                }
                                runRouterLaunchButton = button().apply {
                                    text = "Launch Router"
                                    action {
                                        if (isRouterRunning == false) {
                                            router.launchRouter(Integer.parseInt(runRouterPortTextBox.text))
                                            println("Launched Router on Port: " + Integer.parseInt(runRouterPortTextBox.text))
                                            isRouterRunning = true
                                            runRouterLaunchButton.text = "Stop Router"
                                        } else if (isRouterRunning == true) {
                                            router.cancelRouter()
                                            println("Closing Router")
                                            isRouterRunning = false
                                            runRouterLaunchButton.text = "Start Router"
                                        }
                                    }
                                }
                            }
                            field("Link Router") {
                                connectRouterIPTextBox = textfield().apply {
                                    appendText("127.0.0.1")
                                }
                                connectRouterPortTextBox = textfield().apply {
                                    appendText("9999")
                                }

                                connectRouterButton = button().apply {
                                    text = "Link"
                                    action {
                                        router.sendListMessage(Inet4Address.getByName(connectRouterIPTextBox.text) as Inet4Address, Integer.parseInt(connectRouterPortTextBox.text))
                                    }
                                }
                            }
                        }
                    }
                }

                //Right side for running own router
                right = vbox {

                }

                //TODO: Bottom for viewing all the ips connected to the router
            }
        }

    }
}