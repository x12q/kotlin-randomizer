# Randomizer

A library for generating random instance of any class, interface, object, and enum (kinda), for testing and prototyping.


# Introduction

Generating random data for testing sometimes can be tedious, especially with large old classes that have deep nesting. This library aims to making generating random instances for any class easier.

A random instance of any class can be created as easy as this:

```kotlin
val instance = random<MyClass>()
```
Custom random logic can also be easily provided via `random()` function or `@Randomizable` annotation. Read more below to see how.

Get it here:


## [Gradle](#gradle) <a id="gradle"></a>
```
dependencies {
    implementation("com.x12q:randomizer:1.0.0-alpha.5")
}
```
## [Maven](#maven) <a id="maven"></a>
```
<dependency>
    <groupId>com.x12q</groupId>
    <artifactId>randomizer</artifactId>
    <version>1.0.0-alpha.5</version>
</dependency>
```
# Content
<a id="top"></a>

- [How to](#how-to)
    - [Randomize a class](#how-to-1)
    - [Randomize a class with custom randomizers](#how-to-2)
        - [Custom randomizers via `random()` function](#how-to-2-1)
            - [Create custom randomizers with `randomizers()` builder](#how-to-2-1-1)
            - [Create custom randomizers with factory function](#how-to-2-1-2)
        - [Custom randomizers via `@Randomizable` annotation](#how-to-2-2)
            - [Create custom randomizers by implementing `ClassRandomizer` interface](#how-to-2-2-1)
    - [Randomize a parameter](#how-to-3)
    - [Randomize a parameter with custom randomizers](#how-to-4)
        - [Custom param randomizers via `random()` function](#how-to-4-1)
            - [Create custom param randomizers with `paramRandomizers()` builder](#how-to-4-1-1)
            - [Create custom param randomizers with factory functions](#how-to-4-1-1)
        - [Custom param randomizers via `@Randomizable` annotation](#how-to-4-2)
            - [Create custom param randomizers by implementing `ParamRandomizer` interface](#how-to-4-2-1)
    - [Randomize an inner class](#how-to-6)
    - [Change base random configs](#how-to-7)
- [Rule](#rule)
    - [Custom randomizer order of priority (important !!!)](#rule-4)
    - [How `@Randomizable` works?](#rule-2)
    - [How are constructors picked?](#rule-3)
    - [How is randomization being done?](#rule-1)
        - [For concrete class](#rule-1-1)
        - [For sealed class](#rule-1-2)
        - [For interface & abstract class](#rule-1-3)
        - [For object](#rule-1-4)
        - [For enum](#rule-1-5)
        - [For inner class](#rule-1-6)
- [Limitation](#limitation)



# [How to &#9650;](#top) <a id="how-to"></a>

To generate random instance of any class, always use `random()` function.

The use of this library can be as simple or as complex as it can be, it's all up to the users.

For most use case, these are often more than enough:

- `random()`: to create random instances
- `randomizers()`: to create custom class randomizers
- `paramRandomizers()`: to create custom param randomizers

`@Randomizable` annotation is for global configuration.

See below for more detail on how to use these.

## [Randomize a class &#9650;](#top) <a id="how-to-1"></a>

```kotlin
val randomInstance = random<SomeClass>()
```

## [Randomize a class with custom randomizers &#9650;](#top) <a id="how-to-2"></a>

There are two ways to use custom randomizers to generate random instances of classes:

- via `random()` functions
- via `@Randomizable` annotation, which can be used on class, interface, sealed class, enum, constructor, and
  constructor parameter (parameter for short)
  <a id=""></a>

### [Custom randomizers via `random()` function &#9650;](#top) <a id="how-to-2-1"></a>

`random()` function accepts a list of custom randomizers, and can use them to generate random to applicable classes.
Like this:

```kotlin
val randomInstance = random<SomeClass>(
    randomizers = listOf<ClassRandomizer<*>>()
)
```

There are 3 ways to create custom randomizers that can be passed to `random()`.

- use `randomizers()`builder (preferred)
- use factory functions
- implement `ClassRandomizer` interface (detailed in `@Randomizable` section)

#### [Create custom randomizers with `randomizers()` builder &#9650;](#top)  <a id="how-to-2-1-1"></a>

`randomizers()` is a builder function that can be used to create custom randomizers for various primitive types, as
well, as any class. It is the recommended way to create custom randomizers. It looks like this:

```kotlin
val randomInstance = random<SomeClass>(
    randomizers = randomizers {
        randomizerForClass {
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
        add(YourRandomizer()) // call add() to add your custom randomizer
    }
)
```

#### [Create custom randomizers with factory functions &#9650;](#top) <a id="how-to-2-1-2"></a>

Custom randomizers can also be created using factory functions, in fact, this is equivalent to using the builder above
because the builder is actually backed by these factory functions. It looks like this:

```kotlin
val randomInstance = random<MyClass>(
    randomizers = listOf(
        classRandomizer {
            // custom randomizer for some class 
            SomeClass.random()
        },
        intRandomizer {
            // custom randomizer for Int
            (1..200).random() * (-1)
        },
        floatRandomizer {
            // custom randomizer for Float
            123f
        },
        stringRandomizer {
            // custom randomizer for String
            "abc123"
        },
        listRandomizer {
            // custom randomizer for List<Float>
            listOf(1f, 2f)
        },
        YourRandomizer(), // your custom randomizer
    )
)
```

### [Custom randomizers via `@Randomizable` annotation &#9650;](#top) <a id="how-to-2-2"></a>

`@Randomizable` annotation is provided by this library. It can be used to provide custom randomizers to:

- class, interface, sealed class, abstract class
- enum
- constructor (both primary and secondary)
- constructor parameter (both primary and secondary)

It looks like this:

```kotlin
// on class
@Randomizer(randomizer = MyABCRandomizerClass::class)
class ABC

class QWE(
    // on parameter
    @Randomizable(randomizer = MyX1RandomizerClass::class)
    val x1: X1
)

class MNO(
    val x2: X2,
    val str: String,
) {
    // on constructor
    @Randomizable(randomizer = MyMNORandomizerClass::class)
    constructor(
        x2: X2,
        @Randomizable(randomizer = MyX4Randomizer::class)
        x4: X4,
    ) : this(x2, "someStr")
}
```

`@Randomizable` is also used to marked preferred constructors. For detail on how `@Randomizable` works, click [here](#rule-3)

Custom randomizer class passed to `@Randomizable` must:

- implement `ClassRandomizer` interface (but `AbsSameClassRandomizer` is preferred in most cases)
- have a no-argument public constructor

#### [Create custom randomizers by implementing `ClassRandomizer` interface &#9650;](#top) <a id="how-to-2-2-1"></a>

In most cases where custom randomizers only need trivia type check, it is preferred to extend `AbsSameClassRandomizer`
abstract class because this one already contains the basic type check logic.

It looks like this:

```kotlin
class SomeCustomRandomizer : AbsSameClassRandomizer<SomeClass>() {
    // this is a must
    override val returnedInstanceData: RDClassData = RDClassData.from<SomeClass>()

    override fun random(): SomeClass {
        // your random logic
        return SomeClass.random()
    }
}
```

Or, if you prefer implementing the `ClassRandomizer` interface, it looks like this

```kotlin
class SomeCustomRandomizer : ClassRandomizer<SomeClass> {
    // this is a must
    override val returnedInstanceData: RDClassData = RDClassData.from<SomeClass>()

    override fun isApplicableTo(classData: RDClassData): Boolean {
        // your checking logic
        return classData == returnedInstanceData
    }

    override fun random(): SomeClass {
        // your random logic
        return SomeClass.random()
    }
}
```

## [Randomize a parameter &#9650;](#top) <a id="how-to-3"></a>

Constructor parameters (parameters for short) is randomized when we randomize a class. But this library can do more than
just that. It can randomize specific parameters of specific classes in specific ways.

For example: we may want a `val latitude:Float` to be within a certain valid range, but at the same, we don't want to
apply that range to other `Float` in other classes. Or maybe, on top of all that, we want to apply some other random
logic to `val money: Float` parameter in another class.

We can do that by providing custom parameter randomizers.

## [Randomize a parameter with custom randomizers &#9650;](#top) <a id="how-to-4"></a>

### [Custom param randomizers via `random()` function &#9650;](#top) <a id="how-to-4-1"></a>

Similarly to class randomizers, `random()` function accepts a list of custom param randomizers, and can use them to
generate random parameter to targeted classes. Like this:

```kotlin
val randomInstance = random<SomeClass>(
    paramRandomizers = listOf<ParamRandomizer<*>>()
)
```

Again, similarly to class randomizer, there are 3 ways to create custom param randomizers that can be passed
to `random()`.

- use `paramRandomizers()`builder (preferred)
- use factory functions
- implement `ParamRandomizer` interface (detailed in `@Randomizable` section)

#### [Create custom param randomizers via `paramRandomizers()` builder &#9650;](#top) <a id="how-to-4-1-1"></a>

Unlike the other builder, `paramRandomizers()` builder is a bit more capable. It can create conditional parameter
randomizers as well as non-conditional randomizers.

Similar to the other builder, `paramRandomizers()` also provides functions to create randomizers for primitive
types and custom classes.

Like this:

```kotlin
val randomInstance = random<MyClass>(
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
        
        add(YourRandomizer()) // call add() to add your custom randomizer
    }
)
```

#### [Create custom param randomizers with factory functions &#9650;](#top) <a id="how-to-4-1-1"></a>

Similar to class randomier, param randomizers can also be created using factory function. This is equivalent to using
the builder.

Like this:

```kotlin
 val randomInstance = random<MyClass>(
    paramRandomizers = listOf(
        paramRandomizer {
            ABC.random()
        },

        paramRandomizer(
            condition = {
                // some condition
            }
        ) {
            SomeClass.random()
        },
        
        floatParamRandomizer(
            condition = { paramInfo ->
                // this means: only apply this randomizer to latitude parameter in LatLng class
                paramInfo.paramName == "latitude" && paramInfo.enclosingClassIs<LatLng>()
            }
        ) {
            456f
        },
        
        floatParamRandomizer {
            // this applies to all other float parameter
            123f
        },
        
        YourRandomizer(), // your custom randomizer
    )
)
```

### [Custom param randomizers via `@Randomizable` annotation &#9650;](#top) <a id="how-to-4-2"></a>

It looks like this:

```kotlin
class QWE(
    @Randomizable(randomizer = MyX1RandomizerClass::class)
    val x1: X1
)

class MNO(
    val x2: X2,
    val str: String,
) {
    constructor(
        x2: X2,
        @Randomizable(randomizer = MyX4Randomizer::class)
        x4: X4,
    ) : this(x2, "someStr")
}
```

Custom randomizer class passed to `@Randomizable` must:

- implement `ParamRandomizer` interface (but `AbsSameClassParamRandomizer` is preferred in most cases)
- have a no-argument public constructor
-

#### [Create custom param randomizers by implementing `ParamRandomizer` interface &#9650;](#top) <a id="how-to-4-2-1"></a>

In most cases where custom randomizers only need trivia type check, it is preferred to
extend `AbsSameClassParamRandomizer` abstract class because this one already contains the basic type check logic.

It looks like this:

```kotlin
class A2Randomizer : AbsSameClassParamRandomizer<A2>() {
    // this is a must
    override val paramClassData: RDClassData = RDClassData.from<A2>()

    override fun random(
        parameterClassData: RDClassData,
        parameter: KParameter,
        enclosingClassData: RDClassData
    ): A2? {
        // do your random business here
        return A2("from custom randomizer")
    }
}
```

Or, if you prefer implementing the `ParamRandomizer` interface, it looks like this:

```kotlin
class A3Randomizer : ParameterRandomizer<A3> {
    // this is a must
    val paramClassData: RDClassData = RDClassData.from<A3>()

    fun isApplicableTo(
        paramInfo: ParamInfo
    ): Boolean {
        // you can provide custom check here
        return paramInfo.paramClassData == this.paramClassData

    }

    fun random(
        parameterClassData: RDClassData,
        parameter: KParameter,
        enclosingClassData: RDClassData,
    ): A3? {
        // do you random business here
        return A3("something random")
    }
}
```

## [Randomize an inner class &#9650;](#top) <a id="how-to-6"></a>

Inner class can be randomized like this:

```kotlin
class Outer {
    inner class Inner
}

val outer = Outer()
val randomInner = randomInnerClass<Outer.Inner>(outer)
```

All the rules of normal randomizers all apply to inner classes.

## [Change base random configs &#9650;](#top) <a id="how-to-7"></a>

Base random configurations can be changed like this. This consist of configure use by the default randomizers when
no custom randomizers are specified.

```kotlin
val randomABC = random<ABC>(
    defaultRandomConfig = DefaultRandomConfig.default.copy(
        // change it here
    )
)
```

# [Rule &#9650;](#top) <a id="rule"></a>

These are the randomizing rules used by the library.


## [Randomizers order of priority (important !!!) &#9650;](#top) <a id="rule-4"></a>

Randomizers are categorized into 4 `lv` in this library:
- `lv1`: randomizers provided in `random()` function
- `lv2`: randomizers provided in `@Randomizable` at constructor parameters
- `lv3`: randomizers provided in `@Randomizable` at class and constructor
- `lv4`: the default, baked-in randomizers

The order of priority is: `lv1` > `lv2` > `lv3` > `lv4` . (`lv1` has the highest priority)

If at the same `lv`, there are multiple valid randomizers, then one is chosen randomly.


## [How `@Randomizable` works?](#top) <a id="rule-2"></a>

- `@Randomizable` can be used to annotate:
    - class, enum, sealed class, interface, abstract class, inner class
    - constructor
    - constructor parameter

- `@Randomizable` plays a role in constructor picking, see the below rule.

- if a `@Randomizable` with valid randomizer is applied on a constructor, that valid randomizer will be used instead of the constructor.

## [How are constructors picked?](#top) <a id="rule-3"></a>

In a class:
- constructors annotated with `@Randomizable` (either blank, or with a valid randomizer) are prioritized over non-annotated constructors, including primary constructors.
- if some constructors are annotated with `@Randomizable`, one will be picked randomly among the annotated constructors.
- if no constructor is annotated with `@Randomizable`, the primary constructor will be used. All other constructors are ignored.

## [How is randomization being done?](#top) <a id="rule-1"></a>

Randomization is done recursively.

### [For concrete class](#top) <a id="rule-1-1"></a>

For a concrete class, randomization is done by invoking one of its constructor with randomized parameters.

### [For sealed class](#top) <a id="rule-1-2"></a>

For sealed class, if no custom randomizer is provided (either via `random()` or `@Randomizable`), a random implementation will be chosen and generated.

### [For interface & abstract class](#top) <a id="rule-1-3"></a>

For interface and abstract classes, a custom randomizer must be provided:
- either via the `random()` function
- or `@Randomizable` on such interfaces or abstract classes
- or `@Randomizable`on parameter of such type

Otherwise the library will crash.

### [For object](#top) <a id="rule-1-4"></a>

For object, the object itself is returned. So no randomization is done here.

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

It is not possible for this library to generate a random instance of `MyClass` below.

This is a limitation of the kotlin reflection library (see https://youtrack.jetbrains.com/issue/KT-25573/). So until
then, ...


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

At the moment, this library cannot generate random function properties such as in this case. It will throw an exception.

A solution is being worked on and hopefully will be in next release
```kotlin
class ABC(
    val f:(Int,String)->Float
)
```

There are others limitation that I am not aware of. Please let me know.
