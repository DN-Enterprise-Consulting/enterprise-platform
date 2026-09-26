package de.dn.enterprise.platform.identity.query;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.inmemory.InMemoryIdentityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IdentityQueryServiceTest {

    private InMemoryIdentityRepository repository;
    private IdentityQueryService queryService;
    private Identity userActive;
    private Identity serviceActive;
    private Identity userLocked;

    @BeforeEach
    void setUp() {
        repository = new InMemoryIdentityRepository();
        queryService = new IdentityQueryService(repository);

        userActive = identity(
                IdentityType.USER, IdentityStatus.ACTIVE, "User Active");
        serviceActive = identity(
                IdentityType.SERVICE, IdentityStatus.ACTIVE, "Service Active");
        userLocked = identity(
                IdentityType.USER, IdentityStatus.LOCKED, "User Locked");

        repository.save(userActive);
        repository.save(serviceActive);
        repository.save(userLocked);
    }

    @Test
    void findsAll() {
        assertThat(queryService.findAll())
                .containsExactlyElementsOf(repository.findAll());
    }

    @Test
    void filtersByType() {
        assertThat(queryService.find(IdentityQuery.byType(IdentityType.USER)))
                .containsExactlyElementsOf(expected(userActive, userLocked));
    }

    @Test
    void filtersByStatus() {
        assertThat(queryService.find(
                IdentityQuery.byStatus(IdentityStatus.ACTIVE)))
                .containsExactlyElementsOf(expected(userActive, serviceActive));
    }

    @Test
    void combinesTypeAndStatusWithAndSemantics() {
        assertThat(queryService.find(
                IdentityQuery.byTypeAndStatus(
                        IdentityType.USER,
                        IdentityStatus.LOCKED)))
                .containsExactly(userLocked);
    }

    @Test
    void returnsEmptyForNoMatch() {
        assertThat(queryService.find(
                IdentityQuery.byTypeAndStatus(
                        IdentityType.SERVICE,
                        IdentityStatus.DISABLED)))
                .isEmpty();
    }

    @Test
    void queryFactoriesExposeExpectedFilters() {
        assertThat(IdentityQuery.all().type()).isEmpty();
        assertThat(IdentityQuery.all().status()).isEmpty();
        assertThat(IdentityQuery.byType(IdentityType.USER).type())
                .contains(IdentityType.USER);
        assertThat(IdentityQuery.byStatus(IdentityStatus.LOCKED).status())
                .contains(IdentityStatus.LOCKED);
    }

    private static List<Identity> expected(Identity... identities) {
        return List.of(identities).stream()
                .sorted(Comparator.comparing(identity -> identity.id().value()))
                .toList();
    }

    private static Identity identity(
            IdentityType type,
            IdentityStatus status,
            String name) {
        return new Identity(
                de.dn.enterprise.platform.identity.domain.IdentityId.random(),
                type,
                status,
                IdentityMetadata.of(name, "Query test identity"));
    }
}
