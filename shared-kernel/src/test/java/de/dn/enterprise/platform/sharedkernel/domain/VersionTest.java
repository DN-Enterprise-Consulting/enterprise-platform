package de.dn.enterprise.platform.sharedkernel.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionTest {
    @Test
    void parsesSemanticVersion() {
        Version version = Version.parse("1.2.3");

        assertThat(version).isEqualTo(new Version(1, 2, 3));
        assertThat(version.toString()).isEqualTo("1.2.3");
    }

    @Test
    void rejectsInvalidVersion() {
        assertThatThrownBy(() -> Version.parse("1.2"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
