package ru.ok.byteweaver.transform

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import ru.ok.byteweaver.config.Op
import ru.ok.byteweaver.config.Operation
import ru.ok.byteweaver.util.THROWABLE_JVM_NAME
import ru.ok.byteweaver.util.VOID_JVM_DESC

class AfterMethodBodyVisitor(
        api: Int = Opcodes.ASM6,
        mv: MethodVisitor?,
        override val transformLocation: TransformLocation,
        private val operation: Operation,
) : MethodBodyVisitor(api, mv) {
    init {
        check(operation.op == Op.AFTER)
        check(operation.returnTypeName.jvmDesc == VOID_JVM_DESC)
        check(operation.parameters.isEmpty())
    }

    private val start = Label()
    private val end = Label()

    override fun beforeVisitFirstInsn() {
        if (AFTER_MEANS_FINALLY) {
            super.visitTryCatchBlock(start, end, end, THROWABLE_JVM_NAME)
            super.visitLabel(start)
        }
        super.beforeVisitFirstInsn()
    }

    override fun beforeVisitLastInsn(opcode: Int) {
        if (AFTER_MEANS_FINALLY) {
            if (opcode == Opcodes.ATHROW) {
                return // will catch
            }
        }
        visitCallAfterInsn()
    }

    private fun visitCallAfterInsn() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                operation.declaringClassName.jvmName,
                operation.methodName.name,
                methodJvmDesc(),
                false,
        )
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        if (AFTER_MEANS_FINALLY) {
            super.visitLabel(end)
            if (transformLocation.version >= Opcodes.V1_6) {
                super.visitFrame(Opcodes.F_SAME1, 0, null, 1, ARRAY_OF_THROWABLE_JVM_NAME)
            }
            visitCallAfterInsn()
            visitInsn(Opcodes.ATHROW)
            super.visitMaxs(maxStack.coerceAtLeast(1), maxLocals)
        } else {
            super.visitMaxs(maxStack, maxLocals)
        }
    }

    private fun methodJvmDesc() = "()V"

    companion object {
        const val AFTER_MEANS_FINALLY = true

        private val ARRAY_OF_THROWABLE_JVM_NAME = arrayOf(THROWABLE_JVM_NAME)
    }
}
