// class version 52.0 (52)
// access flags 0x21
public class ru/ok/android/prefs/FastSharedPreferences {

  // compiled from: FastSharedPreferences.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 6 L0
    ALOAD 0
    INVOKESPECIAL java/lang/Object.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lru/ok/android/prefs/FastSharedPreferences; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x1
  public getSharedPreferences(Landroid/content/Context;Ljava/lang/String;I)Landroid/content/SharedPreferences;
   L0
    LINENUMBER 8 L0
    ALOAD 1
    ALOAD 2
    ILOAD 3
    INVOKEVIRTUAL android/content/Context.getSharedPreferences (Ljava/lang/String;I)Landroid/content/SharedPreferences;
    ARETURN
   L1
    LOCALVARIABLE this Lru/ok/android/prefs/FastSharedPreferences; L0 L1 0
    LOCALVARIABLE context Landroid/content/Context; L0 L1 1
    LOCALVARIABLE name Ljava/lang/String; L0 L1 2
    LOCALVARIABLE mode I L0 L1 3
    MAXSTACK = 3
    MAXLOCALS = 4
}
