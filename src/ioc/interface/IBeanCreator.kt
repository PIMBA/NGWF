package ioc.`interface`

import kotlin.reflect.KClass

interface IBeanCreator {
    fun addSingle(index: KClass<out Any>): IBeanCreator?
    fun addTransient(index: KClass<out Any>): IBeanCreator?
}