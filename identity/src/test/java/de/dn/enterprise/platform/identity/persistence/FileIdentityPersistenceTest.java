package de.dn.enterprise.platform.identity.persistence;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileIdentityPersistenceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndLoadsIdentityRoundTrip() {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        Identity identity = identity();

        persistence.save(identity);

        assertThat(persistence.load(identity.id())).isEqualTo(identity);
        assertThat(persistence.exists(identity.id())).isTrue();
    }

    @Test
    void createsStorageDirectoryWhenSaving() throws Exception {
        Path storage = tempDirectory.resolve("nested").resolve("identities");
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(storage);

        persistence.save(identity());

        assertThat(Files.isDirectory(storage)).isTrue();
        try (var files = Files.list(storage)) {
            assertThat(files.count()).isEqualTo(1);
        }
    }

    @Test
    void returnsNullForMissingIdentity() {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        assertThat(persistence.load(IdentityId.random())).isNull();
    }

    @Test
    void deletesIdentity() {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        Identity identity = identity();
        persistence.save(identity);

        persistence.delete(identity.id());

        assertThat(persistence.exists(identity.id())).isFalse();
        assertThat(persistence.load(identity.id())).isNull();
    }

    @Test
    void deleteMissingIdentityIsNoOp() {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        persistence.delete(IdentityId.random());
    }

    @Test
    void saveReplacesExistingIdentityWithSameId() {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        Identity first = identity();
        Identity second = new Identity(
                first.id(),
                first.type(),
                IdentityStatus.LOCKED,
                IdentityMetadata.of("Updated", "Updated description"));

        persistence.save(first);
        persistence.save(second);

        assertThat(persistence.load(first.id())).isEqualTo(second);
    }

    @Test
    void preservesUtf8Metadata() {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        Identity identity = Identity.create(
                IdentityType.USER,
                IdentityMetadata.of(
                        "ÄÖÜ – Benutzer",
                        "Beschreibung mit UTF-8: äöü €"));

        persistence.save(identity);

        assertThat(persistence.load(identity.id())).isEqualTo(identity);
    }

    @Test
    void storesFreeFormValuesEncoded() throws Exception {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        Identity identity = Identity.create(
                IdentityType.SERVICE,
                IdentityMetadata.of(
                        "ÄÖÜ",
                        "Beschreibung mit Zeile\nund Unicode €"));

        persistence.save(identity);

        Path file;
        try (var files = Files.list(tempDirectory)) {
            file = files.findFirst().orElseThrow();
        }

        String raw = Files.readString(file, StandardCharsets.UTF_8);

        assertThat(raw).doesNotContain("ÄÖÜ");
        assertThat(raw).doesNotContain("Beschreibung");
    }

    @Test
    void malformedFileIsRejected() throws Exception {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        IdentityId id = IdentityId.random();
        Path file = tempDirectory.resolve(id.value() + ".identity");
        Files.writeString(file, "invalid\ndata\n", StandardCharsets.UTF_8);

        assertThatThrownBy(() -> persistence.load(id))
                .isInstanceOf(IdentityPersistenceException.class);
    }

    @Test
    void rejectsNullArguments() {
        FileIdentityPersistence persistence =
                new FileIdentityPersistence(tempDirectory);

        assertThatThrownBy(() -> persistence.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.load(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.exists(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.delete(null))
                .isInstanceOf(NullPointerException.class);
    }

    private static Identity identity() {
        return Identity.create(
                IdentityType.SERVICE,
                IdentityMetadata.of(
                        "Execution Service",
                        "Service identity for platform execution"));
    }
}
