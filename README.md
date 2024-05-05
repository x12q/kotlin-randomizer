# Randomizer

A library for generate random instance for any class (kinda). Like this:

```kotlin
val instance:MyClass = random<SomeClass>()
```
or this

```kotlin
@Randomizable(MyClassRandomizable::class)
class MyClass
```
<a id="top"></a>
- [Install](#install)
- [Usage](#usage)
  - [Generate randoms of some class](#generate-randoms-of-some-class)
  - [Add custom randomizers](#add-custom-randomizers)
    - [Via `random()` function](#via-random-function)
      - [For classes](#for-classes)
      - [For parameters](#for-constructor-parameters)
    - [Via `@Randomizable` annotation](#via-randomizable-annotation)
      - [Implement `ClassRandomizer`](#implement-classrandomizer)
      - [Implement `ParameterRandomizer`](#implement-parameterrandomizer)
- [Limitation](#limitation)

<a id="install"></a>
# [Install &#9650;](#top)
TODO add maven + gradle


<a id="usage"></a>
# [Usage &#9650;](#top)


<a id="generate-random-of-some-class"></a>
## [Generate randoms of some class &#9650;](#top)
```kotlin
val instance:MyClass = random<SomeClass>()
```

<a id="add-custom-randomizers"></a>
## [Add custom randomizers &#9650;](#top)

Custom randomizers can be added to override the default random behavior. 

There are two ways to add custom randomizers:
- Directly via the `random<>()` function (these are called `lv1` randomizers)
- Via `@Randomizable` annotation:
  - These are called `lv2` when `@Randomizable` is used on constructor parameters
  - These are called `lv3` when `@Randomizable` is used on class or constructor

#### **Order of priority (Important)**
When mulitple randomizers are provided to one class, the order of priority is:
- `lv1` > `lv2` > `lv3` 
- This means `lv1` has the highest priority and will be used first even when there exist `lv2`, `lv3` randomizers.
- If there are multiple matching at the same `lv`, a random one at that `lv` will be chosen.

<a id="via-random-function"></a>
## [Via `random()` function &#9650;](#top)


<a id="for-classes"></a>
### [For classes  &#9650;](#top)

Class custom randomizers:
- can override the default randomizing behavior for:
    - all (kinda) classes, abstract classes, interfaces, sealed classes and sealed interface
    - generic classes and interfaces
```kotlin
import kotlin.random.Random

val instance:SomeClass = random<SomeClass>(
    randomizers = randomizers {
        add(classRandomizer {
            // override the default randomizer for OtherClass
            OtherClass(1,2, Random.nextFloat())
        })
        int {
            // override the default Int randomizer
            99
        }
        float {
            // override the default Float randomizer
            1f
        }
        string {
            // override the default String randomizer
            "abc123"
        }
        list {
            // override the default List<Int> randomizer
            listOf(1f, 2f)
        }
    },
)
```

<a id="for-constructor-parameters"></a>
### [For parameters &#9650;](#top)
Custom parameter randomizers:
- can override the default randomizer.
- can check and apply its random logic only when certain conditions are met, such as:
    - when parameter name has to be certain name, and/or
    - the parameter has to be in a certain enclosing class
```kotlin
val instance:SomeClass = random<SomeClass>(
    paramRandomizers = paramRandomizers {
        add(paramRandomizer {
            // override default param randomizer for OtherClass
            OtherClass(123)
        })
        add(paramRandomizer(
            // override default param randomizer for OtherClass with condition
            condition = {paramInfo ->
                paramInfo.paramName == "someParamName"
            },
            random= {
                OtherClass(456)
            }
        ))
        string { paramInfo->
            // override default param randomizer for string
            "${paramInfo.paramName} -- some str"
        }
        int(
            // override default param randomizer for int with condition
            condition = { paramInfo->
                paramInfo.paramName="age" && paramInfo.enclosingKClass == Person::class
            },
            random= {
                Random.nextInt(1000)
            }
        )
    }
)

```

<a id="via-randomizable-annotation"></a>
## [Via `@Randomizable` annotation &#9650;](#top)

This library provide `@Randomizable` annotation that can be used to specified custom randomizers for:
- classes
- constructor parameters
- constructors

The `@Randomizable` annotation can be used as followed:
```kotlin
// on class
@Randomizer(randomizer = MyABCRandomizerClass::class)
class ABC

class QWE(
    // on parameter
    @Randomizable(randomizer = MyX1RandomizerClass::class)
    val x1:X1
)

class MNO(
    val x2:X2,
    val str:String,
){
    // on constructor
    @Randomizable(randomizer = MyMNORandomizerClass::class)
    constructor(x2:X2):this(x2,"someStr")
}
```

Randomizer classes passed to `@Randomizable` must:
- implement/extend either `ClassRandomizer` or `ParameterRandomizer`
- and have a no-argument constructor 


<a id="implement-classRandomizer"></a>
### [Implement `ClassRandomizer` &#9650;](#top)

- `ClassRandomizer` can be implemented directly. 

- For common use case, extend `AbsSameClassRandomizer` instead for less boilerplate code.
```kotlin
class MyX1RandomizerClass:AbsSameClassRandomizer<X1>(){
  // This is a must
  val returnedInstanceData: RDClassData = RDClassData.from<X1>()
  
  fun random(): ABC{
    // do your random business here
      return X1()
  }
}
```

```kotlin

class MyABCRandomizerClass : ClassRandomizer<ABC>{
    // This is a must
    val returnedInstanceData: RDClassData = RDClassData.from<ABC>()
    
    fun isApplicableTo(classData: RDClassData): Boolean{
        // you can provide custom check if you want here
        return classData == returnedInstanceData
    }
    
    fun random(): ABC{
        // do your random business here
        return ABC()
    }
}
```

<a id="implement-paramRandomizer"></a>
### [Implement `ParameterRandomizer` &#9650;](#top)

- `ParameterRandomizer` can be implemented directly.
- For common use case, extend `AbsSameClassParamRandomizer` instead for less boilerplate code.


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

```kotlin
class A3Randomizer: ParameterRandomizer<A3>{
    // this is a must
    val paramClassData: RDClassData = RDClassData.from<A3>()

    fun isApplicableTo(
      paramInfo:ParamInfo
    ): Boolean{
        // you can provide custom check here
        return paramInfo.paramClassData == this.paramClassData
        
    }

    fun random(
      parameterClassData: RDClassData,
      parameter: KParameter,
      enclosingClassData: RDClassData,
    ):A3?{
        // do you random business here
        return A3("something random")
    }
}

```

<a id="limitation"></a>
## [Limitation &#9650;](#top)

There are cases in which this library will crash. Fortunately, these are pretty weird cases that are very uncommon in real scenarios.

For example, it is not possible for this library to generate a random instance of `MyClass` below.

This is a limitation of the kotlin reflection library (see https://youtrack.jetbrains.com/issue/KT-25573/). So until then, ...  

Well, there may be others that I am not aware of ...

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
