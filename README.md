# UniDiffStatic

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25%2B-orange.svg)](https://www.oracle.com/java/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.moriline/UniDiffStatic.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.moriline/UniDiffStatic)
[![Build Status](https://img.shields.io/badge/Build-Gradle-green.svg)](https://gradle.org/)

A **zero-dependency** Java library for computing text diffs and applying patches using the **Myers greedy difference algorithm**. Produces standard **unified diff** format output — the same format as `git diff` and `diff -u`.

## ✨ Features

- **Myers Algorithm** — O(N·D) time complexity for optimal diff output
- **Unified Diff Format** — produces standard `@@ -X,Y +A,B @@` hunks compatible with standard diff tools
- **Round-Trip Guarantee** — `patch(original, diff(original, target))` always equals `target`
- **Custom Delimiters** — split on any delimiter, not just newlines
- **Configurable Context** — control the number of context lines around changes
- **Zero External Dependencies** — self-contained, lightweight implementation
- **Modern Java** — built with Java 25, records, sealed interfaces, and pattern matching

## 📦 Quick Start

### Requirements

- Java 25 or later
- Gradle 9+ (for building from source)

### Add to Your Project

**Maven Central:** [io.github.moriline:UniDiffStatic:1.0.0](https://central.sonatype.com/artifact/io.github.moriline/UniDiffStatic) · [POM](https://repo1.maven.org/maven2/io/github/moriline/UniDiffStatic/1.0.0/UniDiffStatic-1.0.0.pom)

#### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.moriline:UniDiffStatic:1.0.0")
}
```

#### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.moriline:UniDiffStatic:1.0.0'
}
```

#### Maven

```xml
<dependency>
    <groupId>io.github.moriline</groupId>
    <artifactId>UniDiffStatic</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage

#### Compute a Diff

```java
import org.unidiffstatic.UniDiffStatic;

String source = "hello\nworld";
String target = "hello\nuniverse";

// Generate unified diff
String diff = UniDiffStatic.diff(source, target);

System.out.println(diff);
// --- source
// +++ target
// @@ -1,2 +1,2 @@
//  hello
// -world
// +universe
```

#### Apply a Patch

```java
String original = "hello\nworld";
String diff = /* unified diff string */;

// Apply the diff to get the revised text
String result = UniDiffStatic.patch(original, diff);
// result == "hello\nuniverse"
```

#### Reverse a Patch

```java
String revised = "hello\nuniverse";
String diff = /* unified diff string */;

// Restore the original text
String original = UniDiffStatic.unpatch(revised, diff);
// original == "hello\nworld"
```

## 📖 API Reference

### `diff()` — Generate Unified Diff

| Method | Description |
|--------|-------------|
| `diff(String source, String target)` | Default: `\n` delimiter, 3 context lines |
| `diff(String source, String target, String delimiter)` | Custom delimiter |
| `diff(String source, String target, String sourceName, String targetName, int contextSize)` | Full control |
| `diff(String source, String target, String sourceName, String targetName, int contextSize, String delimiter)` | Most complete |

**Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `source` | `String` | — | Original text |
| `target` | `String` | — | Revised text |
| `sourceName` | `String` | `"source"` | Name shown in `---` header |
| `targetName` | `String` | `"target"` | Name shown in `+++` header |
| `contextSize` | `int` | `3` | Number of context lines around changes |
| `delimiter` | `String` | `"\n"` | Line separator for splitting text |

**Return:** Unified diff string. Returns empty string (`""`) when texts are identical.

### `patch()` — Apply a Unified Diff

```java
String patch(String original, String uniDiff) throws PatchFailedException
String patch(String original, String uniDiff, String delimiter) throws PatchFailedException
```

Applies the given unified diff to the original text.

**Throws:** `PatchFailedException` if the patch cannot be applied.

### `unpatch()` — Reverse a Unified Diff

```java
String unpatch(String revised, String uniDiff)
String unpatch(String revised, String uniDiff, String delimiter)
```

Inverse of `patch()` — restores the original text from the revised text and the unified diff.

## 🔧 Advanced Usage

### Custom Delimiter

```java
String source = "hello|world";
String target = "hello|universe";

String diff = UniDiffStatic.diff(source, target, "|");
String result = UniDiffStatic.patch(source, diff, "|");
```

### Custom Context Size

```java
// 10 context lines instead of default 3
String diff = UniDiffStatic.diff(source, target, "original.txt", "revised.txt", 10);
```

### Full Control

```java
String diff = UniDiffStatic.diff(
    source,
    target,
    "a/file.txt",     // source name (shown in --- header)
    "b/file.txt",     // target name (shown in +++ header)
    5,                // context lines
    "\n"              // delimiter
);
```

## 🏗️ Architecture

The library is a **single-file implementation** (~630 lines) with no external dependencies:

```
src/main/java/org/unidiffstatic/
├── UniDiffStatic.java          # Core API + algorithm + unified diff writer
├── algorithm/
│   ├── MyersDiff.java          # Myers greedy difference algorithm
│   ├── Change.java             # Represents a single change in the diff
│   └── DiffAlgorithmListener.java  # Callback interface for algorithm events
└── patch/
    ├── Patch.java              # Patch application engine
    ├── PatchFailedException.java
    ├── Delta.java              # Sealed hierarchy: ChangeDelta, DeleteDelta, InsertDelta, EqualDelta
    ├── Chunk.java              # Represents a chunk of text in a delta
    └── AbstractDelta.java      # Base class for delta types
```

### Algorithm

- **Myers greedy diff** — O(N·D) complexity where N = text length, D = number of changes
- **Line-level** — splits text by delimiter, then computes diff per line
- **Merge adjacent changes** — groups consecutive INSERT/DELETE pairs for clean output

## 🧪 Testing

The library is thoroughly tested with **115+ unit tests** covering:

| Category | Tests | Description |
|----------|-------|-------------|
| Core diff functionality | 16 | Basic diff generation |
| Patch/unpatch operations | 15 | Apply and reverse diffs |
| File-based tests | 10 | Real-world file diffs |
| Algorithm unit tests | 12 | Myers algorithm correctness |
| Unified diff parsing/writing | 8 | Format compliance |
| Text edge cases | 33 | Empty strings, special chars, etc. |
| Patch application | 14 | Various patch scenarios |
| Usage examples | 7 | API usage patterns |

### Run Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "org.unidiffstatic.UniDiffStaticTest"

# Run tests with verbose output
./gradlew test --info
```

## 📋 Round-Trip Guarantee

The library guarantees that applying a diff and then reversing it will restore the original text:

```java
String source = "original text";
String target = "modified text";

String diff = UniDiffStatic.diff(source, target);
String patched = UniDiffStatic.patch(source, diff);

assert patched.equals(target) : "Round-trip failed";
```

## 📝 Unified Diff Format Output

The library produces standard unified diff format:

```diff
--- source
+++ target
@@ -1,3 +1,3 @@
 hello
-world
+universe
 foo
```

This format is compatible with standard tools like `git apply`, `patch`, and other unified diff parsers.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the **Apache License 2.0** — see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Based on the excellent work of [java-diff-utils](https://github.com/java-diff-utils/java-diff-utils) by Wolfgang Bernhardt
- Myers algorithm from Eugene W. Myers, "An O(ND) Difference Algorithm and Its Variations" (1986)
