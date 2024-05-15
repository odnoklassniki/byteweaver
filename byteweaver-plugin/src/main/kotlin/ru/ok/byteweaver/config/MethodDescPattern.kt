package ru.ok.byteweaver.config

sealed class MethodDescPattern(
) {
    abstract val returnTypePattern: TypePattern
    abstract val parameterTypePatterns: List<TypePattern>
    abstract val hasThreeStarsPattern: Boolean
    abstract fun jvmDescMatches(jvmDesc: String): Boolean

    companion object {
        operator fun invoke(
                returnTypePattern: TypePattern,
                parameterTypePatterns: List<TypePattern>,
                hasThreeStarsPattern: Boolean,
        ): MethodDescPattern = when {
            hasThreeStarsPattern && parameterTypePatterns.isEmpty() && returnTypePattern.jvmDescMatchesAny -> StarMethodDescPattern
            !hasThreeStarsPattern && returnTypePattern.exactJvmDesc != null && parameterTypePatterns.all { it.exactJvmDesc != null } -> ExactMethodDescPattern(
                    returnTypePattern,
                    parameterTypePatterns,
            )
            else -> RegularMethodDescPattern(
                    returnTypePattern,
                    parameterTypePatterns,
                    hasThreeStarsPattern,
            )
        }
    }
}

private object StarMethodDescPattern : MethodDescPattern() {
    override val returnTypePattern = TypePattern("*")
    override val parameterTypePatterns = emptyList<TypePattern>()
    override val hasThreeStarsPattern = true
    override fun jvmDescMatches(jvmDesc: String) =
            true
}

private class RegularMethodDescPattern(
        override val returnTypePattern: TypePattern,
        override val parameterTypePatterns: List<TypePattern>,
        override val hasThreeStarsPattern: Boolean,
) : MethodDescPattern() {
    override fun jvmDescMatches(jvmDesc: String): Boolean {
        val bracketIndex = jvmDesc.lastIndexOf(')')
        if (!returnTypePattern.jvmDescMatches(jvmDesc, bracketIndex + 1)) {
            return false
        }
        var startIndex = 1
        for (parameterTypePattern in parameterTypePatterns) {
            if (startIndex >= bracketIndex) {
                return false
            }
            val endIndex = jvmDesc.indexOfEndDesc(startIndex)
            if (!parameterTypePattern.jvmDescMatches(jvmDesc, startIndex, endIndex)) {
                return false
            }
            startIndex = endIndex
        }
        if (startIndex < bracketIndex) {
            return hasThreeStarsPattern
        }
        return true
    }

    private fun String.indexOfEndDesc(startIndex: Int = 0): Int = when (get(startIndex)) {
        in "BSIJZCFD" -> startIndex + 1
        '[' -> indexOfEndDesc(startIndex + 1)
        'L' -> indexOf(';', startIndex = startIndex) + 1
        else -> throw IllegalArgumentException(this)
    }
}

private class ExactMethodDescPattern(
        override val returnTypePattern: TypePattern,
        override val parameterTypePatterns: List<TypePattern>,
) : MethodDescPattern() {
    private val exactJvmDesc = buildString {
        append('(')
        parameterTypePatterns.forEach {
            append(it.exactJvmDesc)
        }
        append(')')
        append(returnTypePattern.exactJvmDesc)
    }
    override val hasThreeStarsPattern get() = false
    override fun jvmDescMatches(jvmDesc: String) =
            jvmDesc == exactJvmDesc
}