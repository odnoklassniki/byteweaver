package ru.ok.byteweaver.transform

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import ru.ok.byteweaver.config.CallBlock
import ru.ok.byteweaver.config.ClassName
import ru.ok.byteweaver.config.ForwardParameter
import ru.ok.byteweaver.config.Op
import ru.ok.byteweaver.config.Operation
import ru.ok.byteweaver.config.SelfParameter

class ReplaceCallMethodVisitor(
        api: Int = Opcodes.ASM6,
        mv: MethodVisitor?,
        override val transformLocation: TransformLocation,
        private val checkAncestorNames: List<ClassName>?,
        override val callBlock: CallBlock,
        override val operation: Operation,
) : MethodCallVisitor(api, mv) {
    init {
        require(operation.op == Op.REPLACE)
        if (callBlock.isStatic) {
            for (i in 0..operation.parameters.lastIndex) {
                val parameter = operation.parameters[i]
                check(parameter is ForwardParameter) { "Should have forward parameter at position $i" }
                check(parameter.typeName == null) { "Should have untyped forward parameter at position $i" }
                check(parameter.position == i) { "Should have forward parameter $i at position $i" }
            }
        } else {
            check(operation.parameters.isNotEmpty()) { "Should have self parameter" }
            check(operation.parameters[0] is SelfParameter) { "Should have self parameter at position 0" }
            for (i in 1..operation.parameters.lastIndex) {
                val parameter = operation.parameters[i]
                check(parameter is ForwardParameter) { "Should have forward parameter at position $i" }
                check(parameter.typeName == null) { "Should have untyped forward parameter at position $i" }
                check(parameter.position == i - 1) { "Should have forward parameter ${i - 1} at position $i" }
            }
        }
    }

    override fun transformVisitMethodInsn(
            opcode: Int,
            selfClassJvmName: String,
            methodName: String,
            methodJvmDesc: String,
            isInterface: Boolean,
    ) {
        val isMethodStatic = callBlock.isStatic
        when (opcode) {
            Opcodes.INVOKEVIRTUAL -> require(!isMethodStatic) { "Expected static call for method ${callBlock.methodName}" }
            Opcodes.INVOKEINTERFACE -> require(!isMethodStatic) { "Expected static call for method ${callBlock.methodName}" }
            Opcodes.INVOKESTATIC -> require(isMethodStatic) { "Expected virtual call for method ${callBlock.methodName}" }
            else -> TODO()
        }
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                operation.declaringClassName.jvmName,
                operation.methodName.name,
            if (!isMethodStatic) {
                composeSelfMethodJvmDesc(
                    selfClassJvmName,
                    methodJvmDesc,
                )
            } else {
                methodJvmDesc
            },
                isInterface
        )
    }
}
