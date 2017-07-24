package org.kottpd.pal

import kotlinx.cinterop.*
import sockets.*

//
//import java.io.OutputStream
//import java.io.OutputStreamWriter
//import java.io.PrintWriter

/**
 * Created by Andrei Chernyshev on 7/11/17.
 */
interface Writer {
    fun println(string: String = "")
    fun print(content: String)
    fun flush()
}

fun writer(source: Any): Writer {
    val writer: Int = source as Int
    return object : Writer {
        //        private val writer = PrintWriter(OutputStreamWriter(source as OutputStream))
        override fun println(string: String) {
            if (string.isEmpty()) {
//                writer.println()

                print("\n")

            } else {
                print(string + "\n")
            }
        }

        override fun print(content: String) {
//            writer.print(content)

            memScoped {
                //                val bufferLength = content.length
//                val buffer = allocArray<ByteVar>(bufferLength)
//                for (i in content.indices) {
//                    buffer[i] = content.get(i).toByte() as ByteVar
//                }
                write(writer, content.cstr.getPointer(this), content.length.toLong())
            }
        }

        override fun flush() {
            fsync(writer)
//            writer.flush()
        }

    }
}