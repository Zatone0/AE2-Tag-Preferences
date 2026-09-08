# 1.4.1

- Exclude the entire pattern from auditing when its primary output matches any valid configured preference tag, including cross-tier production chains.
- Transfer preferences are unchanged. In-game retest pending.

# AE2 Tag Preferences 1.4.0

- General-purpose, ordered tag-to-item preferences for AE2 processing-recipe transfer.
- Starts with an empty preference list: configure only the items you want for your pack.
- Includes copyable general-purpose and Star Technology config examples.
- Validates item existence, tag membership, and recipe candidacy before selecting a preference; falls back to AE2 otherwise.
- Generalizes tooltip and terminal auditing to configured tag preferences through `enableAudit`, disabled by default.
- Reports actual and preferred items with matching tags as advisory differences in tooltips and terminal reports.
- Skips a matching audit rule when the primary output belongs to the same tag, while retaining checks for unrelated inputs.
- Preserves optional ExtendedAE provider location reporting.
- Uses `config/ae2-tag-preferences-client.toml` for preferences and audit settings.
- Includes a Gradle wrapper and reproducible archive settings.

Upgrade: remove the previous Universal Circuit/Tag Patterns JAR. Use `config/ae2-tag-preferences-client.toml` and set `enableAudit = true` to enable auditing.

Minecraft 1.20.1 / Forge / AE2 15.4.10 through 15.x. Built against Forge 47.4.20 and AE2 15.4.10. Client-side. ExtendedAE is optional. In-game acceptance checks are pending; prepared as Beta.