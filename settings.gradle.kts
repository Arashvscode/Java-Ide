@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Cosmic-Ide"

include(
    ":app", 
    ":android-compiler",
    ":common",
    ":eclipse-jdt",
    ":google-java-format",
    ":sora-editor",
    ":project-creator",
    ":kotlinc",
    ":jaxp:xml",
    ":jaxp:internal"
) 