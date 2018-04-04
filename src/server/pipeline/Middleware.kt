package server.pipeline

import server.http.HttpContext

typealias RequestDelegate = (context: HttpContext)-> Unit;

// typealias Middleware = (next: RequestDelegate)-> RequestDelegate;

/**
 * 约定 Middleware :
 * 必须包含一个签名为 invoke(httpContext: HttpContext)-> Unit; 方法
 * 必须包含一个只包含一个 RequestDelegate 类型参数的构造函数
 */

