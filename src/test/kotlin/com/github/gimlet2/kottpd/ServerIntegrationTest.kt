package com.github.gimlet2.kottpd

import org.junit.After
import org.junit.Test
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerIntegrationTest {

    private var server: Server? = null
    
    companion object {
        private val portCounter = AtomicInteger(10000)
        
        private fun getNextPort(): Int = portCounter.getAndIncrement()
        
        private fun waitForServerToStart(port: Int, maxAttempts: Int = 50, delayMs: Long = 100) {
            repeat(maxAttempts) { attempt ->
                try {
                    Socket("localhost", port).use { 
                        // Successfully connected, server is up
                    }
                    return
                } catch (e: ConnectException) {
                    if (attempt == maxAttempts - 1) {
                        throw RuntimeException("Server failed to start on port $port", e)
                    }
                    Thread.sleep(delayMs)
                }
            }
        }
        
        private fun makeHttpRequest(
            port: Int,
            path: String,
            method: String = "GET",
            body: String? = null
        ): Pair<Int, String> {
            val url = URL("http://localhost:$port$path")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "text/plain")
                connection.outputStream.write(body.toByteArray())
            }
            
            val responseCode = connection.responseCode
            val responseBody = if (responseCode >= 400) {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            } else {
                connection.inputStream.bufferedReader().use { it.readText() }
            }
            
            return Pair(responseCode, responseBody)
        }
    }

    @After
    fun tearDown() {
        // Shutdown the server
        server?.shutdown()
        Thread.sleep(100) // Brief pause for cleanup
    }

    @Test
    fun testGetRequest() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server
        server!!.get("/test") { _, _ -> "Hello from GET" }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Make HTTP request
        val (responseCode, responseBody) = makeHttpRequest(testPort, "/test")
        
        assertEquals(200, responseCode)
        assertTrue(responseBody.contains("Hello from GET"))
    }

    @Test
    fun testPostRequest() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server
        server!!.post("/data") { req, _ -> "Received: ${req.content}" }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Make HTTP POST request
        val (responseCode, responseBody) = makeHttpRequest(testPort, "/data", "POST", "test data")
        
        assertEquals(200, responseCode)
        assertTrue(responseBody.contains("Received: test data"))
    }

    @Test
    fun testPutRequest() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server
        server!!.put("/update") { _, _ -> "Updated" }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Make HTTP PUT request
        val (responseCode, responseBody) = makeHttpRequest(testPort, "/update", "PUT")
        
        assertEquals(200, responseCode)
        assertTrue(responseBody.contains("Updated"))
    }

    @Test
    fun testDeleteRequest() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server
        server!!.delete("/remove") { _, _ -> "Deleted" }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Make HTTP DELETE request
        val (responseCode, responseBody) = makeHttpRequest(testPort, "/remove", "DELETE")
        
        assertEquals(200, responseCode)
        assertTrue(responseBody.contains("Deleted"))
    }

    @Test
    fun testNotFoundRoute() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server with a single route
        server!!.get("/exists") { _, _ -> "Found" }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Request a non-existent route
        val (responseCode, responseBody) = makeHttpRequest(testPort, "/notfound")
        
        assertEquals(404, responseCode)
        assertTrue(responseBody.contains("Resource not found"))
    }

    @Test
    fun testRegexRoute() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server with regex route
        server!!.get("/do/.*/smth") { _, _ -> "Regex matched" }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Test regex route
        val (responseCode, responseBody) = makeHttpRequest(testPort, "/do/anything/smth")
        
        assertEquals(200, responseCode)
        assertTrue(responseBody.contains("Regex matched"))
    }

    @Test
    fun testBeforeFilter() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server with before filter
        server!!.before("/filtered") { _, res -> res.send("BEFORE ") }
        server!!.get("/filtered") { _, res -> res.send("MAIN") }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Make request
        val (_, responseBody) = makeHttpRequest(testPort, "/filtered")
        
        assertTrue(responseBody.contains("BEFORE"))
        assertTrue(responseBody.contains("MAIN"))
    }

    @Test
    fun testAfterFilter() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server with after filter
        server!!.get("/filtered") { _, res -> res.send("MAIN") }
        server!!.after("/filtered") { _, res -> res.send(" AFTER") }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Make request
        val (_, responseBody) = makeHttpRequest(testPort, "/filtered")
        
        assertTrue(responseBody.contains("MAIN"))
        assertTrue(responseBody.contains("AFTER"))
    }

    @Test
    fun testExceptionHandler() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server with exception handler
        server!!.get("/error") { _, _ -> throw IllegalStateException("Test error") }
        server!!.exception(IllegalStateException::class) { _, _ -> "Error handled" }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Make request
        val (responseCode, responseBody) = makeHttpRequest(testPort, "/error")
        
        assertEquals(200, responseCode)
        assertTrue(responseBody.contains("Error handled"))
    }

    @Test
    fun testMultipleRoutes() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server with multiple routes
        server!!.get("/route1") { _, _ -> "Route 1" }
        server!!.get("/route2") { _, _ -> "Route 2" }
        server!!.post("/route3") { _, _ -> "Route 3" }
        server!!.start(testPort)
        
        waitForServerToStart(testPort)
        
        // Test first route
        var (_, responseBody) = makeHttpRequest(testPort, "/route1")
        assertTrue(responseBody.contains("Route 1"))
        
        // Test second route
        val (_, responseBody2) = makeHttpRequest(testPort, "/route2")
        assertTrue(responseBody2.contains("Route 2"))
        
        // Test third route (POST)
        val (_, responseBody3) = makeHttpRequest(testPort, "/route3", "POST")
        assertTrue(responseBody3.contains("Route 3"))
    }
}
