package middleware.router

import middleware.router.abstract.IRouter
import server.http.HttpContext
import server.pipeline.RequestDelegate

class RouteMiddleware(private val next: RequestDelegate, private val router: IRouter) {

    fun excute(httpContext: HttpContext){
        val context = RouteContext(httpContext);
        context.routeData.routers.add(router);

        router.route(context);

        if(context.handler == null) next(httpContext);
        else {
            httpContext.features[RouteData::class] = context.routeData;
            context.handler!!(httpContext)
        };
    }
}