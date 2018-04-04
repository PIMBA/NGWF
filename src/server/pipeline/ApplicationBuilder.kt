import ioc.Beans
import server.http.HttpContext
import server.pipeline.RequestDelegate
import kotlin.reflect.*
import kotlin.reflect.full.*

fun <R> KFunction<R>.callNamed(params: Map<KParameter, Any>, self: Any? = null, extSelf: Any? = null): R {
    val map = params.toMutableMap();
    if (self != null) map += instanceParameter!! to self
    if (extSelf != null) map += extensionReceiverParameter!! to extSelf
    return callBy(map.toMap());
}

object ApplicationBuilder{
    const val INVOKE_NAME = "excute";

    private val components = mutableListOf<(next: RequestDelegate)-> RequestDelegate>();
    fun use(middleware: (next: RequestDelegate)-> RequestDelegate): ApplicationBuilder {
        components.add(middleware);
        return this;
    }

    fun build() : RequestDelegate{
        components.reverse();
        var app: RequestDelegate = { println("the default") };
        this.components.forEach { app = it(app); }
        return app;
    }

    fun useMiddleware(clazz: KClass<out Any>,vararg params: Any): ApplicationBuilder{
        val fn = clazz.functions.first{
            it.name == INVOKE_NAME // named "excute"
                    && it.visibility == KVisibility.PUBLIC // is public
                    && it.parameters[1].type.classifier ==  HttpContext::class// first parameter is typeof HttpContext .
        };
        val ps = fn.parameters;
        return use{
            val next = it;
            {
                val pps = clazz.primaryConstructor!!.parameters;
                val map = findAllDependences(pps.drop(1),*params);
                val mmap = map.toMutableMap();
                mmap[pps[0]] = next;
                val instance = clazz.primaryConstructor!!.callBy(mmap);

                val context = it;
                val dmap = findAllDependences(ps.drop(1), *(Array(1,{context})));
                fn.callNamed(dmap,instance);
            }
        }
    }

    private fun findAllDependences(ps:List<KParameter>, vararg params:Any): Map<KParameter, Any> {
        val map = mutableMapOf<KParameter,Any>();
        val p = params.toMutableList();
        if(ps.isNotEmpty()) ps.forEach {
            val t = it.type;
            try {
                map[it] = p.first { it::class.isSubclassOf(t.classifier as KClass<*>) };
                p.removeAt(p.indexOfFirst { it::class.isSubclassOf(t.classifier as KClass<*>) });
            }catch (e:NoSuchElementException){
                val bean = ioc.Beans[t.classifier as KClass<out Any>] ?: throw  e;
                map[it] = bean;
            }
        }
        return map.toMap();
    }

    inline fun <reified T: Any> useMiddleware(vararg params: Any) : ApplicationBuilder = useMiddleware(T::class,*params);
}