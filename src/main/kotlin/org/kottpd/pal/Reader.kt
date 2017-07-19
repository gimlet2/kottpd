package org.kottpd.pal

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by Andrei Chernyshev on 7/11/17.
 */
interface Reader {

    fun lines(): Sequence<String>
    fun line(): String
    fun read(): Int
}

fun reader(source: Any): Reader {
    return object : Reader {
        private val reader = BufferedReader(InputStreamReader(source as InputStream))
        override fun read(): Int {
            return reader.read()
        }

        override fun line(): String {
            return reader.readLine()
        }

        override fun lines(): Sequence<String> {
            return reader.lineSequence()
        }
    }
}