package server.pipeline

import server.http.HttpContext

typealias RequestDelegate = (context: HttpContext)-> Unit;

typealias Middleware = (next: RequestDelegate)-> RequestDelegate;