# Randomizer
A library for (kinda) effortlessly generating random objects. 


## :construction: :construction: :construction: Work in progress :construction: :construction: :construction: 
  
> :warning:
> - Everything, including the public API, is subject to change.
> - This only works with some certain versions of kotlin, see below for a list of supported versions.


# Usage note
- This plugin is best applied before any constructor manipulator plugins (such as kotlinx.serialization plugin) to ensure that no generated constructors are taken into randomization code.
- This plugin is best used for randomizing defined classes or interfaces that only contain data.
- Although the plugin code can be called anywhere, it is intended for use in test code (such as unit test or integration test).
- For service classes that depend on complex interfaces or abstract classes, it's better to initialize them directly.
- See [Limitation](#limitation) below to see when it is not possible to use the plugin.


## Install with gradle
```
plugins{
  id("com.x12q.kotlin.randomizer") version "1.0.0-alpha.16-2.1.0"
  // for other kotlin version: id("com.x12q.kotlin.randomizer") version "1.0.0-alpha.16-<other-kotlin-version>"
}

kotlinRandomizer{
    enable = true
}

dependencies {
     implementation("com.x12q:kotlin-randomizer-lib:1.0.0-alpha.16-2.1.0")
     // for other kotlin version: implementation("com.x12q:kotlin-randomizer-lib:1.0.0-alpha.16-<other-kotlin-version>")
}
```
Current supported kotlin versions are:
```
2.1.0
2.0.21
2.0.20
2.0.10
2.0.0
```


# Introduction

Given a class as followed:

```kotlin
class ExampleClass(
    val i: Int,
    val f: Float,
)
```

A random instance of such class can be created as this:

```kotlin
import com.x12q.kotlin.randomizer.lib.random

val i = random<ExampleClass>()
```

## Add custom randomizers

Custom randomizers can be added to override the default random logic for certain data type. A demonstration is below.

```kotlin

val i = random<ExampleClass>(randomizers = {
    // overriding for Int type
    int(333)

    // overriding for string type
    string("zxcasdqwe")
    
    // overriding for class ABC type
    constant<ABC>(ABC("something", 123))
    
    // overriding for List<Int> type
    constant<List<Int>>(listOf(1,2,3,4))
    
    // overriding for Float type using a factory function
    factory<Float>{
        listOf(1.2f, 2f,4f).random()
    }
    
    // overriding for class XYZ type using a factory function
    factory<XYZ>{
        val x = 1+2
        val y = "yyyy"
        val z = "$x"
        XYZ(x,y,z)
    }
})
```

## Overriding the default randomizer altogether
The default randomizing logic can be override altogether by providing your own `makeRandom = ...` lambda, like this:

```kotlin
val i = random<ExampleClass>(
    makeRandom = { ExampleClass(123, 33f) }
)
```

In this example, the `instance` variable will always have the value `ExampleClass(123,33f)`.


## Generic support

Given this class:
```kotlin
class ExampleClass<T>(
    val t:T
)
```

Generic is supported, so it is possible to do this:

```kotlin
import com.x12q.kotlin.randomizer.lib.random

val i = random<ExampleClass<Int>>()
```

Nested generic is also supported

```kotlin
import com.x12q.kotlin.randomizer.lib.random

val i = random<ExampleClass<Map<Int, String>>>()
val i2 = random<ExampleClass<List<Map<Int, String>>>>()
```

# Customize random seed

A custom kotlin `Random` object with custom seed can be added via a `RandomConfig` object passed to `random()` function

```kotlin
import com.x12q.kotlin.randomizer.lib.random
import kotlin.random.Random

val yourRandomObj: Random = Random(seed=123)
        
val i = random<ExampleClass>(
    randomConfig = RandomConfig.defaultWith(random = yourRandomObj)
)
```


# How the plugin works
- This plugin generates a new and replaces `makeRandom = ...` parameter of the `random()` function wherever the function is called.
- The generated code is the one responsible for creating random instances of classes.
- This happens at compile time.
- If users provide a `makeRandom = ...` argument, this plugin will NOT modify that one. Code generation only runs when users __DO NOT__ provide a `makeRandom = ...`.


# Limitation
<a id="limitation"></a>

The plugin it relies on type arguments passed to `random()` to modify `makeRandom = ...`. Therefore, if `random()` is called without a defined type, it will crash. See the below example for better clarification.

```kotlin
val i = random<ExampleClass>() // ok because `ExampleClass` is defined
val i2 = random<List<Int>>() // ok because `List<Int>` is defined

fun <T> someFunction():T {
    val i = random<T>() // crash because `T` is not defined.
    return i
}

fun <T> makeRandomList():T {
    val i = random<List<T>>() // crash because `T` is not defined.
    return i
}
```

# Constructor picking rules and @Randomizable annotation

The plugin employs the following rules when picking a constructor to generate random instances.

- Rule 1: Only `public` and `internal` constructors are used. This applies to all constructors, annotated with `@Randomizable` or not.
- Rule 2: If the target class has constructors annotated with `@Randomizable`, one of them will be picked randomly to construct the random instance. The non-annotated constructors will be ignored entirely.
- Rule 3: If the target class does NOT have any constructor annotated with `@Randomizable`, a random constructor among all legit constructors (obeying `Rule 1`) will be used instead.
- Rule 4: Constructor picking is influenced by the randomness of `RandomConfig`. It is noticed that users should not take this route to customize constructor picking behavior, see [Why](#why_not_random_config) below. It's best to use `@Randomizable` annotation.

Example of using `@Randomizable` to appoint constructor to be used:

```kotlin
class SomeClass @Randomizable constructor(val i:Int, val d:Double, val str:String){
    constructor(i:Int):this(-1.0,"str1")
    @Randomizable
    constructor(i:Int, d:Double):this("str2")
}
```
The primary constructor of a class annotated with `@Randomizable` is considered annotated too.

```kotlin
// These two are the same
@Randomizable
class SomeClass (val i:Int, val d:Double, val str:String)
class SomeClass @Randomizable constructor(val i:Int, val d:Double, val str:String)
```
<a id="why_not_random_config"></a>
## Why not use `RandomConfig` to change constructor picking behavior?

Constructors are picked randomly under the influence of `RandomConfig`. Users can therefore control this behavior by providing a custom `RandomConfig`. However, this is not advisable because:
- `RandomConfig` provides a random integer index at runtime to pick constructor. For example, if the index is 0, the first constructor will be picked. If the index is 1, the second constructor will be picked. This behavior is dynamic, that means when new constructors are added, or old ones get deleted, the index generation mechanism will change automatically to reflect that.
- If a fixed behavior (such as a fixed index) is to replace the default behavior, then it will no longer be able to adapt to code changes anymore. Actions such as changing the order of constructors or deleting a constructor in the source files may lead to calling the wrong constructor or crashing.

Therefore, it's best to use `@Randomizable` to customize constructor picking behavior.

Nevertheless, if users choose to use `RandomConfig` instead for whatever reason, here is how to do it. But keep in mid that it is not safe to do this.

```kotlin
class YourRandomConfig () : RandomConfig {
  override fun randomizableCandidateIndex(candidateCount: Int): Int {
    val yourIndex = 0
    return yourIndex
  }
}
```

# Handling interfaces, abstract classes, sealed classes and sealed interfaces

These targets are not handled by the randomizer plugin because there are cases that it is not possible to automatically infer the concrete class to use, such as the below example

```kotlin
interface SomeInterface<T>

class Class1<T> : SomeInterface<T>
class Class2<T, E> : SomeInterface<T>

val i = random<SomeInterface<Int>>()
```

In this example, it is not possible to infer the concrete class for `SomeInterface<Int>`, it could be `Class1<Int>`, but it could also be `Class2<Int, E>`, and `E` is completely unknown.

A non-complete solution can be implemented to aid with some cases, but it will introduce a lot of gotcha and confusion.

Therefore, for interfaces, abstract classes, sealed classes, sealed interfaces, users must provide their own explicit custom randomizers. Like this:

```kotlin
val i = random<SomeInterface<Int>>(randomizers = {
    factory<SomeInterface<Int>>{ random<Class1<Int>>() }
})
```
