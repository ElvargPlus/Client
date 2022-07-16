import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.runescape"

repositories {
    mavenCentral()
    maven("https://repo.runelite.net")
}

plugins {
    id("java")
    kotlin("jvm") version "1.7.0"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.24")

    compileOnly(group = "javax.annotation", name = "javax.annotation-api", version = "1.3.2")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.24")
    compileOnly(group = "net.runelite", name = "orange-extensions", version = "1.0")

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.9")
    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.5")
    implementation(group = "com.google.guava", name = "guava", version = "30.1.1-jre") {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
        exclude(group = "com.google.errorprone", module = "error_prone_annotations")
        exclude(group = "com.google.j2objc", module = "j2objc-annotations")
        exclude(group = "org.codehaus.mojo", module = "animal-sniffer-annotations")
    }
    implementation(group = "com.google.inject", name = "guice", version = "5.0.1")
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.1")

    implementation(group = "net.java.dev.jna", name = "jna", version = "5.9.0")
    implementation(group = "net.java.dev.jna", name = "jna-platform", version = "5.9.0")
    implementation(group = "net.runelite", name = "discord", version = "1.4")
    implementation(group = "net.runelite.pushingpixels", name = "substance", version = "8.0.02")
    implementation(group = "net.sf.jopt-simple", name = "jopt-simple", version = "5.0.4")
    implementation(group = "org.apache.commons", name = "commons-text", version = "1.9")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.9.0")
    implementation(group = "commons-io", name = "commons-io", version = "2.8.0")
    implementation(group = "org.jetbrains", name = "annotations", version = "22.0.0")
    implementation(group = "io.github.microutils", name = "kotlin-logging-jvm", version = "2.1.20")
    implementation(group = "org.slf4j", name = "slf4j-simple", version = "1.7.29")


    runtimeOnly(group = "net.runelite.pushingpixels", name = "trident", version = "1.5.00")

    implementation(group = "net.runelite.jogl", name = "jogl-rl", version = "2.4.0-rc-20220318")
    implementation(group = "net.runelite.jogl", name = "jogl-gldesktop-dbg", version = "2.4.0-rc-20220318")
    implementation(group = "net.runelite.jocl", name = "jocl", version = "1.0")

    implementation(platform("org.lwjgl:lwjgl-bom:3.3.1"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("net.runelite:rlawt:1.3")

    listOf("linux", "macos", "macos-arm64", "windows-x86", "windows").forEach {
        runtimeOnly("org.lwjgl:lwjgl::natives-$it")
        runtimeOnly("org.lwjgl:lwjgl-opengl::natives-$it")
    }

}

tasks {

    register<JavaExec>("Development-ElvargPlus") {
        group = "ElvargPlus"
        classpath = project.sourceSets.main.get().runtimeClasspath
        enableAssertions = true
        args = listOf(
            "--developer-mode"
        )
        mainClass.set("net.runelite.client.RuneLite")
    }

    register<JavaExec>("ElvargPlus") {
        group = "ElvargPlus"
        classpath = project.sourceSets.main.get().runtimeClasspath
        mainClass.set("net.runelite.client.RuneLite")
    }

}
