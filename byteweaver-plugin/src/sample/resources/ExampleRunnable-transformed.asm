// class version 52.0 (52)
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
   L0
    LDC "com.example.ExampleRunnable.run(ExampleRunnable.java)"
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.beginTraceSection (Ljava/lang/String;)V
   L2
    LINENUMBER 7 L2
   FRAME SAME
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    LDC "Running"
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
    GOTO L2
   L1
   FRAME SAME1 java/lang/Throwable
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.endSection ()V
    ATHROW
    LOCALVARIABLE this Lcom/example/ExampleRunnable; L2 L1 0
    MAXSTACK = 2
    MAXLOCALS = 1
}
