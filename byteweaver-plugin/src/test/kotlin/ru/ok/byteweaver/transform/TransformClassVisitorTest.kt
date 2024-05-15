package ru.ok.byteweaver.transform

import com.example.ExampleActivity
import com.example.ExampleAutoTrace
import com.example.ExampleNotification
import com.example.ExamplePreferences
import com.example.ExampleRunnable
import org.junit.Assert.assertEquals
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
    private val classBlocks: List<ClassBlock> = mutableListOf<ClassBlock>()
            .apply {
                addAll(parseConfig("example-notification.conf"))
                addAll(parseConfig("example-trace.conf"))
                addAll(parseConfig("example-preferences.conf"))
            }

    @Test
    fun testActivity() {
        val old = ExampleActivity::class.java.bytes
        val new = transform(old, classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleActivity-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testAutoTrace() {
        val old = ExampleAutoTrace::class.java.bytes
        val new = transform(old, classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleAutoTrace-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testNotification() {
        val old = ExampleNotification::class.java.bytes
        val new = transform(old, classBlocks)

        val actual = asm(new)
        val expected = resource("ExampleNotification-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testPreferences() {
        val old = ExamplePreferences::class.java.bytes
        val new = transform(old, classBlocks)

        val actual = asm(new)
        val expected = resource("ExamplePreferences-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testPreferencesImpl() {
        val old = FastSharedPreferences::class.java.bytes
        val new = transform(old, classBlocks)

        val actual = asm(new)
        val expected = resource("ExamplePreferencesImpl-transformed.asm").text()

        assertWellFormed(ClassReader(new), javaClass.classLoader)
        assertEquals(expected, actual)
    }

    @Test
    fun testRunnable() {
        val old = ExampleRunnable::class.java.bytes
        val new = transform(old, classBlocks)

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

private fun transform(source: ByteArray, classBlocks: List<ClassBlock>): ByteArray {
    val cr = ClassReader(source)
    val cw = ClassWriter(cr, 0x0)
    val cv = TransformClassVisitor(Opcodes.ASM6, cw, classBlocks)
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
            CheckClassAdapter(classNode, false),
            ClassReader.SKIP_DEBUG)
    val syperType = if (classNode.superName == null) null else Type.getObjectType(classNode.superName)
    val methods = classNode.methods
    val interfaces = classNode.interfaces
            .map { Type.getObjectType(it) }
    for (method in methods) {
        val verifier = SimpleVerifier(
                Type.getObjectType(classNode.name),
                syperType,
                interfaces,
                classNode.access and Opcodes.ACC_INTERFACE != 0)
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
