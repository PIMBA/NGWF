package ioc

import async.Messenger
import common.sameas
import ioc.annotation.Injectable

@Injectable(BeanType.Transient)
private class Test (val type1:Type1,val type2:Type2);

@Injectable(BeanType.Single)
private class Type1 (val type3:Type3,val type4:Type4);

@Injectable(BeanType.Single)
private class Type2 (val type3:Type3);

@Injectable(BeanType.Single)
private class Type3 (val type4:Type4);

@Injectable(BeanType.Single)
private class Type4 ();

class TestIOC{
    @org.junit.Test
    fun test (){
        build();
        val b = Test::class in Beans;
        val life = Beans.lifeOf(Test::class);
        val test = Beans.get<Type1>()
        val test1 = Beans.get<Type1>()

        test sameas test1;

        Messenger.Default.send("Token", "registered message");
    }

    fun build(){
        Beans.registerBeans(Test::class, Type1::class, Type2::class, Type3::class, Type4::class)

        Messenger.Default.register("Token", {
            msg: String -> print(msg);
        } );
    }
}