package org.kottpd.pal

import java.io.IOException
import java.net.ServerSocket

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
}

fun serverSocker(port: Int): org.kottpd.pal.ServerSocket {
    return object : org.kottpd.pal.ServerSocket {
        val socket = ServerSocket(port)
        override fun accept(): Socket {
            try {

                return org.kottpd.pal.socket(socket.accept())
            } catch (e: IOException) {
                throw org.kottpd.pal.IOException()
            }
        }
    }
}

fun socket(s: Any): Socket {
    return object : Socket {
        val socket = s as java.net.Socket
        override fun close() {
            socket.close()
        }

        override fun reader(): Reader {
            return org.kottpd.pal.reader(socket.getInputStream())
        }

        override fun writer(): Writer {
            return org.kottpd.pal.writer(socket.getOutputStream())
        }
    }
}