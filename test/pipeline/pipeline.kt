package pipeline

import ApplicationBuilder

fun main(agrs:Array<String>){
    ApplicationBuilder.use {
        val next = it;
        {
            println("middleware 1");
            next(it);
            println("back into m 1");
        }
    }.use {
        val next = it;
        {
            println("middleware 2");
            next(it);
            println("back into m 2");
        }
    }.use {
        val next = it;
        {
            println("middleware 3");
            next(it);
            println("back into m 3");
        }
    }.build();
}