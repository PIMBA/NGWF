package middleware.router.abstract

import server.http.HttpContext

interface IRouteConstraint {
    fun match(httpContext: HttpContext,
              route:IRouter,
              routeKey: String
    );
}