# Contributing to Kottpd

First off, thank you for considering contributing to Kottpd! It's people like you that make Kottpd such a great tool.

## Code of Conduct

This project and everyone participating in it is governed by our commitment to being respectful and professional. By participating, you are expected to uphold this standard.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* **Use a clear and descriptive title**
* **Describe the exact steps to reproduce the problem**
* **Provide specific examples** to demonstrate the steps
* **Describe the behavior you observed** and what behavior you expected
* **Include code samples and stack traces** if applicable
* **Specify the Kotlin version** and operating system you're using

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **Use a clear and descriptive title**
* **Provide a detailed description of the suggested enhancement**
* **Provide specific examples** to demonstrate the use case
* **Explain why this enhancement would be useful** to most Kottpd users
* **List any alternative solutions** you've considered

### Pull Requests

The process described here has several goals:

- Maintain Kottpd's quality
- Fix problems that are important to users
- Engage the community in working toward the best possible Kottpd
- Enable a sustainable system for Kottpd's maintainers to review contributions

Please follow these steps to have your contribution considered by the maintainers:

1. **Fork the repository** and create your branch from `main`
2. **Make your changes** following the coding standards below
3. **Add tests** for any new functionality
4. **Update documentation** including README.md if needed
5. **Ensure all tests pass** by running `./mvnw clean test`
6. **Update the CHANGELOG.md** if applicable
7. **Write a good commit message**
8. **Submit a pull request**

## Development Setup

### Prerequisites

* JDK 11 or higher
* Maven 3.6+
* Git

### Building the Project

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/kottpd.git
cd kottpd

# Build the project
./mvnw clean package

# Run tests
./mvnw test
```

### Running Examples

Create a simple test file to run the server:

```kotlin
import com.github.gimlet2.kottpd.Server

fun main() {
    val server = Server(9000)
    server.get("/hello") { _, _ -> "Hello, World!" }
    server.start()
    println("Server running on http://localhost:9000")
    Thread.sleep(Long.MAX_VALUE) // Keep server running
}
```

## Coding Standards

### Kotlin Style Guide

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

* Use 4 spaces for indentation
* Use camelCase for functions and variables
* Use PascalCase for classes
* Keep lines under 120 characters when possible
* Add KDoc comments for public APIs

### Code Quality

* Write clean, readable code with meaningful names
* Keep functions small and focused (ideally <20 lines)
* Avoid code duplication (DRY principle)
* Handle errors appropriately
* Write comprehensive tests for new features

### Testing

* Write unit tests for all new functionality
* Use descriptive test names that explain what is being tested
* Follow the Arrange-Act-Assert pattern
* Aim for >80% code coverage for new code
* Test edge cases and error conditions

Example test:

```kotlin
@Test
fun `should return 404 for non-existent route`() {
    // Arrange
    val server = Server(9001)
    
    // Act
    val response = makeRequest("GET", "http://localhost:9001/nonexistent")
    
    // Assert
    assertEquals(404, response.statusCode)
}
```

### Documentation

* Add KDoc comments for all public classes, functions, and properties
* Include usage examples in documentation
* Update README.md for user-facing changes
* Update ROADMAP.md if adding planned features

## Git Workflow

### Branch Naming

* `feature/description` - for new features
* `fix/description` - for bug fixes
* `docs/description` - for documentation changes
* `refactor/description` - for code refactoring
* `test/description` - for adding tests

### Commit Messages

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>: <description>

[optional body]

[optional footer]
```

Types:
* `feat`: A new feature
* `fix`: A bug fix
* `docs`: Documentation changes
* `style`: Code style changes (formatting, etc.)
* `refactor`: Code refactoring
* `test`: Adding or updating tests
* `chore`: Maintenance tasks

Examples:

```
feat: add CORS support for cross-origin requests

Add middleware to handle CORS headers and preflight requests.
Configurable via CorsConfig class.

Closes #42
```

```
fix: prevent null pointer exception in static file handling

Check if resource exists before attempting to read.
Add test case for missing static files.
```

## Release Process

Releases are managed by maintainers. The process is:

1. Update version in `pom.xml`
2. Update `CHANGELOG.md`
3. Create and push a git tag
4. GitHub Actions will handle the Maven Central release

## Getting Help

* Check the [README.md](README.md) for basic usage
* Check the [ROADMAP.md](ROADMAP.md) for planned features
* Search existing [Issues](https://github.com/gimlet2/kottpd/issues)
* Ask questions in [Discussions](https://github.com/gimlet2/kottpd/discussions)

## Recognition

Contributors will be recognized in:
* The project README.md
* Release notes
* The project's contributors page

Thank you for contributing to Kottpd! 🎉
