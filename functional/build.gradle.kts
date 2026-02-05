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
				api(project(":numbers"))
				api("com.github.fsbarata.kotlin-functional:base:$kotlinfVersion")
			}
		}
		commonTest {
			dependencies {
				implementation(kotlin("test"))
				implementation(project(":test"))
			}
		}
	}
}
