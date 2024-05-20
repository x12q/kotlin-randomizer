**# Randomizer

Generate random instance of (kinda) any class, interface, object, and enum, for testing and prototyping.


⚠️ Note:
- It is noted that, this library is best used to randomize classes that only contain data.
- For service classes that depends on complex interfaces or abstract classes, it's better just initialize them directly. Using this library is possible, but you will need to declare many custom randomizers that it's not worth it anymore.


# Introduction

Generating random data for testing and prototyping sometimes can be tedious, especially with large old classes that have deep nesting. This library aims to make that easier.

A random instance of any class can be created as easy as this:

```kotlin
val instance = random<MyClass>()
```
Custom random logic can also be easily provided via `random()` function or `@Randomizer` annotation. Read more below to see how.

## [Gradle](#gradle) <a id="gradle"></a>
```
dependencies {
    implementation("com.x12q:randomizer:1.0.0-alpha.6")
}
```
## [Maven](#maven) <a id="maven"></a>
```
<dependency>
    <groupId>com.x12q</groupId>
    <artifactId>randomizer</artifactId>
    <version>1.0.0-alpha.6</version>
</dependency>
```
# Content
<a id="top"></a>
- [Randomize a class](#how-to-1)
- [`random()` function](#random-function)
- [Annotations](#annotations)
  - [`@Randomizer`](#randomizer-annotation)
  - [`@RandomInt*` family](#random-int-annotation)
  - [`@RandomFloat*` family](#random-float-annotation)
  - [`@RandomDouble*` family](#random-double-annotation)
  - [`@RandomString*` family](#random-string-annotation)

- [Randomize an inner class](#how-to-6)

- [Rule](#rule)
  - [Randomizer order of priority (important !!!)](#rule-4)
  - [How `@Randomizer` works?](#rule-2)
  - [How are constructors picked?](#rule-3)
  - [How is randomization being done?](#rule-1)
      - [For concrete class](#rule-1-1)
      - [For sealed class](#rule-1-2)
      - [For interface & abstract class](#rule-1-3)
      - [For object](#rule-1-4)
      - [For enum](#rule-1-5)
      - [For inner class](#rule-1-6)
- [Limitation](#limitation)


## [Randomize a class &#9650;](#top) <a id="how-to-1"></a>
To create a random instance of a class, run this:
```kotlin
val randomInstance = random<SomeClass>()
```
## [`random()` function &#9650;](#top)<a id="random-function"></a>
`random()` is the main function to use create random instances. 

It can accept custom randomizers that can alter the randomization process. 

There are two type of custom randomizers that `random()` can accept: `class randomizers` and `parameter randomizers`.

Here's an example of providing randomizers using builder function `randomizers()` and `paramRandomizers()`.

⚠️ It is noted that `random()` does `not` need custom randomizers to work. This option is for when the default randomization is not good enough for your requirements.

```kotlin
val randomInstance = random<SomeClass>(
    /**
     * These are custom class randomizers,
     * they can override default randomization logic for their target class
     */
    randomizers = randomizers {
        randomizer {
            // custom randomizer for some class 
            SomeClass.random()
        }

        int {
            // custom randomizer for Int
            (1..200).random() * (-1)
        }

        float {
            // custom randomizer for Float
            123f
        }

        string {
            // custom randomizer for String
            "abc123"
        }

        list {
            // custom randomizer for List<Float>
            listOf(1f, 2f)
        }
        // call add() to add your custom randomizer
        add(YourRandomizer()) 
    },

    /**
     * These are custom parameter randomizers,
     * they can override default randomization logic for their target class
     */
    paramRandomizers = paramRandomizers {

        randomizerForParameter {
            ABC.random()
        }

        randomizerForParameter(
            condition = {
                // some condition
            }
        ) {
            SomeClass.random()
        }

        float(
            condition = { paramInfo ->
                // this means: only apply this randomizer to latitude parameter in LatLng class 
                paramInfo.paramName == "latitude" && paramInfo.enclosingClassIs<LatLng>()
            }
        ) {
            456f
        }

        float {
            // this applies to all other float parameter
            123f
        }
        // call add() to add your custom randomizer
        add(YourRandomizer()) 
    }
)
```

## [Annotations &#9650;](#top) <a id="annotations"></a>

Aside from injecting randomizers via `random()` function, this library provides some annotations doing the same thing.

### [`@Randomizer` &#9650;](#top) <a id="randomizer-annotation"></a>

`@Randomizer` can provide custom randomizers to classes, constructors, and parameters. A custom randomizers passed to `@Randomizer` must extend :
- `ClassRandomizer` / `BaseClassRandomizer` : can be used everywhere
- `ParameterRandomizer` / `BaseParameterRandomizer` : can only be used on parameter
- have a no-arg (zero argument) constructor

Here's an example:

```kotlin
/**
 * Custom randomizer on class
 */
@Randomizer(randomizer = ABC_Randomizer::class)
class ABC

/**
 * A custom randomizer class. Must extend BaseClassRandomizer and has a no-arg constructor
 */
class ABC_Randomizer : BaseClassRandomizer<ABC>() {
    /**
     * returnedInstanceData must be like this
     */
    override val returnedInstanceData: RDClassData = RDClassData.from<ABC>()

    override fun random(): ABC {
        return ABC()
    }
}

class QWE(val x1: Int)

class QWE_Randomizer:BaseParameterRandomizer<QWE>(){
    override val paramClassData: RDClassData = RDClassData.from<QWE>()

    override fun random(parameterClassData: RDClassData, parameter: KParameter, enclosingClassData: RDClassData): QWE? {
        TODO()
    }
}

class MNO(
    val abc: ABC,
    /**
     * Custom randomizer on parameter
     */
    @Randomizer(randomizer = QWE_Randomizer::class)
    val qwe: QWE?,
) {
    /**
     * Custom randomizer on constructor
     */
    @Randomizer(randomizer = MNO_Randomizer::class)
    constructor(
        abc: ABC,
    ) : this(abc, null)
}


```

### [`@RandomInt*` family &#9650;](#top)<a id="random-int-annotation"></a>
This annotation family includes:
- `@RandomIntFixed(<some int>)`: can provide a randomizer that produce a fixed int value
- `@RandomIntOneOf(<an int array>)`: a randomizer that produce an int by randomly picking one from an array of int.
- `@RandomInWithin(from=<Int>, to=<Int>)`: a randomizer that generate random int within a range.

Example:

```kotlin
class ABC(
    @RandomIntFix(3)
    val i1:Int,
    @RandomIntOneOf([1,2,3,4])
    val i2:Int,
    @RandomIntWithin(1,100)
    val i3:Int,
)
```

### [`@RandomFloat*` family &#9650;](#top) <a id="random-float-annotation"></a>
Similar to int, this annotation family provides annotation for generating random float, including:
- `@RandomFloatFixed(<some float>)` : fixed float
- `@RandomFloatOneOf(<a float array>)`: pick one random float from an array of float
- `@RandomFloatWithin(from=<some float>, to=<some float>)`: pick a random float within a range
- `@RandomLatitude`: generate a random float that can be used as latitude (between -90 and 90)
- `@RandomLongitude`: generate a random float that can be used as longitude (between -180 and 180)

Example:
```kotlin
class ABC(
    @RandomFloatFix(3f)
    val f1:Float,
    @RandomFloatOneOf([1f,2f,3f,4f])
    val f2:Float,
    @RandomFloatWithin(1f,100f)
    val f3:Float,
    @RandomLatitude
    val lat:Float,
    @RandomLongitude
    val lng:Float,
)
```

### [`@RandomDouble*` family &#9650;](#top) <a id="random-double-annotation"></a>

Similarly, this annotation family includes:
- `@RandomDoubleFixed(<some double>)`: fixed double value
- `@RandomDoubleOneOf(<a double array>)`: pick one double from an array of double
- `@RandomDoubleWithin(from=<some double>, to=<some double>)`: pick a random double within a range

Example:

```kotlin
class ABC(
    @RandomDoubleFix(3.0)
    val d1:Double,
    @RandomDoubleOneOf([1.0,2.0,3.0,4.0])
    val d2:Double,
    @RandomDoubleWithin(1.0,100.0)
    val d3:Double,
)
```

### [`@RandomString*` family &#9650;](#top) <a id="random-string-annotation"></a>

Similarly, this annotation family includes:
- `@RandomStringFixed(<some String>)`: fixed String value
- `@RandomStringOneOf(<a String array>)`: pick one String from an array of String
- `@RandomStringUUID`: generate a random uuid as string

Example:

```kotlin
class ABC(
    @RandomStringFix("abc")
    val str1:String,
    @RandomStringOneOf(["abc","qwe","bnm"])
    val str2:String,
    @RandomStringUUID
    val uuid:String,
)
```



## [Randomize an inner class &#9650;](#top) <a id="how-to-6"></a>

Inner class is different from normal class, and can be randomized using`randomInnerClass()` function like this:

```kotlin
class Outer {
    inner class Inner
}

val outer = Outer()
val randomInner = randomInnerClass<Outer.Inner>(outer)
```

All the rules of normal randomizers all apply to inner classes.


# [Rule &#9650;](#top) <a id="rule"></a>

These are the randomizing rules used by the library.


## [Randomizers order of priority (important !!!) &#9650;](#top) <a id="rule-4"></a>

Randomizers are categorized into 4 `lv` in this library:
- `lv1`: randomizers provided in `random()` function
- `lv2`: randomizers provided by annotations at parameters
- `lv3`: randomizers provided by annotations at class and constructor
- `lv4`: the default, baked-in randomizers

The order of priority is: `lv1` > `lv2` > `lv3` > `lv4` . (`lv1` has the highest priority)

At the same `lv`, if there are multiple valid randomizers, then one is chosen randomly.

## [How are constructors picked?](#top) <a id="rule-3"></a>

- Annotated constructors (with the above annotations) are prioritized over non-annotated constructors.
- if there are multiple annotated constructors, one will be picked randomly.
- if there is no annotated constructor, the primary constructor will be prioritized. If there's no primary constructor, a random constructor is chosen.

## [How is randomization being done?](#top) <a id="rule-1"></a>

Randomization is done recursively.

### [For concrete class](#top) <a id="rule-1-1"></a>

For a concrete class, randomization is done by invoking one of its constructor with randomized parameters.

### [For sealed class](#top) <a id="rule-1-2"></a>

For sealed class, if no randomizer is provided (either via `random()` or annotations), a random implementation will be chosen and generated.

### [For interface & abstract class](#top) <a id="rule-1-3"></a>

For interface and abstract classes, a custom randomizer `must` be provided either via `random()` function or annotations.

Otherwise, the library will crash.

### [For object](#top) <a id="rule-1-4"></a>

For object, the object itself is returned. So no randomization is actually performed here.

### [For enum](#top) <a id="rule-1-5"></a>

For enum, if no custom randomizer is provided, a random enum value is picked.

### [For inner class](#top) <a id="rule-1-6"></a>

For inner class, randomization is done similarly to a normal class.

<a id="rule"></a>

## [Limitation &#9650;](#top)

<a id="limitation"></a>

### [Class inside function]()

There is a case in which this library will crash. Fortunately, this is a rather uncommon case in
real scenarios.
This is a limitation of the kotlin reflection library (see https://youtrack.jetbrains.com/issue/KT-25573/). So until
then, ...

For example, it is not possible for the library to generate a random instance of `MyClass` below.


```kotlin

fun main() {
    var outsideBool = false
    var outsideStr = ""

    data class MyClass(val i: Int) {
        init {
            outsideBool = true
            outsideStr = "something"
        }
    }

    random<MyClass>() // => this crashes
}
```
### [Function properties]()

At the moment, this library cannot generate random function properties. It will throw an exception.

For example:

```kotlin
class ABC(
    val f:(Int,String)->Float
)
```

A solution is being worked on and hopefully will be in next release


If there are others limitation that I am not aware of. Please let me know.**
