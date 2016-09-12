package org.kottpd

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.util.*

class ClientThread(val socket: Socket, val match: (HttpRequest) -> (HttpRequest, HttpResponse) -> Unit) : Runnable {

    override fun run() {
        val input = BufferedReader(InputStreamReader(socket.inputStream))
        val out = PrintWriter(OutputStreamWriter(socket.outputStream))
        val request = readRequest(input, {
            LinkedHashMap<String, String>().apply {
                input.lineSequence().takeWhile { a -> a.isNotBlank() }.forEach { line ->
                    line.split(":").let {
                        put(it[0], it[1].trim())
                    }
                }
            }
        })
        match(request).invoke(request, HttpResponse(stream = out))
        out.flush()
        socket.close()
    }

    fun readRequest(reader: BufferedReader, eval: () -> Map<String, String>): HttpRequest {
        reader.readLine().split(' ').let {
            return HttpRequest(HttpMethod.valueOf(it[0]), it[1], it[2], eval(), reader)
        }
    }
}

