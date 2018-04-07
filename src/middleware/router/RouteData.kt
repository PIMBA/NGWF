package middleware.router

import middleware.router.abstract.IRouter

class RouteData {
    val routers = mutableListOf<IRouter>()
        get;
    val dataTokens = mutableMapOf<String,Any>()
        get;
    val values = mutableMapOf<String,Any>()
        get;
}