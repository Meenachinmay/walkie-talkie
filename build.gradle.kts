import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.google.protobuf") version "0.9.1"
    id("application")
}

group = "org.meenachinmay"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // grpc
    implementation("io.grpc:grpc-netty-shaded:1.54.0")
    implementation("io.grpc:grpc-protobuf:1.54.0")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("com.google.protobuf:protobuf-kotlin:3.22.2")

    //
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")

    testImplementation(kotlin("test"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.2"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.54.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

// Create two separate run tasks
tasks.register<JavaExec>("runServer") {
    group = "application"
    mainClass.set("org.meenachinmay.ServerKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("runClient") {
    group = "application"
    mainClass.set("org.meenachinmay.ClientKt")
    classpath = sourceSets["main"].runtimeClasspath
}