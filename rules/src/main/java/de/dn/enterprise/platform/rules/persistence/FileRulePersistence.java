package de.dn.enterprise.platform.rules.persistence;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import de.dn.enterprise.platform.rules.spi.RulePersistence;

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
import java.util.Optional;
import java.util.UUID;

public final class FileRulePersistence implements RulePersistence {

    private static final String EXTENSION = ".rule";

    private final Path rootDirectory;

    public FileRulePersistence(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory, "rootDirectory must not be null");
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException e) {
            throw new RulePersistenceException("Could not create persistence directory: " + rootDirectory, e);
        }
    }

    @Override
    public Rule save(Rule rule) {
        Objects.requireNonNull(rule, "rule must not be null");

        List<String> lines = new ArrayList<>();
        lines.add(encode(rule.id().value().toString()));
        lines.add(rule.type().name());
        lines.add(rule.status().name());
        lines.add(encode(rule.metadata().name()));
        lines.add(encode(rule.metadata().description()));
        lines.add(encode(rule.definition()));

        rule.metadata().attributes().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry ->
                        lines.add(encode(entry.getKey()) + ":" + encode(entry.getValue())));

        try {
            Files.write(fileFor(rule.id()), lines, StandardCharsets.UTF_8);
            return rule;
        } catch (IOException e) {
            throw new RulePersistenceException("Could not save rule: " + rule.id(), e);
        }
    }

    @Override
    public Optional<Rule> load(RuleId id) {
        Objects.requireNonNull(id, "id must not be null");

        Path source = fileFor(id);
        if (!Files.exists(source)) {
            return Optional.empty();
        }

        try {
            return Optional.of(read(source));
        } catch (IllegalArgumentException e) {
            throw new RulePersistenceException("Invalid rule data in: " + source, e);
        } catch (IOException e) {
            throw new RulePersistenceException("Could not load rule: " + id, e);
        }
    }

    public List<Rule> loadAll() {
        try {
            if (!Files.exists(rootDirectory)) {
                return List.of();
            }

            List<Rule> rules = new ArrayList<>();
            try (var stream = Files.list(rootDirectory)) {
                stream.filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                        .sorted()
                        .forEach(path -> {
                            try {
                                rules.add(read(path));
                            } catch (IOException | IllegalArgumentException e) {
                                throw new RulePersistenceException(
                                        "Could not load rule file: " + path, e);
                            }
                        });
            }
            return List.copyOf(rules);
        } catch (IOException e) {
            throw new RulePersistenceException("Could not list rules: " + rootDirectory, e);
        }
    }

    @Override
    public boolean exists(RuleId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Files.exists(fileFor(id));
    }

    @Override
    public void delete(RuleId id) {
        Objects.requireNonNull(id, "id must not be null");
        try {
            Files.deleteIfExists(fileFor(id));
        } catch (IOException e) {
            throw new RulePersistenceException("Could not delete rule: " + id, e);
        }
    }

    private Rule read(Path source) throws IOException {
        List<String> lines = Files.readAllLines(source, StandardCharsets.UTF_8);
        if (lines.size() < 6) {
            throw new RulePersistenceException("Invalid rule file: " + source);
        }

        RuleId storedId = RuleId.of(UUID.fromString(decode(lines.get(0))));
        RuleType type = RuleType.valueOf(lines.get(1));
        RuleStatus status = RuleStatus.valueOf(lines.get(2));
        String name = decode(lines.get(3));
        String description = decode(lines.get(4));
        String definition = decode(lines.get(5));

        Map<String, String> attributes = new LinkedHashMap<>();
        for (int i = 6; i < lines.size(); i++) {
            String line = lines.get(i);
            int separator = line.indexOf(':');
            if (separator <= 0) {
                throw new RulePersistenceException("Invalid attribute entry in: " + source);
            }
            attributes.put(
                    decode(line.substring(0, separator)),
                    decode(line.substring(separator + 1)));
        }

        RuleMetadata metadata = new RuleMetadata(name, description, attributes);
        return new Rule(storedId, type, status, metadata, definition);
    }

    private Path fileFor(RuleId id) {
        return rootDirectory.resolve(id.value() + EXTENSION);
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
