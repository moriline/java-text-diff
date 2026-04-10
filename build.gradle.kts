import java.time.Duration

plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "io.github.moriline"
version = "1.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("JavaTextDiff")
                description.set("Zero-dependency Java library for computing text diffs and applying patches using the Myers greedy difference algorithm")
                url.set("https://github.com/moriline/java-text-diff")

                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("moriline")
                        name.set("Alec Moriline")
                        email.set("alemoroz@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/moriline/java-text-diff.git")
                    developerConnection.set("scm:git:ssh://github.com:moriline/java-text-diff.git")
                    url.set("https://github.com/moriline/java-text-diff")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.findProperty("sonatypeUsername")?.toString() ?: "")
            password.set(project.findProperty("sonatypePassword")?.toString() ?: "")
            packageGroup.set("io.github.moriline")
        }
    }
    connectTimeout.set(Duration.ofMinutes(3))
    clientTimeout.set(Duration.ofMinutes(3))
}

signing {
    val sonatypeUsername = project.findProperty("sonatypeUsername")?.toString()?.isNotBlank() == true
    if (sonatypeUsername) {
        useGpgCmd()
        sign(publishing.publications["mavenJava"])
    }
}