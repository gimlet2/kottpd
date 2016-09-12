package org.kottpd

import java.io.PrintWriter

data class HttpResponse(var status: Status = Status.OK,
                        val stream: PrintWriter) {
    fun send(content: String, status: Status = this.status, headers: Map<String, String> = emptyMap()) {
        stream.println("HTTP/1.1 ${status.code} ${status.value}")
        if (headers.isNotEmpty()) {
            headers.forEach { stream.println("${it.key}: ${it.value}") }
            stream.println()
        }
        stream.println()
        stream.print(content)
    }

}