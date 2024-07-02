package ru.ok.byteweaver.transform

import com.example.ExampleActivity
import com.example.ExampleAutoTrace
import com.example.ExampleCall
import com.example.ExampleCallDelegate
import com.example.ExampleNonActivity
import com.example.ExampleNotification
import com.example.ExamplePreferences
import com.example.ExampleRunnable
import com.example.ExampleService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.AnalyzerException
import org.objectweb.asm.tree.analysis.SimpleVerifier
import org.objectweb.asm.util.CheckClassAdapter
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import ru.ok.android.prefs.FastSharedPreferences
import ru.ok.byteweaver.config.ClassBlock
import ru.ok.byteweaver.config.parseConfig
import ru.ok.byteweaver.resource
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.AssertionError

class TransformClassVisitorTest {
    private val classBlocks: List<ClassBlock> = listOf(
        "example-call.conf",
        "example-notification.conf",
        "example-trace.conf",
        "example-preferences.conf",
    ).flatMap(::parseConfig)

    @Test
    fun testCall() {
        val classLoader = BytesClassLoader(
            "com.example.ExampleCall" to ExampleCall::class.java.transform(classBlocks),
            "com.example.ExampleCallDelegate" to ExampleCallDelegate::class.java.bytes,
        )

        val exampleClass = classLoader.loadClass("com.example.ExampleCall")
        val exampleConstructor = exampleClass.getConstructor()
        val exampleMethod = exampleClass.getMethod("method")
        val exampleSomeOtherMethodCalled = exampleClass.getField("someOtherMethodCalled")
        val exampleInstance = exampleConstructor.newInstance()

        exampleMethod.invoke(exampleInstance)

        assertFalse(exampleSomeOtherMethodCalled[exampleInstance] as Boolean)

        val exampleDelegateClass = classLoader.loadClass("com.example.ExampleCallDelegate")
        val exampleDelegateBeforeMethodCalled = exampleDelegateClass.getField("beforeMethodCalled")
        val exampleDelegateAfterMethodCalled = exampleDelegateClass.getField("afterMethodCalled")
        val exampleDelegateReplaceSomeOtherMethodCalled = exampleDelegateClass.getField("replaceSomeOtherMethodCalled")

        assertTrue(exampleDelegateBeforeMethodCalled[null] as Boolean)
        assertTrue(exampleDelegateAfterMethodCalled[null] as Boolean)
        assertTrue(exampleDelegateReplaceSomeOtherMethodCalled[null] as Boolean)
    }

    @Test
    fun testActivity() {
        val new = ExampleActivity::class.java.transform(classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleActivity-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testActivityNoAncestors() {
        val new = ExampleActivity::class.java.transform(classBlocks, collectAncestors = false)

        val actual = asm(new)
        val expected = resource("ExampleActivity-transformed-no-ancestors.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testNonActivity() {
        val new = ExampleNonActivity::class.java.transform(classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleNonActivity-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testNonActivityNoAncestors() {
        val new = ExampleNonActivity::class.java.transform(classBlocks, collectAncestors = false)

        val actual = asm(new)
        val expected = resource("ExampleNonActivity-transformed-no-ancestors.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testAutoTrace() {
        val new = ExampleAutoTrace::class.java.transform(classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleAutoTrace-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testNotification() {
        val new = ExampleNotification::class.java.transform(classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleNotification-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testService() {
        val new = ExampleService::class.java.transform(classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleService-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testPreferences() {
        val new = ExamplePreferences::class.java.transform(classBlocks)

        val actual = asm(new)
        val expected = resource("ExamplePreferences-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testPreferencesImpl() {
        val new = FastSharedPreferences::class.java.transform(classBlocks)

        val actual = asm(new)
        val expected = resource("ExamplePreferencesImpl-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testRunnable() {
        val new = ExampleRunnable::class.java.transform(classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleRunnable-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }
}

private fun parseConfig(name: String) = resource(name)
        .reader()
        .use { parseConfig(it) }

private val Class<*>.bytes
    get() = resource(name.replace('.', '/') + ".class").bytes()

private fun Class<*>.transform(
    classBlocks: List<ClassBlock>,
    collectAncestors: Boolean = true,
): ByteArray {
    val knownSuperClassJavaNames = if (collectAncestors) collectSuperClasses().map { it.name } else null
    val knownInterfaceJavaNames = if (collectAncestors) interfaces.map { it.name } else null
    val cr = ClassReader(bytes)
    val cw = ClassWriter(cr, 0x0)
    val cv = TransformClassVisitor(
        Opcodes.ASM6,
        cw,
        classBlocks,
        knownSuperClassJavaNames = knownSuperClassJavaNames,
        knownInterfaceJavaNames = knownInterfaceJavaNames
    )
    cr.accept(cv, 0x0)
    return cw.toByteArray()
}

private fun asm(source: ByteArray): String {
    val sw = StringWriter()
    val tcv = TraceClassVisitor(null, Textifier(), PrintWriter(sw))
    val cr = ClassReader(source)
    cr.accept(tcv, 0x0)
    return sw.toString()
}

private fun assertWellFormed(
        classReader: ClassReader,
        loader: ClassLoader?,
) {
    val classNode = ClassNode()
    classReader.accept(
        CheckClassAdapter(classNode, true),
        ClassReader.SKIP_DEBUG
    )
    val superType = if (classNode.superName == null) null else Type.getObjectType(classNode.superName)
    val methods = classNode.methods
    val interfaces = classNode.interfaces
            .map { Type.getObjectType(it) }
    for (method in methods) {
        val verifier = SimpleVerifier(
            Type.getObjectType(classNode.name),
            superType,
            interfaces,
            classNode.access and Opcodes.ACC_INTERFACE != 0
        )
        val analyzer = Analyzer(verifier)
        if (loader != null) {
            verifier.setClassLoader(loader)
        }
        try {
            analyzer.analyze(classNode.name, method)
        } catch (e: AnalyzerException) {
            throw AssertionError("Class ${classNode.name} is not well-formed", e)
        }
    }
}

private fun Class<*>.collectSuperClasses() = buildList {
    while (true) {
        val self = lastOrNull() ?: this@collectSuperClasses
        this += self.superclass ?: break
    }
}

class BytesClassLoader(
    private val map: Map<String, ByteArray>,
) : ClassLoader(null) {
    constructor(vararg pairs: Pair<String, ByteArray>) : this(mapOf(*pairs))

    override fun findClass(name: String?): Class<*> {
        val bytes = map[name] ?: throw NoSuchElementException(name)
        return defineClass(name, bytes, 0, bytes.size)
    }
}
