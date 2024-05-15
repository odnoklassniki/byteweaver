package ru.ok.byteweaver.config

class AnnotationPattern(
        val className: ClassName,
        arguments: Map<LiteralName, String>,
) {
    val arguments = sortedMapOf<LiteralName, String>(LiteralName.NAME_COMPARATOR).apply { putAll(arguments) }

    override fun toString() = buildString {
        append("@").append(className)
        if (arguments.isNotEmpty()) {
            append('(')
            var first = true
            for ((name, value) in arguments) {
                if (!first) {
                    append(", ")
                } else {
                    first = false
                }
                append(name).append(" = ").append(value)
            }
            append(')')
        }
    }
}
