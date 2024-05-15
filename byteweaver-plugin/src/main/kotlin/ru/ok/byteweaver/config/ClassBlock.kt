package ru.ok.byteweaver.config

class ClassBlock(
        val annotationPatterns: List<AnnotationPattern>,
        val namePattern: ClassPattern,
        val ancestorNames: List<ClassName>,
        val methodBlocks: List<MethodBlock>,
) {
    override fun toString() = buildString {
        for (annotationPattern in annotationPatterns) {
            if (isNotEmpty()) {
                append(' ')
            }
            append(annotationPattern)
        }
        append("class ").append(namePattern)
        if (ancestorNames.isNotEmpty()) {
            append(" extends ")
            var first = true
            for (ancestorName in ancestorNames) {
                if (first) {
                    first = false
                } else {
                    append(", ")
                }
                append(ancestorName)
            }
        }
        if (methodBlocks.isNotEmpty()) {
            append(" { ... }")
        } else {
            append(';')
        }
    }
}
