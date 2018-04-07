package server.pipeline

import server.http.HttpContext

typealias RequestDelegate = (context: HttpContext)-> Unit;

/**
 * 约定 Middleware :
 * 必须包含且仅包含一个签名为 excute(httpContext: HttpContext)-> Unit; 或拥有更多其他参数的方法
 * 主构造函数必定为第一个参数是RequestDelegate类型的构造函数。
 */