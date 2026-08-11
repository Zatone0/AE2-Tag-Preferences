plugins {
    java
    id("net.neoforged.moddev.legacyforge") version "2.0.119"
}

val modId = providers.gradleProperty("mod_id").get()
val modName = providers.gradleProperty("mod_name").get()
val modVersion = providers.gradleProperty("mod_version").get()
val modGroup = providers.gradleProperty("mod_group").get()

base {
    archivesName = "start-universal-circuit-patterns-1.20.1"
    version = modVersion
    group = modGroup
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

legacyForge {
    version = "1.20.1-47.4.20"

    parchment {
        minecraftVersion = "1.20.1"
        mappingsVersion = "2023.09.03"
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId.mixins.json")
}

repositories {
    maven("https://maven.gtceu.com") {
        content { includeGroup("appeng") }
    }
    maven("https://modmaven.dev/") {
        content { includeGroup("appeng") }
    }
}

dependencies {
    modImplementation("appeng:appliedenergistics2-forge:15.4.10")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 17
}

tasks.processResources {
    val properties = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version" to modVersion
    )
    inputs.properties(properties)
    filesMatching("META-INF/mods.toml") {
        expand(properties)
    }
}

tasks.jar {
    manifest.attributes(
        "MixinConfigs" to "$modId.mixins.json",
        "Implementation-Title" to modName,
        "Implementation-Version" to modVersion
    )
}
