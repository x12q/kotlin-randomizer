# Usage
lv1: randomizer specified by user in the top lv function
lv2: paramter randomizer specified at constructor paramter
lv3: constructor randomizer specified at either class or constructor:
    - if there are multiple valid lv3 randomizers -> pick a random one
lv4: defaut recursive randomizer baked into the random generator.

# Crash case:
lv2 with incompatible target class
lv3 with incompatible target class
target class is abstract without valid lv3 or lv2


# Requirement:
 - recursive function to traverse the constructor tree, and init object.
 - for abstract parameter, add @Randomizable(ConcreteClass1::class, ConcreteClass2::class, AnotherRandomizableAbstractClass::class)
 - For abstract class, add the same @Randomizable, the one in parameter override the one in the class.
 - For concrete class, add @Randomizable(factoryFunction=...) or @Randomizable(randomizer = CustomRandomizer::class)

## The top level random function:
- should it accept some kind of master rule that override everything?
     - if so, what should those rule looks like?
         - each param rule contains:
             - a param name
             - type/ class of the param
             - a Parent class
             - a factory function
         - each class rule contains:
             - a class name
             - a factory function
    - Rule for primitive types (string, int, etc) must be very easy to set         
    
## Roadmap:
- x: random enum
- x: random object
- x: random sealed class
  - object
  - class with param
  - class with generic param
- x: create structure to manage custom class and parameter randomizer
  - x: allow user to provide class randomizer + param randomizer
- x: double check primitive class randomizers (for int, float, double, etc...)
- x: remove repetition in RandomizerEnd
- x: improve performance of randomizer by avoiding creating randomizer obj before it is needed

- x: Add @Randomizable annotation + integrate its content(concrete class + randomizer) into the random logic
  - x: Priority order: randomizer from top-level function (lv1) -> parameter randomize (lv2) -> class randomizer (lv3) -> no randomizer (lv4)
  - TODO Test more, this is very important
    - test generic
    - test appropriate overriding
    - take a look at randomChildren
- x: Add easier to use builder for param randomizer + class randomizer (add a simple DSL + simplify ClassRandomizer + ParamRandomizer factory functions)
- x: add ability to pick constructor
- x: support inner class
    
- TODO Add some aspect-wise configuration / chain randomizer:
  - TODO The len of randomized collection
  - TODO The range of primitive number
  - TODO way to generate string:
    - random str
    - UUID

Tentative feature:
- Constructor rule (low priority)
  - If no rule is provided -> default to primary constructor
  - One way to make constructor marking easier is to use annotation to mark constructor. And then declare such annotation in the constructor rule.
  - Provide user a way to access the low level constructor data so that they can do whatever they want at the low level.


Not support (yet) and known crash:

Issue 1: this is a limitation of kotlin language, there's an open issue: https://l.messenger.com/l.php?u=https%3A%2F%2Fyoutrack.jetbrains.com%2Fissue%2FKT-25573%2F&h=AT1tpVdGxWJYHcu2XCgZbEF4IVMVCHQIrbGqG6cG0awC5uTWpq20a8eSJk_Fu3AfLvyauZJxJh9N1Ww6P8kPeleimIeP2oQvo6sELDpku6hRfrCSzr80utKVkhr0zQ

```kotlin

fun main() {


    var q = false
    var __x = false
    var k = false
    var j = false

    data class C(val i: Int, val str: String, val b: Boolean) {
        init {
            q = true
            __x = true
            k = true
            j = false
        }
    }
    C::class.constructors

    println(
        C::class.primaryConstructor!!.call(1, "", true)
    ) // this throw exception IllegalArgumentException: Callable expects 7 arguments, but 3 were provided

    println(C::class.primaryConstructor!!.call(BooleanRef(), BooleanRef(), BooleanRef(), BooleanRef(),1, "", true)) // this works
}
```
Issue 2

```kotlin
// inner class
class QX{
    inner class C(val i: Int, val str: String, val b: Boolean) 
}

```
