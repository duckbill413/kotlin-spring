plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.allopen") version "1.9.25" // all-open
	// kotlin("plugin.noarg") version "1.9.25" // no-argument plugin
	kotlin("plugin.jpa") version "1.9.25" //
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
}

// allopen 을 사용하면 에노테이션이 붙어 있는 클래스에 대해서는 모두 다 allopen 상태가 됩니다.
// kotlin spring 에서는 자동으로 all-open 처리가 된다.
//allOpen {
//	annotations("org.springframework.boot.autoconfigure.SpringBootApplication")
//}

// plugin.noarg 를 사용하는 경우에는 annotation 에 path를 정의할 필요가 있다.
// jpa 를 사용하는 경우 이러한 점을 불편
// 이를 대체하기 위해서 plugin.jpa plugin을 사용할 수 있다.
//noArg {
//	annotations("package-path")
//}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
