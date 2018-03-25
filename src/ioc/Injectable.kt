package ioc

@Target(AnnotationTarget.CLASS)
annotation class Injectable(val type: BeanType)
