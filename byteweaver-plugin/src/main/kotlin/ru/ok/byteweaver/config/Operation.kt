package ru.ok.byteweaver.config

class Operation(
        val op: Op,
        val returnTypeName: TypeName,
        val declaringClassName: ClassName,
        val methodName: LiteralName,
        val parameters: List<Parameter>,
) {
    override fun toString() = buildString {
        append(op.toString().lowercase()).append(' ')
        append(returnTypeName).append(' ')
        append(declaringClassName).append('.').append(methodName)
        append('(')
        var first = true
        for (parameter in parameters) {
            if (first) {
                first = false
            } else {
                append(", ")
            }
            append(parameter)
        }
        append(')')
    }
}
