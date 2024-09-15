import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("signing")
    id("com.gradleup.shadow") version "8.3.1"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo1.maven.org/maven2/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("com.discordsrv:discordsrv:1.28.1")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("commons-io:commons-io:2.16.1")
    implementation("dev.jorel:commandapi-bukkit-shade:9.5.3")
}

tasks.withType<ShadowJar> {
    dependencies {
        include(dependency("dev.jorel:commandapi-bukkit-shade:9.5.3"))
        include(dependency("org.bstats:bstats-bukkit:3.0.3"))
    }

    relocate("dev.jorel.commandapi", "com.buape.kiaimc.commandapi")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn("shadowJar")
}

group = "com.buape"
version = "2.0.0"
description = "KiaiMC - Integrating Kiai's extensive leveling system with Minecraft servers"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("KiaiMC API")
                description.set("KiaiMC - Integrating Kiai's extensive leveling system with Minecraft servers")
                url.set("https://github.com/buape/kiaimc")

                licenses {
                    license {
                        name.set("GNU General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    }
                }

                developers {
                    developer {
                        id.set("buape")
                        name.set("Buape Studios")
                        email.set("support@buape.com")
                    }
                    developer {
                        id.set("thewilloftheshadow")
                        name.set("Shadow")
                        email.set("hi@shadowing.dev")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/buape/kiaimc.git")
                    developerConnection.set("scm:git:ssh://github.com:buape/kiaimc.git")
                    url.set("https://github.com/buape/kiaimc")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/buape/kiaimc")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("GPG_KEY_ID"),
        System.getenv("GPG_KEY"),
        System.getenv("GPG_KEY_PASSWORD")
    )
    sign(publishing.publications["mavenJava"])
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}