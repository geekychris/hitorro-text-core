# Hitorro Text Core

Standalone text processing library extracted from the Hitorro platform.

## Overview

**hitorro-text-core** provides comprehensive text processing, natural language processing, and document analysis capabilities as a standalone Maven project.

## Features

- **Text Analysis** - Lucene-based indexing, tokenization, and search
- **NLP** - Sentence segmentation, POS tagging, named entity recognition
- **Document Processing** - Extract text from Office docs, PDFs, emails
- **Language Processing** - Language detection, stemming, lemmatization
- **Wikipedia** - Parse and process Wikipedia/MediaWiki markup
- **WordNet** - Semantic analysis and word relationships

## Building

```bash
mvn clean install
```

## Usage

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.hitorro</groupId>
    <artifactId>hitorro-text-core</artifactId>
    <version>3.0.0</version>
</dependency>
```

## Requirements

- Java 19+
- Maven 3.6+

## License

MIT License - Copyright (c) 2006-2025 Chris Collins

See [LICENSE](LICENSE) for details.
