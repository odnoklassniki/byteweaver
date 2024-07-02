package ru.ok.byteweaver.config

import java.io.IOException
import java.io.LineNumberReader
import java.io.Reader
import java.util.regex.Pattern
import kotlin.text.StringBuilder

private const val TYPE_PATTERN = "[^\\s]+"
private const val CLASS_NAME_PATTERN = "[^\\s]+"

fun parseConfig(reader: Reader): List<ClassBlock> {
    val classBlocks = mutableListOf<ClassBlock>()
    parseConfigInto(reader, classBlocks)
    return classBlocks
}

fun parseConfigInto(reader: Reader, outClassBlocks: MutableList<ClassBlock>) {
    parseConfigInto(reader as? LineNumberReader ?: LineNumberReader(reader), outClassBlocks)
}

private fun parseConfigInto(reader: LineNumberReader, outClassBlocks: MutableList<ClassBlock>) {
    val outImports = mutableMapOf<String, String>()
    val statements = reader.readStatements(topLevel = true)

    for (s in statements) {
        if (parseImportInto(s, outImports)) continue
        if (parseClassInto(s, outImports, outClassBlocks)) continue
        else throw unknownStatementException(s)
    }
}

private val IMPORT_PATTERN: Pattern = Pattern.compile("import (.+\\.([^.]+))")

private fun parseImportInto(
        statement: Statement,
        outImports: MutableMap<String, String>,
): Boolean {
    val matcher = IMPORT_PATTERN.matcher(statement.value)
    if (!matcher.matches()) {
        return false
    }
    if (statement.block.isNotEmpty()) {
        throw IOException("No block expected at line ${statement.lineNumber}")
    }

    val fullName = matcher.group(2)
    val shortName = matcher.group(1)

    outImports[fullName] = shortName

    return true
}

private val CLASS_PATTERN: Pattern = Pattern.compile("class ($CLASS_NAME_PATTERN)(?: extends ((?:.+,)*.+))?")

private fun parseClassInto(
        statement: Statement,
        imports: Map<String, String>,
        outClassBlocks: MutableList<ClassBlock>,
): Boolean {
    val annotationPatterns = statement.annotations.map { parseAnnotation(imports, it) }

    val matcher = CLASS_PATTERN.matcher(statement.value)
    if (!matcher.matches()) {
        return false
    }

    val namePattern = matcher.group(1).let(ClassPattern::invoke)
    val ancestorNames = matcher.group(2)
            ?.split(',')
            .let { it ?: emptyList() }
            .map(imports::import)
            .map(::ClassName)

    val methodBlocks = mutableListOf<MethodBlock>()

    for (s in statement.block) {
        if (parseMethodInto(s, imports, ancestorNames, methodBlocks)) continue
        else throw unknownStatementException(s)
    }

    outClassBlocks += ClassBlock(
            annotationPatterns = annotationPatterns,
            namePattern = namePattern,
            ancestorNames = ancestorNames,
            methodBlocks = methodBlocks,
    )
    return true
}

private val METHOD_PATTERN: Pattern = Pattern.compile("(.+) (.+)\\(((?:.+,)*.+)?\\)")

private fun parseMethodInto(
        statement: Statement,
        imports: Map<String, String>,
        ancestorNames: List<ClassName>,
        outMethodBlocks: MutableList<MethodBlock>,
): Boolean {
    val annotationPatterns = statement.annotations.map { parseAnnotation(imports, it) }

    val matcher = METHOD_PATTERN.matcher(statement.value)
    if (!matcher.matches()) {
        return false
    }

    val returnTypePattern = matcher.group(1).let(TypePattern::invoke)
    val namePattern = matcher.group(2).let(NamePattern::invoke)
    val rawParams = matcher.group(3)
            ?.split(',')
            .let { it ?: emptyList() }

    val hasThreeStarsPattern = rawParams.lastOrNull() == "***"
    val parameterTypePatterns = rawParams
            .let { if (hasThreeStarsPattern) it.dropLast(1) else it }
            .map(imports::import)
            .map(TypePattern::invoke)

    val operations = mutableListOf<Operation>()
    val callBlocks = mutableListOf<CallBlock>()

    for (s in statement.block) {
        if (parseCallInfo(s, imports, callBlocks)) continue
        if (parseOperationInto(s, imports, operations)) continue
        else throw unknownStatementException(s)
    }

    outMethodBlocks += MethodBlock(
            annotationPatterns = annotationPatterns,
            namePattern = namePattern,
            classAncestorNames = ancestorNames,
            descPattern = MethodDescPattern(
                    returnTypePattern = returnTypePattern,
                    parameterTypePatterns = parameterTypePatterns,
                    hasThreeStarsPattern = hasThreeStarsPattern,
            ),
            operations = operations,
            callBlocks = callBlocks,
    )

    return true
}

private val CALL_PATTERN: Pattern = Pattern.compile("($TYPE_PATTERN) ($CLASS_NAME_PATTERN)\\.([^.]+)\\(((?:.+,)*.+)?\\)")

private fun parseCallInfo(
        statement: Statement,
        imports: Map<String, String>,
        outCallBlocks: MutableList<CallBlock>,
): Boolean {
    val matcher = CALL_PATTERN.matcher(statement.value)
    if (!matcher.matches()) {
        return false
    }

    val returnTypeName = matcher.group(1)
            .let(imports::import)
            .let(TypeName::invoke)

    val declaringClassName = matcher.group(2)
            .let(imports::import)
            .let(DeclaringClassPattern::invoke)

    val methodName = matcher.group(3)
            .let(::LiteralName)

    val rawParams = matcher.group(4)
            ?.split(',')
            .let { it ?: emptyList() }

    val hasThreeStarsPattern = rawParams.lastOrNull() == "***"

    val parameterTypePatterns = rawParams
            .let { if (hasThreeStarsPattern) it.dropLast(1) else it }
            .map(imports::import)
            .map(TypePattern::invoke)

    val operations = mutableListOf<Operation>()

    for (s in statement.block) {
        if (parseOperationInto(s, imports, operations)) continue
        else throw unknownStatementException(s)
    }

    outCallBlocks += CallBlock(
            declaringClassPattern = declaringClassName,
            methodName = methodName,
            descPattern = MethodDescPattern(
                    returnTypePattern = returnTypeName,
                    parameterTypePatterns = parameterTypePatterns,
                    hasThreeStarsPattern = hasThreeStarsPattern,
            ),
            operations = operations,
    )
    return true
}

private val OPERATION_PATTERN: Pattern = Pattern.compile("(before|after|replace) ($TYPE_PATTERN) ($CLASS_NAME_PATTERN)\\.([^.]+)\\(((?:.+,)*.+)?\\)")

private fun parseOperationInto(
        statement: Statement,
        imports: Map<String, String>,
        outOperations: MutableList<Operation>,
): Boolean {
    val matcher = OPERATION_PATTERN.matcher(statement.value)
    if (!matcher.matches()) {
        return false
    }
    if (statement.block.isNotEmpty()) {
        throw IOException("No block expected at line ${statement.lineNumber}")
    }

    val op = matcher.group(1)
            .uppercase()
            .let(Op::valueOf)

    val returnTypeName = matcher.group(2)
            .let(imports::import)
            .let(TypeName::invoke)

    val declaringClassName = matcher.group(3)
            .let(imports::import)
            .let(::ClassName)

    val methodName = matcher.group(4)
            .let(::LiteralName)

    val parameters = matcher.group(5)
            ?.split(',')
            .let { it ?: emptyList() }
            .map { Parameter(it) }

    outOperations += Operation(
            op = op,
            returnTypeName = returnTypeName,
            declaringClassName = declaringClassName,
            methodName = methodName,
            parameters = parameters,
    )
    return true
}

private val ANNOTATION_PATTERN = Pattern.compile("@([^(]*)(?:\\((.*)\\))?")
private fun parseAnnotation(
        imports: Map<String, String>,
        annotation: String,
): AnnotationPattern {
    val matcher = ANNOTATION_PATTERN.matcher(annotation)
    if (!matcher.matches()) {
        throw IllegalArgumentException(annotation)
    }
    val className = matcher.group(1)
            .let(imports::import)
            .let(::ClassName)
    val arguments = matcher.group(2)
            ?.split(",")
            .let { it ?: emptyList() }
            .map { it.splitPair("=") }
            .map { LiteralName(it.first) to it.second }
            .toMap()

    return AnnotationPattern(className, arguments)
}

private fun Map<String, String>.import(name: String): String {
    return getOrDefault(name, name)
}

private class Statement(
        val annotations: List<String>,
        val value: String,
        val block: List<Statement>,
        val lineNumber: Int = -1,
) {
    override fun toString() = value + if (block.isNotEmpty()) " {...}" else ";"
}

private fun Reader.readStatements(topLevel: Boolean = true): List<Statement> {
    val result = arrayListOf<Statement>()
    while (true) {
        result += readStatement(topLevel) ?: break
    }
    return result
}

private fun Reader.readStatement(topLevel: Boolean): Statement? {
    var r: Int = read()
    while (true) {
        if (r < 0) {
            if (topLevel) {
                return null
            }
        }
        val c = r.toChar()
        if (!c.isWhitespace()) {
            break
        }
        r = read()
    }

    val annotations: List<String>
    if (r.toChar() != '@') {
        annotations = emptyList()
    } else {
        annotations = mutableListOf()

        val anno = StringBuilder()
        while (true) {
            while (true) {
                val c = r.toChar()
                when {
                    c.isWhitespace() -> when {
                        anno.last() == '@' -> Unit
                        anno.last().isWhitespace() -> Unit
                        anno.last() == ')' -> Unit
                        else -> anno.append(' ')
                    }
                    c == '(' -> {
                        anno.append('(').append(readBraced()).append(')')
                    }
                    else -> when {
                        anno.isEmpty() -> anno.append(c)
                        anno.last().isWhitespace() -> break
                        anno.last() == ')' -> break
                        else -> anno.append(c)
                    }
                }
                r = read()
            }
            annotations += anno.apply { if (last().isWhitespace()) deleteCharAt(lastIndex) }.toString()

            if (r.toChar() != '@') {
                break
            }
        }
    }

    val result = StringBuilder()
    val lineNumber = lineNumber
    fun statement(block: List<Statement>) = result
            .apply { if (isNotEmpty() && last().isWhitespace()) deleteCharAt(lastIndex) }
            .toString()
            .let { Statement(annotations, it, block, lineNumber) }

    while (true) {
        if (r < 0) {
            throw IOException("Unexpected EOF at line $lineNumberString")
        }
        val c = r.toChar()
        when {
            c == '{' -> return statement(readStatements(topLevel = false))
            c == ';' -> return statement(emptyList())
            c == '}' -> when {
                topLevel || result.isNotEmpty() -> throw IOException("Unexpected block end at line $lineNumberString")
                else -> return null
            }
            c.isWhitespace() -> when {
                result.last() in ".,[]()" -> Unit
                result.last().isWhitespace() -> Unit
                else -> result.append(' ')
            }
            c in ".,[]()" -> when {
                result.last().isWhitespace() -> result.deleteCharAt(result.lastIndex).append(c)
                else -> result.append(c)
            }
            else -> result.append(c)
        }
        r = read()
    }
}

private fun Reader.readBraced(): String {
    val result = StringBuilder()
    while (true) {
        val r = read()
        if (r < 0) {
            throw IOException("Unexpected EOF at line $lineNumberString")
        }
        val c = r.toChar()
        when {
            c == ')' -> break
            c.isWhitespace() -> when {
                result.last() in ".,=" -> Unit
                result.last().isWhitespace() -> Unit
                else -> result.append(' ')
            }
            c in ".,=" -> when {
                result.last().isWhitespace() -> result.deleteCharAt(result.lastIndex).append(c)
                else -> result.append(c)
            }
            else -> result.append(c)
        }
    }
    return result.toString()
}

private val Reader.lineNumber
    get() = when (this) {
        is LineNumberReader -> lineNumber
        else -> -1
    }

private val Reader.lineNumberString
    get() = when (this) {
        is LineNumberReader -> lineNumber.toString()
        else -> "unknown"
    }

private fun unknownStatementException(statement: Statement): Throwable = IOException("Unknown statement at line " + statement.lineNumber)

private fun String.splitPair(delimiter: String): Pair<String, String> {
    val index = indexOf(delimiter)
    if (index < 0) {
        throw IllegalArgumentException()
    }
    return substring(0, index) to substring(index + delimiter.length)
}
