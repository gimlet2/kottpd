package com.github.gimlet2.kottpd

import org.junit.Test
import kotlin.test.assertEquals

class HttpMethodTest {

    @Test
    fun testHttpMethodValues() {
        assertEquals(HttpMethod.GET, HttpMethod.valueOf("GET"))
        assertEquals(HttpMethod.POST, HttpMethod.valueOf("POST"))
        assertEquals(HttpMethod.PUT, HttpMethod.valueOf("PUT"))
        assertEquals(HttpMethod.DELETE, HttpMethod.valueOf("DELETE"))
        assertEquals(HttpMethod.OPTIONS, HttpMethod.valueOf("OPTIONS"))
        assertEquals(HttpMethod.HEAD, HttpMethod.valueOf("HEAD"))
        assertEquals(HttpMethod.TRACE, HttpMethod.valueOf("TRACE"))
        assertEquals(HttpMethod.CONNECT, HttpMethod.valueOf("CONNECT"))
        assertEquals(HttpMethod.PATCH, HttpMethod.valueOf("PATCH"))
    }

    @Test
    fun testHttpMethodCount() {
        assertEquals(9, HttpMethod.values().size)
    }
}
