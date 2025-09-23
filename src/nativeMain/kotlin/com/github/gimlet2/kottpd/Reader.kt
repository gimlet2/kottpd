package com.github.gimlet2.kottpd

import kotlinx.cinterop.*
import platform.posix.read

/**
 * Created by Andrei Chernyshev on 1/7/22.
 */
actual class Reader(private val source: Int) {

    actual fun lines(): Sequence<String> {
        return LinesSequence(this).constrainOnce()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun read(): Int {
        memScoped {
            val bufferLength = 1uL
            val buffer = allocArray<ByteVar>(bufferLength.toInt())
            read(source, buffer, bufferLength)
            val kString: String = buffer.toKString()
            if (kString.isEmpty()) return 0
            return kString[0].code
        }
    }

    actual fun line(): String {
        var result = ""
        var s = read()
        while (s != 13) {
            result += s.toChar()
            s = read()
        }
        read()
        return result
    }
}

private class LinesSequence(private val reader: Reader) : Sequence<String> {
    override fun iterator(): Iterator<String> {
        return object : Iterator<String> {
            private var nextValue: String? = null
            private var done = false

            override fun hasNext(): Boolean {
                if (nextValue == null && !done) {
                    nextValue = reader.line()
                    if (nextValue == null) done = true
                }
                return nextValue != null
            }

            override fun next(): String {
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