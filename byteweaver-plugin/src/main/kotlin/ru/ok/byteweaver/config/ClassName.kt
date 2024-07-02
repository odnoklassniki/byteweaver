package ru.ok.byteweaver.config

import ru.ok.byteweaver.util.PRIMITIVE_BOOLEAN_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_BYTE_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_CHAR_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_DOUBLE_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_FLOAT_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_INT_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_LONG_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_SHORT_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_VOID_JAVA_NAME

class ClassName(
        override val javaName: String,
) : DeclaringClassPattern, TypeName {
    init {
        require(!javaName.endsWith("[]"))
        require(javaName != PRIMITIVE_VOID_JAVA_NAME)
        require(javaName != PRIMITIVE_BYTE_JAVA_NAME)
        require(javaName != PRIMITIVE_SHORT_JAVA_NAME)
        require(javaName != PRIMITIVE_INT_JAVA_NAME)
        require(javaName != PRIMITIVE_LONG_JAVA_NAME)
        require(javaName != PRIMITIVE_BOOLEAN_JAVA_NAME)
        require(javaName != PRIMITIVE_CHAR_JAVA_NAME)
        require(javaName != PRIMITIVE_FLOAT_JAVA_NAME)
        require(javaName != PRIMITIVE_DOUBLE_JAVA_NAME)
    }

    val jvmName = javaName.replace('.', '/')
    override val jvmDesc = "L$jvmName;"

    override val declaringJvmName: String
        get() = jvmName

    override fun jvmNameMatches(name: String, startIndex: Int, endIndex: Int): Boolean {
        return name.regionMatches(startIndex, jvmName, 0, endIndex - startIndex)
    }

    override fun javaNameMatches(name: String, startIndex: Int, endIndex: Int): Boolean {
        return name.regionMatches(startIndex, javaName, 0, endIndex - startIndex)
    }

    override fun toString() = javaName
}
