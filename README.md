# Randomizer

A library for (kinda) effortlessly generate random objects. 

## :construction: :construction: :construction: Work in progress :construction: :construction: :construction: 
  
> :warning:
> - Everything, including the public API, is subject to change.


# Usage note
- This library is best used to randomize classes that only contain data.
- For service classes that depends on complex interfaces or abstract classes, it's better to initialize them directly.

# Introduction

A random instance of any class can be created as easy as this:

```kotlin
import com.x12q.randomizer.lib.random

class ExampleClass(
    val i: Int,
    val f: Float,
)

val instance = random<ExampleClass>()
```

## Install with gradle
```
plugins{
  id("com.x12q.randomizer") version "1.0.0-alpha.7"
}

dependencies {
    implementation("com.x12q:randomizer-ir-lib:1.0.0-alpha.7")
}
```

## Add custom randomizers

Custom randomizers can be added like this:

```kotlin

val instance = ExampleClass.random<ExampleClass>(randomizers = {
    int(333)
    factory<Float>{
        listOf(1.2f, 2f,4f).random()
    }
})
```

## Generic support

Generic is supported, so it is possible to do this:

```kotlin
import com.x12q.randomizer.lib.random

class ExampleClass<T>(
    val t:T
)
val instance = random<ExampleClass<Int>>()
```

Nested generic is also supported

```kotlin
import com.x12q.randomizer.lib.random

class ExampleClass<T>(
    val t:T
)
val instance = random<ExampleClass<Map<Int, String>>>()
val instance2 = random<ExampleClass<List<Map<Int, String>>>>()
```

# Customize the randomness

A custom `Random` object can be added via a `RandomConfig` object passed to `random()` function

```kotlin
import com.x12q.randomizer.lib.random
import kotlin.random.Random

val yourRandomObj: Random = Random(seed=123)
        
val instance = random<ExampleClass>(
    randomConfig = RandomConfig.defaultWith(random = yourRandomObj)
)
```
