package com.github.gimlet2.kottpd

import java.io.BufferedReader

data class HttpRequest(val method: HttpMethod,
                       val url: String,
                       val httpVersion: String,
                       val headers: Map<String, String>,
                       val stream: BufferedReader) {
    val content: String by lazy {
        val length = headers.getOrElse("Content-Length") { "0" }.toInt()
        buildString(length) {
            repeat(length) { append(stream.read().toChar()) }
        }
    }
}