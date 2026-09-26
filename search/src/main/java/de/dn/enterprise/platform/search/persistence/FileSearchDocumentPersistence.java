package de.dn.enterprise.platform.search.persistence;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.spi.SearchDocumentPersistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * File-system based implementation of the SearchDocumentPersistence SPI.
 *
 * <p>Free-form text is Base64 encoded so UTF-8 content and embedded newlines
 * remain safe inside the line-oriented file format.</p>
 */
public final class FileSearchDocumentPersistence implements SearchDocumentPersistence {

    private static final String FILE_EXTENSION = ".searchdocument";

    private final Path rootDirectory;

    public FileSearchDocumentPersistence(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(
                rootDirectory, "rootDirectory must not be null");
    }

    @Override
    public void save(SearchDocument document) {
        Objects.requireNonNull(document, "document must not be null");

        try {
            Files.createDirectories(rootDirectory);
            Files.write(
                    fileFor(document.id()),
                    serialize(document),
                    StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new SearchDocumentPersistenceException(
                    "Could not save search document " + document.id(), exception);
        }
    }

    @Override
    public SearchDocument load(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");

        Path file = fileFor(id);
        if (!Files.exists(file)) {
            return null;
        }

        try {
            return deserialize(
                    Files.readAllLines(file, StandardCharsets.UTF_8),
                    file);
        } catch (IOException exception) {
            throw new SearchDocumentPersistenceException(
                    "Could not load search document " + id, exception);
        }
    }

    @Override
    public List<SearchDocument> loadAll() {
        if (!Files.exists(rootDirectory)) {
            return List.of();
        }

        try (var files = Files.list(rootDirectory)) {
            return files
                    .filter(path -> path.getFileName().toString().endsWith(FILE_EXTENSION))
                    .sorted()
                    .map(this::loadFromFile)
                    .toList();
        } catch (IOException exception) {
            throw new SearchDocumentPersistenceException(
                    "Could not load search documents from " + rootDirectory, exception);
        }
    }

    private SearchDocument loadFromFile(Path file) {
        try {
            return deserialize(Files.readAllLines(file, StandardCharsets.UTF_8), file);
        } catch (IOException exception) {
            throw new SearchDocumentPersistenceException(
                    "Could not load search document file " + file, exception);
        }
    }

    @Override
    public boolean exists(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Files.exists(fileFor(id));
    }

    @Override
    public void delete(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");

        try {
            Files.deleteIfExists(fileFor(id));
        } catch (IOException exception) {
            throw new SearchDocumentPersistenceException(
                    "Could not delete search document " + id, exception);
        }
    }

    private Path fileFor(SearchDocumentId id) {
        return rootDirectory.resolve(id.value() + FILE_EXTENSION);
    }

    private List<String> serialize(SearchDocument document) {
        List<String> lines = new ArrayList<>();
        lines.add(encode(document.id().value().toString()));
        lines.add(document.type().name());
        lines.add(document.status().name());
        lines.add(encode(document.metadata().name()));
        lines.add(encode(document.metadata().description()));
        lines.add(encode(document.content()));

        document.attributes().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry ->
                        lines.add(encode(entry.getKey()) + ":" + encode(entry.getValue())));

        return lines;
    }

    private SearchDocument deserialize(List<String> lines, Path source) {
        if (lines.size() < 6) {
            throw new SearchDocumentPersistenceException(
                    "Invalid search document file: expected at least 6 lines: " + source);
        }

        try {
            SearchDocumentId id = SearchDocumentId.of(decode(lines.get(0)));
            SearchDocumentType type = SearchDocumentType.valueOf(lines.get(1));
            SearchDocumentStatus status = SearchDocumentStatus.valueOf(lines.get(2));
            String name = decode(lines.get(3));
            String description = decode(lines.get(4));
            String content = decode(lines.get(5));

            Map<String, String> attributes = new LinkedHashMap<>();
            for (int i = 6; i < lines.size(); i++) {
                String line = lines.get(i);
                int separator = line.indexOf(':');
                if (separator <= 0) {
                    throw new SearchDocumentPersistenceException(
                            "Invalid attribute entry in: " + source);
                }
                attributes.put(
                        decode(line.substring(0, separator)),
                        decode(line.substring(separator + 1)));
            }

            return new SearchDocument(
                    id,
                    type,
                    status,
                    SearchDocumentMetadata.of(name, description),
                    content,
                    attributes);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new SearchDocumentPersistenceException(
                    "Invalid search document data in: " + source, exception);
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8);
    }
}
