package pipeline

import ApplicationBuilder
import ioc.BeanType
import ioc.Beans
import ioc.annotation.Injectable
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import server.http.HttpContext
import server.pipeline.RequestDelegate

open class MiddleBase(private val next: RequestDelegate){
    fun excute(context: HttpContext, bean1: BeanClass1 ,bean2: BeanClass2){
        println(this::class.simpleName);
        next(context);
        println(this::class.simpleName + "b");
        println(bean1.toString() + " "+ bean2.toString());
    }
}

open class BeanBase(){
    init {
        println(this::class.simpleName + "was built");
    }
}

@Injectable(BeanType.Transient)
class BeanClass1: BeanBase();

@Injectable(BeanType.Single)
class BeanClass2: BeanBase();

class PiplineTest {
    var delegate : RequestDelegate? = null;

    @Before
    fun before(){
        Beans.register<BeanClass1>()
                .register<BeanClass2>();

        class Middle1(next: RequestDelegate, private val bean1:BeanClass1) : MiddleBase(next);
        class Middle2(next: RequestDelegate, private val bean2:BeanClass2) : MiddleBase(next);
        class Middle3(next: RequestDelegate, private val bean1:BeanClass1) : MiddleBase(next);
        class Middle4(next: RequestDelegate, private val bean2:BeanClass2) : MiddleBase(next);

        delegate = ApplicationBuilder.useMiddleware<Middle1>()
                .useMiddleware<Middle2>()
                .useMiddleware<Middle3>()
                .useMiddleware<Middle4>()
                .build();
        println("application was built");
    }

    @Test
    fun withIOC(){
        for(i in Array<Int>(1000,{1})) {
            delegate!!(HttpContext()); // in 1.5ms
        }
    }
}