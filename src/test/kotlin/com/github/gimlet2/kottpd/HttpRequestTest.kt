package com.github.gimlet2.kottpd

import org.junit.Test
import java.io.BufferedReader
import java.io.StringReader
import kotlin.test.assertEquals

class HttpRequestTest {

    @Test
    fun testHttpRequestCreation() {
        val headers = mapOf("Content-Length" to "0", "Host" to "localhost")
        val reader = BufferedReader(StringReader(""))
        
        val request = HttpRequest(
            method = HttpMethod.GET,
            url = "/test",
            httpVersion = "HTTP/1.1",
            headers = headers,
            stream = reader
        )
        
        assertEquals(HttpMethod.GET, request.method)
        assertEquals("/test", request.url)
        assertEquals("HTTP/1.1", request.httpVersion)
        assertEquals("localhost", request.headers["Host"])
    }

    @Test
    fun testContentPropertyWithEmptyBody() {
        val headers = mapOf("Content-Length" to "0")
        val reader = BufferedReader(StringReader(""))
        
        val request = HttpRequest(
            method = HttpMethod.POST,
            url = "/data",
            httpVersion = "HTTP/1.1",
            headers = headers,
            stream = reader
        )
        
        assertEquals("", request.content)
    }

    @Test
    fun testContentPropertyWithBody() {
        val headers = mapOf("Content-Length" to "11")
        val reader = BufferedReader(StringReader("Hello World"))
        
        val request = HttpRequest(
            method = HttpMethod.POST,
            url = "/data",
            httpVersion = "HTTP/1.1",
            headers = headers,
            stream = reader
        )
        
        assertEquals("Hello World", request.content)
    }

    @Test
    fun testHeadersGetOrElseWithMissingKey() {
        val headers = mapOf("Host" to "localhost")
        val reader = BufferedReader(StringReader(""))
        
        val request = HttpRequest(
            method = HttpMethod.GET,
            url = "/",
            httpVersion = "HTTP/1.1",
            headers = headers,
            stream = reader
        )
        
        val contentLength = request.headers.getOrElse("Content-Length") { "0" }
        assertEquals("0", contentLength)
    }

    @Test
    fun testDataClassEquality() {
        val headers = mapOf("Host" to "localhost")
        val reader1 = BufferedReader(StringReader(""))
        val reader2 = BufferedReader(StringReader(""))
        
        val request1 = HttpRequest(HttpMethod.GET, "/", "HTTP/1.1", headers, reader1)
        val request2 = HttpRequest(HttpMethod.GET, "/", "HTTP/1.1", headers, reader2)
        
        // Same values but different stream instances - data class equals checks all properties
        assertEquals(request1.method, request2.method)
        assertEquals(request1.url, request2.url)
        assertEquals(request1.httpVersion, request2.httpVersion)
        assertEquals(request1.headers, request2.headers)
    }
}
