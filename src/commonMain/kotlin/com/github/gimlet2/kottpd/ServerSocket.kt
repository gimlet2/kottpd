package com.github.gimlet2.kottpd

/**
 * Created by Andrei Chernyshev on 1/14/22.
 */
expect class ServerSocket(port: Int, secure: Boolean = false, keyStoreFile: String = "", password: String = "") {
    fun accept(): Socket
    fun close()
}