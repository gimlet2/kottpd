package com.github.gimlet2.kottpd

import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HttpResponseTest {

    @Test
    fun testDefaultStatus() {
        val stream = ByteArrayOutputStream()
        val response = HttpResponse(stream = stream)
        assertEquals(Status.OK, response.status)
    }

    @Test
    fun testSendWithDefaultStatus() {
        val stream = ByteArrayOutputStream()
        val response = HttpResponse(stream = stream)
        response.send("Hello World")
        response.flush()
        
        val output = stream.toString()
        assertTrue(output.contains("HTTP/1.1 200 OK"))
        assertTrue(output.contains("Hello World"))
    }

    @Test
    fun testSendWithCustomStatus() {
        val stream = ByteArrayOutputStream()
        val response = HttpResponse(stream = stream)
        response.send("Not Found", Status.NotFound)
        response.flush()
        
        val output = stream.toString()
        assertTrue(output.contains("HTTP/1.1 404 Not Found"))
        assertTrue(output.contains("Not Found"))
    }

    @Test
    fun testSendWithHeaders() {
        val stream = ByteArrayOutputStream()
        val response = HttpResponse(stream = stream)
        val headers = mapOf("Content-Type" to "application/json", "X-Custom" to "test")
        response.send("Content", Status.OK, headers)
        response.flush()
        
        val output = stream.toString()
        assertTrue(output.contains("Content-Type: application/json"))
        assertTrue(output.contains("X-Custom: test"))
        assertTrue(output.contains("Content"))
    }

    @Test
    fun testMultipleSendCallsOnlyWriteHeaderOnce() {
        val stream = ByteArrayOutputStream()
        val response = HttpResponse(stream = stream)
        response.send("First")
        response.send("Second")
        response.flush()
        
        val output = stream.toString()
        // Should only have one HTTP header line
        val headerCount = output.split("HTTP/1.1").size - 1
        assertEquals(1, headerCount)
        assertTrue(output.contains("FirstSecond"))
    }

    @Test
    fun testStatusCanBeSetBeforeSend() {
        val stream = ByteArrayOutputStream()
        val response = HttpResponse(status = Status.Created, stream = stream)
        response.send("Resource Created")
        response.flush()
        
        val output = stream.toString()
        assertTrue(output.contains("HTTP/1.1 201 Created"))
    }
}
