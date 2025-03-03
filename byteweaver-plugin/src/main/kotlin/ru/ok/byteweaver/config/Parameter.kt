package ru.ok.byteweaver.config

import java.lang.IllegalArgumentException

sealed class Parameter {
    abstract val name: String

    companion object {
        operator fun invoke(value: String) = when (value) {
            "this" -> ThisParameter
            "self" -> SelfParameter
            "trace" -> TraceParameter
            else -> {
                val split = value.split(" ")
                val type = when (split.size) {
                    1 -> null
                    2 -> TypeName(split[1])
                    else -> throw IllegalArgumentException(value)
                }
                val position = split.last().toInt()
                ForwardParameter(type, position)
            }
        }
    }
}

object ThisParameter : Parameter() {
    override val name get() = "this"
    override fun toString() = name
}

object SelfParameter : Parameter() {
    override val name get() = "self"
    override fun toString() = name
}

object TraceParameter : Parameter() {
    override val name get() = "trace"
    override fun toString() = name
}

class ForwardParameter(
        val typeName: TypeName? = null,
        val position: Int,
) : Parameter() {
    init {
        check(typeName == null) { TODO() }
    }

    override val name get() = position.toString()
    override fun equals(other: Any?) = other is ForwardParameter && typeName == other.typeName && position == other.position
    override fun hashCode() = typeName.hashCode() + position
    override fun toString() = if (typeName != null) "$typeName $position" else position.toString()
}
