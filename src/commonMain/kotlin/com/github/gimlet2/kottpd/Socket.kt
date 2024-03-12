package com.github.gimlet2.kottpd

/**
 * Created by Andrei Chernyshev on 1/14/22.
 */
class Socket(val reader: Reader, val writer: Writer, val closeAction: () -> Unit) {
    fun close() {
        closeAction()
    }
}