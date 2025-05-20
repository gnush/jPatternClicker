plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "io.github.gnush"
version = "1.0.1"

val mainClassName = "io.github.gnush.patternclicker.MainApp"
val launcherClass = "io.github.gnush.patternclicker.Launcher"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.kwhat:jnativehook:2.2.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:linux")
    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:win")
    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:mac")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to launcherClass)
    }
    from(
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    )

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

javafx {
    version = "21"
    modules("javafx.controls")
}

application {
    mainClass = mainClassName
//    applicationDefaultJvmArgs = listOf(
//        "--enable-native-access=javafx.graphics,ALL-UNNAMED",
//        "--sun-misc-unsafe-memory-access=allow" // remove when https://bugs.openjdk.org/browse/JDK-8345121 has been fixed
//    )
}