package main

import async.Messenger
import ioc.BeanType
import ioc.Beans
import ioc.Injectable
import javax.swing.MenuSelectionManager

@Injectable(BeanType.Transient)
class Test (val type1:Type1,val type2:Type2);

@Injectable(BeanType.Single)
class Type1 (val type3:Type3,val type4:Type4);

@Injectable(BeanType.Single)
class Type2 (val type3:Type3);

@Injectable(BeanType.Single)
class Type3 (val type4:Type4);

@Injectable(BeanType.Single)
class Type4 ();

class Type5 ();

fun main (agrs: Array<String>) {
    build();
    val b = Test::class in Beans;
    val life = Beans.lifeOf(Test::class);
    val test = Beans.get<Type1>()
    val test1 = Beans.get<Type1>()

    if(test === test1) {
        print("ok\n")
    };

    Messenger.Default.send("Token", "registered message");
}

fun build(){
//    Beans
//        // .addSingle<Test>()
//        .addTransient<Test>()
//        .addSingleton<Type1>()
//        .addSingleton<Type2>()
//        .addSingleton<Type3>()
//        .addSingleton<Type4>();

    Beans.registerBeans(Test::class, Type1::class, Type2::class, Type3::class, Type4::class)

    Messenger.Default.register("Token", {
        msg: String -> print(msg);
    } );
}