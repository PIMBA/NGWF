package server.http

import common.Features
import java.nio.channels.SelectionKey

//class HttpContext(private val header:String, private val key: SelectionKey) {
//    val request = HttpRequest(header);
//    val response = HttpResponse(key);
//}

class HttpContext() {
    val features : Features = Features();
}