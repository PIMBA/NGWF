import server.pipeline.Middleware
import server.pipeline.RequestDelegate

object ApplicationBuilder{
    val components = mutableListOf<Middleware>();
    fun use(middleware: Middleware): ApplicationBuilder {
        components.add(middleware);
        return this;
    }

    fun build() : RequestDelegate{
        components.reverse();
        var app: RequestDelegate = { println("the default") };
        this.components.forEach { app = it(app); }
        return app;
    }
}