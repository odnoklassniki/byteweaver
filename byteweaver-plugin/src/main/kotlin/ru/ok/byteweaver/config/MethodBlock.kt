package ru.ok.byteweaver.config

class MethodBlock(
        val annotationPatterns: List<AnnotationPattern>,
        val namePattern: NamePattern,
        val descPattern: MethodDescPattern,
        val operations: List<Operation>,
        val callBlocks: List<CallBlock>,
) {
    override fun toString() = buildString {
        for (annotationPattern in annotationPatterns) {
            if (isNotEmpty()) {
                append(' ')
            }
            append(annotationPattern)
        }
        append(descPattern.returnTypePattern).append(" ").append(namePattern)
        append('(')
        var first = true
        for (parameterTypePattern in descPattern.parameterTypePatterns) {
            if (!first) {
                append(", ")
            } else {
                first = false
            }
            append(parameterTypePattern)
        }
        if (descPattern.hasThreeStarsPattern) {
            if (!first) {
                append(", ")
            }
            append("***")
        }
        append(')')
        if (operations.isNotEmpty() || callBlocks.isNotEmpty()) {
            append(" { ... }")
        } else {
            append(';')
        }
    }
}
