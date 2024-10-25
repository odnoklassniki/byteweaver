@file:Suppress("UnstableApiUsage")

package ru.ok.byteweaver.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.internal.utils.setDisallowChanges
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException

class ByteweaverPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val configs = project.container(ByteweaverConfig::class.java)
        project.extensions.add("byteweaver", configs)

        project.pluginManager.withPlugin("com.android.application") {
            val androidComponents = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

            hook(project, androidComponents, configs)
        }
        project.pluginManager.withPlugin("com.android.library") {
            val androidComponents = project.extensions.getByType(LibraryAndroidComponentsExtension::class.java)

            hook(project, androidComponents, configs)
        }
    }

    private fun hook(
        project: Project,
        androidComponents: AndroidComponentsExtension<*, *, *>,
        configs: Set<ByteweaverConfig>
    ) {
        androidComponents.onVariants { variant ->
            val srcFiles = configs
                    .filter { config ->
                        when {
                            variant.name == config.name -> true
                            variant.flavorName == config.name -> true
                            variant.buildType == config.name -> true
                            variant.productFlavors.any { it.second == config.name } -> true
                            else -> false
                        }
                    }
                    .flatMap { it.srcFiles }
                    .map { project.file(it) }

            if (srcFiles.isNotEmpty()) {
                variant.instrumentation.transformClassesWith(ByteweaverAsmClassVisitorFactory::class.java, InstrumentationScope.ALL) { parameters ->
                    parameters.srcFiles.setDisallowChanges(srcFiles)
                }
                variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
            }
        }
    }
}
