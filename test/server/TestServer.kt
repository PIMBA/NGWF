package server

import org.junit.Test
import server.bootstrap.HttpServer

class TestServer {
    @Test
    fun test(){
        HttpServer.start();
    }
}