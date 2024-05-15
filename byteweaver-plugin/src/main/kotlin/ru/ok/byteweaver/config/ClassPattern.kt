package ru.ok.byteweaver.config

import java.util.regex.Pattern

sealed interface ClassPattern : TypePattern {
    fun jvmNameMatches(name: String, startIndex: Int = 0, endIndex: Int = name.length): Boolean

    fun javaNameMatches(name: String, startIndex: Int = 0, endIndex: Int = name.length): Boolean

    override fun jvmDescMatches(desc: String, startIndex: Int, endIndex: Int): Boolean {
        return desc[startIndex] == 'L' && desc[endIndex - 1] == ';' && jvmNameMatches(desc, startIndex + 1, endIndex - 1)
    }

    companion object {
        operator fun invoke(value: String): ClassPattern {
            require(!value.endsWith("[]"))

            val indexOfStar = value.indexOf('*')
            if (indexOfStar < 0) {
                return ClassName(value)
            }
            val length = value.length
            val lastIndexOfStar = value.lastIndexOf('*')
            if (length == 1 || length == 2 && lastIndexOfStar == 1) {
                return StarClassPattern
            }
            if (indexOfStar == lastIndexOfStar || indexOfStar == lastIndexOfStar - 1) {
                return SimpleClassPattern(value)
            }
            return RegexClassPattern(value)
        }
    }
}

private object StarClassPattern : ClassPattern {
    override fun jvmNameMatches(name: String, startIndex: Int, endIndex: Int): Boolean {
        return startIndex < endIndex
    }

    override fun javaNameMatches(name: String, startIndex: Int, endIndex: Int): Boolean {
        return startIndex < endIndex
    }

    override fun toString() = "*"
}

private class SimpleClassPattern(val value: String) : ClassPattern {
    private val javaNamePrefix: String
    private val jvmNamePrefix: String
    private val namePrefixLength: Int
    private val javaNameSuffix: String
    private val jvmNameSuffix: String
    private val nameSuffixLength: Int
    private var hasTwoStars: Boolean

    init {
        val indexOfStar = value.indexOf('*')
        val lastIndexOfStar = value.lastIndexOf('*')

        javaNamePrefix = value.substring(0, indexOfStar)
        jvmNamePrefix = javaNamePrefix.replace('.', '/')
        namePrefixLength = indexOfStar
        javaNameSuffix = value.substring(lastIndexOfStar + 1, value.length)
        jvmNameSuffix = javaNameSuffix.replace('.', '/')
        nameSuffixLength = jvmNameSuffix.length
        hasTwoStars = indexOfStar != lastIndexOfStar
    }

    override fun jvmNameMatches(name: String, startIndex: Int, endIndex: Int): Boolean {
        return nameMatshes(name, startIndex, endIndex, jvmNamePrefix, jvmNameSuffix)
    }

    override fun javaNameMatches(name: String, startIndex: Int, endIndex: Int): Boolean {
        return nameMatshes(name, startIndex, endIndex, javaNamePrefix, javaNameSuffix)
    }

    private fun nameMatshes(name: String, startIndex: Int, endIndex: Int, namePrefix: String, nameSuffix: String): Boolean {
        val infixStartIndex = startIndex + namePrefixLength
        val infixEndIndex = endIndex - nameSuffixLength
        if (infixStartIndex > infixEndIndex) {
            return false
        }
        if (!name.regionMatches(startIndex, namePrefix, 0, namePrefixLength)) {
            return false
        }
        if (!name.regionMatches(infixEndIndex, nameSuffix, 0, nameSuffixLength)) {
            return false
        }
        if (hasTwoStars) {
            return true
        }
        val indexOfSlash = name.indexOf('/', infixStartIndex)
        return indexOfSlash < 0 || indexOfSlash >= infixEndIndex
    }

    override fun toString() = value
}

private class RegexClassPattern(val value: String) : ClassPattern {
    private val javaNameRegex: Pattern = value
            .replace(".", "\\.")
            .replace("**", ".<star>")
            .replace("*", "[^\\.]<star>")
            .replace("<star>", "*")
            .let(Pattern::compile)

    private val jvmNameRegex: Pattern = javaNameRegex
            .pattern()
            .replace("\\.", "/")
            .let(Pattern::compile)

    override fun jvmNameMatches(name: String, startIndex: Int, endIndex: Int): Boolean {
        return nameMatches(name, startIndex, endIndex, jvmNameRegex)
    }

    override fun javaNameMatches(name: String, startIndex: Int, endIndex: Int): Boolean {
        return nameMatches(name, startIndex, endIndex, javaNameRegex)
    }

    private fun nameMatches(name: String, startIndex: Int, endIndex: Int, nameRegex: Pattern): Boolean {
        return name.substring(startIndex, endIndex).let(nameRegex::matcher).matches()
    }

    override fun toString() = value
}
