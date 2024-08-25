plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    api(libs.org.bstats.bstats.bukkit)
    api(libs.commons.io.commons.io)
    api(libs.com.fasterxml.jackson.core.jackson.databind)
    compileOnly(libs.com.discordsrv.discordsrv)
    compileOnly(libs.io.papermc.paper.paper.api)
}

group = "com.buape"
version = "2.0.0"
description = "KiaiMC - Integrating Kiai's extensive leveling system with Minecraft servers"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
                username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.token") ?: System.getenv("TOKEN")
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