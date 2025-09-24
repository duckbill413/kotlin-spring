plugins {
  id("org.springframework.boot") version "3.2.3"
  kotlin("jvm") version "2.0.21"
  kotlin("plugin.spring") version "1.8.0"
  id("io.spring.dependency-management") version "1.0.15.RELEASE"
  kotlin("plugin.serialization") version "1.8.0"
}

group = "wh.duckbill"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

  // mysql
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("mysql:mysql-connector-java:8.0.33")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  // mongo
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

  // client
  implementation("com.squareup.okhttp3:okhttp:4.12.0")

  // jwt
  implementation("com.auth0:java-jwt:3.12.0")

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(17)
}