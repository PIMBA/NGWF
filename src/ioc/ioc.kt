package ioc

import exception.NoInjectableAnnotationException
import exception.UnregisteredBeanException
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

enum class BeanType {
    Single,
    Transient
}

private class BeanDefinition(defines: ConcurrentHashMap<KClass<out Any>, BeanDefinition>, beanClass: KClass<out Any>, val beanType: BeanType) {
    val dependencies: MutableList<KClass<out Any>> = mutableListOf()

    init {
        beanClass.primaryConstructor?.parameters?.forEach { p ->
            val type = p.type.classifier as KClass<*>
            if (type !in dependencies) {
                dependencies.add(type)
            } else {
                throw UnregisteredBeanException("Unregistered Bean ${type.simpleName} has been injected");
            }
        }
    }
}

/**
 * default bean factory
 * if beans factory is not set,
 * this class will injected into beans object.
 * cycle bean reference is not supported in default bean factory.
 */
private class DefaultBeanFactory : IBeanFactory {
    private val singleBeans: ConcurrentHashMap<KClass<out Any>, Any> = ConcurrentHashMap()
    private val defines: ConcurrentHashMap<KClass<out Any>, BeanDefinition> = ConcurrentHashMap()

//    private val classPool: Set<KClass<out Any>> =
//            Collections.newSetFromMap(ConcurrentHashMap<KClass<out Any>, Boolean>())

    override fun addTransient(index: KClass<out Any>): DefaultBeanFactory {
        if (index in defines.keys) return this
        defines[index] = BeanDefinition(this.defines, index, BeanType.Transient)
        return this
    }

    override fun addSingle(index: KClass<out Any>): DefaultBeanFactory {
        if (index in defines.keys) return this
        defines[index] = BeanDefinition(this.defines, index, BeanType.Single)
        return this
    }

    private fun <T : Any> createBean(c: KClass<T>, tempBeanMap: MutableMap<KClass<out Any>, Any> = mutableMapOf<KClass<out Any>, Any>()): T? {
        if (c !in defines.keys) return null
        if (c in singleBeans.keys)
            return singleBeans[c]!! as T;
        // c.createInstance();
        val cons = c.primaryConstructor ?: return c.createInstance()
        val ps = cons.parameters
        val cd = defines[c]
        val de = cd!!.dependencies
        de.forEach { x ->
            if (x !in tempBeanMap) {
                val tmp = createBean(x, tempBeanMap)!!
                tempBeanMap[x] = tmp
            }
        }
        val pms = mutableMapOf<KParameter, Any>()
        ps.forEach { x ->
            pms[x] = tempBeanMap[x.type.classifier as KClass<out Any>]!!
        }
        val bean = cons.callBy(pms)
        if (defines[c]!!.beanType == BeanType.Single) singleBeans[c] = bean
        return bean
    }

    override fun get(index: KClass<out Any>): Any? = createBean(index)

    override fun contains(index: KClass<out Any>): Boolean = index in defines.keys

    override fun lifeOf(index: KClass<out Any>): BeanType? = defines[index]?.beanType
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
            var found = false;
            klass.annotations.forEach {
                if (it is Injectable) {
                    var injectable = it as Injectable;
                    when (injectable.type) {
                        BeanType.Transient -> this.beanFactory.addTransient(klass)
                        BeanType.Single -> this.beanFactory.addSingle(klass)
                    }
                    found = true;
                }
            }

            if (!found) {
                throw throw NoInjectableAnnotationException("No Annotation of Injectable for Bean ${klass.simpleName} registering");
            }
        }

        return this;
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
}