package examples

import com.github.gimlet2.kottpd.Server

/**
 * Hello World Example
 * 
 * This is the simplest possible Kottpd application.
 * It creates a server on port 9000 and responds to GET requests at /hello.
 */
fun main() {
    val server = Server(port = 9000)
    
    // Simple GET endpoint
    server.get("/hello") { _, _ -> 
        "Hello, World!" 
    }
    
    // Start the server
    server.start()
    
    println("Server started on http://localhost:9000")
    println("Try: curl http://localhost:9000/hello")
    
    // Keep the application running
    Thread.currentThread().join()
}
