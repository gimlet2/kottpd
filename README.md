# kottpd

Kottpd - REST framework written in pure Kotlin. It is avaliable from maven central repository. It supports plain HTTP and secured HTTPs. 
``` 
    <dependency>
        <groupId>com.github.gimlet2</groupId>
        <artifactId>kottpd</artifactId>
        <version>0.0.6</version>
    </dependency>
```


```
    val server = Server() // default port is 9000
    server.staticFiles("/public") // specify path to static content folder
    server.get("/hello", { req, res -> res.send("Hello") }) // use res.send to send data to response explicitly
    server.get("/hello_simple", { req, res -> "Hello" }) // or just return some value and that will be sent to response automatically
    server.get("/do/.*/smth", { req, res -> res.send("Hello world") }) // also you could bind handlers by regular expressions
    server.post("/data", { req, res -> res.send(req.content, Status.Created) }) // send method accepts status
    server.start(9443, true, "./keystore.jks", "password") // for secured conection
    server.start()
```
