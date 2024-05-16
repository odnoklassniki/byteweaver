@file:Suppress("UnstableApiUsage")

package ru.ok.byteweaver.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.internal.utils.setDisallowChanges
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException

class ByteweaverPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val configs = project.container(ByteweaverConfig::class.java)
        project.extensions.add("byteweaver", configs)

        project.afterEvaluate {
            afterEvaluate(project, configs)
        }
    }

    private fun afterEvaluate(project: Project, configs: Set<ByteweaverConfig>) {
        val android = try {
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        } catch (ex: UnknownDomainObjectException) {
            project.logger.error("Only android application projects supported", ex)
            throw ex
        }
        android.onVariants { variant ->
            val srcFiles = configs
                    .filter { config ->
                        variant.buildType == config.name || variant.productFlavors.any { it.second == config.name }
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
