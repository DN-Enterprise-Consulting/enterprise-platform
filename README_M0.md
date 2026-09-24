# M0 – enterprise-platform module skeleton

This package contains the missing Maven module skeletons for the existing
`enterprise-platform` root POM.

## Modules

- platform-bom
- shared-kernel
- assessment
- knowledge
- rules
- publishing
- workflow
- identity
- search
- web-api
- bootstrap

## Intent

M0 establishes the Maven reactor structure only. No business logic or
framework dependencies are introduced at this stage.

## Integration

Copy the module directories into the existing repository branch:

`feature/M0-running-skeleton`

Do not replace the existing root `pom.xml`.

Then run:

```bash
mvn -B verify
```

The next milestone is M1 – Platform Foundation.
