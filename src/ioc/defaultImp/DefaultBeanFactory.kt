package ioc.defaultImp

import ioc.BeanDefinition
import ioc.BeanType
import ioc.`interface`.IBeanFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

/**
 * default bean factory
 * if beans factory is not set,
 * this class will injected into beans object.
 * cycle bean reference is not supported in default bean factory.
 */
class DefaultBeanFactory : IBeanFactory {
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
            return singleBeans[c]!! as T
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
