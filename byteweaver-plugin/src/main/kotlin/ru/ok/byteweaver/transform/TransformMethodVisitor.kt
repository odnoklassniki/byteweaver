package ru.ok.byteweaver.transform

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

abstract class TransformMethodVisitor(
        api: Int = Opcodes.ASM6,
        mv: MethodVisitor?,
) : MethodVisitor(api, mv) {
    protected abstract val transformLocation: TransformLocation
}
