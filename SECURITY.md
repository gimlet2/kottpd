# Security Best Practices for Kottpd

## Overview

This document outlines security best practices when using Kottpd for building web applications and REST APIs. While Kottpd provides a lightweight HTTP server, you must implement additional security measures for production use.

## Critical Security Considerations

### ⚠️ Production Readiness Warning

**Kottpd is currently in early development (v0.2.x) and should be used with caution in production environments.** We recommend:

- Conducting thorough security testing before production deployment
- Using Kottpd behind a reverse proxy (nginx, Apache, etc.)
- Implementing additional security layers at the infrastructure level
- Staying updated with security patches and updates

## HTTPS/TLS Configuration

### Always Use HTTPS in Production

```kotlin
val server = Server()

// Configure HTTPS with proper keystore
server.start(
    port = 9443,
    secure = true,
    keyStoreFile = "/path/to/keystore.jks",
    password = "strong-password"
)
```

### Best Practices for TLS

1. **Use Strong Certificates**
   - Obtain certificates from trusted Certificate Authorities
   - Use at least 2048-bit RSA keys or 256-bit ECC keys
   - Keep certificates up to date (renew before expiration)

2. **Secure Keystore Storage**
   - Never commit keystores to version control
   - Use environment variables for passwords
   - Set restrictive file permissions (chmod 600)
   - Rotate certificates regularly

3. **Disable HTTP in Production**
   - Only expose HTTPS endpoint
   - Redirect HTTP to HTTPS at reverse proxy level

```kotlin
// Load password from environment
val keystorePassword = System.getenv("KEYSTORE_PASSWORD") 
    ?: throw IllegalStateException("KEYSTORE_PASSWORD not set")

server.start(9443, true, "./keystore.jks", keystorePassword)
```

## Input Validation

### Validate All User Input

**Never trust user input.** Always validate and sanitize:

```kotlin
server.post("/user") { req, res ->
    // Validate required fields
    val username = req.headers["username"] 
        ?: return@post res.send("Username required", Status.BadRequest)
    
    // Validate format
    if (!username.matches(Regex("^[a-zA-Z0-9_]{3,20}$"))) {
        return@post res.send("Invalid username format", Status.BadRequest)
    }
    
    // Process valid input
    res.send("User created", Status.Created)
}
```

### Content Length Validation

Protect against large payload attacks:

```kotlin
server.before { req, res ->
    val contentLength = req.headers["Content-Length"]?.toIntOrNull() ?: 0
    if (contentLength > 10_485_760) { // 10MB limit
        res.send("Payload too large", Status.PayloadTooLarge)
    }
}
```

### Path Traversal Prevention

When serving static files, ensure paths are validated:

```kotlin
// Current implementation has basic protection via staticFiles()
// For custom file handling, always validate:
fun validatePath(requestPath: String): Boolean {
    val normalized = Paths.get(requestPath).normalize().toString()
    return !normalized.contains("..") && normalized.startsWith("/public")
}
```

## Authentication & Authorization

### Implement Authentication

Kottpd doesn't include built-in authentication. Implement it using filters:

```kotlin
// Simple token-based authentication example
val validTokens = setOf("secret-token-1", "secret-token-2")

server.before { req, res ->
    val token = req.headers["Authorization"]?.removePrefix("Bearer ")
    
    if (token == null || token !in validTokens) {
        res.send("Unauthorized", Status.Unauthorized)
        // Don't continue to handler
    }
}
```

### Use Industry-Standard Authentication

For production, use established authentication methods:
- **JWT (JSON Web Tokens)** for stateless authentication
- **OAuth 2.0** for third-party authentication
- **Basic Auth** only over HTTPS
- **API Keys** with proper rotation policies

### Authorization Example

```kotlin
// Role-based access control
data class User(val username: String, val role: String)

fun checkPermission(user: User?, requiredRole: String): Boolean {
    return user?.role == requiredRole || user?.role == "admin"
}

server.delete("/admin/user/:id") { req, res ->
    val user = getUserFromToken(req.headers["Authorization"])
    
    if (!checkPermission(user, "admin")) {
        return@delete res.send("Forbidden", Status.Forbidden)
    }
    
    // Process admin request
    res.send("User deleted")
}
```

## Security Headers

### Essential Security Headers

Implement security headers in a before filter:

```kotlin
server.before { req, res ->
    val securityHeaders = mapOf(
        "X-Content-Type-Options" to "nosniff",
        "X-Frame-Options" to "DENY",
        "X-XSS-Protection" to "1; mode=block",
        "Strict-Transport-Security" to "max-age=31536000; includeSubDomains",
        "Content-Security-Policy" to "default-src 'self'",
        "Referrer-Policy" to "strict-origin-when-cross-origin",
        "Permissions-Policy" to "geolocation=(), microphone=(), camera=()"
    )
    
    // Note: Current HttpResponse doesn't support setting headers in filters
    // This is a proposed enhancement
}
```

## CORS (Cross-Origin Resource Sharing)

### Implement CORS Carefully

Be restrictive with CORS policies:

```kotlin
server.before { req, res ->
    // Only allow specific origins
    val allowedOrigins = setOf("https://trusted-domain.com")
    val origin = req.headers["Origin"]
    
    if (origin in allowedOrigins) {
        // Would need to add header support to HttpResponse
        // res.setHeader("Access-Control-Allow-Origin", origin)
        // res.setHeader("Access-Control-Allow-Methods", "GET, POST")
        // res.setHeader("Access-Control-Allow-Headers", "Content-Type")
    }
}

// Handle preflight requests
server.bind(HttpMethod.OPTIONS, ".*") { req, res ->
    res.send("", Status.NoContent)
}
```

## SQL Injection Prevention

### Use Parameterized Queries

Never concatenate user input into SQL queries:

```kotlin
// ❌ VULNERABLE - Don't do this!
val userId = req.headers["userId"]
val query = "SELECT * FROM users WHERE id = $userId"

// ✅ SAFE - Use parameterized queries
val userId = req.headers["userId"]?.toIntOrNull() 
    ?: return@get res.send("Invalid ID", Status.BadRequest)

// Use your database library's parameterized query support
val query = connection.prepareStatement("SELECT * FROM users WHERE id = ?")
query.setInt(1, userId)
```

## Logging and Monitoring

### Log Security Events

```kotlin
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("SecurityAudit")

server.before { req, res ->
    // Log authentication attempts
    logger.info("Request: ${req.method} ${req.url} from ${req.headers["X-Forwarded-For"]}")
}

server.exception(IllegalStateException::class) { req, res ->
    logger.error("Security exception: ${req.url}", it)
    "Internal Server Error"
}
```

### What to Log

- Authentication attempts (success and failure)
- Authorization failures
- Input validation failures
- Suspicious patterns (repeated failed attempts)
- Server errors and exceptions

### What NOT to Log

- Passwords or credentials
- Sensitive personal information
- Full credit card numbers
- Session tokens or API keys

## Rate Limiting

### Implement Rate Limiting

Protect against abuse and DDoS:

```kotlin
import java.util.concurrent.ConcurrentHashMap
import java.time.Instant

data class RateLimit(var count: Int, var resetTime: Long)

val rateLimits = ConcurrentHashMap<String, RateLimit>()
val MAX_REQUESTS = 100
val WINDOW_SECONDS = 60L

server.before { req, res ->
    val clientIp = req.headers["X-Forwarded-For"] ?: "unknown"
    val now = Instant.now().epochSecond
    
    val limit = rateLimits.compute(clientIp) { _, existing ->
        if (existing == null || existing.resetTime < now) {
            RateLimit(1, now + WINDOW_SECONDS)
        } else {
            existing.copy(count = existing.count + 1)
        }
    }!!
    
    if (limit.count > MAX_REQUESTS) {
        res.send("Too Many Requests", Status.TooManyRequests)
    }
}
```

## Session Management

### Secure Session Handling

```kotlin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class Session(val userId: String, val createdAt: Long)

val sessions = ConcurrentHashMap<String, Session>()
val SESSION_TIMEOUT = 3600000L // 1 hour

fun createSession(userId: String): String {
    val sessionId = UUID.randomUUID().toString()
    sessions[sessionId] = Session(userId, System.currentTimeMillis())
    return sessionId
}

fun validateSession(sessionId: String?): Session? {
    if (sessionId == null) return null
    
    val session = sessions[sessionId] ?: return null
    val age = System.currentTimeMillis() - session.createdAt
    
    if (age > SESSION_TIMEOUT) {
        sessions.remove(sessionId)
        return null
    }
    
    return session
}
```

## Dependency Security

### Keep Dependencies Updated

```xml
<!-- Regularly update dependencies -->
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib</artifactId>
    <version>1.9.23</version> <!-- Keep up to date -->
</dependency>
```

### Monitor for Vulnerabilities

- Enable GitHub Dependabot
- Use OWASP Dependency-Check
- Regularly review security advisories
- Update promptly when vulnerabilities are found

## Deployment Security

### Environment Variables

```kotlin
// ✅ Use environment variables for sensitive data
val dbPassword = System.getenv("DB_PASSWORD")
val apiKey = System.getenv("API_KEY")

// ❌ Never hardcode secrets
val dbPassword = "supersecret123" // DON'T DO THIS
```

### Reverse Proxy Configuration

Always use a reverse proxy in production:

```nginx
# nginx example
server {
    listen 443 ssl http2;
    server_name api.example.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    
    location / {
        proxy_pass http://localhost:9000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Docker Security

```dockerfile
# Use specific versions
FROM eclipse-temurin:11-jre-alpine

# Run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy only necessary files
COPY --chown=appuser:appgroup target/kottpd.jar /app/

WORKDIR /app
EXPOSE 9000

ENTRYPOINT ["java", "-jar", "kottpd.jar"]
```

## Incident Response

### Have a Security Plan

1. **Monitor** - Implement logging and alerting
2. **Detect** - Set up anomaly detection
3. **Respond** - Have incident response procedures
4. **Recover** - Plan for disaster recovery
5. **Review** - Post-incident analysis

### Reporting Security Issues

If you discover a security vulnerability:

1. **DO NOT** open a public issue
2. Email security concerns to the maintainer
3. Provide detailed information about the vulnerability
4. Allow reasonable time for a fix before public disclosure

## Security Checklist for Production

- [ ] HTTPS enabled with valid certificates
- [ ] Input validation on all endpoints
- [ ] Authentication implemented
- [ ] Authorization checks in place
- [ ] Security headers configured
- [ ] CORS properly restricted
- [ ] SQL injection prevention
- [ ] Rate limiting implemented
- [ ] Logging and monitoring active
- [ ] Dependencies up to date
- [ ] Secrets in environment variables
- [ ] Reverse proxy configured
- [ ] Security testing completed
- [ ] Incident response plan ready

## Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP REST Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/REST_Security_Cheat_Sheet.html)
- [Kotlin Security Best Practices](https://kotlinlang.org/docs/security.html)

---

**Remember:** Security is an ongoing process, not a one-time checklist. Regularly review and update your security measures.

*Last updated: 2025-10-29*
