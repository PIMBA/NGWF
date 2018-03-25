package ioc.`interface`

import ioc.BeanType
import kotlin.reflect.KClass

interface IBeanFactory : IBeanCreator {
    operator fun get(index: KClass<out Any>): Any?
    operator fun contains(index: KClass<out Any>): Boolean
    fun lifeOf(index: KClass<out Any>): BeanType?
}