plugins {
    id(BuildPlugins.JAVA_LIBRARY)
    id(BuildPlugins.KOTLIN_JAVA)
    id(BuildPlugins.LINT)
    id(BuildPlugins.KTLINT)
}

configurations.implementation {
    exclude("org.jetbrains", "annotations")
}

dependencies {
    implementation(Dependencies.ANDROIDX_ANNOTATION)
    implementation(Dependencies.JAVAC)
    implementation(projects.jaxp.xml)
    implementation(projects.jaxp.internal)

    runtimeOnly(Dependencies.KOTLIN_REFLECT)

    api(Dependencies.TROVE4J)

    api(files("libs/kotlin-compiler-embeddable-1.7.20-Beta.jar"))

    compileOnly(files("libs/the-unsafe.jar"))
    compileOnly(files("libs/android-stubs.jar"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
