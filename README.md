

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
- TODO: random sealed class
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
- TODO Err accumulation:
  - TODO Randomizers at multiple level can be faulty at once. If all fail (including lv4), a comprehensive error report on all lv must be created so that users can debug their code easier.
  - 
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
    
  

