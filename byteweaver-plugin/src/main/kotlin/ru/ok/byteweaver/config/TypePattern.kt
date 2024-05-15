package ru.ok.byteweaver.config

sealed interface TypePattern {
    fun jvmDescMatches(desc: String, startIndex: Int = 0, endIndex: Int = desc.length): Boolean

    companion object {
        operator fun invoke(value: String): TypePattern {
            val indexOfStar = value.indexOf('*')
            if (indexOfStar < 0) {
                return TypeName(value)
            }
            val length = value.length
            if (length == 1) {
                return StarTypePattern
            }
            if (value.endsWith("[]")) {
                return ArrayTypePattern(value)
            }
            return ClassPattern(value)
        }
    }
}

val TypePattern.exactJvmDesc: String?
    get() = when (this) {
        is TypeName -> jvmDesc
        is ArrayTypePattern -> elementTypePattern.exactJvmDesc?.let { "[$it" }
        else -> null
    }

val TypePattern.jvmDescMatchesAny get() = this is StarTypePattern

private object StarTypePattern : TypePattern {
    override fun jvmDescMatches(desc: String, startIndex: Int, endIndex: Int) = startIndex < endIndex

    override fun toString() = "*"
}

private class ArrayTypePattern(private val value: String) : TypePattern {
    val elementTypePattern = TypePattern(value.dropLast(2))

    override fun jvmDescMatches(desc: String, startIndex: Int, endIndex: Int): Boolean {
        return desc[startIndex] == '[' && elementTypePattern.jvmDescMatches(desc, startIndex + 1, endIndex)
    }

    override fun toString() = value
}
