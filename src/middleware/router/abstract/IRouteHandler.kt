package middleware.router.abstract

import middleware.router.RouteData
import server.pipeline.RequestDelegate
import javax.xml.ws.spi.http.HttpContext

interface IRouteHandler {
    fun getRequestHandler(httpContext: HttpContext, routeData: RouteData): RequestDelegate;
}