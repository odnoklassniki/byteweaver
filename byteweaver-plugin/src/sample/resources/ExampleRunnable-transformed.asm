// class version 61.0 (61)
// access flags 0x21
public class com/example/ExampleRunnable implements java/lang/Runnable {

  // compiled from: ExampleRunnable.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 3 L0
    ALOAD 0
    INVOKESPECIAL java/lang/Object.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lcom/example/ExampleRunnable; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x1
  public run()V
    TRYCATCHBLOCK L0 L1 L1 java/lang/Throwable
    GOTO L2
   L0
    LINENUMBER 7 L0
   FRAME SAME
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    LDC "Running"
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
    GOTO L0
   L1
   FRAME SAME1 java/lang/Throwable
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.endSection ()V
    ATHROW
   L2
   FRAME SAME
    LDC "com.example.ExampleRunnable.run(ExampleRunnable.java:7)"
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.beginTraceSection (Ljava/lang/String;)V
    GOTO L0
    LOCALVARIABLE this Lcom/example/ExampleRunnable; L0 L1 0
    MAXSTACK = 2
    MAXLOCALS = 1
}
