plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    id("application")
}

group = "de.seuhd.worldcup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("de.seuhd.worldcup.MainKt")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "de.seuhd.worldcup.MainKt"
        )
    }
}

tasks.register<Jar>("fatJar") {
    archiveBaseName.set("worldcup")
    archiveVersion.set("1.0-SNAPSHOT")
    archiveClassifier.set("")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "de.seuhd.worldcup.MainKt"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}


dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
}

tasks.test {
    useJUnitPlatform()
}