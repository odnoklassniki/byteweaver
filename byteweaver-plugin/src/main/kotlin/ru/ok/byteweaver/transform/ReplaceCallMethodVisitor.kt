package ru.ok.byteweaver.transform

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import ru.ok.byteweaver.config.CallBlock
import ru.ok.byteweaver.config.ClassName
import ru.ok.byteweaver.config.DeclaringClassPattern
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
        check(operation.parameters.isNotEmpty()) { TODO() }
        check(operation.parameters[0] is SelfParameter) { TODO() }
        for (i in 1..operation.parameters.lastIndex) {
            val parameter = operation.parameters[i]
            check(parameter is ForwardParameter) { TODO() }
            check(parameter.typeName == null) { TODO() }
            check(parameter.position == i - 1) { TODO() }
        }
    }

    override fun transformVisitMethodInsn(
            opcode: Int,
            declaringClassPattern: DeclaringClassPattern,
            methodName: String,
            methodJvmDesc: String,
            isInterface: Boolean,
    ) {
        when (opcode) {
            Opcodes.INVOKEVIRTUAL -> Unit
            Opcodes.INVOKEINTERFACE -> Unit
            else -> TODO()
        }
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                operation.declaringClassName.jvmName,
                operation.methodName.name,
                composeSelfMethodJvmDesc(
                        declaringClassPattern.declaringJvmName,
                        methodJvmDesc,
                ),
                isInterface
        )
    }
}
