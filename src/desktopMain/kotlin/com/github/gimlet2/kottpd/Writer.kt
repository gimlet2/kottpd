package com.github.gimlet2.kottpd

import java.io.PrintWriter
import java.io.Writer

actual class Writer(writer: Writer) {

    private val printWriter = PrintWriter(writer)
    actual fun println(str: String) {
        printWriter.println(str)
    }

    actual fun print(str: String) {
        printWriter.print(str)
    }

    actual fun flush() {
        printWriter.flush()
    }

}