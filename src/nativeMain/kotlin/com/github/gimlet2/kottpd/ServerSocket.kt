package com.github.gimlet2.kottpd

import kotlinx.cinterop.*
import platform.posix.*

/**
 * Created by Andrei Chernyshev on 1/14/22.
 */
actual class ServerSocket actual constructor(port: Int, secure: Boolean, keyStoreFile: String, password: String) {

    @OptIn(ExperimentalForeignApi::class)
    val serverSocket = memScoped {
        val serverAddr = alloc<sockaddr_in>()

        val result = socket(AF_INET, SOCK_STREAM, 0)

        with(serverAddr) {
            memset(this.ptr, 0, sizeOf<sockaddr_in>().toULong())
            sin_family = AF_INET.toUShort()
            sin_addr.s_addr = htons(0u).toUInt()
            sin_port = htons(port.toUShort())
        }

        bind(result, serverAddr.ptr.reinterpret(), sizeOf<sockaddr_in>().toUInt())

        listen(result, 100)
        result
    }


    @OptIn(ExperimentalForeignApi::class)
    actual fun accept(): Socket {
        val accept: Int = accept(serverSocket, null, null)
        return Socket(Reader(accept), Writer(accept)) { close(accept) }
    }

    actual fun close() {
        close(serverSocket)
    }
}