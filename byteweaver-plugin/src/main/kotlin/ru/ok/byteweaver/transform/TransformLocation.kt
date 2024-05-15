package ru.ok.byteweaver.transform

data class TransformLocation(
        val enclosingClassJvmName: String?,
        val enclosingMethodName: String?,
        val declaringClassJvmName: String,
        val methodName: String,
        val methodJvmDesc: String,
        val fileName: String?,
        val version: Int,
) {
    val declaringClassJavaName: String
        get() = declaringClassJvmName.replace('/', '.') // TODO use enclosingClassJvmName
}
