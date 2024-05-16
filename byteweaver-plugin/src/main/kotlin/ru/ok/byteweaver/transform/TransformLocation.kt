package ru.ok.byteweaver.transform

@Suppress("ArrayInDataClass")
data class TransformLocation(
        val enclosingClassJvmName: String?,
        val enclosingMethodName: String?,
        val declaringClassJvmName: String,
        val superClassJvmName: String?,
        val interfaceJvmNames: Array<out String>?,
        val knownSuperClassJavaNames: List<String>?,
        val knownInterfaceJavaNames: List<String>?,
        val methodName: String,
        val methodJvmDesc: String,
        val fileName: String?,
        val version: Int,
) {
    val declaringClassJavaName: String
        get() = declaringClassJvmName.replace('/', '.') // TODO use enclosingClassJvmName
}
