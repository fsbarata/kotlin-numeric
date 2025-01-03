plugins {
	kotlin("multiplatform")
    id("maven-publish")
}

val kotlinfVersion: String by rootProject.extra
kotlin {
	applyDefaultHierarchyTemplate()

	jvm()

	mingwX64()
	linuxX64()

	sourceSets {
		commonMain {
			dependencies {
				api(project(":lib"))
				api("com.github.fsbarata.kotlin-functional:base:$kotlinfVersion")
			}
		}
		commonTest {
			dependencies {
				implementation(kotlin("test"))
			}
		}
	}
}
