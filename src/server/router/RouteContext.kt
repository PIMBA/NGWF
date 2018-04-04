package server.router

import server.pipeline.RequestDelegate

class RouteContext {
    var handler: RequestDelegate? = null
        set(value) {
            field = value;
            if(field != null) doJob();
        }

    private fun doJob(){

    }
}