package de.dn.enterprise.platform.sharedkernel.domain;

import java.util.Objects;

/** Minimal semantic version value object. */
public record Version(int major, int minor, int patch) {
    public Version {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version components must not be negative");
        }
    }

    public static Version of(int major, int minor, int patch) {
        return new Version(major, minor, patch);
    }

    public static Version parse(String value) {
        Objects.requireNonNull(value, "value must not be null");
        String[] parts = value.trim().split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Version must have the form major.minor.patch");
        }
        try {
            return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Version must contain numeric components", ex);
        }
    }

    @Override
    public String toString() {
        return "%d.%d.%d".formatted(major, minor, patch);
    }
}
