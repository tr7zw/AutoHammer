plugins {
    idea
    `maven-publish`
    id("hytale-plugin")
}

group = "dev.tr7zw"
version = "0.1.0"
val javaVersion = 25

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.jspecify)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }

    withSourcesJar()
}

val generateMetadataFile = tasks.register("generateMetadataFile", ProcessResources::class) {
    var replaceProperties = mapOf(
        "plugin_group" to project.group,
        "plugin_name" to project.name,
        "plugin_version" to project.version,
        "server_version" to findProperty("server_version"),

        "plugin_description" to findProperty("plugin_description"),
        "plugin_website" to findProperty("plugin_website"),

        "plugin_main_entrypoint" to findProperty("plugin_main_entrypoint"),
        "plugin_author" to findProperty("plugin_author")
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    //into("build/generated/sources/customMetadata")
    into("src/main/resources")
}

// Make sure the metadata file is generated before building the plugin jar
tasks.named("processResources") {
    dependsOn(generateMetadataFile)
}
tasks.named("sourcesJar") {
    dependsOn(generateMetadataFile)
}

hytale {
    syncTask = generateMetadataFile
}

//sourceSets.main.configure {
//    resources.srcDir(generateMetadataFile)
//}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
