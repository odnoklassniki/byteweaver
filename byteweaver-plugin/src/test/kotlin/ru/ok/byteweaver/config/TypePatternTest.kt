package ru.ok.byteweaver.config

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TypePatternTest {
    @Test
    fun `StarTypePattern jvmDescMatches`() {
        assertTrue(TypePattern("*").jvmDescMatches("V"))
        assertTrue(TypePattern("*").jvmDescMatches("[I"))
        assertTrue(TypePattern("*").jvmDescMatches("Ljava/lang/Object;"))
        assertTrue(TypePattern("*").jvmDescMatches("[Ljava/lang/Object;"))
    }

    @Test
    fun `RegexClassPattern jvmDescMatches`() {
        assertFalse(TypePattern("java.**.*").jvmDescMatches("V"))
        assertTrue(TypePattern("java.**.*").jvmDescMatches("Ljava/lang/String;"))
        assertFalse(TypePattern("java.**.*").jvmDescMatches("[Ljava/lang/Object;"))
    }

    @Test
    fun `SimpleClassPattern jvmDescMatches`() {
        assertTrue(TypePattern("**.String").jvmDescMatches("Ljava/lang/String;"))
        assertFalse(TypePattern("**.String").jvmDescMatches("Ljava/lang/Integer;"))
        assertTrue(TypePattern("java.**").jvmDescMatches("Ljava/lang/String;"))
        assertFalse(TypePattern("java.**").jvmDescMatches("Landroid/os/Handler;"))
        assertTrue(TypePattern("java.*.String").jvmDescMatches("Ljava/lang/String;"))
        assertFalse(TypePattern("java.*.String").jvmDescMatches("Ljava/lang/StringBuilder;"))
        assertTrue(TypePattern("java.lang.*String").jvmDescMatches("Ljava/lang/String;"))
        assertTrue(TypePattern("java.lang.String*").jvmDescMatches("Ljava/lang/StringBuilder;"))
    }

    @Test
    fun `PrimitiveTypeName jvmDescMatches`() {
        assertFalse(TypePattern("int").jvmDescMatches("V"))
        assertTrue(TypePattern("int").jvmDescMatches("I"))
        assertFalse(TypePattern("int").jvmDescMatches("[I"))
        assertFalse(TypePattern("int").jvmDescMatches("Ljava/lang/Object;"))
        assertFalse(TypePattern("int").jvmDescMatches("[Ljava/lang/Object;"))
    }

    @Test
    fun `ClassName jvmDescMatches`() {
        assertTrue(TypePattern("java.lang.String").jvmDescMatches("Ljava/lang/String;"))
        assertFalse(TypePattern("java.lang.Integer").jvmDescMatches("Ljava/lang/String;"))
        assertFalse(TypePattern("java.lang.String").jvmDescMatches("Ljava/lang/Integer;"))
    }

    @Test
    fun `ClassName jvmNameMatches`() {
        assertTrue(ClassName("java.lang.String").jvmNameMatches("java/lang/String"))
        assertFalse(ClassName("java.lang.Integer").jvmNameMatches("java/lang/String"))
        assertFalse(ClassName("java.lang.String").jvmNameMatches("java/lang/Integer"))
    }

    @Test
    fun `ArrayTypePattern of StarTypePattern jvmDescMatches`() {
        assertTrue(TypePattern("*[]").jvmDescMatches("[I"))
        assertFalse(TypePattern("*[]").jvmDescMatches("I"))
        assertTrue(TypePattern("*[]").jvmDescMatches("[[I"))
        assertFalse(TypePattern("*[]").jvmDescMatches("Ljava/lang/String;"))
        assertTrue(TypePattern("*[]").jvmDescMatches("[Ljava/lang/String;"))
    }

    @Test
    fun `ArrayTypeName of PrimitiveTypeName jvmDescMatches`() {
        assertTrue(TypePattern("int[]").jvmDescMatches("[I"))
        assertFalse(TypePattern("int[]").jvmDescMatches("I"))
        assertFalse(TypePattern("int[]").jvmDescMatches("[[I"))
        assertFalse(TypePattern("int[]").jvmDescMatches("Ljava/lang/String;"))
        assertFalse(TypePattern("int[]").jvmDescMatches("[Ljava/lang/String;"))
    }

    @Test
    fun `ArrayTypePattern of RegexClassPattern jvmDescMatches`() {
        assertTrue(TypePattern("java.**.*[]").jvmDescMatches("[Ljava/lang/String;"))
        assertFalse(TypePattern("java.**.*[]").jvmDescMatches("Ljava/lang/String;"))
    }

    @Test
    fun `ArrayTypeName of ClassName jvmDescMatches`() {
        assertFalse(TypePattern("java.lang.String[]").jvmDescMatches("[I"))
        assertFalse(TypePattern("java.lang.String[]").jvmDescMatches("I"))
        assertFalse(TypePattern("java.lang.String[]").jvmDescMatches("[[I"))
        assertFalse(TypePattern("java.lang.String[]").jvmDescMatches("Ljava/lang/String;"))
        assertTrue(TypePattern("java.lang.String[]").jvmDescMatches("[Ljava/lang/String;"))
    }
}
