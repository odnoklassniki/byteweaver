ByteWeaver от OK.TECH это легковесное решение для авторов андроидных приложений и библиотек, которое позволяет им совершать некоторые манипуляции с байткодом во время сборки приложения.

Доклад от автора ByteWeaver на Mobius 2024 очень подробно описывает решение, и содержит исчерпывающее руководство с примерами.

[![Инструментирование байткода во имя великого блага](https://img.youtube.com/vi/KPRPJLwdf8Y/0.jpg)](https://www.youtube.com/watch?v=KPRPJLwdf8Y)

[Статья на Хабре](https://habr.com/ru/companies/vk/articles/845872/) от автора ByteWeaver в каком-то смысле повторяет доклад, и содержит те же примеры.

# Обзор архитектуры ByteWeaver

ByteWeaver выполнен в виде плагина для Gradle. В свою очередь ByteWeaver использует инфраструктуру Android Gradle Plugin для того, чтобы встроиться в процесс сборки андроид приложения или библиотеки. На этапе обработки байт-кода (после компиляции и подключения транзитивных зависимостей, но до обфускации) ByteWeaver обрабатывает классы по одному согласно указанным спецификациям на языке конфигурирования ByteWeaver.

ByteWeawer поддерживает классы, скомпилированные из Java или Kotlin, не важно, однако в случае Kotlin может потребоваться дополнительная работа, чтобы понять, какой байткод сгенерировал компилятор.

# Подключение ByteWeaver к проекту

В вашем `<project>/settings.gradle.kts` добавьте репозиторий с проектом ByteWeaver:

```kotlin
pluginManagement {
    repositories {
        // здесь другие репозитории c вашими зависимостями
        maven { setUrl("https://artifactory-external.vkpartner.ru/artifactory/maven/") }
    }
}
```

Если вы в вашем проекте уже используете [Tracer](https://apptracer.ru), то этот шаг можно пропустить.

В вашем `<project>/<app_module>/build.gradle.kts` подключите плагин ByteWeaver актуальной версии:

```kotlin
plugins {
    id("ru.ok.byteweaver").version("1.0.0")
}
```

<details>

<summary>Инструкция для Groovy</summary>

Если ваши билд-скрипты написаны на Groovy, то инструкция по подключению в целом такая же с поправкой на синтаксис Groovy.

В вашем `<project>/settings.gradle` добавьте репозиторий с проектом ByteWeaver:
```groovy
pluginManagement {
    repositories {
        // другие репозитории c вашими зависимостями
        maven { url 'https://artifactory-external.vkpartner.ru/artifactory/maven/' }
    }
}
```

В вашем `<project>/<app_module>/build.gradle` подключите плагин ByteWeaver актуальной версии:

```groovy
plugins {
    id 'ru.ok.byteweaver' version '1.0.0'
}
```

</details>

<details>

<summary>Инструкция для Legacy Groovy</summary>

Если вы используете более старую версию Gradle и конструкция `plugins` вам недоступна, то инструкция по подключению плагина ByyeWeaver несколько отличается.

В вашем корневом `<project>/build.gradle` добавьте репозиторий и зависимость на модуль с проектом ByteWeaver актуальной версии:

```groovy
buildscript {
    repositories {
        maven {
            url "https://artifactory-external.vkpartner.ru/artifactory/maven/"
        }
    }
    dependencies {
        classpath 'ru.ok.byteweaver:byteweaver-plugin:1.0.0'
    }
}
```

В вашем `<project>/<app_module>/build.gradle` подключите плагин ByteWeaver:
```groovy
apply plugin 'ru.ok.byteweaver'
```

</details>

# Конфигурация ByteWeaver

То, какие ByteWeaver обрабатывает классы и методы, а также какие преобразования он применяет, описывается на языке конфигурации ByteWeaver. Этот несложный язык описан далее, но для того, чтобы конфигурации применились, необходимо указать путь до них плагину.

В вашем `<project>/<app_module>/build.gradle.kts` (в том, в котором вы подключали плагин) задаем следующий блок:

```kotlin
byteweaver {
    create("debug") {
        srcFiles += "byteweaver/patch-foo.conf"
    }
    create("release") {
        srcFiles += "byteweaver/patch-bar.conf"
    }
}
```

Здесь мы видим, что для build type `debug` будет использоваться преобразование из файла `byteweaver/patch-foo.conf`, а для build type `release` из `byteweaver/patch-bar.conf`.

Точно также можно задавать несколько преобразований для одного build type или не задавать их вовсе. Если в вашем проекте используются другие build types или flavors, можно задавать конфигурацию и для них.

<details>

<summary>Инструция для Groovy</summary>

Если в вашем проекте билд-скрипты написаны на Groovy то синтаксис слегка отличается.

В вашем `<project>/<app_module>/build.gradle` (в том, в котором вы подключали плагин) задаем следующий блок:

```groovy
byteweaver {
    debug {
        srcFiles += 'byteweaver/patch-foo.conf'
    }
    release {
        srcFiles += 'byteweaver/patch-bar.conf'
    }
}
```

</details>

## Указание классов

В первую очередь нужно описать какие классы подвергаются преобразованиям.

Здесь и далее примеры на языке конфигурации ByteWeaver.

Явно указываем класс `io.reactivex.rxjava3.internal.operators.single.SingleFromCallable`:
```
class io.reactivex.rxjava3.internal.operators.single.SingleFromCallable {
}
```

Все классы, которые наследуют от `android.view.View`:
```
class * extends android.view.View {
}
```

Все классы, которые реализуют `java.lang.Runnable` (обратите внимание, что используется ключевое слово `extends`):
```
class * extends java.lang.Runnable {
}
```

Любой класс:
```
class * {
}
```

Любой класс, который лежит в пакете `ru.ok.android` (и подпакетах) и аннотирован `@SomeAnnotation`:
```
@SomeAnnotation
class ru.ok.android.* {
}
```

Также в языке конфигурации ByteWeaver поддерживаются импорты:
```
import ru.ok.android.app.NotificationsLogger;
import java.lang.String;
```

Более того, импорты обязательны (см. `java.lang.String`). Никакого неявного импорта `java.lang.*` как в Java и кучи пакетов как в Котлине нет.

## Указание методов

Внутри блоков классов нужно указать блоки методов, которые будут обрабатываться ByteWeaver.

Метод класса, наследующего от `android.app.Activity`, который называется `onCreate`, принимает `android.os.Bundle` и ничего не возвращает (ключевое слово `void`):
```
class * extends android.app.Activity {
    void onCreate(android.os.Bundle) {
    }
}
```

Метод класса, реализующего `java.lang.Runnable`, который называется `run`, не имеет аргументов и ничего не возвращает:
```
class * extends java.lang.Runnable {
    void run() {
    }
}
```

Любой метод, в любом классе, вне зависимости от имени, типов аргументов и возвращаемого значения, но аннотированный `@ru.ok.android.commons.os.AutoTraceCompat`:
```
class * {
   @ru.ok.android.commons.os.AutoTraceCompat
   * *(***) {
   }
}
```

Любой метод:
```
class * {
   * *(***) {
   }
}
```

Важная информация, как ByteWeaver обрабатывает методы:
- Не указываются модификаторы видимости `public`/`protected`/`private`
- Не указываются также модификаторы `final`/`static`/`synchronized`
- Совсем-совсем не указываются котлиновские `internal`/`override`
- Абстрактные (и интерфейсные) методы пропатчить не получится
- Методы по умолчанию в интерфейсах пропатчить получится и для этого не нужно указывать модификатор `default`
- Статические методы возможно пропатчить и для этого не нужно указывать модификатор `static`
- Чтобы пропатчить конструктор используйте имя `<init>` и тип возвращаемого значения `void`
- Чтобы пропатчить статический инициализатор класса используйте `void <clinit>()`

## Добавление вызовов в начало методов

ByteWeaver позволяет добавлять вызовы методов в начало тела ваших методов.

В любой метод аннотированный `@AutoTraceCompat` вставить вызов метода `TraceCompat.beginSection` с параметром `trace` (о нем ниже):
```
class * {
    @ru.ok.android.commons.os.AutoTraceCompat
    * *(***) {
        before void TraceCompat.beginTraceSection(trace);
    }
}
```

Это примерно эквивалентно, как если бы вы вручную переписали класс:
```java
public class Main {
    @AutoTraceCompat
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
```
... получили бы:
```java
public class Main {
    public static void main(String[] args) {
        TraceCompat.beginTraceSection("Main.main(String[])");
        System.out.println("Hello World");
    }
}
```

Как ByteWeaver вставляет вызовы в начало методов:
- Вставляется всегда вызов статической функции, при этом модификатор `static` указывать не нужно
- Вставляется всегда вызов функции, которая ничего не возвращает, но тип `void` указывать нужно!
- Параметр `trace` имеет тип `String` и содержит имя вызывающего класса и метода (и типы параметров вызывающего метода)
- Значение параметра `trace` генерируется до обработки обфускатором
- Параметр `this` — _этот_ объект (как в java)
- Позиционные параметры `0`, `1`, `2` и т.д. — соответствующие параметры метода, в который встраивается вызов `before` 

## Добавление вызовов в конец метода

ByteWeaver позволяет добавлять вызовы методов в конец тела ваших методов.

В конец любого метода аннотированного `@AutoTraceCompat` вставить вызов метода `TraceCompat.endTraceSection`
```
class * {
    @ru.ok.android.commons.os.AutoTraceCompat
    * *(***) {
        after void TraceCompat.endTraceSection();
    }
}
```

Это примерно эквивалентно, как если бы вы вручную переписали класс:
```java
public class Main {
    @AutoTraceCompat
    public static void main(String[] args) {
        System.out.println();
    }
}
```
... получили бы:
```java
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Hellow World");
        } finally {
            TraceCompat.endTraceSection();
        }
   }
}
```
При необходимости в конец метода можно добавить код, использующий параметр`trace` :
```
class * {
    @ru.ok.android.commons.os.AutoTraceCompat
    * *(***) {
        after void SomeLogger.logAfter(trace);
    }
}
```

Это примерно эквивалентно, как если бы вы вручную переписали класс:
```java
public class Main {
    @AutoTraceCompat
    public static void main(String[] args) {
        System.out.println();
    }
}
```
... получили бы:
```java
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Hello World");
        } finally {
            SomeLogger.logAfter("Main.main(String[])");
        }
   }
}
```
При этом класс SomeLogger должен выглядеть как-то так:
```java
public class SomeLogger {
    private static final String AFTER_PREFIX = "AFTER: ";

    private static void log(String tag, String msg) {
        System.out.println(tag + " " + msg);
    }

    public static void logAfter(String msg) {
        log(AFTER_PREFIX, msg);
    }
}
```

Как ByteWeaver вставляет вызовы в конец методов:
- Вставляется всегда вызов статической функции, при этом модификатор `static` указывать не нужно
- Вставляется всегда вызов функции, которая ничего не возвращает, но тип `void` указывать нужно!
- Вызываем строго функцию без параметров, либо с параметром `trace`
- Вызов будет осуществлен вне зависимости от того, нормально или аварийно завершится вызывающий метод

## Замена вызовов методов другими

ByteWeaver позволяет заменять одни вызовы другими.

Везде-везде заменить вызовы `NotificationManager.notify` на вызовы `NotificationsLogger.logNotify`:
```
class * {
   * *(***) {
       void NotificationManager.notify(int, Notification) {
           replace void NotificationsLogger.logNotify(self, 0, 1);
       }
   }
}
```

При этом класс NotificationsLogger должен выглядеть как-то так:
```java
public class NotificationsLogger {
   public static void logNotify(NotificationManager manager, String tag, int id, Notification notification) {
       manager.notify(tag, id, notification);
   }
}
```

Как ByteWeaver заменяет вызовы методов:
- На замену всегда вставляется вызов статического метода, при этом модификатор `static` не указывается
- Если заменяемый метод не статический, то первый параметр заменяющего метода должен быть всегда `self`
- Параметр `self` содержит ссылку на объект, на котором был бы вызван заменяемый метод (не путать с `this`, это ссылка на вызывающий объект)
- Заменяемый метод может быть статическим, при этом нужно указывать модификатор `static` обязательно!
- Если заменяемый метод статический, то первый параметр заменяющего метода не! должен быть `self`
- Остальные параметры заменяемого метода становятся позиционными параметрами заменяемого и должны быть перечисллены цифрами начиная с 0
