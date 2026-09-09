# AE2 Tag Preferences

Choose which item AE2 uses for interchangeable tagged ingredients when transferring a processing recipe into the Pattern Encoding Terminal. Set your preferred copper ingot, circuit, or other tagged item in a simple config file.

AE2 normally picks ingredients based on network stock and craftability. This client-side Forge mod checks your ordered preferences first. It only picks an item if it exists, belongs to the configured tag, and is an actual candidate for that recipe slot. Otherwise, AE2 makes its normal choice.

## Quick start

1. Install the mod on your **client**, alongside **Minecraft 1.20.1**, **Forge**, and **Applied Energistics 2 15.4.10 or compatible 15.x**. Use your recipe viewer's AE2 processing-pattern transfer integration.
2. Launch and close Minecraft to generate `config/ae2-tag-preferences-client.toml`, or copy [the example config](examples/ae2-tag-preferences-client.toml) into that location before launching.
3. Add your preferences and restart Minecraft:

```toml
presets = [
    "forge:ingots/copper=minecraft:copper_ingot",
    "forge:ingots/iron=minecraft:iron_ingot"
]
enableAudit = false
```

4. Transfer a processing recipe into AE2's Pattern Encoding Terminal. For each ingredient slot, the first valid matching preference wins.

The generated config starts with `presets = []`, so it changes no ingredient choices until you configure it. Each entry is `namespace:tag=namespace:item`, without a leading `#`. Use IDs from your own pack. Missing items, malformed entries, incorrect tag membership, and items unavailable in the recipe are ignored. This supports item preferences, not fluid preferences. The original candidate's amount and stack data are retained.

## For modpack authors

Ship the configured file at `config/ae2-tag-preferences-client.toml` in the client pack. This is a Forge CLIENT config, not a world/server config. Players can use their own preferences; the server does not need this mod. No GTCEu, KubeJS, or Star Technology dependency is required for general preferences.

When upgrading from Universal Circuit Patterns or Universal Tag Patterns, remove the older JAR first; do not install both. Restart the client after editing the config.

## Optional Pattern Audit

Set `enableAudit = true` to compare encoded processing-pattern ingredients with your configured tag preferences. Tooltips show mismatches as **actual item -> preferred item**, including the matching tag.

For a bulk audit, open a Pattern Access Terminal, set its provider filter to **Show All**, and press **Ctrl+Shift+A**. Results identify the pattern output, provider group, and ingredient differences. ExtendedAE optionally adds provider coordinates and dimension.

The audit uses the first valid configured tag matching each stored item. Missing items and invalid tag memberships are ignored. If the primary output belongs to any valid configured preference tag, the entire pattern is excluded from auditing. This protects production chains, including recipes that upgrade one tier of an ingredient into another. Other inputs on excluded patterns are not audited.

Audit differences are suggestions to review, not proof that a replacement works. Encoded processing patterns do not retain the original recipe's ingredient alternatives. Check the recipe before re-encoding. Transfer-time selection still verifies actual recipe candidates. The audit never modifies patterns.

Auditing is disabled by default.

### Star Technology example

Copy [star-technology.toml](examples/star-technology.toml) to `config/ae2-tag-preferences-client.toml` for ULV through UXV Universal Circuit preferences with auditing enabled. Merge entries manually if you already have custom preferences. These are example data; the audit itself contains no specific rules.

## Scope and compatibility

Ingredient preferences affect processing-pattern terminal population; the optional audit inspects existing processing patterns without modifying them. Existing patterns are not rewritten. Recipes, tags, autocrafting execution, providers, and machines are unaffected.

Built against Forge **47.4.20** and AE2 **15.4.10**, targeting **Java 17**. Minecraft compatibility is restricted to **1.20.1**. EMI/JEI transfer and optional ExtendedAE support still need the live checks listed in [release/CURSEFORGE.md](release/CURSEFORGE.md) before a stable release.

## Building

Run `./gradlew clean build --no-configuration-cache` (`gradlew.bat` on Windows). JDK 21 is used for the local Gradle build; compiled classes target Java 17. Upload `build/libs/ae2-tag-preferences-1.20.1-1.4.0.jar`.

License: [LGPL-3.0-only](LICENSE) for code. See [third-party notices](NOTICE.md) and [separate artwork terms](artwork/README.md).

Crafting-pattern recipe transfers also honor configured item preferences, including when another accepted ingredient is already stocked. The selected item must satisfy the actual recipe ingredient and recipe-viewer filter. Auditing remains limited to processing patterns.
