package ru.ok.byteweaver.config

import org.junit.Assert.assertTrue
import org.junit.Test

class MethodDescPatternTest {
    @Test
    fun testExactJvmNameMatches() {
        val descPattern = methodDescPattern("java.lang.Object", "int", "double", "java.lang.Thread")

        assertTrue(descPattern.jvmDescMatches("(IDLjava/lang/Thread;)Ljava/lang/Object;"))
    }

    @Test
    fun testRegularJvmNameMatches() {
        val descPattern = methodDescPattern("*", "*")

        assertTrue(descPattern.jvmDescMatches("(Ljava/lang/Thread;)Ljava/lang/Object;"))
    }

    @Test
    fun testStarsJvmNameMatches() {
        val descPattern = methodDescPattern("*", hasThreeStartPattern = true)

        assertTrue(descPattern.jvmDescMatches("(IDLjava/lang/Thread;)Ljava/lang/Object;"))
    }

    private fun methodDescPattern(returnTypePattern: String, vararg parameterTypePatterns: String, hasThreeStartPattern: Boolean = false) = MethodDescPattern(
            returnTypePattern = TypePattern(returnTypePattern),
            parameterTypePatterns = parameterTypePatterns.map(TypePattern::invoke),
            hasThreeStarsPattern = hasThreeStartPattern,
    )
}
