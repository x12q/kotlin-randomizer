# Randomizer

A library for (kinda) effortlessly generate random objects. 

## :construction: :construction: :construction: Work in progress :construction: :construction: :construction: 
  
> :warning:
> - Everything, including the public API, is subject to change.


# Usage note
- This library is best used for randomizing defined classes or interfaces that only contain data.
- Although the plugin code can be called anywhere, it is intended for use in test code (such as unit test or integration test).
- For service classes that depends on complex interfaces or abstract classes, it's better to initialize them directly.
- See [Limitation](#limitation) below to see when it is not possible to use the plugin.


## Install with gradle
```
plugins{
  id("com.x12q.kotlin.randomizer") version "1.0.0-alpha.11"
}

dependencies {
     implementation("com.x12q:kotlin-randomizer-lib:1.0.0-alpha.11")
}
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

val instance = random<ExampleClass>()
```

## Add custom randomizers

Custom randomizers can be added to override the default random logic for certain data type. A demonstration is below.

```kotlin

val instance = random<ExampleClass>(randomizers = {
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

Given this class
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
- This plugin generates and injects code into `makeRandom = ...` parameter of the `random()` function when it is called.
- The generated code is the one responsible for creating random instances of classes.
- This happens at compile time.
- If users provide a `makeRandom = ...` argument, this plugin will NOT modify that one. Code generation only runs when users __DO NOT__ provide a `makeRandom = ...`.


# Limitation
<a id="limitation"></a>

The plugin it relies on type arguments passed to `random()` to modify `makeRandom = ...`. Therefore, if `random()` is called without a defined type, it will crash. See the below example for better clarification.

```kotlin
val i = random<ExampleClass>() // ok because `ExampleClass` is a defined type

fun <T> someFunction():T {
    val i = random<T>() // crash because `T` is not defined.
    return i
}
```

# Constructor picking rules

The plugin employs the follwing rules when picking a constructor to generate random instances.

- Rule 1: Only public and internal constructors are used. This applies to all constructors, annotated or not.
- Rule 2: If the target class has constructors annotated with `@Randomizable`, one of them will be picked randomly to construct the random instance.
- Rule 3: If the target class does NOT have any constructor annotated with `@Randomizable`, a random constructor among all legit constructors (obeying `Rule 1`) will be used instead.
- Rule 4: Constructor picking is influenced by the randomness of `RandomConfig`. It is noticed that users should not take this route to customize constructor picking logic. It's best to use `@Randomizable` annotation.

Example of using `@Randomizable` on constructor:

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

## Why not use `RandomConfig` to change constructor picking behavior?

Constructors are picked randomly under the influence of `RandomConfig`. Users can therefore control this behavior by providing a custom `RandomConfig`. However, this is not advisable because:
- This involves declaring an explicit positional integer index that will be used to pick a constructor.
- For example, if the index is 0, the first constructor will be picked. If the index is 1, the second constructor will be picked.
- If users change the order of constructors or delete a constructor in their code, a fixed index will lead to calling the wrong constructor, or outright crash their code.

Therefore, it's best to use `@Randomizable` to customize constructor picking behavior.

Nevertheless, if users choose to use `RandomConfig` instead for whatever reason, here is how to do it.

```kotlin
class YourRandomConfig () : RandomConfig {
  override fun randomizableCandidateIndex(candidateCount: Int): Int {
    val yourIndex = 0
    return yourIndex
  }
  //...
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

In this example, it is not possible to infer the concrete class for SomeInterface<Int>, it could be `Class1<Int>`, but it could also be `Class2<Int,E>`, and `E` is completely unknown.

A non-complete solution can be implemented to aid with some cases, but it will introduce a lot of gotcha and confusion.

So, for interfaces, abstract classes, sealed classes, sealed interfaces, users must provide their own explicit custom randomizers. Like this:

```kotlin
val i = random<SomeInterface<Int>>(randomizers = {
    factory<SomeInterface<Int>>{ random<Class1<Int>>() }
})
```

