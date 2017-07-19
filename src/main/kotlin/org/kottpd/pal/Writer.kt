package org.kottpd.pal

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

/**
 * Created by Andrei Chernyshev on 7/11/17.
 */
interface Writer {
    fun println(string: String = "")
    fun print(content: String)
    fun flush()
}

fun writer(source: Any): Writer {
    return object : Writer {
        private val writer = PrintWriter(OutputStreamWriter(source as OutputStream))
        override fun println(string: String) {
            if (string.isEmpty()) {
                writer.println()
            } else {
                writer.println(string)
            }
        }

        override fun print(content: String) {
            writer.print(content)
        }

        override fun flush() {
            writer.flush()
        }

    }
}