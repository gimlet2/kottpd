# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Comprehensive development roadmap (ROADMAP.md)
- Contributing guidelines (CONTRIBUTING.md)
- Security best practices documentation (SECURITY.md)
- Detailed repository review summary (REVIEW.md)
- GitHub issue templates (bug report, feature request, question)
- Pull request template
- Enhanced README with examples and comparisons
- Example applications (hello-world, rest-api, auth-example)
- EditorConfig for consistent code style
- Git attributes file for line ending consistency
- CHANGELOG.md for tracking project changes

### Changed
- Updated GitHub Actions workflows to use latest versions (v4)
- Improved build workflow with artifact uploads and test execution
- Updated Kotlin from 1.9.23 to 2.0.21
- Updated Dokka from 1.6.10 to 1.9.20 (fixes compatibility warnings)
- Updated Maven Surefire plugin to 3.2.5
- Updated Maven Source plugin to 3.3.1
- Added Maven compiler source/target properties (Java 11)

### Improved
- Documentation structure and organization
- README examples and clarity
- Build configuration and dependency management
- CI/CD pipeline with better caching

## [0.2.2] - 2024

### Changed
- Updated Maven plugins and dependencies
- Updated org.apache.maven.plugins:maven-assembly-plugin from 3.3.0 to 3.7.1

## [0.2.0] - Previous Release

### Added
- Basic HTTP server functionality
- Support for GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE, CONNECT, PATCH methods
- Regex-based routing
- Before/after filters
- Exception handling
- Static file serving
- HTTPS/TLS support
- HTTP request and response handling

### Dependencies
- Kotlin 1.9.23
- SLF4J 2.0.13
- JUnit 4.13.2

## Legend

- `Added` for new features
- `Changed` for changes in existing functionality
- `Deprecated` for soon-to-be removed features
- `Removed` for now removed features
- `Fixed` for any bug fixes
- `Security` for vulnerability fixes
- `Improved` for improvements to existing features

---

For more details, see the [releases page](https://github.com/gimlet2/kottpd/releases).
