package ru.ok.byteweaver.config

import java.util.regex.Pattern

sealed interface NamePattern {
    fun nameMatches(name: String): Boolean

    companion object {
        operator fun invoke(value: String): NamePattern {
            val length = value.length
            val indexOfStar = value.indexOf('*')
            if (indexOfStar < 0) {
                return LiteralName(value)
            }
            val lastIndexOfStar = value.lastIndexOf('*')
            if (length == 1 || length == 2 && lastIndexOfStar == 1) {
                return StarNamePattern
            }
            if (indexOfStar == lastIndexOfStar || indexOfStar == lastIndexOfStar - 1) {
                return SimpleNamePattern(value)
            }
            return RegexNamePattern(value)
        }
    }
}

private object StarNamePattern : NamePattern {
    override fun nameMatches(name: String) = name.isNotEmpty()

    override fun toString() = "*"
}

private class SimpleNamePattern(val value: String) : NamePattern {
    private val jvmNamePrefix: String
    private val jvmNameSuffix: String
    private val minJvmNameLength: Int

    init {
        val indexOfStar = value.indexOf('*')
        val lastIndexOfStar = value.lastIndexOf('*')

        jvmNamePrefix = value.substring(0, indexOfStar).replace('.', '/')
        jvmNameSuffix = value.substring(lastIndexOfStar + 1, value.length).replace('.', '/')
        minJvmNameLength = jvmNamePrefix.length + jvmNameSuffix.length
    }

    override fun nameMatches(name: String): Boolean {
        if (name.length < minJvmNameLength) {
            return false
        }
        if (!name.startsWith(jvmNamePrefix)) {
            return false
        }
        if (!name.endsWith(jvmNameSuffix)) {
            return false
        }
        return true
    }

    override fun toString() = value
}

private class RegexNamePattern(val value: String) : NamePattern {
    private val jvmNamePattern: Pattern = value
            .replace("*", ".*")
            .let(Pattern::compile)

    override fun nameMatches(name: String): Boolean {
        return jvmNamePattern.matcher(name).matches()
    }

    override fun toString() = value
}
