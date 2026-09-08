# Automatic full releases

Push a version tag such as `1.4.2` or `v1.4.2` after updating `mod_version` in gradle.properties and adding its CHANGELOG.md entry. The tag must be exactly the same numeric version. Do not retag published versions.

The workflow builds on Java 21 with the Java 17 compilation toolchain, validates packaged licenses/version, then publishes a non-prerelease GitHub release containing the mod JAR, sources JAR, and SHA-256 checksums. Notes come from that version's changelog section. GitHub also provides the complete tagged source archive.

It then uploads the main mod JAR to CurseForge as releaseType=release, tagged Minecraft 1.20.1, Forge, Java 17, Client. AE2 is required; ExtendedAE is optional. CurseForge moderation still applies. No artwork is uploaded by this workflow.

## One-time settings

In GitHub Settings > Secrets and variables > Actions:

- Secret CURSEFORGE_TOKEN: an upload token generated in the CurseForge author dashboard. Never commit or paste this token into chat.
- Variable CURSEFORGE_PROJECT_ID: the numeric CurseForge project ID.

GitHub publishing uses the built-in GITHUB_TOKEN. No personal GitHub token is needed in repository secrets.

## Validation and retries

Run the Release workflow manually on main to build and validate without publishing. Tag pushes publish full releases. An existing tag created before this workflow was installed does not automatically trigger it.

If CurseForge fails after GitHub succeeds, fix the configuration and rerun only failed jobs. Do not rerun the successful GitHub creation job. If an upload times out, inspect CurseForge before retrying; its upload API does not provide an idempotency key.

The workflow does not certify gameplay testing. Finish your in-game checks before pushing a release tag; changelog validation status is preserved verbatim.
