@file:Suppress("UnstableApiUsage")

package ru.ok.byteweaver.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.gradle.api.tasks.Internal
import org.objectweb.asm.ClassVisitor
import ru.ok.byteweaver.config.ClassBlock
import ru.ok.byteweaver.config.ClassPattern
import ru.ok.byteweaver.config.parseConfigInto
import ru.ok.byteweaver.transform.TransformClassVisitor
import java.util.*

abstract class ByteweaverAsmClassVisitorFactory : AsmClassVisitorFactory<ByteweaverInstrumentationParameters> {
    @get:Internal
    val classBlocks: List<ClassBlock>
        get() = synchronized(CACHED_CLASS_BLOCKS) {
            CACHED_CLASS_BLOCKS.getOrPut(this) {
                mutableListOf<ClassBlock>().also { outClassBlocks ->
                    for (srcFile in parameters.get().srcFiles.get()) {
                        srcFile.bufferedReader().use { srcReader ->
                            parseConfigInto(srcReader, outClassBlocks)
                        }
                    }
                }
            }
        }

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        val classBlocks = classBlocks.filter { isInstrumentable(classContext.currentClassData, it) }
        if (classBlocks.isNotEmpty()) {
            return TransformClassVisitor(
                    api = instrumentationContext.apiVersion.get(),
                    cv = nextClassVisitor,
                    classBlocks = classBlocks,
                    filterClassName = false,
                    filterClassAnnotations = false,
                    knownSuperClassJavaNames = classContext.currentClassData.superClasses,
                    knownInterfaceJavaNames = classContext.currentClassData.interfaces,
            )
        }
        return nextClassVisitor
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classBlocks.any { isInstrumentable(classData, it) }
    }

    companion object {
        private val CACHED_CLASS_BLOCKS = WeakHashMap<Any, List<ClassBlock>>()

        @JvmStatic
        private fun isInstrumentable(classData: ClassData, classBlock: ClassBlock): Boolean {
            if (!classBlock.namePattern.javaNameMatches(classData.className)) {
                return false
            }
            if (classBlock.annotationPatterns.isNotEmpty()) {
                for (annotationPattern in classBlock.annotationPatterns) {
                    if (!annotationPattern.className.javaNameMatchesAny(classData.classAnnotations)) {
                        return false
                    }
                }
            }
            if (classBlock.ancestorNames.isNotEmpty()) {
                for (ancestorName in classBlock.ancestorNames) {
                    if (!ancestorName.javaNameMatchesAny(classData.superClasses) && !ancestorName.javaNameMatchesAny(classData.interfaces)) {
                        return false
                    }
                }
            }
            return true
        }

        private fun ClassPattern.javaNameMatchesAny(names: Iterable<String>) = names.any { javaNameMatches(it) }
    }
}

