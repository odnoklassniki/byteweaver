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
    RETURN
   L2
    LOCALVARIABLE args [Ljava/lang/String; L0 L2 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x9
  // signature (Ljava/lang/Class<Ljava/util/concurrent/Executors;>;)Ljava/util/concurrent/ExecutorService;
  // declaration: java.util.concurrent.ExecutorService customCachedThreadPool(java.lang.Class<java.util.concurrent.Executors>)
  public static customCachedThreadPool(Ljava/lang/Class;)Ljava/util/concurrent/ExecutorService;
   L0
    LINENUMBER 12 L0
    INVOKESTATIC java/util/concurrent/Executors.newCachedThreadPool ()Ljava/util/concurrent/ExecutorService;
    ARETURN
   L1
    LOCALVARIABLE klass Ljava/lang/Class; L0 L1 0
    // signature Ljava/lang/Class<Ljava/util/concurrent/Executors;>;
    // declaration: klass extends java.lang.Class<java.util.concurrent.Executors>
    MAXSTACK = 1
    MAXLOCALS = 1
}
