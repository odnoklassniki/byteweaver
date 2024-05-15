package ru.ok.byteweaver.config

import ru.ok.byteweaver.util.PRIMITIVE_BOOLEAN_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_BOOLEAN_JVM_DESC
import ru.ok.byteweaver.util.PRIMITIVE_BYTE_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_BYTE_JVM_DESC
import ru.ok.byteweaver.util.PRIMITIVE_CHAR_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_CHAR_JVM_DESC
import ru.ok.byteweaver.util.PRIMITIVE_DOUBLE_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_DOUBLE_JVM_DESC
import ru.ok.byteweaver.util.PRIMITIVE_FLOAT_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_FLOAT_JVM_DESC
import ru.ok.byteweaver.util.PRIMITIVE_INT_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_INT_JVM_DESC
import ru.ok.byteweaver.util.PRIMITIVE_LONG_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_LONG_JVM_DESC
import ru.ok.byteweaver.util.PRIMITIVE_SHORT_JAVA_NAME
import ru.ok.byteweaver.util.PRIMITIVE_SHORT_JVM_DESC
import ru.ok.byteweaver.util.PRIMITIVE_VOID_JAVA_NAME
import ru.ok.byteweaver.util.VOID_JVM_DESC
import java.lang.IllegalArgumentException

sealed interface TypeName : TypePattern {
    val javaName: String
    val jvmDesc: String

    companion object {
        operator fun invoke(value: String): TypeName = when {
            value.contains("*") -> throw IllegalArgumentException(value)
            value.endsWith("[]") -> ArrayTypeName(value)
            else -> when (value) {
                PRIMITIVE_VOID_JAVA_NAME -> VOID_TYPE_NAME
                PRIMITIVE_BYTE_JAVA_NAME -> BYTE_TYPE_NAME
                PRIMITIVE_SHORT_JAVA_NAME -> SHORT_TYPE_NAME
                PRIMITIVE_INT_JAVA_NAME -> INT_TYPE_NAME
                PRIMITIVE_LONG_JAVA_NAME -> LONG_TYPE_NAME
                PRIMITIVE_BOOLEAN_JAVA_NAME -> BOOLEAN_TYPE_NAME
                PRIMITIVE_CHAR_JAVA_NAME -> CHAR_TYPE_NAME
                PRIMITIVE_FLOAT_JAVA_NAME -> FLOAT_TYPE_NAME
                PRIMITIVE_DOUBLE_JAVA_NAME -> DOUBLE_TYPE_NAME
                else -> ClassName(value)
            }
        }
    }
}

private class ArrayTypeName(
        override val javaName: String,
) : TypeName {
    private val elementTypeName = TypeName(javaName.dropLast(2))

    override val jvmDesc = "[" + elementTypeName.jvmDesc

    override fun jvmDescMatches(desc: String, startIndex: Int, endIndex: Int): Boolean {
        return desc.regionMatches(startIndex, jvmDesc, 0, endIndex - startIndex)
    }

    override fun toString() = javaName
}

private class PrimitiveTypeName(
        override val javaName: String,
        override val jvmDesc: String,
) : TypeName {
    override fun jvmDescMatches(desc: String, startIndex: Int, endIndex: Int): Boolean {
        return desc.regionMatches(startIndex, jvmDesc, 0, length = 1)
    }

    override fun toString() = javaName
}

private val VOID_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_VOID_JAVA_NAME, VOID_JVM_DESC)
private val BYTE_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_BYTE_JAVA_NAME, PRIMITIVE_BYTE_JVM_DESC)
private val SHORT_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_SHORT_JAVA_NAME, PRIMITIVE_SHORT_JVM_DESC)
private val INT_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_INT_JAVA_NAME, PRIMITIVE_INT_JVM_DESC)
private val LONG_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_LONG_JAVA_NAME, PRIMITIVE_LONG_JVM_DESC)
private val BOOLEAN_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_BOOLEAN_JAVA_NAME, PRIMITIVE_BOOLEAN_JVM_DESC)
private val CHAR_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_CHAR_JAVA_NAME, PRIMITIVE_CHAR_JVM_DESC)
private val FLOAT_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_FLOAT_JAVA_NAME, PRIMITIVE_FLOAT_JVM_DESC)
private val DOUBLE_TYPE_NAME = PrimitiveTypeName(PRIMITIVE_DOUBLE_JAVA_NAME, PRIMITIVE_DOUBLE_JVM_DESC)
