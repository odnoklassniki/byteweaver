// class version 61.0 (61)
// access flags 0x21
public class com/example/ExampleAutoTrace {

  // compiled from: ExampleAutoTrace.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 5 L0
    ALOAD 0
    INVOKESPECIAL java/lang/Object.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lcom/example/ExampleAutoTrace; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x0
  doNothing()V
  @Lru/ok/android/commons/os/AutoTrace;() // invisible
    TRYCATCHBLOCK L0 L1 L2 java/lang/Exception
    TRYCATCHBLOCK L0 L3 L3 java/lang/Throwable
    GOTO L4
   L0
    LINENUMBER 9 L0
   FRAME SAME
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    INVOKEVIRTUAL java/io/PrintStream.println ()V
   L1
    LINENUMBER 12 L1
    GOTO L5
   L2
    LINENUMBER 10 L2
   FRAME SAME1 java/lang/Exception
    ASTORE 1
   L6
    LINENUMBER 11 L6
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    INVOKEVIRTUAL java/io/PrintStream.println ()V
   L5
    LINENUMBER 13 L5
   FRAME SAME
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.endSection ()V
    RETURN
   L3
   FRAME SAME1 java/lang/Throwable
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.endSection ()V
    ATHROW
   L4
   FRAME SAME
    LDC "com.example.ExampleAutoTrace.doNothing(ExampleAutoTrace.java:9)"
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.beginTraceSection (Ljava/lang/String;)V
    GOTO L0
    LOCALVARIABLE ex Ljava/lang/Exception; L6 L5 1
    LOCALVARIABLE this Lcom/example/ExampleAutoTrace; L0 L3 0
    MAXSTACK = 1
    MAXLOCALS = 2
}
