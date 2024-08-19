import com.google.protobuf.gradle.*

plugins {
    `java-library`
    `maven-publish`

    // gRPC
    id("com.google.protobuf") version "0.9.4"
}

group = "dev.emortal.api.agonessdk"
version = "1.0-SNAPSHOT"

val grpcVersion = "1.66.0"

repositories {
    mavenCentral()
}

dependencies {
    // gRPC
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")
    api("io.grpc:grpc-netty:$grpcVersion")

//    compileOnly("jakarta.annotation:jakarta.annotation-api:3.0.0")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    implementation("org.slf4j:slf4j-api:2.0.16")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {}
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

publishing {
    repositories {
        maven {
            name = "development"
            url = uri("https://repo.emortal.dev/snapshots")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_SECRET")
            }
        }
        maven {
            name = "release"
            url = uri("https://repo.emortal.dev/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_SECRET")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.emortal.api"
            artifactId = "agones-sdk"

            val commitHash = System.getenv("COMMIT_HASH_SHORT")
            val releaseVersion = System.getenv("RELEASE_VERSION")
            version = commitHash ?: releaseVersion ?: "local"

            from(components["java"])
        }
    }
}

// Inform IDEs like IntelliJ IDEA, Eclipse or NetBeans about the generated code.
sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}
