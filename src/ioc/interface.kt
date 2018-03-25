package ioc

import kotlin.reflect.KClass

interface IBeanCreator {
    fun addSingle(index: KClass<out Any>): IBeanCreator?
    fun addTransient(index: KClass<out Any>): IBeanCreator?
}

interface IBeanFactory : IBeanCreator {
    operator fun get(index: KClass<out Any>): Any?
    operator fun contains(index: KClass<out Any>): Boolean
    fun lifeOf(index: KClass<out Any>): BeanType?
}