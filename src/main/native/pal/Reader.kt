package org.kottpd.pal

import kotlinx.cinterop.*
import sockets.*

//import java.io.BufferedReader
//import java.io.InputStream
//import java.io.InputStreamReader

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
        //        private val reader = BufferedReader(InputStreamReader(source as InputStream))
        private val reader: Int = source as Int

        override fun read(): Int {
            memScoped {
                val bufferLength = 1L
                val buffer = allocArray<ByteVar>(bufferLength)
                read(reader, buffer, bufferLength)
                val kString: String = buffer.toKString()
                if (kString.isEmpty()) return 0
                return kString[0].toInt()
            }
//            return reader.read()
        }

        override fun line(): String {
            var result: String = ""
            var s = read()
            while (s != 13) {
                result += s.toChar()
                s = read()
            }
            read()
            println(result)
            return result
//            return reader.readLine()
        }

        override fun lines(): Sequence<String> {

            return LinesSequence(this).constrainOnce()
//            return reader.lineSequence()
        }
    }
}

private class LinesSequence(private val reader: Reader) : Sequence<String> {
    override public fun iterator(): Iterator<String> {
        return object : Iterator<String> {
            private var nextValue: String? = null
            private var done = false

            override public fun hasNext(): Boolean {
                if (nextValue == null && !done) {
                    nextValue = reader.line()
                    if (nextValue == null) done = true
                }
                return nextValue != null
            }

            override public fun next(): String {
                if (!hasNext()) {
                    throw Error()
                }
                val answer = nextValue
                nextValue = null
                return answer!!
            }
        }
    }
}