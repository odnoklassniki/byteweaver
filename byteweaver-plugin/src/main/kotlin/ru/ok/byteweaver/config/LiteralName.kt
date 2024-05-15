package ru.ok.byteweaver.config

class LiteralName(val name: String) : NamePattern {
    override fun nameMatches(name: String) = name == this.name

    override fun toString() = name

    companion object {
        val NAME_COMPARATOR = compareBy(LiteralName::name)
    }
}
