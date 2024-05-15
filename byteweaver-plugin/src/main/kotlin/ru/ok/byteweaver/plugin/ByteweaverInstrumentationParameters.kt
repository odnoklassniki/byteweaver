@file:Suppress("UnstableApiUsage")

package ru.ok.byteweaver.plugin

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File

interface ByteweaverInstrumentationParameters : InstrumentationParameters {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    val srcFiles: ListProperty<File>
}
