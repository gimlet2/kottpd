package org.kottpd

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

data class HttpResponse(var status: Status = Status.OK,
                        val stream: OutputStream) {
    fun send(content: String, status: Status = this.status, headers: Map<String, String> = emptyMap()) {
        val writer = PrintWriter(OutputStreamWriter(stream))
        writer.println("HTTP/1.1 ${status.code} ${status.value}")
        if (headers.isNotEmpty()) {
            headers.forEach { writer.println("${it.key}: ${it.value}") }
            writer.println()
        }
        writer.println()
        writer.print(content)
    }

}