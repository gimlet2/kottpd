# kottpd

Kottpd - REST framework written in pure Kotlin.
``` 
    <repository>
        <id>stage</id>
        <url>https://oss.sonatype.org/content/groups/central-staging/</url>
    </repository>
    ...
    <dependency>
        <groupId>org.kottpd</groupId>
        <artifactId>server</artifactId>
        <version>0.0.3</version>
    </dependency>
```


```
    val server = Server() // default port is 9000
    server.get("/hello", { req, res -> res.send("Hello") })
    server.get("/do/.*/smth", { req, res -> res.send("Hello world") })
    server.post("/data", { req, res -> res.send(req.content, Status.Created) })
    server.start()
```
