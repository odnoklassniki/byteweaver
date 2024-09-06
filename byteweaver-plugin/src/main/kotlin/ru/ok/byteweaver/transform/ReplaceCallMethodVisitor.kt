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
    val hasFirstSelfParameter: Boolean

    init {
        require(operation.op == Op.REPLACE)
        hasFirstSelfParameter = operation.parameters.isNotEmpty() && operation.parameters[0] is SelfParameter
        val startRestParameters = if (hasFirstSelfParameter) 1 else 0
        for (i in startRestParameters..operation.parameters.lastIndex) {
            val parameter = operation.parameters[i]
            check(parameter is ForwardParameter) { TODO() }
            check(parameter.typeName == null) { TODO() }
            check(parameter.position == i - 1) { TODO() }
        }
    }

    override fun transformVisitMethodInsn(
            opcode: Int,
            selfClassJvmName: String,
            methodName: String,
            methodJvmDesc: String,
            isInterface: Boolean,
    ) {
        when (opcode) {
            Opcodes.INVOKEVIRTUAL -> check(hasFirstSelfParameter) { "Expected self parameter for invokevirtual "}
            Opcodes.INVOKEINTERFACE -> check(hasFirstSelfParameter) { "Expected self parameter for invokeinterface "}
            Opcodes.INVOKESTATIC -> check(!hasFirstSelfParameter) { "Expected no self parameter for invokestatic "}
            else -> TODO()
        }
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                operation.declaringClassName.jvmName,
                operation.methodName.name,
                if (hasFirstSelfParameter) {
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
