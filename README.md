# kottpd

Kottpd - REST framework written in pure Kotlin.
``` 
    <dependency>
        <groupId>com.github.gimlet2</groupId>
        <artifactId>kottpd</artifactId>
        <version>0.0.4</version>
    </dependency>
```


```
    val server = Server() // default port is 9000
    server.get("/hello", { req, res -> res.send("Hello") })
    server.get("/do/.*/smth", { req, res -> res.send("Hello world") })
    server.post("/data", { req, res -> res.send(req.content, Status.Created) })
    server.start(9443, true, "./keystore.jks", "password")
    server.start()
```
