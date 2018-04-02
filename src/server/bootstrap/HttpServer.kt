package server.bootstrap

import common.using
import server.dispatcher.Dispatcher
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel


object HttpServer : Runnable{
    var interrupted: Boolean = false
    fun interrupt() {
        this.interrupted = true
    }
    var port:Int = 8080
        get
        set

    fun start() = Thread(this).start()

    override fun run() {
        try{
            val selector = Selector.open()
            val serverSocketChannel = ServerSocketChannel.open()
            val serverSocket = serverSocketChannel.socket()
            serverSocket.reuseAddress = true
            serverSocket.bind(InetSocketAddress(port))
            println("server bind at port 8080.")
            serverSocketChannel.configureBlocking(false)
            println("server running in nio mode.")
            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT)
            println("server started.")
            while (!interrupted) lifecycle(selector)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun lifecycle(selector: Selector){
        val size = selector.select()
        if(size <= 0) return
        val keys = selector.selectedKeys()
        if(keys.size == 0) return
        val iter = keys.iterator();
        while (iter.hasNext()){
            val key = iter.next();
            when{
                key.isAcceptable->{
                    val server = key.channel() as ServerSocketChannel
                    val socketChannel = server.accept()
                    val address = socketChannel.remoteAddress
                    println("receive request from $address")
                    socketChannel.configureBlocking(false)
                    socketChannel.register(selector,SelectionKey.OP_READ)
                }
                key.isReadable->{
                    val socketChannel = key.channel() as SocketChannel
                    val requestHeader = receive(socketChannel)
                    if (requestHeader.isNotEmpty()) Dispatcher.commit(requestHeader, key)
                }
                key.isWritable->{
                    val socketChannel = key.channel() as SocketChannel
                    socketChannel.shutdownInput()
                    socketChannel.close()
                }
            }
            iter.remove();
        }
    }

    /**
     * core of nio
     */
    private fun receive(socketChannel: SocketChannel):String {
        val buffer = ByteBuffer.allocate(1024)
        var bytes: ByteArray? = null
        val baos = ByteArrayOutputStream()
        while (true) {
            val size = socketChannel.read(buffer)
            if(size <= 0) break
            buffer.flip()
            bytes = ByteArray(size)
            buffer.get(bytes)
            baos.write(bytes)
            buffer.clear()
        }
        bytes = baos.toByteArray()
        return String(bytes!!)
    }
}