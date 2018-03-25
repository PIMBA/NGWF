package starter

import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket

class Resources : AutoCloseable {
    val resources = mutableListOf<AutoCloseable>()

    fun <T : AutoCloseable> T.use(): T {
        resources += this
        return this
    }

    override fun close() {
        var exception: Exception? = null
        for (resource in resources.reversed()) {
            try {
                resource.close()
            } catch (closeException: Exception) {
                if (exception == null) {
                    exception = closeException
                } else {
                    exception.addSuppressed(closeException)
                }
            }
        }
        if (exception != null) throw exception
    }
}

inline fun <T> using(block: Resources.() -> T): T = Resources().use(block)

class Request(var inputStream: InputStream, var outStream: OutputStream) {}

fun start(agrs: Array<String>) {
    val serverSocket = ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
    using {
        val accept = serverSocket.accept().use();
        val istream = accept.getInputStream().use();
        val ostream = accept.getOutputStream().use();

        val request = Request(istream, ostream);

    }
}