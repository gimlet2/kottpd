package com.github.gimlet2.kottpd

import org.junit.After
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerIntegrationTest {

    private var server: Server? = null
    
    companion object {
        private val portCounter = AtomicInteger(10000)
        
        private fun getNextPort(): Int = portCounter.getAndIncrement()
    }

    @After
    fun tearDown() {
        // Shutdown the thread pool to stop the server
        server?.threadPool?.shutdownNow()
        Thread.sleep(200) // Give it time to shutdown
    }

    @Test
    fun testGetRequest() {
        val testPort = getNextPort()
        server = Server(testPort)
        
        // Configure server
        server!!.get("/test") { _, _ -> "Hello from GET" }
        server!!.start(testPort)
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Make HTTP request
        val url = URL("http://localhost:$testPort/test")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Make HTTP POST request
        val url = URL("http://localhost:$testPort/data")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "text/plain")
        
        val postData = "test data"
        connection.outputStream.write(postData.toByteArray())
        
        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Make HTTP PUT request
        val url = URL("http://localhost:$testPort/update")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        
        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Make HTTP DELETE request
        val url = URL("http://localhost:$testPort/remove")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "DELETE"
        
        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Request a non-existent route
        val url = URL("http://localhost:$testPort/notfound")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        val responseCode = connection.responseCode
        // For error responses (4xx, 5xx), use errorStream instead of inputStream
        val responseBody = if (responseCode >= 400) {
            connection.errorStream.bufferedReader().use { it.readText() }
        } else {
            connection.inputStream.bufferedReader().use { it.readText() }
        }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Test regex route
        val url = URL("http://localhost:$testPort/do/anything/smth")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Make request
        val url = URL("http://localhost:$testPort/filtered")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Make request
        val url = URL("http://localhost:$testPort/filtered")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Make request
        val url = URL("http://localhost:$testPort/error")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        
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
        
        // Wait for server to start
        Thread.sleep(500)
        
        // Test first route
        var url = URL("http://localhost:$testPort/route1")
        var connection = url.openConnection() as HttpURLConnection
        var responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        assertTrue(responseBody.contains("Route 1"))
        
        // Test second route
        url = URL("http://localhost:$testPort/route2")
        connection = url.openConnection() as HttpURLConnection
        responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        assertTrue(responseBody.contains("Route 2"))
        
        // Test third route (POST)
        url = URL("http://localhost:$testPort/route3")
        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        responseBody = connection.inputStream.bufferedReader().use { it.readText() }
        assertTrue(responseBody.contains("Route 3"))
    }
}
