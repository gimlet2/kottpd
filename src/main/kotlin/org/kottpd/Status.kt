package org.kottpd

enum class Status(val code: Int, val value: String) {
    // 100
    Continue(100, "Continue"),
    SwitchingProtocols(101, "Switching Protocols"), Processing(102, "Processing"),
    // 200
    OK(200, "OK"),
    Created(201, "Created"), Accepted(202, "Accepted"), NonAuthoritativeInformation(203, "Non-Authoritative Information"),
    NoContent(204, "No Content"), ResetContent(205, "Reset Content"), PartialContent(206, "Partial Content"),
    MultiStatus(207, "Multi-Status"), AlreadyReported(208, "Already Reported"), IMUsed(226, "IM Used"),
    // 300
    MultipleChoices(300, "Multiple Choices"),
    MovedPermanently(301, "Moved Permanently"), Found(302, "Found"),
    SeeOther(303, "See Other"), NotModified(304, "Not Modified"), UseProxy(305, "Use Proxy"), SwitchProxy(306, "Switch Proxy"),
    TemporaryRedirect(307, "Temporary Redirect"), PermanentRedirect(308, "Permanent Redirect"),
    // 400
    BadRequest(400, "Bad Request"),
    Unauthorized(401, "Unauthorized"), PaymentRequired(402, "Payment Required"),
    Forbidden(403, "Forbidden"), NotFound(404, "Not Found"), MethodNotAllowed(405, "Method Not Allowed"),
    NotAcceptable(406, "Not Acceptable"), ProxyAuthenticationRequired(407, "Proxy Authentication Required"),
    RequestTimeout(408, "Request Timeout"), Conflict(409, "Conflict"), Gone(410, "Gone"), LengthRequired(411, "Length Required"),
    PreconditionFailed(412, "Precondition Failed"), PayloadTooLarge(413, "Payload Too Large"), URITooLong(414, "URI Too Long"),
    UnsupportedMediaType(415, "Unsupported Media Type"), RangeNotSatisfiable(416, "Range Not Satisfiable"),
    ExpectationFailed(417, "Expectation Failed"), ImATeapot(418, "I'm a teapot"), MisdirectedRequest(421, "Misdirected Request"),
    UnprocessableEntity(422, "Unprocessable Entity"), Locked(423, "Locked"), FailedDependency(424, "Failed Dependency"),
    UpgradeRequired(426, "Upgrade Required"), PreconditionRequired(428, "Precondition Required"),
    TooManyRequests(429, "Too Many Requests"), RequestHeaderFieldsTooLarge(431, "Request Header Fields Too Large"),
    UnavailableForLegalReasons(451, "Unavailable For Legal Reasons"),
    // 500
    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented"), BadGateway(502, "Bad Gateway"),
    ServiceUnavailable(503, "Service Unavailable"), GatewayTimeout(504, "Gateway Timeout"),
    HTTPVersionNotSupported(505, "HTTP Version Not Supported"), VariantAlsoNegotiates(506, "Variant Also Negotiates"),
    InsufficientStorage(507, "Insufficient Storage"), LoopDetected(508, "Loop Detected"), NotExtended(510, "Not Extended"),
    NetworkAuthenticationRequired(511, "Network Authentication Required")
}