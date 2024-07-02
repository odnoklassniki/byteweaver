// class version 61.0 (61)
// access flags 0x21
public class com/example/ExampleService extends dagger/android/DaggerService {

  // compiled from: ExampleService.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 8 L0
    ALOAD 0
    INVOKESPECIAL dagger/android/DaggerService.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lcom/example/ExampleService; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x1
  public onStartCommand(Landroid/content/Intent;II)I
   L0
    LINENUMBER 11 L0
    ALOAD 0
    BIPUSH 10
    NEW android/app/Notification
    DUP
    INVOKESPECIAL android/app/Notification.<init> ()V
    INVOKESTATIC ru/ok/android/app/NotificationsLogger.logStartForeground (Ljava/lang/Object;ILandroid/app/Notification;)V
   L1
    LINENUMBER 12 L1
    ICONST_0
    IRETURN
   L2
    LOCALVARIABLE this Lcom/example/ExampleService; L0 L2 0
    LOCALVARIABLE intent Landroid/content/Intent; L0 L2 1
    LOCALVARIABLE flags I L0 L2 2
    LOCALVARIABLE startId I L0 L2 3
    MAXSTACK = 4
    MAXLOCALS = 4
}
