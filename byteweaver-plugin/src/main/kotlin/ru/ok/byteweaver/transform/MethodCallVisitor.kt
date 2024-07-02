package ru.ok.byteweaver.transform

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import ru.ok.byteweaver.config.CallBlock
import ru.ok.byteweaver.config.DeclaringClassPattern
import ru.ok.byteweaver.config.Operation

abstract class MethodCallVisitor(
        api: Int = Opcodes.ASM6,
        mv: MethodVisitor?,
) : TransformMethodVisitor(api, mv) {
    abstract val callBlock: CallBlock

    abstract val operation: Operation

    protected open fun transformVisitMethodInsn(
            opcode: Int,
            declaringClassPattern: DeclaringClassPattern,
            methodName: String,
            methodJvmDesc: String,
            isInterface: Boolean,
    ) {
        super.visitMethodInsn(opcode, declaringClassPattern.declaringJvmName, methodName, methodJvmDesc, isInterface)
    }

    final override fun visitMethodInsn(
            opcode: Int,
            declaringClassJvmName: String,
            methodName: String,
            methodJvmDesc: String,
            isInterface: Boolean,
    ) {
        val matches = callBlock.declaringClassPattern.jvmNameMatches(declaringClassJvmName)
                && callBlock.methodName.nameMatches(methodName)
                && callBlock.descPattern.jvmDescMatches(methodJvmDesc)
        if (matches) {
            val matchesBack = operation.declaringClassName.jvmNameMatches(transformLocation.declaringClassJvmName)
                    && operation.methodName.nameMatches(transformLocation.methodName)
                    && composedSelfMethodJvmDescMatches(declaringClassJvmName, methodJvmDesc, transformLocation.methodJvmDesc)
            if (!matchesBack) {
                transformVisitMethodInsn(opcode, callBlock.declaringClassPattern, methodName, methodJvmDesc, isInterface)
                return
            }
        }
        super.visitMethodInsn(opcode, declaringClassJvmName, methodName, methodJvmDesc, isInterface)
    }

    companion object {
        @JvmStatic
        protected fun composeSelfMethodJvmDesc(selfClassJvmName: String, restMethodJvmDesc: String) = buildString {
            append(restMethodJvmDesc, 0, 1)
            append('L').append(selfClassJvmName).append(";")
            append(restMethodJvmDesc, 1, restMethodJvmDesc.length)
        }

        @JvmStatic
        protected fun composedSelfMethodJvmDescMatches(selfClassJvmName: String, restMethodJvmDesc: String, matchingMethodJvmDesc: String): Boolean {
            return matchingMethodJvmDesc[1] == 'L'
                    && matchingMethodJvmDesc.regionMatches(2, selfClassJvmName, 0, selfClassJvmName.length)
                    && matchingMethodJvmDesc[2 + selfClassJvmName.length] == ';'
                    && matchingMethodJvmDesc.regionMatches(3 + selfClassJvmName.length, restMethodJvmDesc, 1, restMethodJvmDesc.length - 1)
        }
    }
}
