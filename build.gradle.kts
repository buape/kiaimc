plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("signing")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo1.maven.org/maven2/")
}

dependencies {
    compileOnly("com.discordsrv:discordsrv:1.28.0")
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.0.3")
    implementation("commons-io:commons-io:2.16.1")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

group = "com.buape"
version = "2.0.0"
description = "KiaiMC - Integrating Kiai's extensive leveling system with Minecraft servers"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.isFork = true
    // options.forkOptions.executable = "javac"
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

tasks.shadowJar {
    relocate("co.aikar.commands", "com.buape.kiaimc.acf")
    relocate("co.aikar.locales", "com.buape.kiaimc.locales")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}