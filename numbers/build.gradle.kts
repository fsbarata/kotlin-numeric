plugins {
	kotlin("multiplatform")
	id("maven-publish")
}

kotlin {
	applyDefaultHierarchyTemplate()

	jvm()

	mingwX64()
	linuxX64()

	sourceSets {
		commonMain {}
		commonTest {
			dependencies {
				implementation(kotlin("test"))
				implementation(project(":test"))
			}
		}

		val bigIntMain by creating {
			dependsOn(commonMain.get())
		}
		val bigIntTest by creating {
			dependsOn(commonTest.get())
		}

		nativeMain {
			dependsOn(bigIntMain)
		}
		nativeTest {
			dependsOn(bigIntTest)
		}

		jvmMain {}
		jvmTest {}
	}
}
