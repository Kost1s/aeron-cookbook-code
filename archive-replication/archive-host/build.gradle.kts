
plugins {
    application
    checkstyle
}

dependencies {
    checkstyle(libs.checkstyle)
    implementation(libs.agrona)
    implementation(libs.aeron.archive)
    implementation(libs.slf4j)
    implementation(libs.logback)
    implementation(project(":archive-replication:common"))
    testImplementation(libs.bundles.testing)
}


tasks {

    task("uberJar", Jar::class) {
        group = "uber"
        manifest {
            attributes["Main-Class"] = "com.aeroncookbook.archive.replication.ArchiveHost"
        }
        archiveClassifier.set("uber")
        from(sourceSets.main.get().output)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }
}

testing {
    suites {
        // Configure the built-in test suite
        @Suppress("UNUSED_VARIABLE")
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter(libs.versions.junitVersion.get())

            targets {
                all {
                    testTask {
                        jvmArgs(
                            "--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED",
                            "--add-opens", "java.base/java.util.zip=ALL-UNNAMED")
                    }
                }
            }
        }
    }
}
