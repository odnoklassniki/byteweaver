package ru.ok.byteweaver.transform

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import ru.ok.byteweaver.config.Op
import ru.ok.byteweaver.config.Operation
import ru.ok.byteweaver.config.TraceParameter
import ru.ok.byteweaver.util.VOID_JVM_DESC

class BeforeMethodBodyVisitor(
        api: Int = Opcodes.ASM6,
        mv: MethodVisitor?,
        override val transformLocation: TransformLocation,
        private val operation: Operation,
) : MethodBodyVisitor(api, mv) {

    init {
        check(operation.op == Op.BEFORE)
        check(operation.returnTypeName.jvmDesc == VOID_JVM_DESC)
        if (operation.parameters.isNotEmpty()) {
            check(operation.parameters.size == 1)
            check(operation.parameters.first() == TraceParameter)
        }
    }

    override fun beforeVisitFirstInsn() {
        if (operation.parameters.isNotEmpty()) {
            assert(operation.parameters.size == 1)
            assert(operation.parameters.first() == TraceParameter)
            super.visitLdcInsn(composeTraceString())
        }
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                operation.declaringClassName.jvmName,
                operation.methodName.name,
                methodJvmDesc(),
                false,
        )
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack.coerceAtLeast(operation.parameters.size), maxLocals)
    }

    private fun methodJvmDesc() = when {
        operation.parameters.isEmpty() -> "()V"
        else -> "(Ljava/lang/String;)V"
    }
}
