import org.kottpd.Server
import org.kottpd.Status

/**
 * Created by Andrei Chernyshev on 7/24/17.
 */


fun main(args: Array<String>) {
    Server().apply {
        //        staticFiles("/public")
        get("/hello", { req, res -> res.send("Hello") })
        get("/test", { req, res -> throw IllegalStateException("AAA") })
        get("/do/.*/smth", { req, res -> res.send("Hello world") })
        post("/data", { req, res -> res.send(req.content, Status.Created) })
        before("/hello", { req, res -> res.send("before\n") })
        before({ req, res -> res.send("ALL before\n") })
        after("/hello", { req, res -> res.send("\nafter\n") })
        after({ req, res -> res.send("ALL after\n") })
//        exception(IllegalStateException::class, { req, res -> "Illegal State" })
    }.start()
//    server.start(9443, true, "./keystore.jks", "password")
}