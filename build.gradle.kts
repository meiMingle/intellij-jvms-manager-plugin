fun properties(key: String) = project.findProperty(key).toString()

plugins {
  java
  kotlin("jvm") version "1.5.10"
  id("org.jetbrains.intellij") version "1.5.3"
  id("org.jetbrains.changelog") version "1.3.1"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.github.oshi:oshi-core:6.1.6") {
    exclude(group = "org.slf4j", module = "slf4j-api")
  }
  val jUnitVersion = "5.7.1"
  testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$jUnitVersion")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

intellij {
  version.set(properties("platformVersion"))
  type.set(properties("platformType"))
  downloadSources.set(properties("platformDownloadSources").toBoolean())
  updateSinceUntilBuild.set(true)
  plugins.set(properties("platformPlugins").split(",").map(String::trim).filter(String::isNotEmpty))
}

changelog {
  val projectVersion = project.version as String
  version.set(projectVersion)
  header.set("[$projectVersion] - ${org.jetbrains.changelog.date()}")
  groups.set(listOf("Added", "Changed", "Removed", "Fixed"))
}

tasks {
  patchPluginXml {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))
    changeNotes.set(provider { changelog.getLatest().toHTML() })
  }

  runPluginVerifier {
    ideVersions.set(properties("pluginVerifierIdeVersions").split(",").map(String::trim).filter(String::isNotEmpty))
  }
}
