# Star Technology Universal Circuit Patterns

Client-side Forge compatibility patch for Minecraft 1.20.1.

When EMI or JEI transfers a processing recipe into an AE2 Pattern Encoding Terminal, AE2 normally chooses among equivalent inputs using network craftability and stock. This patch adds an ordered, client-configurable list of tag-to-item presets. Star Technology's Universal Circuits are included as the defaults.

The config is generated at `config/start_universal_circuit_patterns-client.toml`. Each entry has this form:

```toml
"gtceu:circuits/lv=kubejs:lv_universal_circuit"
```

Entries are checked from top to bottom. A preset is used only when the preferred item exists, belongs to the configured tag, and is one of the recipe slot's candidates. Otherwise AE2 retains its normal selection behavior. This makes the selection system useful for other interchangeable tagged ingredients without allowing it to insert an invalid ingredient.

The patch affects pattern-terminal population only. It does not alter recipes, tags, encoded-pattern execution, autocrafting, pattern providers, GT machines, or numbered Programmed Circuits.

Encoded processing-pattern tooltips also audit tiered circuit inputs. A green line confirms that every tiered circuit input is a Universal Circuit. A red `RE-ENCODE` warning names conventional circuit inputs that should be replaced.

For a network-wide audit, set an AE2 Pattern Access Terminal or ExtendedAE Extended Pattern Access Terminal's provider filter to **Show All** and press `Ctrl+Shift+A`. The client scans every pattern sent by the terminal, reports totals in chat, and lists the output and provider group for each pattern that needs re-encoding. It refuses to report an incomplete result when the terminal is filtering providers.

ExtendedAE also sends provider locations, so failures audited from its terminal include clickable `x y z [dimension]` coordinates that suggest the matching `/tp` command. Standard AE2 does not send coordinates to its client screen, so its audit can only identify the provider group.

Patterns whose output is itself a tiered circuit are intentionally excluded. Circuit-production chains such as Runic Processors consume predecessor circuits as components, while Universal Circuit recipes consume the circuit being converted; neither input should be audited as a generic machine circuit choice.
