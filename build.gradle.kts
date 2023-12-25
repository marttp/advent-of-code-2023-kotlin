plugins {
    kotlin("jvm") version "1.9.21"
}

dependencies {
    implementation("io.github.tudo-aqua:z3-turnkey:4.8.14")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
