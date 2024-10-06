# Randomizer

A library for (kinda) effortlessly generate random objects. 

## :construction: :construction: :construction: Work in progress :construction: :construction: :construction: 
  
> :warning: This is an pre-alpha release
> - Everything including the public API is subject to changes.
> - Not suitable for use in production. 
> - not integrated to IDEs.

# Usage note
- This library is best used to randomize classes that only contain data.
- For service classes that depends on complex interfaces or abstract classes, it's better to initialize them directly.

# Introduction

A random instance of any class can be created as easy as this:

```kotlin
@Randomizable
class ExampleClass(
    val i: Int,
    val f: Float,
)

val instance = ExampleClass.random()
```

## Gradle
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
@Randomizable
class ExampleClass(
    val i: Int,
    val f: Float,
)

val instance = ExampleClass.random(randomizers = {
    constant<Int>{333}
    factory<Float>{
        listOf(1.2f, 2f,4f).random()
    }
})
```

## Generic support

Generic is supported, so it is possible to do this:

```kotlin
@Randomizable
class ExampleClass<T>(
    val t:T
)

val instance = ExampleClass.random<Int>()
```

Nested generic is also supported

```kotlin
@Randomizable
class ExampleClass<T>(
    val t:T
)

val instance = ExampleClass.random<Map<Int, String>>()
val instance2 = ExampleClass.random<List<Map<Int, String>>>()
```

# Built-in type support

The library support the following built-in types:
- `Int`, `Long`, `Byte`, `Short`
- `UInt`, `ULong`, `UByte`, `UShort`
- `Float`, `Double`, `Number`
- `Boolean`, `Char`
- `String`, `Unit`, `Any`
- `List`, `Map`, `Set`
- `ArrayList`
- `HashMap`
- `LinkedHashMap`
- `HashSet`
- `LinkedHashSet`
- `Array`

