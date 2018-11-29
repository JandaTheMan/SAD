package Prac3


import Prac3.sockets.MyServerSocket
import Prac3.sockets.MySocket
import Prac3.sockets.MyServerSocket.Companion as ServerSocketFactory
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread


fun main(args: Array<String>) = ChatServer(args).run()

class ChatServer(args: Array<String>) {

    private val serverSocket: MyServerSocket? = ServerSocketFactory.fromPort(args[0].toInt())

    //Map with all the users connected to the server
    private val activeUsers = ConcurrentHashMap<String, MySocket>()

    fun run() {

        //The bad creation of the server socket avoids the start of the program. The error is handled and printed by the server socket wrapper class
        if (serverSocket == null) return

        //loop waiting for new connections
        while (true) {


            //The bad creation of the accepted socket avoids the start of the program. The error is handled and printed by the socket wrapper class
            val socket: MySocket? = (serverSocket.accept() as MySocket)
            socket?.setStreams()

            if (socket != null) {

                var userName:String

                while (true) {
                    val nickName: String = socket.readLine() ?: ""
                    if (!activeUsers.containsKey(nickName) && nickName != "") {
                        userName = nickName
                        break
                    }
                    socket.println("Nick Already Taken. Introduce different name: ")
                }

                if (userName != "") {
                    thread(
                            start = true,
                            block = serverLogic(userName, socket)
                    )
                }
            }

        }

    }

    private fun serverLogic(userName: String, threadSocket: MySocket): () -> Unit = {

        activeUsers[userName] = threadSocket

        //Notification that new user joined the chat
        threadSocket.sendToGroup(
                users = activeUsers,
                message = "$userName has join the chat"
        )

        threadSocket.lines().forEach { line ->

            //matches with private message to users
            if (false) {
                //initialize the map with the users whisper is for
                //val privateGroup = ...
                /*
                threadSocket.sendToGroup(
                        users= privateGroup,
                        message= "<$nick>to(group members): $line"
                )
                 */
            } else {
                threadSocket.sendToGroup(
                        users = activeUsers,
                        message = "<$userName>: $line"
                )
            }
        }

        activeUsers.remove(userName)
        //Notification that the user is leaving the chat
        threadSocket.sendToGroup(
                users = activeUsers,
                message = "$userName has left the chat"
        )
        threadSocket.close()
    }


}

fun Socket.sendToGroup(users: ConcurrentHashMap<String, MySocket>, message: String) {

    users.forEach { nick_id, mySocket ->
        if (users[nick_id] !== this)
            mySocket.println(message)
    }
}


