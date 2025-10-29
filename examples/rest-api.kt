package examples

import com.github.gimlet2.kottpd.Server
import com.github.gimlet2.kottpd.Status

/**
 * REST API Example
 * 
 * Demonstrates a simple REST API for managing users.
 * In a real application, you would use a database instead of an in-memory list.
 */

data class User(val id: Int, val name: String, val email: String)

val users = mutableListOf(
    User(1, "John Doe", "john@example.com"),
    User(2, "Jane Smith", "jane@example.com")
)

fun main() {
    val server = Server(port = 8080)
    
    // GET /users - List all users
    server.get("/users") { _, res ->
        val usersList = users.joinToString(",\n  ", "[\n  ", "\n]") { user ->
            """{"id": ${user.id}, "name": "${user.name}", "email": "${user.email}"}"""
        }
        res.send(usersList, Status.OK, mapOf("Content-Type" to "application/json"))
    }
    
    // GET /users/:id - Get a specific user
    server.get("/users/[0-9]+") { req, res ->
        val id = req.url.substringAfterLast("/").toIntOrNull()
        val user = users.find { it.id == id }
        
        if (user != null) {
            val json = """{"id": ${user.id}, "name": "${user.name}", "email": "${user.email}"}"""
            res.send(json, Status.OK, mapOf("Content-Type" to "application/json"))
        } else {
            res.send("""{"error": "User not found"}""", Status.NotFound, mapOf("Content-Type" to "application/json"))
        }
    }
    
    // POST /users - Create a new user
    server.post("/users") { req, res ->
        // In a real app, parse JSON properly using a library like kotlinx.serialization or Gson
        val content = req.content
        println("Creating user with data: $content")
        
        val newId = (users.maxOfOrNull { it.id } ?: 0) + 1
        val newUser = User(newId, "New User", "new@example.com")
        users.add(newUser)
        
        val json = """{"id": ${newUser.id}, "name": "${newUser.name}", "email": "${newUser.email}"}"""
        res.send(json, Status.Created, mapOf("Content-Type" to "application/json"))
    }
    
    // PUT /users/:id - Update a user
    server.put("/users/[0-9]+") { req, res ->
        val id = req.url.substringAfterLast("/").toIntOrNull()
        val userIndex = users.indexOfFirst { it.id == id }
        
        if (userIndex >= 0) {
            println("Updating user $id with data: ${req.content}")
            // In real app, parse JSON and update user
            res.send("""{"message": "User updated"}""", Status.OK, mapOf("Content-Type" to "application/json"))
        } else {
            res.send("""{"error": "User not found"}""", Status.NotFound, mapOf("Content-Type" to "application/json"))
        }
    }
    
    // DELETE /users/:id - Delete a user
    server.delete("/users/[0-9]+") { req, res ->
        val id = req.url.substringAfterLast("/").toIntOrNull()
        val removed = users.removeIf { it.id == id }
        
        if (removed) {
            res.send("""{"message": "User deleted"}""", Status.OK, mapOf("Content-Type" to "application/json"))
        } else {
            res.send("""{"error": "User not found"}""", Status.NotFound, mapOf("Content-Type" to "application/json"))
        }
    }
    
    // Start server
    server.start()
    
    println("REST API Server started on http://localhost:8080")
    println("\nTry these commands:")
    println("  curl http://localhost:8080/users")
    println("  curl http://localhost:8080/users/1")
    println("  curl -X POST http://localhost:8080/users -d '{\"name\":\"Test\",\"email\":\"test@example.com\"}'")
    println("  curl -X PUT http://localhost:8080/users/1 -d '{\"name\":\"Updated\",\"email\":\"updated@example.com\"}'")
    println("  curl -X DELETE http://localhost:8080/users/1")
    
    Thread.currentThread().join()
}
