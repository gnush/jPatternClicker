plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "io.github.gnush"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.kwhat:jnativehook:2.2.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "21"
    modules("javafx.controls")
}

application {
    mainClass = "io.github.gnush.patternclicker.MainApp"
//    applicationDefaultJvmArgs = listOf(
//        "--enable-native-access=javafx.graphics,ALL-UNNAMED",
//        "--sun-misc-unsafe-memory-access=allow" // remove when https://bugs.openjdk.org/browse/JDK-8345121 has been fixed
//    )
}