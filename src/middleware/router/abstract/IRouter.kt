package middleware.router.abstract

import middleware.router.RouteContext
import middleware.router.VirtualPathContext
import middleware.router.VirtualPathData

interface IRouter{
    fun route(routeContext: RouteContext);
    fun getVirtualPath(context: VirtualPathContext): VirtualPathData;
}