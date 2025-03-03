package ru.ok.byteweaver.transform

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import ru.ok.byteweaver.config.ClassName
import ru.ok.byteweaver.config.ForwardParameter
import ru.ok.byteweaver.config.Op
import ru.ok.byteweaver.config.Operation
import ru.ok.byteweaver.config.ThisParameter
import ru.ok.byteweaver.config.TraceParameter
import ru.ok.byteweaver.util.THROWABLE_JVM_NAME
import ru.ok.byteweaver.util.VOID_JVM_DESC
import kotlin.math.max

class MethodBodyVisitor(
    api: Int = Opcodes.ASM7,
    mv: MethodVisitor,
    override val transformLocation: TransformLocation,
    private val operations: List<Operation>,
    private val checkAncestorNames: List<ClassName>?,
) : TransformMethodVisitor(api, mv) {

    init {
        for (operation in operations) {
            when (operation.op) {
                Op.BEFORE -> {
                    check(operation.returnTypeName.jvmDesc == VOID_JVM_DESC)
                    if (transformLocation.access and Opcodes.ACC_STATIC == Opcodes.ACC_STATIC) {
                        check(operation.parameters.none { it is ThisParameter })
                    }
                }

                Op.AFTER -> {
                    check(operation.returnTypeName.jvmDesc == VOID_JVM_DESC)
                    if (operation.parameters.isNotEmpty()) {
                        check(operation.parameters.size == 1)
                        check(operation.parameters.first() == TraceParameter)
                    }
                }

                Op.REPLACE -> TODO("Replacing method body not yet supported")
            }
        }
    }

    private val hasOperationsBefore = operations.any { it.op == Op.BEFORE }
    private val hasOperationsAfter = operations.any { it.op == Op.AFTER }

    private val labelStart = if (hasOperationsBefore) Label() else null
    private val labelTry = if (hasOperationsAfter) Label() else null
    private val labelCatchBlock = if (hasOperationsAfter) Label() else null
    private val labelBeforeBlock = if (hasOperationsBefore) Label() else null
    private var veryFirstLineNumber: Int? = null

    override fun visitLineNumber(line: Int, start: Label?) {
        if (veryFirstLineNumber == null) {
            veryFirstLineNumber = line
        }
        super.visitLineNumber(line, start)
    }

    override fun visitCode() {
        if (labelStart != null && labelBeforeBlock != null) {
            super.visitJumpInsn(Opcodes.GOTO, labelBeforeBlock)
            super.visitLabel(labelStart)
            if (transformLocation.version >= Opcodes.V1_6) {
                super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            }
        }
        if (labelTry != null && labelCatchBlock != null) {
            super.visitTryCatchBlock(labelTry, labelCatchBlock, labelCatchBlock, THROWABLE_JVM_NAME)
            super.visitLabel(labelTry)
        }
        super.visitCode()
    }

    override fun visitInsn(opcode: Int) {
        when (opcode) {
            Opcodes.RETURN, Opcodes.ARETURN, Opcodes.DRETURN, Opcodes.FRETURN, Opcodes.IRETURN, Opcodes.LRETURN -> visitAfter()
        }
        super.visitInsn(opcode)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        if (labelCatchBlock != null) {
            super.visitLabel(labelCatchBlock)
            if (transformLocation.version >= Opcodes.V1_6) {
                super.visitFrame(Opcodes.F_SAME1, 0, null, 1, ARRAY_OF_THROWABLE_JVM_NAME)
            }
            visitAfter()
            visitInsn(Opcodes.ATHROW)
        }
        if (labelStart != null && labelBeforeBlock != null) {
            super.visitLabel(labelBeforeBlock)
            if (transformLocation.version >= Opcodes.V1_6) {
                super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            }
            visitBefore()
            super.visitJumpInsn(Opcodes.GOTO, labelStart)
        }
        @Suppress("NAME_SHADOWING")
        var maxStack = maxStack

        maxStack = max(maxStack, 1)
        if (!checkAncestorNames.isNullOrEmpty()) {
            maxStack = max(maxStack, 2)
        }
        for (operation in operations) {
            when (operation.op) {
                Op.BEFORE, Op.AFTER -> {
                    maxStack = max(maxStack, operation.parameters.size)
                }
                Op.REPLACE -> {} // max stack already allocated
            }
        }
        super.visitMaxs(
            maxStack,
            maxLocals,
        )
    }

    private fun visitBefore() {
        if (!checkAncestorNames.isNullOrEmpty()) {
            val successLabel = Label()
            for (checkAncestorName in checkAncestorNames) {
                super.visitVarInsn(Opcodes.ALOAD, 0)
                super.visitTypeInsn(Opcodes.INSTANCEOF, checkAncestorName.jvmName)
                super.visitJumpInsn(Opcodes.IFEQ, successLabel)
            }
            super.visitJumpInsn(Opcodes.GOTO, labelStart)
            super.visitLabel(successLabel)
            if (transformLocation.version >= Opcodes.V1_6) {
                super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            }
        }
        for (operation in operations) {
            if (operation.op != Op.BEFORE) {
                continue
            }
            val parameterTypeJvmDescs = buildList {
                for (parameter in operation.parameters) {
                    this += when (parameter) {
                        is TraceParameter -> "Ljava/lang/String;"
                        is ThisParameter -> "L" + transformLocation.declaringClassJvmName + ";"
                        is ForwardParameter -> extractParameterJvmName(
                            transformLocation.methodJvmDesc,
                            parameter.position
                        )
                        else -> TODO()
                    }
                }
            }
            val methodJvmDesc = buildString {
                append("(")
                parameterTypeJvmDescs.forEach {
                    append(it)
                }
                append(")V")
            }
            for (i in operation.parameters.indices) {
                when (val parameter = operation.parameters[i]) {
                    is TraceParameter -> {
                        super.visitLdcInsn(composeTraceString())
                    }
                    is ThisParameter -> {
                        super.visitVarInsn(Opcodes.ALOAD, 0)
                    }
                    is ForwardParameter -> {
                        val index = when {
                            transformLocation.access and Opcodes.ACC_STATIC == Opcodes.ACC_STATIC -> parameter.position
                            else -> parameter.position + 1
                        }
                        val opcode = when (parameterTypeJvmDescs[i].first()) {
                            'B', 'S', 'I', 'Z', 'C' -> Opcodes.ILOAD
                            'J' -> Opcodes.LLOAD
                            'F' -> Opcodes.DLOAD
                            'D' -> Opcodes.DLOAD
                            'L', '[' -> Opcodes.ALOAD
                            else -> throw IllegalArgumentException()
                        }
                        super.visitVarInsn(opcode, index)
                    }
                    else -> TODO()
                }
            }
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                operation.declaringClassName.jvmName,
                operation.methodName.name,
                methodJvmDesc,
                false,
            )
        }
    }

    private fun visitAfter() {
        val failLabel: Label?
        if (!checkAncestorNames.isNullOrEmpty()) {
            failLabel = Label()
            val successLabel = Label()
            for (checkAncestorName in checkAncestorNames) {
                super.visitVarInsn(Opcodes.ALOAD, 0)
                super.visitTypeInsn(Opcodes.INSTANCEOF, checkAncestorName.jvmName)
                super.visitJumpInsn(Opcodes.IFEQ, successLabel)
            }
            super.visitJumpInsn(Opcodes.GOTO, failLabel)
            super.visitLabel(successLabel)
            if (transformLocation.version >= Opcodes.V1_6) {
                super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            }
        } else {
            failLabel = null
        }
        for (operation in operations.asReversed()) {
            if (operation.op != Op.AFTER) {
                continue
            }
            val methodJvmDesc = when {
                operation.parameters.isEmpty() -> "()V"
                else -> "(Ljava/lang/String;)V"
            }
            if (operation.parameters.isNotEmpty()) {
                assert(operation.parameters.size == 1)
                assert(operation.parameters.first() == TraceParameter)
                super.visitLdcInsn(composeTraceString())
            }
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                operation.declaringClassName.jvmName,
                operation.methodName.name,
                methodJvmDesc,
                false,
            )
        }
        if (failLabel != null) {
            super.visitLabel(failLabel)
            if (transformLocation.version >= Opcodes.V1_6) {
                super.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            }
        }
    }

    private fun composeTraceString() = buildString {
        append(transformLocation.declaringClassJavaName)
        append('.')
        append(transformLocation.methodName)
        if (transformLocation.fileName != null) {
            append('(')
            append(transformLocation.fileName)
            val veryFirstLineNumber = veryFirstLineNumber
            if (veryFirstLineNumber != null && veryFirstLineNumber >= 0) {
                append(':')
                append(veryFirstLineNumber)
            }
            append(')')
        } else {
            append("(Unknown Source)")
        }
    }

    private fun extractParameterJvmName(methodJvmDesc: String, position: Int): String {
        var pos = position
        var startIndex = 1
        while (true) {
            val endIndex = when (methodJvmDesc[startIndex]) {
                ')' -> throw IndexOutOfBoundsException("No parameter at position $position")
                else -> methodJvmDesc.indexOfEndDesc(startIndex)
            }
            if (pos == 0) {
                return methodJvmDesc.substring(startIndex, endIndex)
            }

            startIndex = endIndex
            pos -= 1
        }
    }

    private fun String.indexOfEndDesc(startIndex: Int = 0): Int = when (get(startIndex)) {
        in "BSIJZCFD" -> startIndex + 1
        '[' -> indexOfEndDesc(startIndex + 1)
        'L' -> indexOf(';', startIndex = startIndex) + 1
        else -> throw IllegalArgumentException(this)
    }

    companion object {
        private val ARRAY_OF_THROWABLE_JVM_NAME = arrayOf(THROWABLE_JVM_NAME)
    }
}