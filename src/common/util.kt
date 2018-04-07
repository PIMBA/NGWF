package common

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter

class Resources : AutoCloseable {
    val resources = mutableListOf<AutoCloseable>()

    fun <T : AutoCloseable> T.use(): T {
        resources += this
        return this
    }

    override fun close() {
        var exception: Exception? = null
        for (resource in resources.reversed()) {
            try {
                resource.close()
            } catch (closeException: Exception) {
                if (exception == null) {
                    exception = closeException
                } else {
                    exception.addSuppressed(closeException)
                }
            }
        }
        if (exception != null) throw exception
    }
}

inline fun <T> using(block: Resources.() -> T): T = Resources().use(block)

inline fun <R> KFunction<R>.callNamed(params: Map<KParameter, Any>, self: Any? = null, extSelf: Any? = null): R {
    val map = params.toMutableMap();
    if (self != null) map += instanceParameter!! to self
    if (extSelf != null) map += extensionReceiverParameter!! to extSelf
    return callBy(map.toMap());
}

typealias Features = LinkedHashMap<KClass<*>, Any>;