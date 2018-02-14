# Distributed-Computing-Project
This is the code I submitted for the course project for my Distributed Computing class. It is written entirely in Kotlin (the java files were provided to us by the professor as a reference). 
The full requirements are in the Project Specification PDFs. The basic objectives can be broken into two phases or parts:
1. Create a program that can send and recieve text to other copies of the program. These messages are sent though a "router" which handles client registration and forwarding messages.
2. Add support for having multiple routers between a client and a server, and once the client and server have checked that each other exist, send the message directly from server -> client, without having it go through the server.

## Implementation Details:
I used this project as an oppritunity to learn Kotlin, a JVM language that is fully compatible with java, and adds some extra features. The big feature that I used was their support for coroutines, which I used as light-weight threads. This allowed me to easily have the router and Client/Server accept multiple connections and keep working. 
I also used the TornadoFX library to easily make a simple UI. 

The program is borken into 2 parts: 
1. The Client/Server which handles registering with the Router and sending/recieving text
2. The Router, which accepts registration reqeusts from both Client/Servers and other Routers in order to expand the network.

Both parts communicate with eachother over TCP using DataPackets. A DataPacket is a serializeable container of all the information the two components need (Source, Destination, Message Type, and the message itself). 
In order to allow having multiple routers in a network, if a router recieves a message for a client that is not registered to it, it forwards the message to other routers that have registered with it. In order to keep messages from being sent in a loop, there is a limit to how many times a message can be fowarded to a new router.
