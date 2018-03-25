package async

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

typealias MessageCallBack<M> = (M) -> Any;
typealias CallbackQueue<P> = ConcurrentLinkedQueue<MessageCallBack<P>>;

public class Messenger {

    private val observedMap = ConcurrentHashMap<Any, CallbackQueue<Any>>()

    fun <T: Any, M: Any>register(token: T, callBack: MessageCallBack<M>) {
        val callbackQueue = this.observedMap[token];

        if (callbackQueue === null) {
            val newArr = CallbackQueue<Any>();
            newArr.add(callBack as MessageCallBack<Any>?);
            this.observedMap[token] = newArr;
        } else {
            callbackQueue.add(callBack as MessageCallBack<Any>?);
        }
    }

    fun <T: Any, M: Any>send(token: T, message: M) {
        val callbackQueue = this.observedMap[token];
        callbackQueue?.forEach {
            it.invoke(message);
        }
    }

    companion object {
        val Default = Messenger()
    }
}
