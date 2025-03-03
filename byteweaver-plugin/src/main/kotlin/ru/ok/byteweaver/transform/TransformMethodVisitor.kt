package ru.ok.byteweaver.transform

import org.objectweb.asm.MethodVisitor

abstract class TransformMethodVisitor(
        api: Int,
        mv: MethodVisitor?,
) : MethodVisitor(api, mv) {
    protected abstract val transformLocation: TransformLocation
}
