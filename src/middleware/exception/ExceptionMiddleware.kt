package middleware.exception

import server.http.HttpContext
import server.pipeline.RequestDelegate

class ExceptionMiddleware (val next:RequestDelegate){
    fun excute(httpContext: HttpContext){
        try{
            next(httpContext);
        }catch (exception: Exception){
            println(exception.message);
            exception.printStackTrace();
        }
    }
}