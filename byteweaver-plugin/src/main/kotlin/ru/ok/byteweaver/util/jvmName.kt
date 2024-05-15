package ru.ok.byteweaver.util

val Class<*>.jvmName
    get() = name.replace('.', '/')

val Class<*>.jvmDesc
    get() = "L$jvmName;"
