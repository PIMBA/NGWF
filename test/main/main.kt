package main

import ioc.Beans

class Test (val type1:Type1,val type2:Type2);

class Type1 (val type3:Type3,val type4:Type4);

class Type2 (val type3:Type3);

class Type3 (val type4:Type4);

class Type4 ();

fun main (agrs: Array<String>) {
    build();
    val b = Test::class in Beans;
    val life = Beans.lifeOf(Test::class);
    val test = Beans[Test::class] as Test;
    val test1 = Beans[Test::class] as Test;

    if(test === test1) print("ok");
}

fun build(){
    Beans
        // .addSingle<Test>()
        .addTransient<Test>()
        .addSingleton<Type1>()
        .addSingleton<Type2>()
        .addSingleton<Type3>()
        .addSingleton<Type4>();
}