package de.dn.enterprise.platform.identity.query;

import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;

import java.util.Optional;

public record IdentityQuery(
        Optional<IdentityType> type,
        Optional<IdentityStatus> status) {

    public IdentityQuery {
        type = type == null ? Optional.empty() : type;
        status = status == null ? Optional.empty() : status;
    }

    public static IdentityQuery all() {
        return new IdentityQuery(Optional.empty(), Optional.empty());
    }

    public static IdentityQuery byType(IdentityType type) {
        return new IdentityQuery(Optional.of(type), Optional.empty());
    }

    public static IdentityQuery byStatus(IdentityStatus status) {
        return new IdentityQuery(Optional.empty(), Optional.of(status));
    }

    public static IdentityQuery byTypeAndStatus(
            IdentityType type,
            IdentityStatus status) {
        return new IdentityQuery(Optional.of(type), Optional.of(status));
    }
}
