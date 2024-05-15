package ru.ok.byteweaver

import java.io.InputStream
import java.io.Reader
import java.net.URL

class Resource internal constructor(
        private val url: URL,
) {
    fun stream(): InputStream = url.openStream().buffered()
    fun reader(): Reader = url.openStream().bufferedReader()
    fun bytes() = url.openStream().readBytes()
    fun text() = url.openStream().reader().readText()
}

fun resource(name: String) = Resource::class.java.classLoader.getResource(name).let(::Resource)
