package examples

import com.github.gimlet2.kottpd.Server
import com.github.gimlet2.kottpd.Status

/**
 * Authentication Example
 * 
 * Demonstrates simple authentication using before filters.
 * In production, use proper authentication mechanisms like JWT or OAuth.
 */

// Simulated user database
val validTokens = mapOf(
    "token-abc123" to "john",
    "token-xyz789" to "jane"
)

fun main() {
    val server = Server(port = 9000)
    
    // Public endpoint (no authentication required)
    server.get("/") { _, _ ->
        "Welcome! This is a public endpoint."
    }
    
    // Public login endpoint (returns a token)
    server.post("/login") { req, res ->
        // In real app, validate username/password from req.content
        val username = "demo-user"
        val token = "token-demo123"
        
        res.send("""{"token": "$token", "username": "$username"}""", 
                 Status.OK, 
                 mapOf("Content-Type" to "application/json"))
    }
    
    // Authentication filter for /api/* endpoints
    server.before("/api/.*") { req, res ->
        val authHeader = req.headers["Authorization"]
        val token = authHeader?.removePrefix("Bearer ")?.trim()
        
        if (token == null || !validTokens.containsKey(token)) {
            res.send(
                """{"error": "Unauthorized - Valid token required"}""",
                Status.Unauthorized,
                mapOf("Content-Type" to "application/json")
            )
            // Note: In current Kottpd version, we can't stop execution here
            // This is a limitation that should be addressed in future versions
        }
    }
    
    // Protected endpoint
    server.get("/api/profile") { req, res ->
        val token = req.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
        val username = validTokens[token] ?: "unknown"
        
        res.send(
            """{"username": "$username", "email": "$username@example.com"}""",
            Status.OK,
            mapOf("Content-Type" to "application/json")
        )
    }
    
    // Protected endpoint
    server.get("/api/secret") { _, res ->
        res.send(
            """{"message": "This is secret data!"}""",
            Status.OK,
            mapOf("Content-Type" to "application/json")
        )
    }
    
    server.start()
    
    println("Authentication Server started on http://localhost:9000")
    println("\nTry these commands:")
    println("  Public: curl http://localhost:9000/")
    println("  Protected (no auth): curl http://localhost:9000/api/secret")
    println("  Protected (with auth): curl -H 'Authorization: Bearer token-abc123' http://localhost:9000/api/secret")
    println("  Profile: curl -H 'Authorization: Bearer token-abc123' http://localhost:9000/api/profile")
    
    Thread.currentThread().join()
}
