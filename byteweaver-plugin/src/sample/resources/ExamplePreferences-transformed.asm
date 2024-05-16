// class version 61.0 (61)
// access flags 0x21
public class com/example/ExamplePreferences {

  // compiled from: ExamplePreferences.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 6 L0
    ALOAD 0
    INVOKESPECIAL java/lang/Object.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lcom/example/ExamplePreferences; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x9
  public static getSharedPreferences(Landroid/content/Context;)Landroid/content/SharedPreferences;
   L0
    LINENUMBER 8 L0
    ALOAD 0
    LDC "prefs"
    ICONST_0
    INVOKESTATIC ru/ok/android/prefs/FastSharedPreferences.getSharedPreferences (Landroid/content/Context;Ljava/lang/String;I)Landroid/content/SharedPreferences;
    ARETURN
   L1
    LOCALVARIABLE context Landroid/content/Context; L0 L1 0
    MAXSTACK = 3
    MAXLOCALS = 1
}
