package com.github.gimlet2.kottpd

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class ClientThread(val socket: Socket, val match: (HttpRequest) -> (HttpRequest, HttpResponse) -> Any) : Runnable {

    override fun run() {
        val input = BufferedReader(InputStreamReader(socket.inputStream))
        val out = socket.outputStream
        val request = readRequest(input) {
            buildMap {
                input.lineSequence().takeWhile(String::isNotBlank).forEach { line ->
                    val colonIndex = line.indexOf(':')
                    if (colonIndex > 0) {
                        put(line.substring(0, colonIndex), line.substring(colonIndex + 1).trim())
                    }
                }
            }
        }
        val httpResponse = HttpResponse(stream = out)
        val result = match(request)(request, httpResponse)
        if (result !is Unit) {
            httpResponse.send(result.toString())
        }
        httpResponse.flush()
        socket.close()
    }

    fun readRequest(reader: BufferedReader, eval: () -> Map<String, String>): HttpRequest {
        val parts = reader.readLine().split(' ')
        return HttpRequest(HttpMethod.valueOf(parts[0]), parts[1], parts[2], eval(), reader)
    }
}

