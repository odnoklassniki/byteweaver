package ru.ok.byteweaver.transform

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.TryCatchBlockSorter
import ru.ok.byteweaver.config.AnnotationPattern
import ru.ok.byteweaver.config.ClassBlock
import ru.ok.byteweaver.config.ClassName
import ru.ok.byteweaver.config.MethodBlock
import ru.ok.byteweaver.config.Op

class TransformClassVisitor(
        api: Int = Opcodes.ASM6,
        cv: ClassVisitor,
        private val classBlocks: List<ClassBlock>,
        private val filterClassName: Boolean = true,
        private val filterClassAnnotations: Boolean = true,
        private val knownSuperClassJavaNames: List<String>? = null,
        private val knownInterfaceJavaNames: List<String>? = null,
) : ClassVisitor(api, cv) {
    private var version: Int = -1
    private lateinit var classJvmName: String
    private var superClassJvmName: String? = null
    private var interfaceJvmNames: Array<out String>? = null
    private var fileName: String? = null
    private var enclosingClassJvmName: String? = null
    private var enclosingMethodName: String? = null
    private val annotationJvmDescs = mutableListOf<String>()

    override fun visit(version: Int, access: Int, jvmName: String, signature: String?, superJvmName: String?, interfaceJvmNames: Array<out String>?) {
        super.visit(version, access, jvmName, signature, superJvmName, interfaceJvmNames)

        this.version = version
        this.classJvmName = jvmName
        this.superClassJvmName = superJvmName
        this.interfaceJvmNames = interfaceJvmNames
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        annotationJvmDescs += descriptor

        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitSource(fileName: String?, debug: String?) {
        super.visitSource(fileName, debug)

        this.fileName = fileName
    }

    override fun visitOuterClass(enclosingClassJvmName: String?, enclosingMethodName: String?, enclosingMethodDescriptor: String?) {
        super.visitOuterClass(enclosingClassJvmName, enclosingMethodName, enclosingMethodDescriptor)

        this.enclosingClassJvmName = enclosingClassJvmName
        this.enclosingMethodName = enclosingMethodName
    }

    override fun visitMethod(access: Int, name: String, jvmDesc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val mv = super.visitMethod(access, name, jvmDesc, signature, exceptions)

        val methodBlocks = mutableListOf<MethodBlock>()
        for (classBlock in classBlocks) {
            if (filterClassName) {
                if (!classBlock.namePattern.jvmNameMatches(classJvmName)) {
                    continue
                }
            }
            if (filterClassAnnotations) {
                if (!classBlock.annotationPatterns.allJvmDescsMatchAny(annotationJvmDescs)) {
                    continue
                }
            }
            for (methodBlock in classBlock.methodBlocks) {
                if (!methodBlock.namePattern.nameMatches(name)) {
                    continue
                }
                if (!methodBlock.descPattern.jvmDescMatches(jvmDesc)) {
                    continue
                }
                methodBlocks += methodBlock
            }
        }

        if (methodBlocks.isEmpty()) {
            return mv
        }

        val transformLocation = TransformLocation(
                enclosingClassJvmName = enclosingClassJvmName,
                enclosingMethodName = enclosingMethodName,
                declaringClassJvmName = classJvmName,
                superClassJvmName = superClassJvmName,
                interfaceJvmNames = interfaceJvmNames,
                knownSuperClassJavaNames = knownSuperClassJavaNames,
                knownInterfaceJavaNames = knownInterfaceJavaNames,
                methodName = name,
                methodJvmDesc = jvmDesc,
                fileName = fileName,
                version = version,
        )
        val sortMv = TryCatchBlockSorter(mv, access, name, jvmDesc, signature, exceptions)
        return FilterMethodVisitor(api, sortMv, transformLocation, methodBlocks)
    }

    private class FilterMethodVisitor(
            api: Int = Opcodes.ASM6,
            mv: MethodVisitor,
            val transformLocation: TransformLocation,
            val methodBlocks: List<MethodBlock>,
    ) : MethodVisitor(api, mv) {
        private val annotationJvmDescs = mutableListOf<String>()

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
            annotationJvmDescs += descriptor

            return super.visitAnnotation(descriptor, visible)
        }

        override fun visitCode() {
            mv = methodVisitors()
            super.visitCode()
        }

        private fun methodVisitors(): MethodVisitor {
            var mv = super.mv

            for (methodBlock in methodBlocks.asReversed()) {
                if (!methodBlock.annotationPatterns.allJvmDescsMatchAny(annotationJvmDescs)) {
                    continue
                }
                val ancestorNames = methodBlock.classAncestorNames
                if (ancestorNames.isNotEmpty() && ancestorNames.all { transformLocation.isDefinitelyNotDescendantOf(it) }) {
                    continue
                }
                val checkAncestorNames = when {
                    ancestorNames.any { transformLocation.isDefinitelyDescendantOf(it) } -> null
                    else -> ancestorNames
                }
                if (methodBlock.operations.isNotEmpty()) {
                    mv = MethodBodyVisitor(
                        api,
                        mv,
                        transformLocation,
                        methodBlock.operations,
                        checkAncestorNames,
                    )
                }
                for (callBlock in methodBlock.callBlocks.asReversed()) {
                    for (operation in callBlock.operations.asReversed()) {
                        mv = when (operation.op) {
                            Op.BEFORE -> TODO()
                            Op.AFTER -> TODO()
                            Op.REPLACE -> ReplaceCallMethodVisitor(
                                    api,
                                    mv,
                                    transformLocation,
                                    checkAncestorNames,
                                    callBlock,
                                    operation,
                            )
                        }
                    }
                }
            }
            return mv
        }
    }

    companion object {
        private fun TransformLocation.isDefinitelyDescendantOf(className: ClassName): Boolean {
            val jvmName = className.jvmName
            val javaName = className.javaName
            return when {
                jvmName == declaringClassJvmName -> true
                jvmName == superClassJvmName -> true
                interfaceJvmNames != null && jvmName in interfaceJvmNames -> true
                knownSuperClassJavaNames != null && javaName in knownSuperClassJavaNames -> true
                knownInterfaceJavaNames != null && javaName in knownInterfaceJavaNames -> true
                else -> false
            }
        }

        private fun TransformLocation.isDefinitelyNotDescendantOf(className: ClassName): Boolean {
            val javaName = className.javaName
            return when {
                knownInterfaceJavaNames != null && knownSuperClassJavaNames != null -> javaName !in knownSuperClassJavaNames && javaName !in knownInterfaceJavaNames
                else -> false
            }
        }

        private fun List<AnnotationPattern>.allJvmDescsMatchAny(annoJvmDescs: List<String>): Boolean {
            return all { annoPattern ->
                annoJvmDescs.any { annoJvmDesc ->
                    annoPattern.className.jvmDescMatches(annoJvmDesc)
                }
            }
        }
    }
}
