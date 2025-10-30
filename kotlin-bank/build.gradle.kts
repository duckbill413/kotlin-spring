plugins {
  id("org.springframework.boot") version "3.2.3"
  kotlin("jvm") version "2.0.21"
  kotlin("plugin.spring") version "1.8.0"
  id("io.spring.dependency-management") version "1.0.15.RELEASE"
  kotlin("plugin.serialization") version "1.8.0"
  kotlin("plugin.jpa") version "1.9.22"
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

  // security
  implementation("org.springframework.boot:spring-boot-starter-security")

  // mysql
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("mysql:mysql-connector-java:8.0.33")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  // mongo
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  // client
  implementation("com.squareup.okhttp3:okhttp:4.12.0")

  // jwt
  implementation("com.auth0:java-jwt:3.12.0")

  // uuid
  implementation("com.github.f4b6a3:ulid-creator:5.2.3")

  // redis
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.redisson:redisson-spring-boot-starter:3.36.0")

  // kafka
  implementation("org.springframework.kafka:spring-kafka:3.1.0")

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(17)
}