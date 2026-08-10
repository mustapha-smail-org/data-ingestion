# Contributing

## Running CI checks locally

### Maven verify (tests + coverage check)

```bash
./mvnw clean verify
```

Coverage threshold is enforced by the JaCoCo `check` goal (currently 70% line
coverage, see `jacoco.line.coverage.minimum` in `pom.xml`). CI passes the same
value via `-Djacoco.line.coverage.minimum=<ratio>`.

### SonarQube analysis

Requires a token from SonarQube Cloud (`https://sonarcloud.io`):

```bash
./mvnw sonar:sonar \
  -Dsonar.projectKey=data-ingestion \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=$SONAR_TOKEN
```

### Docker build

```bash
docker build -t ghcr.io/mustapha-smail-org/data-ingestion:local .
```

## CI/CD pipeline

This repo's workflows (`.github/workflows/pr.yml`, `main.yml`, `release.yml`) are
thin wrappers around reusable templates defined in
[`deployment-workflows`](https://github.com/mustapha-smail-org/deployment-workflows).
See that repo's `docs/TEMPLATE_GUIDE.md` for how the pipeline works end to end, and
`docs/WORKFLOW_CONTRACTS.md` for the exact inputs/outputs of each template.

- **Pull request:** runs `mvnw verify` + Sonar quality gate. No image is built.
- **Merge to `main`:** re-runs verification, builds and pushes a container image
  tagged `sha-<commit>` to GHCR, then dispatches a `dev` deployment to
  [`data-ingestion-cd`](https://github.com/mustapha-smail-org/data-ingestion-cd).
- **Semantic tag (`vX.Y.Z`):** does **not** rebuild. It resolves the image already
  built for that commit and re-tags it, then dispatches a `staging` promotion to
  `data-ingestion-cd`. Production promotion happens via a reviewed PR in that repo.

## Release process

1. Ensure all checks pass on `main`.
2. Create and push a semantic version tag:
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0"
   git push origin v1.0.0
   ```
3. Watch the `Release` workflow run — it re-tags the existing image and triggers
   staging deployment.
4. Promote to production by reviewing/merging the generated PR in
   `data-ingestion-cd`.
