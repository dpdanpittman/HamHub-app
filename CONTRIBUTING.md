# Contributing to HamHub

Thank you for your interest in contributing to HamHub! This document provides guidelines and information for contributors.

## License Agreement

By contributing to HamHub, you agree that your contributions will be licensed under the [CC BY-NC-SA 4.0](LICENSE) license. This means:

- Your contributions cannot be used for commercial purposes
- Derivative works must use the same license
- Attribution must be given to the original project

## Getting Started

### Prerequisites

- Android Studio (Hedgehog 2023.1.1 or newer)
- JDK 17
- Android SDK with API 34
- Git

### Setting Up the Development Environment

1. Fork the repository on GitHub
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/hamhub-app.git
   cd hamhub-app
   ```
3. Open the project in Android Studio
4. Let Gradle sync complete
5. Build and run on an emulator or device

## How to Contribute

### Reporting Bugs

Before submitting a bug report:
- Check existing issues to avoid duplicates
- Test with the latest version

When submitting a bug report, include:
- Android version and device model
- Steps to reproduce the issue
- Expected vs actual behavior
- Screenshots or logs if applicable

Use the **Bug Report** issue template.

### Suggesting Features

Feature suggestions are welcome! Please:
- Check existing issues and discussions first
- Explain the use case and why it would benefit ham radio operators
- Consider how it fits with the app's offline-first philosophy

Use the **Feature Request** issue template.

### Submitting Code

1. **Create an issue first** - Discuss your proposed changes before starting work
2. **Fork and branch** - Create a feature branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Follow the code style** - Match existing patterns in the codebase
4. **Write tests** - Add unit tests for new functionality
5. **Test thoroughly** - Ensure the app builds and tests pass:
   ```bash
   ./gradlew assembleDebug testDebugUnitTest lintDebug
   ```
6. **Commit with clear messages** - Use descriptive commit messages
7. **Submit a pull request** - Reference the related issue

## Code Style

### Kotlin

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Prefer immutability (`val` over `var`)
- Use data classes for models
- Handle nullability explicitly

### Compose

- Keep composables small and focused
- Extract reusable components to `ui/components/`
- Use `remember` and `derivedStateOf` appropriately
- Follow Material 3 guidelines

### Architecture

- **UI Layer**: Compose screens and ViewModels
- **Domain Layer**: Use cases and domain models
- **Data Layer**: Repositories, Room DAOs, Retrofit APIs

Keep dependencies flowing inward (UI -> Domain -> Data).

## Project Structure

```
app/src/main/java/com/hamhub/app/
├── data/           # Data sources and repositories
├── domain/         # Business logic and models
├── ui/             # Compose UI and ViewModels
└── di/             # Dependency injection modules
```

## Testing

- Write unit tests for ViewModels, repositories, and use cases
- Place tests in `app/src/test/`
- Use descriptive test names: `should_returnError_when_callsignIsInvalid`
- Mock dependencies using MockK or similar

Run tests:
```bash
./gradlew testDebugUnitTest
```

## Pull Request Process

1. Ensure all tests pass and there are no lint errors
2. Update documentation if needed
3. Fill out the PR template completely
4. Request review from maintainers
5. Address review feedback promptly
6. Squash commits if requested

## Communication

- Use GitHub Issues for bugs and feature requests
- Be respectful and constructive in all interactions
- Remember that maintainers are volunteers

## Recognition

Contributors will be acknowledged in:
- The GitHub contributors page
- Release notes for significant contributions

## Questions?

If you have questions about contributing, open a Discussion on GitHub or comment on a relevant issue.

73 and happy coding!
