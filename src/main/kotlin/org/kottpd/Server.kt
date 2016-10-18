package org.kottpd

import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.security.KeyStore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


fun main(args: Array<String>) {
    val server = Server()
    server.staticFiles("/public")
    server.get("/hello", { req, res -> res.send("Hello") })
    server.get("/do/.*/smth", { req, res -> res.send("Hello world") })
    server.post("/data", { req, res -> res.send(req.content, Status.Created) })
    server.start()
//    server.start(9443, true, "./keystore.jks", "password")
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

    fun start(port: Int = this.port, secure: Boolean = false, keyStoreFile: String = "", password: String = "") {
        threadPool.submit {
            println("Server start on port $port")
            val socket = if (secure) secureSocket(port, keyStoreFile, password) else ServerSocket(port)
            while (true) {
                threadPool.submit(ClientThread(socket.accept(), { matchRequest(it) }))
            }
        }
    }

    private fun secureSocket(port: Int, keyStoreFile: String, password: String): ServerSocket {

        /* Create keystore */
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(FileInputStream(keyStoreFile), password.toCharArray())

        /* Get factory for the given keystore */
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply { init(keyStore) }
        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply { init(keyStore, password.toCharArray()) }
        return SSLContext.getInstance("SSL").apply { init(kmf.keyManagers, null, null) }
                .serverSocketFactory.createServerSocket(port)

    }

    private fun matchRequest(request: HttpRequest): (HttpRequest, HttpResponse) -> Unit {
        return bindings[request.method]!!.let { routes ->
            routes.getOrElse(request.url, {
                routes.filter {
                    it.key.toRegex().matches(request.url)
                }.values.firstOrElse {
                    { req: HttpRequest, res: HttpResponse -> res.send("Resource not found", Status.NotFound) }
                }
            }
            )
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

    fun staticFiles(path: String) {
        val fullPath = javaClass.getResource(path).file
        File(fullPath)
                .walkTopDown()
                .forEach {
                    if (!it.isDirectory) {
                        val file = it.path.substring(fullPath.length)
                        get(file, { req, res -> it.inputStream().copyTo(res.stream) })
                        if (file == "/index.html" || file == "/index.htm") {
                            get("/", { req, res -> it.inputStream().copyTo(res.stream) })
                        }
                    }
                }
    }
}

fun <T> Iterable<T>.firstOrElse(eval: () -> T): T {
    return this.firstOrNull() ?: return eval.invoke()
}

