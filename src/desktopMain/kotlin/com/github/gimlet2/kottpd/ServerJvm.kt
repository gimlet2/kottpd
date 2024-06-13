package com.github.gimlet2.kottpd

///*** Example:
fun main() {
    Server().apply {
//        staticFiles("/public")
        get("/hello") { _, res -> res.send("Hello") }
        get("/test") { _, _ -> throw IllegalStateException("AAA") }
        get("/do/.&#42;/smth") { _, res -> res.send("Hello world") }
        post("/data") { req, res -> res.send(req.content, Status.Created) }
        before("/hello") { _, res -> res.send("before\n") }
        before { _, res -> res.send("ALL before\n") }
        after("/hello") { _, res -> res.send("\nafter\n") }
        after { _, res -> res.send("ALL after\n") }
//        exception(IllegalStateException::class) { _, _ -> "Illegal State" }
    }.start()
//    server.start(9443, true, "./keystore.jks", "password")
}

