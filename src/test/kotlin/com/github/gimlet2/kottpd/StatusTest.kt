package com.github.gimlet2.kottpd

import org.junit.Test
import kotlin.test.assertEquals

class StatusTest {

    @Test
    fun testOkStatus() {
        assertEquals(200, Status.OK.code)
        assertEquals("OK", Status.OK.value)
    }

    @Test
    fun testCreatedStatus() {
        assertEquals(201, Status.Created.code)
        assertEquals("Created", Status.Created.value)
    }

    @Test
    fun testNotFoundStatus() {
        assertEquals(404, Status.NotFound.code)
        assertEquals("Not Found", Status.NotFound.value)
    }

    @Test
    fun testInternalServerErrorStatus() {
        assertEquals(500, Status.InternalServerError.code)
        assertEquals("Internal Server Error", Status.InternalServerError.value)
    }

    @Test
    fun testBadRequestStatus() {
        assertEquals(400, Status.BadRequest.code)
        assertEquals("Bad Request", Status.BadRequest.value)
    }

    @Test
    fun testUnauthorizedStatus() {
        assertEquals(401, Status.Unauthorized.code)
        assertEquals("Unauthorized", Status.Unauthorized.value)
    }

    @Test
    fun testForbiddenStatus() {
        assertEquals(403, Status.Forbidden.code)
        assertEquals("Forbidden", Status.Forbidden.value)
    }

    @Test
    fun testContinueStatus() {
        assertEquals(100, Status.Continue.code)
        assertEquals("Continue", Status.Continue.value)
    }

    @Test
    fun testMovedPermanentlyStatus() {
        assertEquals(301, Status.MovedPermanently.code)
        assertEquals("Moved Permanently", Status.MovedPermanently.value)
    }

    @Test
    fun testServiceUnavailableStatus() {
        assertEquals(503, Status.ServiceUnavailable.code)
        assertEquals("Service Unavailable", Status.ServiceUnavailable.value)
    }
}
