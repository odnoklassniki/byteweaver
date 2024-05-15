// class version 52.0 (52)
// access flags 0x21
public class com/example/ExampleNotification {

  // compiled from: ExampleNotification.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 6 L0
    ALOAD 0
    INVOKESPECIAL java/lang/Object.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lcom/example/ExampleNotification; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x1
  public showNotification(Landroid/app/NotificationManager;Landroid/app/Notification;)V
   L0
    LINENUMBER 8 L0
    ALOAD 1
    ICONST_0
    ALOAD 2
    INVOKESTATIC ru/ok/android/app/NotificationsLogger.log (Landroid/app/NotificationManager;ILandroid/app/Notification;)V
   L1
    LINENUMBER 9 L1
    ALOAD 1
    LDC ""
    ICONST_0
    ALOAD 2
    INVOKESTATIC ru/ok/android/app/NotificationsLogger.log (Landroid/app/NotificationManager;Ljava/lang/String;ILandroid/app/Notification;)V
   L2
    LINENUMBER 10 L2
    RETURN
   L3
    LOCALVARIABLE this Lcom/example/ExampleNotification; L0 L3 0
    LOCALVARIABLE manager Landroid/app/NotificationManager; L0 L3 1
    LOCALVARIABLE notification Landroid/app/Notification; L0 L3 2
    MAXSTACK = 4
    MAXLOCALS = 3
}
