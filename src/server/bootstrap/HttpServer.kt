package server.bootstrap

import common.using
import java.net.InetAddress
import java.net.ServerSocket
import server.dispatcher.Dispatcher;

object HttpServer {
    fun start(host:String = "127.0.0.1", port:Int = 8080, backlog:Int = 1){
        val server = ServerSocket(port,backlog, InetAddress.getByName(host));
        while(true){
            using{
                Dispatcher.commit(server.accept().use());
            }
        }
    }
}