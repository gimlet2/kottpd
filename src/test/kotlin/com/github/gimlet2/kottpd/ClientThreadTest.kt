package com.github.gimlet2.kottpd

import org.junit.Test
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.net.Socket
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClientThreadTest {

    @Test
    fun testReadRequest() {
        val requestLine = "GET /test HTTP/1.1\nHost: localhost\nContent-Length: 0\n\n"
        val reader = BufferedReader(StringReader(requestLine))
        
        val clientThread = ClientThread(createMockSocket(), { req -> { _, res -> res.send("OK") } })
        
        val request = clientThread.readRequest(reader) {
            mapOf("Host" to "localhost", "Content-Length" to "0")
        }
        
        assertEquals(HttpMethod.GET, request.method)
        assertEquals("/test", request.url)
        assertEquals("HTTP/1.1", request.httpVersion)
        assertEquals("localhost", request.headers["Host"])
    }

    @Test
    fun testReadRequestWithPost() {
        val requestLine = "POST /data HTTP/1.1"
        val reader = BufferedReader(StringReader(requestLine))
        
        val clientThread = ClientThread(createMockSocket(), { req -> { _, res -> res.send("Created") } })
        
        val request = clientThread.readRequest(reader) {
            mapOf("Content-Length" to "5")
        }
        
        assertEquals(HttpMethod.POST, request.method)
        assertEquals("/data", request.url)
    }

    private fun createMockSocket(): Socket {
        // Create a mock socket for testing - we won't actually use it in these tests
        // In real scenarios, you'd use a mocking framework, but for minimal dependencies we create a simple stub
        return object : Socket() {
            override fun close() {}
            override fun getOutputStream() = ByteArrayOutputStream()
            override fun getInputStream() = "".byteInputStream()
        }
    }
}
