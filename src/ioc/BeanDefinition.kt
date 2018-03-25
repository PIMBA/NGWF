package ioc

import exception.UnregisteredBeanException
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

public class BeanDefinition(defines: ConcurrentHashMap<KClass<out Any>, BeanDefinition>, beanClass: KClass<out Any>, val beanType: BeanType) {
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