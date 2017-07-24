package org.kottpd.pal

//import java.io.IOException
//import java.net.ServerSocket
import kotlinx.cinterop.*
import sockets.*

//import org.kottpd.pal.native.*

/**
 * Created by Andrei Chernyshev on 7/11/17.
 */
interface Socket {
    fun close()


    fun reader(): Reader
    fun writer(): Writer
}

interface ServerSocket {
    fun accept(): Socket
    fun close()
}

fun serverSocket(port: Int): org.kottpd.pal.ServerSocket {

    return object : org.kottpd.pal.ServerSocket {
        var listenFd: Int = 0

        init {
            memScoped {
                val serverAddr = alloc<sockaddr_in>()

                listenFd = socket(AF_INET, SOCK_STREAM, 0)
//                        .ensureUnixCallResult { it >= 0 }

                with(serverAddr) {
                    memset(this.ptr, 0, sockaddr_in.size)
                    sin_family = AF_INET.narrow()
                    sin_addr.s_addr = htons(0).toInt()
                    sin_port = htons(port.toShort())
                }

                bind(listenFd, serverAddr.ptr.reinterpret(), sockaddr_in.size.toInt())
//                        .ensureUnixCallResult { it == 0 }

                listen(listenFd, 100)
//                        .ensureUnixCallResult { it == 0 }

            }
        }

        //        val socket = ServerSocket(port)
        override fun accept(): Socket {
            try {

                return object : Socket {
                    val ac: Int = accept(listenFd, null, null)
//                            .ensureUnixCallResult { it >= 0 }

                    override fun close() {
                        close(ac)
                    }

                    override fun reader(): Reader {
                        return org.kottpd.pal.reader(ac)
                    }

                    override fun writer(): Writer {
                        return org.kottpd.pal.writer(ac)
                    }
                }
            } catch (e: Error) {
                throw org.kottpd.pal.IOException()
            }
        }

        override fun close() {
            close(listenFd)
        }
    }
}


val errno: Int
    get() = interop_errno()

fun htons(value: Short) = interop_htons(value.toInt()).toShort()
//
//inline fun Int.ensureUnixCallResult(predicate: (Int) -> Boolean): Int {
//    if (!predicate(this)) {
//        throw Error(strerror(errno)!!.toKString())
//    }
//    return this
//}
//
//inline fun Long.ensureUnixCallResult(predicate: (Long) -> Boolean): Long {
//    if (!predicate(this)) {
//        throw Error(strerror(errno)!!.toKString())
//    }
//    return this
//}