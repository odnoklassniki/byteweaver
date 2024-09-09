// class version 61.0 (61)
// access flags 0x21
public class com/example/ExampleExecutors {

  // compiled from: ExampleExecutors.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 6 L0
    ALOAD 0
    INVOKESPECIAL java/lang/Object.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lcom/example/ExampleExecutors; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x89
  public static varargs main([Ljava/lang/String;)V
   L0
    LINENUMBER 8 L0
    INVOKESTATIC com/example/ExampleExecutors.customCachedThreadPool ()Ljava/util/concurrent/ExecutorService;
    POP
   L1
    LINENUMBER 9 L1
    ICONST_1
    INVOKESTATIC com/example/ExampleExecutors.customFixedThreadPool (I)Ljava/util/concurrent/ExecutorService;
    POP
   L2
    LINENUMBER 10 L2
    RETURN
   L3
    LOCALVARIABLE args [Ljava/lang/String; L0 L3 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x9
  public static customCachedThreadPool()Ljava/util/concurrent/ExecutorService;
   L0
    LINENUMBER 13 L0
    INVOKESTATIC java/util/concurrent/Executors.newCachedThreadPool ()Ljava/util/concurrent/ExecutorService;
    ARETURN
    MAXSTACK = 1
    MAXLOCALS = 0

  // access flags 0x9
  public static customFixedThreadPool(I)Ljava/util/concurrent/ExecutorService;
   L0
    LINENUMBER 17 L0
    ILOAD 0
    INVOKESTATIC java/util/concurrent/Executors.newFixedThreadPool (I)Ljava/util/concurrent/ExecutorService;
    ARETURN
   L1
    LOCALVARIABLE poolSize I L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1
}
