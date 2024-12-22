
# TODO:
- TODO: cleanup the dangerous code of the new type passing feature
- TODO: at the moment, it is not possible to use custom randomizer with nullable type.
  - Nullable type is being generated based on non-null type and a boolean check from RandomConfig.
  - It should be that nullable randomizer can be declared directly within custom randomizer list.
- Reconsider default Random config creation. Should a new seed be used each time, should all random() of all class shared 1 random config or have their own?
- TODO: somehow, not storing RandomConfig in a variable is causing problem in the new random function 2
- TODO: add an error code system to easier trace error.
- TODO: Randomizable annotation maybe used with open classes too -> so not strictly just for interface and abstract.


# Road map

- write tester infrastructure code for generator : <mark>OK</mark>
- define randomizable annotation : <mark>OK</mark>
- randomize primitive properties:<mark>OK</mark>
  - non-null: <mark>OK</mark>
  - nullable: <mark>OK</mark>
- randomizable standard collection (list, map, set): OK
- randomize concrete class with concrete properties:
  - enum property: <mark>OK</mark>
  - object property: <mark>OK</mark>
  - concrete class: <mark>semi-OK</mark>
  - abstract parameter/class: throw exception if it is not annotated
    - Abstract parameter randomization depends on randomizer in annotation, so work on that first
    - because abstract parameter must be annotated with a @Randomizable. Some question must be answer:
      - For RandomConfig, order of priority is: its own RandomConfig > enclosing class RandomConfig > Default RandomConfig
      - Annotating an abstract with @Randomizable without specifying Randomizer is illegal.
  - generic: <mark>Ok</mark>
    - Solution: generic is handled by asking user to provide random function for each type param
      - The generated `random` function now must be inline, type param must be `reified`. 
      - Example, the generated random() functions will look like this
        ```kotlin
        class ABC<T1:Number,T2>(val t1:T1, val t2:T2){
            companion object{
                inline fun <reified T1:Number,reified T2>random(
                    randomT1:(RandomConfig)->T1, 
                    randomT2:(RandomConfig)->T2,
                ):ABC<T1,T2>{
                    ...
                }
        
                fun <T1,T2>random(
                    randomConfig:RandomConfig, 
                    randomT1:(RandomConfig)->T1, 
                    randomT2:(RandomConfig)->T2,
                ):ABC<T1,T2>{
                   ...
                } 
            }
        } 
        ```
- nullable <mark>OK</mark>
  - add nullable randomization <mark>OK</mark>
    - call nextBool on randomConfig, and choose between a not null and a null value <mark>OK</mark>

- Find a way to inject random logic from random function:
  - Example of injecting random logic for a particular class:
    ```kotlin
    val q = ABC.random(
      randomConfig = randomConfig, 
      randomT1 = {},
      randomT2 = {},
      randomizers = randomizers{
          randomizer{
              SomeClass()
          }
          int{ 1234}
          string{"randomString"}
      },
      paramRandomizers = paramRandomizers{
          ///.....///
      } 
    )
    ```
    - Inject random logic for a particular parameter

- randomize concrete class with abstract properties:
  
  - recognize randomizer in @Randomizable
  - invoke it
  - error reporting on error cases.
  - add a property on @Randomizable allowing user to provide a @Randomizable/or non-annotated implementation directly

- How to pass custom Random instances to generator?
  
  - Conclusion :
    - Provide 2 ways in which user can inject random configuration
      - Via randomizable annotation
      - Via random(randomConfig) function
  - Reasoning:
    - Generate another random function that accepts a RandomConfig Obj
      - can this co-exist with passing RandomConfig via Annotation?
      - What is the advantages + disadvantages of this over Annotation?
        - Advantages
          - i don't need to write code to read the random config + init random config from the annotation
          - users can pass random config explicitly
        - Disadvantage:
          - random() is locked to a default random config
            - counter-point: the default random config can be modified by users in their code.
      - Is it nice to have both?
        - It may not be nice to have both because:
          - it is confusing: users can provide both a config in annotation + use another one in their random(config) called.
        - It is nice to have both because:
          - a target is not locked to a single random config, but can have a default one, and countless other for random(config)
          - at the same time, users can provide a custom default config, so that they don't have to think about what to use down the line if they don't want to.


- randomizer-lib:contain annotation + randomizer interface + basic randomizer implementation: this is used by both the compiler plugin + end user
  
  - randomizer-reflection: contains reflection code

# NEXT:

- randomizing std class from std library implementations? -> can be written directly in the randomizer-lib module

- Constructor selection logic: CONT
  - Annotation on constructors -> pick a random one
  - Annotation on class -> consider it as marked primary constructor
  - Multiple constructors -> put the generation code in lambda -> pick a random lambda from the list, run it.
    - Otherwise, use the normal one.
- Inject custom randomizer via annotation:
  - this one needs to be done after constructor selection logic? or before?

- invoke random functions if param is from a class that is annotated with @Randomizable
  - must create a new random function that accept a RandomContext object, so that it is easier to pass RandomContext to random function called by another random function.

# BACKLOG:
- randomizable enum
- reconsider having RandomContextBuilder as an interface, a class is all it needs maybe
- Randomize Pair
- Inner class pose a challenge because it require an instance of outer class before it can do anything.
  - Generator code must init an outer instance, then use that to construct the inner.
  - Random function on inner class must accept an optional outer instance
