# Kottpd Examples

This directory contains example applications demonstrating various features of Kottpd.

## Examples

### 1. Hello World (hello-world.kt)
Basic server setup with a simple GET endpoint.

```bash
kotlinc hello-world.kt -include-runtime -d hello-world.jar
java -jar hello-world.jar
```

### 2. REST API (rest-api.kt)
Example REST API with CRUD operations.

### 3. Static Files (static-server.kt)
Serving static HTML, CSS, and JavaScript files.

### 4. Authentication (auth-example.kt)
Simple authentication using before filters.

### 5. HTTPS Server (https-server.kt)
Secure server with SSL/TLS configuration.

## Running Examples

1. Ensure you have Kottpd in your classpath
2. Compile the Kotlin file
3. Run the resulting JAR or class file

For development, you can use the Maven dev profile:

```bash
cd kottpd-root
./mvnw clean package -Pdev
# Add example to src/main/kotlin and set as main class
```

## Contributing Examples

We welcome new examples! Please ensure:
- Code is well-commented
- Demonstrates a specific feature or use case
- Includes a brief description
- Follows Kotlin coding conventions
