package common

import kotlin.test.assertEquals

fun <T> T.println() = println(this);

infix fun Any?.expect(ex: Any?) = assertEquals(ex,this);
infix fun Any?.sameas(ex: Any?) = assertEquals(ex,this);