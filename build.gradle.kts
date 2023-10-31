plugins {
	java
	// spring
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
	// vaadin
	id("com.vaadin") version "24.1.12"
}

group = "cz.upce.gremis"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

extra["vaadinVersion"] = "24.1.12"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.vaadin:vaadin-spring-boot-starter")
	implementation("org.jmolecules:jmolecules-events:1.7.0")

	implementation("in.virit:viritin:2.1.1")



	implementation("org.springframework.boot:spring-boot-starter-mail")

	/*
	<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-mail</artifactId>
	</dependency>
*/


	//compile & annotation processors
	compileOnly("org.projectlombok:lombok")
	compileOnly("org.jetbrains:annotations:23.0.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")

	//test scope
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// vaadin { productionMode = false }
