package middleware.router

import server.http.HttpContext
import server.pipeline.RequestDelegate

class RouteContext (val httpContext: HttpContext){
    var handler: RequestDelegate? = null;
    val routeData:RouteData = RouteData();

}