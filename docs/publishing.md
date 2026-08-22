# Publishing to Maven Central

This document describes how menu-action is built, published and released to Maven Central over the
Sonatype Central Portal. The same setup is used in the sibling libraries (model-data,
swing-model-components, ...), so the steps apply there as well.

## Overview

Maven Central is fed through the [Central Portal](https://central.sonatype.com). This project uses
the **OSSRH Staging API** of the Portal, because the gradle `maven-publish` plugin can publish to it
like to a classic nexus repository. The flow of a release is:

```
gradle publish ──► OSSRH staging api ──► open staging repository (invisible in the Portal)
                                              │
                         make central-upload  │  POST /manual/upload/...
                                              ▼
                                   Central Portal deployment
                              (VALIDATING ─► VALIDATED ─► PUBLISHING ─► PUBLISHED)
                                              │
                           click "Publish" in the Portal (user_managed)
                           or publishing_type=automatic
                                              ▼
                                        Maven Central
```

The important detail: after `gradle publish` the artifacts sit in an **open staging repository**
that is **not visible** in the Portal. Only after the *manual upload* call the deployment appears
under [Deployments](https://central.sonatype.com/publishing/deployments). The build contains this
call in the publish workflow and in the `release` target of the Makefile, so normally nothing
has to be done by hand.

Snapshots are different: they go directly to
`https://central.sonatype.com/repository/maven-snapshots/` and need no Portal step.

## Prerequisites

### Central Portal user token

1. Log in to the [Central Portal](https://central.sonatype.com) and open *View Account* →
   *Generate User Token*.
2. The token consists of a username and a password. They are **not** the login credentials.

The token is used in three places:

| Where | Keys |
|---|---|
| GitHub repository secrets | `CENTRAL_PORTAL_TOKEN_USERNAME`, `CENTRAL_PORTAL_TOKEN_PASSWORD` |
| Environment variables (local, CI) | `CENTRAL_USERNAME`, `CENTRAL_PASSWORD` |
| `~/.gradle/gradle.properties` (local) | `centralUsername`, `centralPassword` |

The gradle build reads `CENTRAL_USERNAME`/`CENTRAL_PASSWORD` first and falls back to the gradle
properties. The Makefile does the same for the Portal api calls.

### GPG signing key

Release artifacts must be signed. Two options are supported by `gradle/publishing.gradle`:

- **In-memory key** (used in CI): the environment variables `GPG_PRIVATE_KEY` (ascii-armored
  private key) and `GPG_PASSPHRASE`. Export the key with

  ```
  gpg --armor --export-secret-keys <KEY_ID> > private.asc
  ```

  and store the content of `private.asc` in the GitHub secret `GPG_PRIVATE_KEY`.
- **Local gpg command** (used on the developer machine): if the environment variables are not
  set, gradle uses the `gpg` binary and the gpg-agent. Configure the key in
  `~/.gradle/gradle.properties`:

  ```
  signing.gnupg.keyName=<KEY_ID>
  ```

The public key must be available on a key server (`keys.openpgp.org`, `keyserver.ubuntu.com`),
otherwise the Portal validation fails with *Invalid signature*.

### GitHub repository secrets

| Secret | Purpose |
|---|---|
| `CENTRAL_PORTAL_TOKEN_USERNAME` | Portal user token name |
| `CENTRAL_PORTAL_TOKEN_PASSWORD` | Portal user token password |
| `GPG_PRIVATE_KEY` | ascii-armored private signing key |
| `GPG_PASSPHRASE` | passphrase of the signing key |
| `CODECOV_TOKEN` | optional, coverage upload of the CI workflow |

## Release with GitHub Actions (recommended)

1. Make sure `develop` is green in the
   [CI workflow](https://github.com/astrapi69/menu-action/actions/workflows/gradle.yml).
2. Set the release version in `gradle.properties` and rename the `-SNAPSHOT` section in
   `CHANGELOG.md`:

   ```
   projectVersion=5.0
   ```

3. Commit and tag:

   ```
   git commit -am "prepare major release in version 5.0"
   ./gradlew tagRelease          # creates the annotated tag RELEASE-5.0
   git push origin develop
   git push origin RELEASE-5.0
   ```

4. The tag push triggers the workflow
   [publish.yml](../.github/workflows/publish.yml). It runs `./gradlew build`,
   `./gradlew publish` (signs with the in-memory key) and `make central-upload`.
5. Open [Deployments](https://central.sonatype.com/publishing/deployments). The deployment
   `io.github.astrapi69 (via OSSRH Staging API)` appears with the state `VALIDATED`. Check the
   listed component (`io.github.astrapi69:menu-action:5.0`) and click **Publish**. With
   `PUBLISHING_TYPE=automatic` (see below) this click is not needed.
6. After a few minutes the state is `PUBLISHED`; the artifact is available on Maven Central
   within about 15-30 minutes and on
   [search.maven.org](https://central.sonatype.com/artifact/io.github.astrapi69/menu-action)
   shortly after.
7. Start the next development cycle:

   ```
   projectVersion=5.1-SNAPSHOT
   ```

   add a new `Version 5.1-SNAPSHOT` section to `CHANGELOG.md`, commit with
   `create a new 5.1-SNAPSHOT version for the next development cycle` and push.

The workflow can also be started by hand with *Run workflow* (`workflow_dispatch`) on the
`develop` branch; it then publishes whatever version `gradle.properties` contains, a
`-SNAPSHOT` version goes to the snapshot repository.

## Release from the developer machine

The Makefile target `release` runs the complete sequence:

```
make release
```

which is

```
./gradlew clean publishMavenJavaPublicationToMavenLocal publish tagRelease
make central-upload
```

Afterwards push the branch and the tag and publish the deployment in the Portal as described
above. The credentials come from `~/.gradle/gradle.properties` or the environment variables.

## Publishing type

`make central-upload` uses `PUBLISHING_TYPE=user_managed` by default: the deployment waits in the
Portal for a manual *Publish* or *Drop*. For a fully automatic release use

```
make central-upload PUBLISHING_TYPE=automatic
```

or set `PUBLISHING_TYPE` as environment variable in the workflow step. `user_managed` is the safe
default, because publishing to Maven Central is irreversible and the monthly release count of the
organization is limited (see [Limits](#limits)).

## Managing staging repositories

The OSSRH Staging API keeps one *default repository* per token, client ip address and namespace,
for instance `46IpjV/20.119.41.196/io.github.astrapi69--default-repository`. Every `gradle publish`
of a release version from the same ip goes into that repository. The Makefile wraps the api:

| Target | Description |
|---|---|
| `make central-list` | lists all staging repositories of the namespace with state and Portal deployment id |
| `make central-upload` | uploads the default repository of the **calling ip** to the Portal |
| `make central-upload-repository REPOSITORY_KEY=<key>` | uploads the repository with the given key (use for repositories created by CI runners) |
| `make central-drop REPOSITORY_KEY=<key>` | drops the repository with the given key |

The states are:

| Staging api state | Meaning |
|---|---|
| `open` | artifacts uploaded, **not visible** in the Portal |
| `closed` | uploaded to the Portal, `portal_deployment_id` is set |
| `released` | published to Maven Central |

Portal deployment states: `PENDING`, `VALIDATING`, `VALIDATED` (ready to publish), `PUBLISHING`,
`PUBLISHED`, `FAILED` (see the errors in the Portal, usually missing signature, javadoc or sources
jar, wrong pom metadata or an already existing version).

Because `central-upload` is bound to the calling ip, it must run in the **same job** as
`gradle publish` in CI. A staging repository that was created by a CI runner and not uploaded (for
example because the workflow failed after `publish`) can be uploaded later with
`central-upload-repository` and the key from `central-list`.

## Limits

The Portal limits the number of releases per organization and month. The current usage is shown
on [Usage](https://central.sonatype.com/publishing/usage?org=astrapi69). The staging api reports
an exceeded limit as `warnings` entry in `make central-list`. Therefore:

- collect all changes of a library in **one** release instead of several small ones
- never publish intermediate versions only to test the pipeline; use snapshots for that
- drop stale deployments in the Portal instead of publishing them

## Troubleshooting

**The deployment is not visible in the Portal.** The staging repository is still `open`. Run
`make central-list`, then `make central-upload-repository REPOSITORY_KEY=<key>` for the open
repository of the release.

**`make central-upload` returns curl error 22 (HTTP 4xx).** The default repository of the calling
ip is empty or already uploaded. Check `make central-list`; if the repository shows a
`portal_deployment_id` it was uploaded anyway.

**Portal deployment state `FAILED` with *Invalid signature*.** The public key is not on a key
server or the in-memory key in the secret is not ascii-armored. Re-export the key with
`gpg --armor --export-secret-keys` and upload the public key with
`gpg --keyserver keys.openpgp.org --send-keys <KEY_ID>`.

**`signMavenJavaPublication` fails locally.** Either set `GPG_PRIVATE_KEY`/`GPG_PASSPHRASE` or
configure `signing.gnupg.keyName` in `~/.gradle/gradle.properties` and make sure the gpg-agent is
running.

**`Could not find io.github.astrapi69:...` while resolving a snapshot dependency.** The snapshot
repository `https://central.sonatype.com/repository/maven-snapshots/` is declared in
`gradle/repositories.gradle` with `snapshotsOnly()`; check that the consumer project declares it
as well.

**Consumers can not load the jar: `Unsupported class file major version 69`.** menu-action 5.x is
compiled for Java 25; the consumer must run on Java 25 or later.

**Javadoc warnings in the publish log.** The doclint group `missing` is disabled in
`gradle/publishing.gradle`, remaining warnings point to real javadoc problems (broken links or
html) and should be fixed.

## Files involved

| File | Purpose |
|---|---|
| `gradle.properties` | project version, Central Portal urls, gradle settings |
| `gradle/publishing.gradle` | publication, pom metadata, repositories, signing |
| `gradle/repositories.gradle` | dependency repositories including the snapshot repository |
| `gradle/tagging.gradle` | `tagRelease` task |
| `Makefile` | build, release and Central Portal targets |
| `.github/workflows/gradle.yml` | CI build on push and pull request |
| `.github/workflows/publish.yml` | publish on `RELEASE-*` tags or by hand |
