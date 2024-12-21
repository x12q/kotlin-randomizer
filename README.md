# Randomizer

A library for (kinda) effortlessly generate random objects. 

## :construction: :construction: :construction: Work in progress :construction: :construction: :construction: 
  
> :warning:
> - Everything, including the public API, is subject to change.


# Usage note
- This library is best used for randomizing classes that only contain data.
- For service classes that depends on complex interfaces or abstract classes, it's better to initialize them directly.


## Install with gradle
```
plugins{
  id("com.x12q.randomizer") version "1.0.0-alpha.8"
}

dependencies {
    implementation("com.x12q:randomizer-ir-lib:1.0.0-alpha.8")
}
```


# Introduction

Given a class 

```kotlin
class ExampleClass(
    val i: Int,
    val f: Float,
)
```

A random instance of it can be created as this:

```kotlin
import com.x12q.kotlin.randomizer.lib.random

val instance = random<ExampleClass>()
```

## Add custom randomizers

Custom randomizers can be added to override the default random logic for certain data type. Demostration is below

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
        val x=1+2
        val y="yyyy"
        val z = "$x"
        XYZ(x,y,z)
    }
})
```

## Overriding the default randomizer altogether
The default randomizing logic can be override altogether by providing your own `makeRandom` lambda, like this:

```kotlin
val instance = random<ExampleClass>(
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

val instance = random<ExampleClass<Int>>()
```

Nested generic is also supported

```kotlin
import com.x12q.kotlin.randomizer.lib.random

val i = random<ExampleClass<Map<Int, String>>>()
val i2 = random<ExampleClass<List<Map<Int, String>>>>()
```

# Customize the randomness

A custom kotlin `Random` object with custom seed can be added via a `RandomConfig` object passed to `random()` function

```kotlin
import com.x12q.kotlin.randomizer.lib.random
import kotlin.random.Random

val yourRandomObj: Random = Random(seed=123)
        
val instance = random<ExampleClass>(
    randomConfig = RandomConfig.defaultWith(random = yourRandomObj)
)
```


