package server.dispatcher

import server.http.HttpContext
import server.http.HttpRequest
import java.nio.channels.SelectionKey

object Dispatcher {
    init {
        
    }


    fun commit(requestHeader: String, selectionKey: SelectionKey){
        val httpContext = HttpContext(requestHeader,selectionKey);
    }
}