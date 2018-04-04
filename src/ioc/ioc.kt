package ioc

import exception.NoInjectableAnnotationException
import ioc.`interface`.IBeanFactory
import ioc.annotation.Injectable
import ioc.defaultImp.DefaultBeanFactory
import kotlin.reflect.KClass

enum class BeanType {
    Single,
    Transient
}
/**
 * Beans
 * contains all bean in application context.
 * init beans before application run.
 */
object Beans {
    /**
     * get a bean by bean class.
     * if bean is not define in beans will return a null.
     */
    operator fun get(klass: KClass<out Any>): Any? = beanFactory[klass]
    inline fun <reified K: Any>get(): K? = this.get(K::class) as K

    /**
     * if beans contains a bean definition.
     */
    operator fun contains(index: KClass<out Any>): Boolean = index in beanFactory
    inline fun <reified K: Any>contains(): Boolean = this.contains(K::class)

    /**
     * get bean life enum by bean class
     * if bean is not defined in beans return a null.
     */
    fun lifeOf(index: KClass<out Any>): BeanType? = beanFactory.lifeOf(index)
    inline fun <reified K: Any>lifeOf(): BeanType? = this.lifeOf(K::class)

    var beanFactory: IBeanFactory = DefaultBeanFactory()

    /**
     * register a batch of bean class
     */
    fun registerBeans(vararg klasses: KClass<out Any>): Beans {
        klasses.forEach { klass ->
            var found = false
            klass.annotations.forEach {
                if (it is Injectable) {
                    val injectable = it
                    when (injectable.type) {
                        BeanType.Transient -> this.beanFactory.addTransient(klass)
                        BeanType.Single -> this.beanFactory.addSingle(klass)
                    }
                    found = true
                }
            }

            if (!found) {
                throw throw NoInjectableAnnotationException("No Annotation of Injectable for Bean ${klass.simpleName} registering")
            }
        }

        return this
    }

    /**
     * add a singleton bean define.
     */
    inline fun <reified T : Any> addSingleton(): Beans {
        this.beanFactory.addSingle(T::class)
        return this
    }

    /**
     * add a transient bean define.
     */
    inline fun <reified T : Any> addTransient(): Beans {
        this.beanFactory.addTransient(T::class)
        return this
    }

    inline fun <reified  T:Any> register(): Beans{
        registerBeans(T::class);
        return this;
    }
}