# Copilot Instructions for kottpd

## Project Overview
kottpd is a REST framework written in pure Kotlin. It provides a simple API for creating HTTP and HTTPS servers with support for routing, static files, filters, and exception handling.

## Technology Stack
- **Language**: Kotlin 1.9.23
- **Build Tool**: Maven (use `./mvnw` wrapper)
- **JDK Version**: Java 11
- **Testing**: JUnit 4.13.2 with kotlin-test-junit

## Project Structure
```
kottpd/
├── src/
│   ├── main/kotlin/com/github/gimlet2/kottpd/
│   │   ├── Server.kt           # Main server class with routing and configuration
│   │   ├── HttpRequest.kt      # HTTP request representation
│   │   ├── HttpResponse.kt     # HTTP response handling
│   │   ├── HttpMethod.kt       # HTTP method enums
│   │   ├── Status.kt           # HTTP status codes
│   │   └── ClientThread.kt     # Client connection handling
│   └── test/kotlin/com/github/gimlet2/kottpd/
├── pom.xml                     # Maven configuration
└── .github/workflows/          # CI/CD workflows
```

## Coding Standards

### Kotlin Style
- Use idiomatic Kotlin features (data classes, extension functions, lambda syntax)
- Prefer immutability where possible (use `val` over `var`)
- Follow Kotlin naming conventions (camelCase for functions, PascalCase for classes)
- Use functional programming patterns where appropriate
- Leverage Kotlin's null-safety features

### Code Organization
- Keep files focused on a single responsibility
- Place related functionality in the same package
- Use meaningful variable and function names
- Add KDoc comments for public APIs

## Building and Testing

### Build Commands
```bash
# Build the project
./mvnw clean package

# Build without transfer progress (for CI)
./mvnw package --file pom.xml --no-transfer-progress

# Build with specific profile
./mvnw package -P dev
```

### Testing
- Write unit tests using JUnit 4 and kotlin-test-junit
- Place test files in `src/test/kotlin/` mirroring the main source structure
- Test naming: use descriptive test method names that explain what is being tested
- Run tests with: `./mvnw test`

## API Design Patterns

### Server Configuration
The Server class uses a builder-like pattern with method chaining:
```kotlin
Server().apply {
    staticFiles("/public")
    get("/path") { req, res -> res.send("response") }
    post("/path") { req, res -> "response" }
    before("/path") { req, res -> /* filter */ }
    after("/path") { req, res -> /* filter */ }
    exception(ExceptionClass::class) { req, res -> "error response" }
}.start()
```

### Request Handlers
- Handlers are lambdas: `(HttpRequest, HttpResponse) -> Any`
- Can explicitly send via `res.send()` or return a value
- Support for regex patterns in routes: `/do/.*/smth`
- Filters (before/after) can be global or path-specific

## Release Process
- The project publishes to Maven Central (ossrh) and GitHub Packages
- Uses semantic versioning
- Release configuration is in `pom.xml` profiles: `oss-sonatype` and `github`
- Artifacts are signed with GPG during release

## Dependencies
- **kotlin-stdlib**: Core Kotlin standard library
- **slf4j-simple**: Logging framework
- **kotlin-test-junit**: Testing framework
- **junit**: Unit testing

When adding new dependencies:
1. Add to `pom.xml` in the `<dependencies>` section
2. Specify version in `<properties>` if it will be reused
3. Use `<scope>test</scope>` for test-only dependencies
4. Keep dependencies minimal and well-justified

## CI/CD
- GitHub Actions workflow runs on pull requests
- Build workflow: `.github/workflows/build.yml`
- Runs `./mvnw package --file pom.xml --no-transfer-progress`
- Must pass before merging

## Important Notes
- The framework supports both HTTP and HTTPS (with keystore configuration)
- Default port is 9000 (configurable via `server.port` system property)
- Uses a cached thread pool for handling concurrent requests
- Routes can use regular expressions for flexible path matching
- Includes support for static file serving

## Making Changes
1. Ensure changes maintain backward compatibility where possible
2. Update README.md if adding new features or changing APIs
3. Keep the public API simple and intuitive
4. Test with both the maven wrapper (`./mvnw`) locally before committing
5. Follow the existing code style and patterns in the codebase
