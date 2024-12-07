Current order of priority:
generic factory function > random context > lv1 random





# Maintain reflection randomizer
- Some built in class need special treatment when call their constructors:
  - Date(). Number passed to constructor must not be null
  - Also, need more descriptive error message for these cases to tell users exactly which constructor is being called, and what is the arguments.


# Transition to compiler plugin

# Preparation

Reasoning:

Front end:

- generate Companion declaration + function declaration, no need the body.
  - for annotated concrete class: generate a static function in companion obj
  - for annotated object: generate a member function inside the object
  - for annotated interface + abstract : if there's a randomizer in the annotation, generate a static function in companion obj
- report error when:
  - a parameter of annotated is abstract, interface
    - can this go deeper and check into nested classes too? No, this may be possible but going too deep will make it very unclear to user. Error reporting should stop at the first level.
  - annotation on interface,abstract class without randomizer
- Generator cannot be created manually, so, there's no way to use dagger with it.
  - to solve that, need to use buffer abstraction layer that is created by dagger, and injected directly into generators.
- testing: use the test library, then write test subject file to test
  - is it possible to include dependencies in test subjects: ok
  - break point:ok 
    Backend:
    Assume that companion object + all function declaration is already there.

# Road map

- write tester infrastructure code for generator : <mark>OK</mark>
- define randomizable annotation : <mark>OK</mark>
- randomize primitive properties:<mark>OK</mark>
  
  - non-null: <mark>OK</mark>
  - nullable: <mark>OK</mark>

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
- Read randomizer from annotation
  - Update randomizer interface, so that it can accept a RandomConfig 
  - Object -> get it
  - Class -> init it
    - find a way to store such instance in a variable so that it can be re-used ???

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


DEBATE:
- so, use random function VS not for nested classes?
  - I can check if a nested class is annotated or not, then call the random function instead of constructor.
    - Additional work
      - the generated random functions now need to accept an external RandomContext/RandomConfig
      - need to check for annotation, and the annotation itself can provide random config, but I don't need to care about that, because I am going to call the random function that accept external context/config.
      - calling that can be challenging because:
        - There are 3 of them.
        - Need to construct the correct Ir param to pass to them. For generic this can be a real hassle because it involve type look up + resolution.
    - Effect: the effect will be exactly as calling constructor, because when I call constructor, I already use all the random config and whatever
  - Conclusion, no need to call random function of nested class

NEXT:

- randomizable list, map, set: ...
  - to generate a random List, I need to: OK
    - Generate expression to invoke List(int){} function within the random() function body: OK
    - Generate expression to construct randomizer for element type of such List at call side: OK
  - to generate random Map: OK
  - tidy up the generator code of list
- need to check isClass function reasonining.
- std class from std library implementations?
  - I can write code to construct them, but how many class do I actually have to write code for?
    - ArrayList, HashMap, and other
    - Date (kt + jvm)
    - Perhaps I need a mode modular architecture for this part to make it easier to add and remove the generator
    
- Constructor selection logic: CONT
  - Annotation on constructors -> pick a random one
  - Annotation on class -> consider it as marked primary constructor
  - Multiple constructors -> put the generation code in lambda -> pick a random lambda from the list, run it.
    - Otherwise, use the normal one.
- Inject custom randomizer via annotation:
  - this one needs to be done after constructor selection logic? or before?

- compile-time (on IDE) error reporting when use @Randomizable on abstract or interface?

- invoke random functions if param is from a class that is annotated with @Randomizable
  - must create a new random function that accept a RandomContext object, so that it is easier to pass RandomContext to random function called by another random function.

BACKLOG:
- randomizable enum
- reconsider having RandomContextBuilder as an interface, a class is all it needs maybe
- Randomize Pair
- Inner class pose a challenge because it require an instance of outer class before it can do anything.
  - Generator code must init an outer instance, then use that to construct the inner.
  - Random function on inner class must accept an optional outer instance
- Reconsider default Random config creation. Should a new seed be used each time, should all random() of all class shared 1 random config or have their own?
