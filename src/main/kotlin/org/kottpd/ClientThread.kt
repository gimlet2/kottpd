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
        val split = reader.readLine().split(' ')
        return HttpRequest(HttpMethod.valueOf(split[0]), split[1], split[2], eval(), reader)
    }
}

