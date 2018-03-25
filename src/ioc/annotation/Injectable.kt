package ioc.annotation

import ioc.BeanType

@Target(AnnotationTarget.CLASS)
annotation class Injectable(val type: BeanType)
