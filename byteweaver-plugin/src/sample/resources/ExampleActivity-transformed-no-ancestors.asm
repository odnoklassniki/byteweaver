// class version 61.0 (61)
// access flags 0x21
public class com/example/ExampleActivity extends androidx/fragment/app/FragmentActivity {

  // compiled from: ExampleActivity.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 6 L0
    ALOAD 0
    INVOKESPECIAL androidx/fragment/app/FragmentActivity.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lcom/example/ExampleActivity; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x1
  public onCreate(Landroid/os/Bundle;)V
    TRYCATCHBLOCK L0 L1 L1 java/lang/Throwable
    GOTO L2
   L0
    LINENUMBER 9 L0
   FRAME SAME
    ALOAD 0
    ALOAD 1
    INVOKESPECIAL androidx/fragment/app/FragmentActivity.onCreate (Landroid/os/Bundle;)V
   L3
    LINENUMBER 10 L3
    ALOAD 0
    INSTANCEOF android/app/Activity
    IFEQ L4
    GOTO L5
   L4
   FRAME SAME
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.endSection ()V
   L5
   FRAME SAME
    RETURN
   L1
   FRAME SAME1 java/lang/Throwable
    ALOAD 0
    INSTANCEOF android/app/Activity
    IFEQ L6
    GOTO L7
   L6
   FRAME SAME
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.endSection ()V
   L7
   FRAME SAME
    ATHROW
   L2
   FRAME SAME
    ALOAD 0
    INSTANCEOF android/app/Activity
    IFEQ L8
    GOTO L0
   L8
   FRAME SAME
    LDC "com.example.ExampleActivity.onCreate(ExampleActivity.java:9)"
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.beginTraceSection (Ljava/lang/String;)V
    GOTO L0
    LOCALVARIABLE this Lcom/example/ExampleActivity; L0 L1 0
    LOCALVARIABLE savedInstanceState Landroid/os/Bundle; L0 L1 1
    MAXSTACK = 2
    MAXLOCALS = 2

  // access flags 0x1
  public onNavigateUp()Z
    TRYCATCHBLOCK L0 L1 L1 java/lang/Throwable
    GOTO L2
   L0
    LINENUMBER 14 L0
   FRAME SAME
    ALOAD 0
    INVOKESPECIAL androidx/fragment/app/FragmentActivity.onNavigateUp ()Z
    ALOAD 0
    INSTANCEOF android/app/Activity
    IFEQ L3
    GOTO L4
   L3
   FRAME SAME
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.endSection ()V
   L4
   FRAME SAME
    IRETURN
   L1
   FRAME SAME1 java/lang/Throwable
    ALOAD 0
    INSTANCEOF android/app/Activity
    IFEQ L5
    GOTO L6
   L5
   FRAME SAME
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.endSection ()V
   L6
   FRAME SAME
    ATHROW
   L2
   FRAME SAME
    ALOAD 0
    INSTANCEOF android/app/Activity
    IFEQ L7
    GOTO L0
   L7
   FRAME SAME
    LDC "com.example.ExampleActivity.onNavigateUp(ExampleActivity.java:14)"
    INVOKESTATIC ru/ok/android/commons/os/TraceCompat.beginTraceSection (Ljava/lang/String;)V
    GOTO L0
    LOCALVARIABLE this Lcom/example/ExampleActivity; L0 L1 0
    MAXSTACK = 2
    MAXLOCALS = 1
}
