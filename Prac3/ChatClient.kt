package Prac3
import Prac3.sockets.MySocket
import Prac3.sockets.MySocket.Companion as SocketFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

fun main(args: Array<String>) = ChatClient().run(args)

class ChatClient {

    private val input = try{BufferedReader(InputStreamReader(System.`in`))}catch(ex:Exception){null}

    fun run(args: Array<String>) {

        //The bad creation of the input stream reader avoids the start of the program.
        if(input==null) return

        //The bad creation of the socket avoids the start of the program. The error is handled and printed by the socket wrapper class
        val socket: MySocket = SocketFactory.fromRute(args[0], Integer.parseInt(args[1])) ?: return

        //Once socket is correctly created sends the nickname introduced by the client user
        socket.println(args[2])

        //Input Thread in charge of read from the keyboard and sends the lines to the server socket
        thread(
                start = true,
                block = {//lambda expression directly written in the block
                    input.lines()?.forEach { socket.println(it) } //When lines are available in the socket stream, they are printed in the console output
                    socket.shutdownInput()
                }
        )
        //Output Socket
        thread(
                start = true,
                block = outputThreadLogic(socket) //named function
        )
    }

    private fun outputThreadLogic(socket: MySocket): () -> Unit {
        return {
            socket.lines().forEach { println(it) }
            socket.close()
        }
    }
}


