package Prac3.sockets

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketImpl
import java.util.stream.Stream

class MyServerSocket private constructor(port: Int) : ServerSocket(port) {

    override fun accept(): Socket? {
        try {
            val sc = MySocket.fromSocketImpl(null as SocketImpl?)
            implAccept(sc)
            return sc
        } catch (e: IOException) {
            println("Error accepting incoming socket+$e")
        }
        return null
    }

    companion object {

        fun fromPort(port: Int): MyServerSocket? =
                try {
                    MyServerSocket(port)
                } catch (ex: IOException) {
                    println("Error in server socket creation +$ex")
                    null
                }
    }
}


class MySocket : Socket {

    private var input: BufferedReader? = null
    private var out: PrintWriter? = null

    private constructor(host: String, port: Int) : super(host, port) {
        input = BufferedReader(InputStreamReader(inputStream))
        out = PrintWriter(outputStream, true)
    }

    private constructor(socket: SocketImpl?) : super(socket)

    fun setStreams(): MySocket? {
        try {
            input = BufferedReader(InputStreamReader(this.inputStream))
            out = PrintWriter(this.outputStream, true)
            return this
        } catch (e: IOException) {
            System.out.println("Error during the creation of the stream")
            return null
        }
    }

    fun readLine(): String? {
        try {
            return input!!.readLine()
        } catch (e: Exception) {
            System.out.println(e)
        }
        return null
    }

    fun lines(): Stream<String> {
        return input!!.lines()
    }

    fun println(line: String) {
        out!!.println(line)
    }

    override fun close() {
        try {
            super.close()
        } catch (e: IOException) {
            System.out.println("Error during closing connection with the server")
        }

    }

    override fun shutdownInput() {
        try {
            super.shutdownInput()
        } catch (e: IOException) {
            System.out.println("Error during closing connection with the server")
        }

    }

    companion object {

        fun fromRute(adress: String, port: Int): MySocket? =
                try {
                    MySocket(adress, port)
                } catch (ex: IOException) {
                    System.out.println("Error connecting with server +$ex")
                    null
                }

        fun fromSocketImpl(impl: SocketImpl?): MySocket? =
                try {
                    MySocket(impl)
                } catch (ex: IOException) {
                    System.out.println("Error connecting with server +$ex")
                    null
                }
    }
}


