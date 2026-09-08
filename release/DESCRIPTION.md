# AE2 Tag Preferences

**Your preferred ingredients. More consistent processing patterns.**

AE2 Tag Preferences lets you choose which items AE2 selects when transferring processing recipes into the Pattern Encoding Terminal. Prefer one mod's copper ingots or a particular circuit? Add a tag-to-item preference to your config.

## Features

- **Simple configuration:** choose preferred items for ingredient tags in your pack.
- **Ordered preferences:** the first valid preference available for the recipe slot wins.
- **Recipe-aware selection:** an item must exist, belong to the configured tag, and be an accepted recipe candidate. Otherwise, AE2 chooses normally.
- **Optional pattern audit:** review existing processing patterns for ingredients that differ from your preferences.
- **Client-side:** use your own preferences or distribute a config with your modpack.

## Getting Started

Place the mod JAR in your Minecraft client's `mods` folder, then launch Minecraft once to generate:

`config/ae2-tag-preferences-client.toml`

Close Minecraft, add your preferences, and restart:

```toml
presets = [
    "forge:ingots/copper=minecraft:copper_ingot",
    "forge:ingots/iron=minecraft:iron_ingot"
]

enableAudit = true
```

Each entry is `"namespace:tag=namespace:preferred_item"`. Use tags and items available in your pack, without a leading `#`. Entries are checked from top to bottom.

The generated config starts with an empty preference list and auditing disabled. Modpack authors can ship this file in their client pack's `config` folder.

## Pattern Audit

Set `enableAudit = true` to check encoded processing patterns against your configured preferences.

- **Pattern tooltips** show differing ingredients as **actual item → preferred item**, with the matching tag.
- **Terminal audit:** open a Pattern Access Terminal, set the provider filter to **Show All**, and press **Ctrl+Shift+A**. Results identify pattern outputs, provider groups, and ingredient differences.
- **ExtendedAE support:** its Extended Pattern Access Terminal can also supply provider coordinates and dimensions.

The audit uses the first valid configured tag matching each stored ingredient. Patterns whose primary output belongs to any valid configured preference tag are excluded from auditing. This protects production chains, including cross-tier upgrades. Other inputs on those patterns are also excluded.

Audit warnings are suggestions to review. Encoded processing patterns do not retain the original recipe's ingredient alternatives, so check the recipe before replacing an ingredient. The audit never changes your patterns.

## Requirements and Compatibility

- **Minecraft 1.20.1 · Forge · Java 17 or later**
- **Applied Energistics 2 15.4.10 or a compatible later 15.x version**
- A recipe viewer with AE2 processing-pattern transfer support

Install on the client; the server does not need this mod. **ExtendedAE is optional.** No GTCEu, KubeJS, or Star Technology dependency is required.

Preferences apply to items when transferring processing recipes. Existing patterns are not automatically rewritten, and recipes, tags, and autocrafting execution remain unchanged. Fluid preferences are not supported. Only processing patterns are audited.

**Beta:** built against Forge 47.4.20 and AE2 15.4.10. In-game acceptance testing remains pending.

[Source code and configuration examples](https://github.com/Zatone0/StarTUniversalTagPatterns)

## License and Distribution

The mod's code is licensed under **GNU LGPL v3**. You may include it in modpacks and redistribute or modify it under that license's terms. Retain the license and notices, and provide the corresponding source as required.

Project artwork is licensed separately under **CC BY-NC-SA 3.0**, with a JEI component under **MIT**. Artwork credits, modifications, license texts, and publication limitations are documented in the repository's artwork directory. This project is not affiliated with or endorsed by AE2 or JEI.
