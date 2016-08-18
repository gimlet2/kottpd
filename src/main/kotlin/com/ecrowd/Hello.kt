package com.ecrowd

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

val threadPool = Executors.newCachedThreadPool()

fun main(args: Array<String>) {
    println("Hello, World")
    val socket = ServerSocket(9000)
    while (true) {
        threadPool.submit(ClientThread(socket.accept()))
    }

}

class ClientThread(val socket: Socket) : Runnable {

    override fun run() {
        val input = BufferedReader(InputStreamReader(socket.inputStream))
        val out = PrintWriter(OutputStreamWriter(socket.outputStream))
        val request = readRequest(input, {
            val headers: MutableMap<String, String> = mutableMapOf()
            var line = input.readLine()
            while (!line.isBlank()) {
                val split = line.split(":")
                headers.put(split[0], split[1].trim())
                line = input.readLine()
            }
            headers
        })
        doSmth(request, HttpResponse(stream = out))
        out.flush()
        socket.close()
    }

    fun readRequest(reader: BufferedReader, eval: () -> Map<String, String>): HttpRequest {
        val split = reader.readLine().split(' ')
        return HttpRequest(HttpMethod.valueOf(split[0]), split[1], split[2], eval(), reader)
    }
}

fun doSmth(request: HttpRequest, response: HttpResponse) {
    println(request.content())
    response.send(200, request.content())
}

data class HttpRequest(val method: HttpMethod,
                       val url: String,
                       val httpVersion: String,
                       val headers: Map<String, String>,
                       val stream: BufferedReader) {
    var content: String? = null
    fun content(): String {
        if (content != null) {
            return content as String
        }
        content = ""
        if (headers.containsKey("Content-Length")) {
            val contentLength: Int = if (headers["Content-Length"] != null) headers["Content-Length"]!!.toInt() else 0
            for (i: Int in (0..contentLength - 1)) {
                content += stream.read().toChar()
            }
        }
        return content as String
    }
}

data class HttpResponse(var status: Int = 200,
                        val stream: PrintWriter) {
    fun send(status: Int, content: String, headers: Map<String, String> = emptyMap()) {
        stream.println("HTTP1/1 $status OK")
        if (headers.isNotEmpty()) {
            headers.forEach { println("${it.key}: ${it.value}") }
            stream.println()
        }
        stream.println(content)
    }
}

enum class HttpMethod {
    GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE, CONNECT
}

