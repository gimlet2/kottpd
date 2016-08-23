package org.kottpd

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


fun main(args: Array<String>) {
    val server = Server()
    server.get("/hello", { req, res -> res.send("Hello") })
    server.get("/do/.*/smth", { req, res -> res.send("Hello world") })
    server.post("/data", { req, res -> res.send(req.content, Status.Created) })
    server.start()
}

class Server(val port: Int = (System.getProperty("server.port") ?: "9000").toInt()) {

    val threadPool: ExecutorService = Executors.newCachedThreadPool()
    val bindings: Map<HttpMethod, MutableMap<String, (HttpRequest, HttpResponse) -> Unit>> = mapOf(
            Pair(HttpMethod.GET, mutableMapOf()),
            Pair(HttpMethod.POST, mutableMapOf()),
            Pair(HttpMethod.PUT, mutableMapOf()),
            Pair(HttpMethod.DELETE, mutableMapOf()),
            Pair(HttpMethod.OPTIONS, mutableMapOf()),
            Pair(HttpMethod.CONNECT, mutableMapOf()),
            Pair(HttpMethod.HEAD, mutableMapOf()),
            Pair(HttpMethod.TRACE, mutableMapOf())
    )

    fun start() {
        println("Server start on port $port")
        val socket = ServerSocket(port)
        while (true) {
            threadPool.submit(ClientThread(socket.accept(), { request ->
                bindings[request.method]!!.let { routes ->
                    routes.getOrElse(request.url, {
                        routes.filter {
                            it.key.toRegex().matches(request.url)
                        }.values.firstOr {
                            { req: HttpRequest, res: HttpResponse -> res.send("Resource not found", Status.NotFound) }
                        }
                    }
                    )
                }
            }))
        }
    }

    fun bind(method: HttpMethod, path: String, call: (request: HttpRequest, response: HttpResponse) -> Unit) {
        bindings[method]?.put(path, call)
    }

    fun get(path: String, call: (request: HttpRequest, response: HttpResponse) -> Unit) {
        bind(HttpMethod.GET, path, call)
    }

    fun post(path: String, call: (request: HttpRequest, response: HttpResponse) -> Unit) {
        bind(HttpMethod.POST, path, call)
    }

    fun put(path: String, call: (request: HttpRequest, response: HttpResponse) -> Unit) {
        bind(HttpMethod.PUT, path, call)
    }

    fun delete(path: String, call: (request: HttpRequest, response: HttpResponse) -> Unit) {
        bind(HttpMethod.DELETE, path, call)
    }
}

class ClientThread(val socket: Socket, val match: (HttpRequest) -> (HttpRequest, HttpResponse) -> Unit) : Runnable {

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
        match(request).invoke(request, HttpResponse(stream = out))
        out.flush()
        socket.close()
    }

    fun readRequest(reader: BufferedReader, eval: () -> Map<String, String>): HttpRequest {
        val split = reader.readLine().split(' ')
        return HttpRequest(HttpMethod.valueOf(split[0]), split[1], split[2], eval(), reader)
    }
}

data class HttpRequest(val method: HttpMethod,
                       val url: String,
                       val httpVersion: String,
                       val headers: Map<String, String>,
                       val stream: BufferedReader) {
    val content: String by lazy {
        (1..headers.getOrElse("Content-Length", { "0" }).toInt()).fold("", { a, b -> a + stream.read().toChar() })
    }
}

data class HttpResponse(var status: Status = Status.OK,
                        val stream: PrintWriter) {
    fun send(content: String, status: Status = this.status, headers: Map<String, String> = emptyMap()) {
        stream.println("HTTP1/1 ${status.code} ${status.value}")
        if (headers.isNotEmpty()) {
            headers.forEach { stream.println("${it.key}: ${it.value}") }
            stream.println()
        }
        stream.println(content)
    }

}

enum class HttpMethod {
    GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE, CONNECT
}

enum class Status(val code: Int, val value: String) {
    // 100
    Continue(100, "Continue"),
    SwitchingProtocols(101, "Switching Protocols"), Processing(102, "Processing"),
    // 200
    OK(200, "OK"),
    Created(201, "Created"), Accepted(202, "Accepted"), NonAuthoritativeInformation(203, "Non-Authoritative Information"),
    NoContent(204, "No Content"), ResetContent(205, "Reset Content"), PartialContent(206, "Partial Content"),
    MultiStatus(207, "Multi-Status"), AlreadyReported(208, "Already Reported"), IMUsed(226, "IM Used"),
    // 300
    MultipleChoices(300, "Multiple Choices"),
    MovedPermanently(301, "Moved Permanently"), Found(302, "Found"),
    SeeOther(303, "See Other"), NotModified(304, "Not Modified"), UseProxy(305, "Use Proxy"), SwitchProxy(306, "Switch Proxy"),
    TemporaryRedirect(307, "Temporary Redirect"), PermanentRedirect(308, "Permanent Redirect"),
    // 400
    BadRequest(400, "Bad Request"),
    Unauthorized(401, "Unauthorized"), PaymentRequired(402, "Payment Required"),
    Forbidden(403, "Forbidden"), NotFound(404, "Not Found"), MethodNotAllowed(405, "Method Not Allowed"),
    NotAcceptable(406, "Not Acceptable"), ProxyAuthenticationRequired(407, "Proxy Authentication Required"),
    RequestTimeout(408, "Request Timeout"), Conflict(409, "Conflict"), Gone(410, "Gone"), LengthRequired(411, "Length Required"),
    PreconditionFailed(412, "Precondition Failed"), PayloadTooLarge(413, "Payload Too Large"), URITooLong(414, "URI Too Long"),
    UnsupportedMediaType(415, "Unsupported Media Type"), RangeNotSatisfiable(416, "Range Not Satisfiable"),
    ExpectationFailed(417, "Expectation Failed"), ImATeapot(418, "I'm a teapot"), MisdirectedRequest(421, "Misdirected Request"),
    UnprocessableEntity(422, "Unprocessable Entity"), Locked(423, "Locked"), FailedDependency(424, "Failed Dependency"),
    UpgradeRequired(426, "Upgrade Required"), PreconditionRequired(428, "Precondition Required"),
    TooManyRequests(429, "Too Many Requests"), RequestHeaderFieldsTooLarge(431, "Request Header Fields Too Large"),
    UnavailableForLegalReasons(451, "Unavailable For Legal Reasons"),
    // 500
    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented"), BadGateway(502, "Bad Gateway"),
    ServiceUnavailable(503, "Service Unavailable"), GatewayTimeout(504, "Gateway Timeout"),
    HTTPVersionNotSupported(505, "HTTP Version Not Supported"), VariantAlsoNegotiates(506, "Variant Also Negotiates"),
    InsufficientStorage(507, "Insufficient Storage"), LoopDetected(508, "Loop Detected"), NotExtended(510, "Not Extended"),
    NetworkAuthenticationRequired(511, "Network Authentication Required")
}

fun <T> Iterable<T>.firstOr(eval: () -> T): T {
    return this.firstOrNull() ?: return eval.invoke()
}

