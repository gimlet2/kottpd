package com.github.gimlet2.kottpd

/**
 * Created by Andrei Chernyshev on 1/14/22.
 */
data class HttpResponse(
    var status: Status = Status.OK,
    val stream: Writer
) {

    private var dirty = false

    fun send(content: String, status: Status = this.status, headers: Map<String, String> = emptyMap()) {
        if (!dirty) {
            stream.println("HTTP/1.1 ${status.code} ${status.value}")
            if (headers.isNotEmpty()) {
                headers.forEach { stream.println("${it.key}: ${it.value}") }
                stream.println()
            }
            stream.println()
            dirty = true
        }
        stream.print(content)
    }

    fun flush() {
        stream.flush()
    }
}