# Kottpd

[![Maven Central](https://img.shields.io/maven-central/v/com.github.gimlet2/kottpd.svg)](https://search.maven.org/artifact/com.github.gimlet2/kottpd)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)

A lightweight REST framework written in pure Kotlin with zero external dependencies. Perfect for building microservices, REST APIs, and simple web servers.

## Features

✨ **Pure Kotlin** - No external framework dependencies  
🚀 **Lightweight** - Minimal footprint (~316 LOC)  
🔒 **HTTPS Support** - Built-in SSL/TLS support  
🛣️ **Flexible Routing** - Path-based and regex routing  
🎯 **Filters** - Before/after request filters  
⚡ **Simple API** - Easy to learn and use  
📁 **Static Files** - Built-in static file serving  
🎭 **Exception Handling** - Custom exception handlers  

## Quick Start

### Installation

Add Kottpd to your project using Maven:

```xml
<dependency>
    <groupId>com.github.gimlet2</groupId>
    <artifactId>kottpd</artifactId>
    <version>0.2.2</version>
</dependency>
```

Or Gradle:

```kotlin
implementation("com.github.gimlet2:kottpd:0.2.2")
```

### Hello World

```kotlin
import com.github.gimlet2.kottpd.Server

fun main() {
    val server = Server() // Default port is 9000
    
    server.get("/hello") { _, _ -> 
        "Hello, World!" 
    }
    
    server.start()
    println("Server running at http://localhost:9000")
}
```

Visit http://localhost:9000/hello to see your server in action!

## Usage Examples

### Basic Routing

```kotlin
val server = Server(port = 8080)

// GET request
server.get("/users") { req, res ->
    res.send("[{\"id\": 1, \"name\": \"John\"}]")
}

// POST request
server.post("/users") { req, res ->
    val userData = req.content
    res.send("User created: $userData", Status.Created)
}

// PUT request
server.put("/users/:id") { req, res ->
    res.send("User ${req.url} updated")
}

// DELETE request
server.delete("/users/:id") { req, res ->
    res.send("User deleted", Status.NoContent)
}

server.start()
```

### Regex-based Routing

```kotlin
server.get("/api/v.*/users") { req, res ->
    res.send("Matches /api/v1/users, /api/v2/users, etc.")
}

server.get("/files/.*\\.pdf") { req, res ->
    res.send("PDF file requested")
}
```

### Request Filters (Middleware)

```kotlin
// Global before filter (runs before all requests)
server.before { req, res ->
    println("Incoming request: ${req.method} ${req.url}")
    res.send("Log: Request received\n")
}

// Path-specific before filter
server.before("/api/.*") { req, res ->
    // Authentication check
    val token = req.headers["Authorization"]
    if (token == null) {
        res.send("Unauthorized", Status.Unauthorized)
    }
}

// After filter
server.after("/.*") { req, res ->
    res.send("\n--- Request completed ---")
}
```

### Exception Handling

```kotlin
server.get("/error") { req, res ->
    throw IllegalStateException("Something went wrong!")
}

server.exception(IllegalStateException::class) { req, res ->
    res.send("Error handled gracefully", Status.InternalServerError)
}
```

### Static File Serving

```kotlin
// Serve static files from resources/public
server.staticFiles("/public")

// Now files are accessible:
// resources/public/index.html -> http://localhost:9000/index.html
// resources/public/css/style.css -> http://localhost:9000/css/style.css
```

### HTTPS/TLS Support

```kotlin
server.start(
    port = 9443,
    secure = true,
    keyStoreFile = "./keystore.jks",
    password = "keystorePassword"
)
```

### Working with Headers

```kotlin
server.get("/headers") { req, res ->
    val userAgent = req.headers["User-Agent"]
    val contentType = req.headers["Content-Type"]
    
    res.send("User-Agent: $userAgent")
}
```

### Reading Request Body

```kotlin
server.post("/data") { req, res ->
    val body = req.content
    val contentLength = req.headers["Content-Length"]
    
    res.send("Received ${contentLength} bytes: $body", Status.Created)
}
```

### Complete Example

```kotlin
fun main() {
    Server(9000).apply {
        // Static files
        staticFiles("/public")
        
        // Routes
        get("/") { _, _ -> "Welcome to Kottpd!" }
        get("/hello") { _, res -> res.send("Hello") }
        get("/user/.*") { req, res -> res.send("User path: ${req.url}") }
        
        // POST with body
        post("/data") { req, res -> 
            res.send(req.content, Status.Created) 
        }
        
        // Filters
        before("/hello") { _, res -> res.send("before\n") }
        after("/hello") { _, res -> res.send("\nafter") }
        
        // Global filter
        before { _, res -> res.send("[LOG] ") }
        
        // Exception handling
        get("/error") { _, _ -> throw IllegalStateException("Test error") }
        exception(IllegalStateException::class) { _, _ -> "Error handled" }
        
        start()
    }.also {
        println("Server started on http://localhost:9000")
        println("Try: http://localhost:9000/hello")
    }
}
```

## Architecture

Kottpd uses a simple architecture:

1. **Server** - Main class that handles routing and server lifecycle
2. **ClientThread** - Processes individual HTTP requests in separate threads
3. **HttpRequest** - Represents incoming HTTP requests
4. **HttpResponse** - Represents outgoing HTTP responses
5. **Status** - HTTP status codes
6. **HttpMethod** - Supported HTTP methods

## Configuration

### Custom Port

```kotlin
val server = Server(port = 8080)
// or via system property
// -Dserver.port=8080
```

### Thread Pool

The server uses a cached thread pool by default, which creates new threads as needed and reuses previously constructed threads when available.

## Documentation

- 📖 [Development Roadmap](ROADMAP.md) - Future plans and features
- 🤝 [Contributing Guide](CONTRIBUTING.md) - How to contribute
- 🔒 [Security Best Practices](SECURITY.md) - Security guidelines

## Comparison with Other Frameworks

| Feature | Kottpd | Ktor | Javalin | Spring Boot |
|---------|--------|------|---------|-------------|
| Size | Tiny | Medium | Small | Large |
| Dependencies | None | Many | Few | Many |
| Learning Curve | Easy | Medium | Easy | Steep |
| Performance | Good | Excellent | Good | Good |
| Best For | Simple APIs | Production apps | REST APIs | Enterprise |

## Limitations

⚠️ **Current limitations to be aware of:**

- No built-in JSON serialization (bring your own library)
- Basic error handling
- Limited documentation
- No built-in CORS support
- Early development stage (v0.2.x)

See [ROADMAP.md](ROADMAP.md) for planned improvements.

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**Andrei Chernyshev** - [@gimlet2](https://github.com/gimlet2)

## Support

- 🐛 [Report a bug](https://github.com/gimlet2/kottpd/issues/new)
- 💡 [Request a feature](https://github.com/gimlet2/kottpd/issues/new)
- ⭐ Star this repository if you find it useful!

---

Made with ❤️ using Kotlin
