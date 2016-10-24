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
    Server().apply {
        staticFiles("/public")
        get("/hello", { req, res -> res.send("Hello") })
        get("/test", { req, res -> "Hello" })
        get("/do/.*/smth", { req, res -> res.send("Hello world") })
        post("/data", { req, res -> res.send(req.content, Status.Created) })
        before("/hello", { req, res -> res.send("before\n") })
        before({ req, res -> res.send("ALL before\n") })
        after("/hello", { req, res -> res.send("\nafter\n") })
        after({ req, res -> res.send("ALL after\n") })
    }.start()
//    server.start(9443, true, "./keystore.jks", "password")
}

class Server(val port: Int = (System.getProperty("server.port") ?: "9000").toInt()) {

    val threadPool: ExecutorService = Executors.newCachedThreadPool()
    val bindings: Map<HttpMethod, MutableMap<String, (HttpRequest, HttpResponse) -> Any>> = mapOf(
            Pair(HttpMethod.GET, mutableMapOf()),
            Pair(HttpMethod.POST, mutableMapOf()),
            Pair(HttpMethod.PUT, mutableMapOf()),
            Pair(HttpMethod.DELETE, mutableMapOf()),
            Pair(HttpMethod.OPTIONS, mutableMapOf()),
            Pair(HttpMethod.CONNECT, mutableMapOf()),
            Pair(HttpMethod.HEAD, mutableMapOf()),
            Pair(HttpMethod.TRACE, mutableMapOf())
    )

    val filtersBefore: MutableMap<String, (HttpRequest, HttpResponse) -> Any> = mutableMapOf()
    val filtersAfter: MutableMap<String, (HttpRequest, HttpResponse) -> Any> = mutableMapOf()

    fun start(port: Int = this.port, secure: Boolean = false, keyStoreFile: String = "", password: String = "") {
        bindFilters()
        threadPool.submit {
            println("Server start on port $port")
            val socket = if (secure) secureSocket(port, keyStoreFile, password) else ServerSocket(port)
            while (true) {
                threadPool.submit(ClientThread(socket.accept(), { matchRequest(it) }))
            }
        }
    }

    private fun bindFilters() {
        for (binding in bindings) {
            val iterator = binding.value.iterator()
            while (iterator.hasNext()) {
                var (key, action) = iterator.next()
                for ((path, before) in filtersBefore) {
                    if (key == path || key.matches(path.toRegex())) {
                        action = chain(before, action)
                        binding.value[key] = action
                    }
                }
                for ((path, after) in filtersAfter) {
                    if (key == path || key.matches(path.toRegex())) {
                        action = chain(action, after)
                        binding.value[key] = action
                    }
                }
            }
        }
    }

    private fun chain(a: (HttpRequest, HttpResponse) -> Any, b: (HttpRequest, HttpResponse) -> Any): (HttpRequest, HttpResponse) -> Any {
        return { req: HttpRequest, res: HttpResponse ->
            a.invoke(req, res)
            b.invoke(req, res)
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

    private fun matchRequest(request: HttpRequest): (HttpRequest, HttpResponse) -> Any {
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

    fun bind(method: HttpMethod, path: String, call: (request: HttpRequest, response: HttpResponse) -> Any) {
        bindings[method]?.put(path, call)
    }

    fun get(path: String, call: (request: HttpRequest, response: HttpResponse) -> Any) {
        bind(HttpMethod.GET, path, call)
    }

    fun post(path: String, call: (request: HttpRequest, response: HttpResponse) -> Any) {
        bind(HttpMethod.POST, path, call)
    }

    fun put(path: String, call: (request: HttpRequest, response: HttpResponse) -> Any) {
        bind(HttpMethod.PUT, path, call)
    }

    fun delete(path: String, call: (request: HttpRequest, response: HttpResponse) -> Any) {
        bind(HttpMethod.DELETE, path, call)
    }

    fun before(path: String, call: (request: HttpRequest, response: HttpResponse) -> Any) {
        filtersBefore.put(path, call)
    }

    fun before(call: (request: HttpRequest, response: HttpResponse) -> Any) {
        filtersBefore.put(".*", call)
    }

    fun after(path: String, call: (request: HttpRequest, response: HttpResponse) -> Any) {
        filtersAfter.put(path, call)
    }

    fun after(call: (request: HttpRequest, response: HttpResponse) -> Any) {
        filtersAfter.put(".*", call)
    }

    fun staticFiles(path: String) {
        val fullPath = javaClass.getResource(path).file
        File(fullPath)
                .walkTopDown()
                .forEach {
                    if (!it.isDirectory) {
                        val file = it.path.substring(fullPath.length)
                        val call: (HttpRequest, HttpResponse) -> Unit = { req, res -> it.inputStream().copyTo(res.stream).let { } }
                        get(file, call)
                        if (file == "/index.html" || file == "/index.htm") {
                            get("/", call)
                        }
                    }
                }
    }
}

fun <T> Iterable<T>.firstOrElse(eval: () -> T): T {
    return this.firstOrNull() ?: return eval.invoke()
}

