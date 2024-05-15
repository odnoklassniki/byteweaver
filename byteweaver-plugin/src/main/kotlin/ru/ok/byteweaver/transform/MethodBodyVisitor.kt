package ru.ok.byteweaver.transform

import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

abstract class MethodBodyVisitor(
        api: Int = Opcodes.ASM6,
        mv: MethodVisitor?,
) : TransformMethodVisitor(api, mv) {
    open fun beforeVisitFirstInsn() = Unit

    open fun beforeVisitLastInsn(opcode: Int) = Unit

    private var visitedInsn = false

    @Suppress("NOTHING_TO_INLINE")
    private inline fun visitSomeInsn() {
        if (visitedInsn) {
            return
        }
        visitedInsn = true
        beforeVisitFirstInsn()
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {
        visitSomeInsn()
        super.visitIntInsn(opcode, operand)
    }

    override fun visitVarInsn(opcode: Int, `var`: Int) {
        visitSomeInsn()
        super.visitVarInsn(opcode, `var`)
    }

    override fun visitTypeInsn(opcode: Int, type: String?) {
        visitSomeInsn()
        super.visitTypeInsn(opcode, type)
    }

    override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
        visitSomeInsn()
        super.visitFieldInsn(opcode, owner, name, descriptor)
    }

    override fun visitMethodInsn(opcode: Int, declaringClassJvmName: String, methodName: String, methodJvmDesc: String, isInterface: Boolean) {
        visitSomeInsn()
        super.visitMethodInsn(opcode, declaringClassJvmName, methodName, methodJvmDesc, isInterface)
    }

    override fun visitInvokeDynamicInsn(name: String?, descriptor: String?, bootstrapMethodHandle: Handle?, vararg bootstrapMethodArguments: Any?) {
        visitSomeInsn()
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
    }

    override fun visitJumpInsn(opcode: Int, label: Label?) {
        visitSomeInsn()
        super.visitJumpInsn(opcode, label)
    }

    override fun visitLdcInsn(value: Any?) {
        visitSomeInsn()
        super.visitLdcInsn(value)
    }

    override fun visitIincInsn(`var`: Int, increment: Int) {
        visitSomeInsn()
        super.visitIincInsn(`var`, increment)
    }

    override fun visitTableSwitchInsn(min: Int, max: Int, dflt: Label?, vararg labels: Label?) {
        visitSomeInsn()
        super.visitTableSwitchInsn(min, max, dflt, *labels)
    }

    override fun visitLookupSwitchInsn(dflt: Label?, keys: IntArray?, labels: Array<out Label>?) {
        visitSomeInsn()
        super.visitLookupSwitchInsn(dflt, keys, labels)
    }

    override fun visitMultiANewArrayInsn(descriptor: String?, numDimensions: Int) {
        visitSomeInsn()
        super.visitMultiANewArrayInsn(descriptor, numDimensions)
    }

    override fun visitLabel(label: Label?) {
        visitSomeInsn()
        super.visitLabel(label)
    }

    override fun visitInsn(opcode: Int) {
        visitSomeInsn()
        when (opcode) {
            Opcodes.RETURN,
            Opcodes.ARETURN,
            Opcodes.DRETURN,
            Opcodes.FRETURN,
            Opcodes.IRETURN,
            Opcodes.LRETURN,
            Opcodes.ATHROW,
            -> beforeVisitLastInsn(opcode)
        }
        super.visitInsn(opcode)
    }
}
