package server.http

import java.nio.channels.SelectionKey

class HttpResponse(val key:SelectionKey){

    var contentType = "text/html"
        get
        set;

    var code = 200
        get
        set(value) {
            val sts = calStatus(value) ?: return;
            status = sts;
            field = value;
        }

    private var status = "OK";
    private fun calStatus(value: Int): String?{
        when {

        }
        return null;
    }

}
