package server.http

class HttpRequest(val requestHeader: String){
    var method = ""
        get;
    var url = ""
        get;
    var protocol = ""
        get;

    val headerMap = mutableMapOf<String,String>();
    init {
        val lines = requestHeader.split("\r\n");
        val requestLineProps = lines[0].split(" ");
        method = requestLineProps[0];
        url = requestLineProps[1];
        protocol = requestLineProps[2];
        val headlines = lines.subList(1,lines.size - 1);
        headlines.forEach {
            val kv = it.split(":");
            if(kv.size > 1) headerMap.put(kv[0],kv[1].substring(1));
        }
    }
}