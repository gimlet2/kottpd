# kottpd

Kottpd - small http server written in Kotlin.

```
    val server = Server() // default port is 9000
    server.get("/hello", { req, res -> res.send("Hello") })
    server.get("/do/.*/smth", { req, res -> res.send("Hello world") })
    server.post("/data", { req, res -> res.send(req.content, Status.Created) })
    server.start()
```
