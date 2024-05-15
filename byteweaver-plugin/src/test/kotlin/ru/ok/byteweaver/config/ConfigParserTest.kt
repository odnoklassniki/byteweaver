package ru.ok.byteweaver.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.ok.byteweaver.resource

class ConfigParserTest {
    @Test
    fun testTrace() {
        val classBlocks = resource("example-trace.conf")
                .reader()
                .use(::parseConfig)

        assertEquals(3, classBlocks.size)

        val activityClassBlock = classBlocks.first()

        assertEquals("*", activityClassBlock.namePattern.toString())
        assertEquals(listOf("android.app.Activity"), activityClassBlock.ancestorNames.map(ClassName::toString))
        assertEquals(1, activityClassBlock.methodBlocks.size)

        val onCreateMethodBlock = activityClassBlock.methodBlocks.first()

        assertEquals("void", onCreateMethodBlock.descPattern.returnTypePattern.toString())
        assertEquals("onCreate", onCreateMethodBlock.namePattern.toString())
        assertFalse(onCreateMethodBlock.descPattern.hasThreeStarsPattern)
        assertEquals(1, onCreateMethodBlock.descPattern.parameterTypePatterns.size)
        assertEquals("android.os.Bundle", onCreateMethodBlock.descPattern.parameterTypePatterns.first().toString())
        assertEquals(2, onCreateMethodBlock.operations.size)
        assertTrue(onCreateMethodBlock.callBlocks.isEmpty())

        val beforeOperation = onCreateMethodBlock.operations[0]

        assertEquals(Op.BEFORE, beforeOperation.op)
        assertEquals("void", beforeOperation.returnTypeName.toString())
        assertEquals("ru.ok.android.commons.os.TraceCompat", beforeOperation.declaringClassName.toString())
        assertEquals("beginTraceSection", beforeOperation.methodName.toString())
        assertEquals(listOf("trace"), beforeOperation.parameters.map(Any::toString))
        assertEquals(TraceParameter, beforeOperation.parameters[0])

        val afterOperation = onCreateMethodBlock.operations[1]

        assertEquals(Op.AFTER, afterOperation.op)
        assertEquals("void", afterOperation.returnTypeName.toString())
        assertEquals("ru.ok.android.commons.os.TraceCompat", afterOperation.declaringClassName.toString())
        assertEquals("endSection", afterOperation.methodName.toString())
        assertEquals(emptyList<Any?>(), afterOperation.parameters)

        val anyClassBlock = classBlocks[1]

        assertEquals("*", anyClassBlock.namePattern.toString())
        assertEquals(1, anyClassBlock.methodBlocks.size)

        val autoTraceMethod = anyClassBlock.methodBlocks.first()

        assertEquals(1, autoTraceMethod.annotationPatterns.size)
        assertEquals("@ru.ok.android.commons.os.AutoTrace", autoTraceMethod.annotationPatterns.first().toString())
        assertEquals("*", autoTraceMethod.descPattern.returnTypePattern.toString())
        assertEquals("*", autoTraceMethod.namePattern.toString())

        val runnableClassBlock = classBlocks[2]

        assertEquals("*", runnableClassBlock.namePattern.toString())
        assertEquals(listOf("java.lang.Runnable"), runnableClassBlock.ancestorNames.map(ClassName::toString))
        assertEquals(1, runnableClassBlock.methodBlocks.size)

        val runMethodBlock = runnableClassBlock.methodBlocks.first()

        assertEquals("void", runMethodBlock.descPattern.returnTypePattern.toString())
        assertEquals("run", runMethodBlock.namePattern.toString())
        assertTrue(runMethodBlock.descPattern.parameterTypePatterns.isEmpty())
        assertFalse(runMethodBlock.descPattern.hasThreeStarsPattern)
    }

    @Test
    fun testPreferences() {
        val classBlocks = resource("example-preferences.conf")
                .reader()
                .use(::parseConfig)

        assertEquals(1, classBlocks.size)

        val anyClassBlock = classBlocks.first()

        assertEquals("*", anyClassBlock.namePattern.toString())
        assertTrue(anyClassBlock.ancestorNames.isEmpty())
        assertEquals(1, anyClassBlock.methodBlocks.size)

        val anyMethodBlock = anyClassBlock.methodBlocks.first()

        assertEquals("*", anyMethodBlock.descPattern.returnTypePattern.toString())
        assertEquals("*", anyMethodBlock.namePattern.toString())
        assertTrue(anyMethodBlock.descPattern.parameterTypePatterns.isEmpty())
        assertTrue(anyMethodBlock.descPattern.hasThreeStarsPattern)
        assertTrue(anyMethodBlock.operations.isEmpty())
        assertEquals(1, anyMethodBlock.callBlocks.size)

        val getSharedPreferencesCall = anyMethodBlock.callBlocks.first()

        assertEquals("android.content.SharedPreferences", getSharedPreferencesCall.descPattern.returnTypePattern.toString())
        assertEquals("android.content.Context", getSharedPreferencesCall.declaringClassName.toString())
        assertEquals("getSharedPreferences", getSharedPreferencesCall.methodName.toString())
        assertEquals(2, getSharedPreferencesCall.descPattern.parameterTypePatterns.size)
        assertEquals(listOf("java.lang.String", "int"), getSharedPreferencesCall.descPattern.parameterTypePatterns.map(TypePattern::toString))
        assertEquals(1, getSharedPreferencesCall.operations.size)

        val replaceOperation = getSharedPreferencesCall.operations.first()

        assertEquals(Op.REPLACE, replaceOperation.op)
        assertEquals("android.content.SharedPreferences", replaceOperation.returnTypeName.toString())
        assertEquals("ru.ok.android.prefs.FastSharedPreferences", replaceOperation.declaringClassName.toString())
        assertEquals("getSharedPreferences", replaceOperation.methodName.toString())
        assertEquals(listOf("self", "0", "1"), replaceOperation.parameters.map(Any::toString))
        assertEquals(SelfParameter, replaceOperation.parameters[0])
        assertEquals(ForwardParameter(position = 0), replaceOperation.parameters[1])
        assertEquals(ForwardParameter(position = 1), replaceOperation.parameters[2])
    }
}
