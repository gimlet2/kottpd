package com.github.gimlet2.kottpd

import org.junit.Test
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.StringReader
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ServerTest {

    @Test
    fun testServerDefaultPort() {
        val server = Server()
        assertEquals(9000, server.port)
    }

    @Test
    fun testServerCustomPort() {
        val server = Server(8080)
        assertEquals(8080, server.port)
    }

    @Test
    fun testGetBinding() {
        val server = Server()
        var called = false
        server.get("/test") { _, _ ->
            called = true
            "Response"
        }
        
        val bindings = server.bindings[HttpMethod.GET]
        assertNotNull(bindings)
        assertTrue(bindings.containsKey("/test"))
    }

    @Test
    fun testPostBinding() {
        val server = Server()
        server.post("/data") { _, _ -> "Created" }
        
        val bindings = server.bindings[HttpMethod.POST]
        assertNotNull(bindings)
        assertTrue(bindings.containsKey("/data"))
    }

    @Test
    fun testPutBinding() {
        val server = Server()
        server.put("/update") { _, _ -> "Updated" }
        
        val bindings = server.bindings[HttpMethod.PUT]
        assertNotNull(bindings)
        assertTrue(bindings.containsKey("/update"))
    }

    @Test
    fun testDeleteBinding() {
        val server = Server()
        server.delete("/remove") { _, _ -> "Deleted" }
        
        val bindings = server.bindings[HttpMethod.DELETE]
        assertNotNull(bindings)
        assertTrue(bindings.containsKey("/remove"))
    }

    @Test
    fun testBeforeFilterWithPath() {
        val server = Server()
        server.before("/test") { _, res -> res.send("Before") }
        
        assertTrue(server.filtersBefore.containsKey("/test"))
    }

    @Test
    fun testBeforeFilterGlobal() {
        val server = Server()
        server.before { _, res -> res.send("Global Before") }
        
        assertTrue(server.filtersBefore.containsKey(".*"))
    }

    @Test
    fun testAfterFilterWithPath() {
        val server = Server()
        server.after("/test") { _, res -> res.send("After") }
        
        assertTrue(server.filtersAfter.containsKey("/test"))
    }

    @Test
    fun testAfterFilterGlobal() {
        val server = Server()
        server.after { _, res -> res.send("Global After") }
        
        assertTrue(server.filtersAfter.containsKey(".*"))
    }

    @Test
    fun testExceptionHandler() {
        val server = Server()
        server.exception(IllegalStateException::class) { _, _ -> "Error Handled" }
        
        assertTrue(server.exceptions.containsKey(IllegalStateException::class.java))
    }

    @Test
    fun testBindMethodForAllHttpMethods() {
        val server = Server()
        
        server.bind(HttpMethod.GET, "/get", { _, _ -> "GET" })
        server.bind(HttpMethod.POST, "/post", { _, _ -> "POST" })
        server.bind(HttpMethod.PUT, "/put", { _, _ -> "PUT" })
        server.bind(HttpMethod.DELETE, "/delete", { _, _ -> "DELETE" })
        server.bind(HttpMethod.OPTIONS, "/options", { _, _ -> "OPTIONS" })
        server.bind(HttpMethod.HEAD, "/head", { _, _ -> "HEAD" })
        server.bind(HttpMethod.TRACE, "/trace", { _, _ -> "TRACE" })
        server.bind(HttpMethod.CONNECT, "/connect", { _, _ -> "CONNECT" })
        
        assertEquals(8, server.bindings.values.filter { it.isNotEmpty() }.size)
    }

    @Test
    fun testFirstOrElseExtensionWithValues() {
        val list = listOf(1, 2, 3)
        val result = list.firstOrElse { 0 }
        assertEquals(1, result)
    }

    @Test
    fun testFirstOrElseExtensionWithEmptyList() {
        val list = emptyList<Int>()
        val result = list.firstOrElse { 42 }
        assertEquals(42, result)
    }

    @Test
    fun testGetRouteReturnsResponse() {
        val server = Server()
        server.get("/hello") { _, res -> res.send("Hello World") }
        
        // Create a mock request
        val request = HttpRequest(
            HttpMethod.GET,
            "/hello",
            "HTTP/1.1",
            emptyMap(),
            BufferedReader(StringReader(""))
        )
        
        val stream = ByteArrayOutputStream()
        val response = HttpResponse(stream = stream)
        
        // Access the handler
        val handler = server.bindings[HttpMethod.GET]?.get("/hello")
        assertNotNull(handler)
        handler.invoke(request, response)
        response.flush()
        
        val output = stream.toString()
        assertTrue(output.contains("Hello World"))
    }

    @Test
    fun testPostRouteWithContent() {
        val server = Server()
        server.post("/data") { req, res -> 
            res.send(req.content, Status.Created)
        }
        
        val headers = mapOf("Content-Length" to "4")
        val request = HttpRequest(
            HttpMethod.POST,
            "/data",
            "HTTP/1.1",
            headers,
            BufferedReader(StringReader("test"))
        )
        
        val stream = ByteArrayOutputStream()
        val response = HttpResponse(stream = stream)
        
        val handler = server.bindings[HttpMethod.POST]?.get("/data")
        assertNotNull(handler)
        handler.invoke(request, response)
        response.flush()
        
        val output = stream.toString()
        assertTrue(output.contains("201 Created"))
        assertTrue(output.contains("test"))
    }
}
