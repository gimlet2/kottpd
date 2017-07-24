package org.kottpd

class ClientThread(val socket: Socket, val match: (HttpRequest) -> (HttpRequest, HttpResponse) -> Any) {

    fun run() {
        val input = socket.reader()
        val out = socket.writer()
        val request = readRequest(input, {
            LinkedHashMap<String, String>().apply {
                input.lines().takeWhile(String::isNotBlank).forEach { line ->
                    line.split(":").let {
                        put(it[0], it[1].trim())
                    }
                }
            }
        })
        val httpResponse = HttpResponse(stream = out)
        val result = match(request).invoke(request, httpResponse)
        if (result !is Unit) {
            httpResponse.send(result.toString())
        }
        httpResponse.flush()
        socket.close()
    }

    fun readRequest(reader: Reader, eval: () -> Map<String, String>): HttpRequest {
        reader.line().split(' ').let {
            return HttpRequest(HttpMethod.valueOf(it[0]), it[1], it[2], eval(), reader)
        }
    }
}

