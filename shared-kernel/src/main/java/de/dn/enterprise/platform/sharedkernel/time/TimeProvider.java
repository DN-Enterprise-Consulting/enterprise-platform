package de.dn.enterprise.platform.sharedkernel.time;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/** Small abstraction for deterministic time handling in domain/application code. */
public final class TimeProvider {
    private final Clock clock;

    public TimeProvider(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public static TimeProvider systemUtc() {
        return new TimeProvider(Clock.systemUTC());
    }

    public Instant now() {
        return clock.instant();
    }
}
