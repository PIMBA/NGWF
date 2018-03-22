package ioc

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

enum class BeanType {
    Single,
    Transient
}

private class BeanDefinition(beanClass: KClass<out Any>, val beanType:BeanType) {
    val dependencies: MutableList<KClass<out Any>> = mutableListOf()

    init {
        if(beanClass.primaryConstructor != null)
            beanClass.primaryConstructor!!.parameters.forEach { p ->
                val type = p.type.classifier as KClass<*>
                if (type !in dependencies) dependencies.add(type)
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
    private val singleBeans: ConcurrentHashMap<KClass<out Any>,Any> =  ConcurrentHashMap()
    private val defines: ConcurrentHashMap<KClass<out Any>,BeanDefinition> =  ConcurrentHashMap()


    override fun addTransient(index: KClass<out Any>): DefaultBeanFactory {
        if(index in defines.keys) return this
        defines[index] = BeanDefinition(index,BeanType.Transient)
        return this
    }

    override fun addSingle(index: KClass<out Any>): DefaultBeanFactory {
        if(index in defines.keys) return this
        defines[index] = BeanDefinition(index,BeanType.Single)
        return this
    }

    private fun <T : Any> createBean(c: KClass<T>,tempBeanMap:MutableMap<KClass<out Any>,Any> = mutableMapOf<KClass<out Any>,Any>()):T?{
        if(c !in defines.keys) return null
        if(c in singleBeans.keys)
            return singleBeans[c]!! as T;
        // c.createInstance();
        val cons = c.primaryConstructor ?: return c.createInstance()
        val ps = cons.parameters
        val cd = defines[c]
        val de = cd!!.dependencies
        de.forEach {
            x-> if(x !in tempBeanMap){
                val tmp = createBean(x,tempBeanMap)!!
                tempBeanMap[x] = tmp
        }
        }
        val pms = mutableMapOf<KParameter,Any>()
        ps.forEach { x ->
            pms[x] = tempBeanMap[x.type.classifier as KClass<out Any>]!!
        }
        val bean = cons.callBy(pms)
        if(defines[c]!!.beanType == BeanType.Single) singleBeans[c] = bean
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
    operator fun get(index: KClass<out Any>): Any? = beanFactory[index]

    /**
     * if beans contains a bean definition.
     */
    operator fun contains(index: KClass<out Any>): Boolean = index in beanFactory

    /**
     * get bean life enum by bean class
     * if bean is not defined in beans return a null.
     */
    fun lifeOf(index: KClass<out Any>): BeanType? = beanFactory.lifeOf(index)

    var beanFactory : IBeanFactory = DefaultBeanFactory()

    /**
     * add a singleton bean define.
     */
    inline fun <reified T: Any> addSingleton():Beans{
        this.beanFactory.addSingle(T::class)
        return this
    }

    /**
     * add a transient bean define.
     */
    inline fun <reified T: Any> addTransient():Beans {
        this.beanFactory.addTransient(T::class)
        return this
    }
}