plugins {
	kotlin("multiplatform")
}

kotlin {
	applyDefaultHierarchyTemplate()

	jvm()

	mingwX64()
	linuxX64()

	sourceSets {
		commonMain {
			dependencies {
				implementation(project(":lib"))
				implementation(kotlin("test"))
			}
		}
		jvmMain {
			dependencies {
				implementation(kotlin("test-junit"))
			}
		}
	}
}
