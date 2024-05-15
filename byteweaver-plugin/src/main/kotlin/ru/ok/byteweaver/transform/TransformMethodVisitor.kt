package ru.ok.byteweaver.transform

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

abstract class TransformMethodVisitor(
        api: Int = Opcodes.ASM6,
        mv: MethodVisitor?,
) : MethodVisitor(api, mv) {
    protected abstract val transformLocation: TransformLocation

    private var lineNumber = -1

    override fun visitLineNumber(line: Int, start: Label) {
        lineNumber = line
        super.visitLineNumber(line, start)
    }

    fun composeTraceString() = buildString {
        append(transformLocation.declaringClassJavaName).append('.').append(transformLocation.methodName)
        if (transformLocation.fileName != null) {
            append('(')
            append(transformLocation.fileName)
            if (lineNumber >= 0) {
                append(':').append(lineNumber)
            }
            append(')')
        } else {
            append("(Unknown Source)")
        }
    }
}
