# Using DeclarationIrBuilder

## create an IR const

To create a literal value IR

```kotlin
val irConstInt = 123.toIrConst(pluginContext.irBuiltIns.intType)
```

## Generate a class
Step1: generate it in the FIR layer first

See: 
- SerializationFirResolveExtension.getNestedClassifiersNames for name SerialEntityNames.SERIALIZER_CLASS_NAME
- SerializationFirResolveExtension.generateNestedClassLikeDeclaration for name SerialEntityNames.SERIALIZER_CLASS_NAME
## Generate something new in FIR
When generate something new in fir, and I don't know how to do that, I can:
- write what I want down manually, and use the debugger to see what it looks like in the FIR tree (class, type, etc)
  - To do this:
    - write what I want into a class
    - pass that class through the compiler test code
    - put a break point somewhere, then inspect `declarations` of the class
- then check around (kotlin source code) to see how it is created if possible.
- just look around, inspect objects, etc

## Generate a lambda

Use `pluginContext.irFactory.buildFun()` to create the lambda. See `generate_makeClassRandomizer_Lambda` for example.
To pass lambda as parameter, put the lambda into `IrFunctionExpressionImpl()`

Example:

```kotlin
IrFunctionExpressionImpl(
    startOffset = makeRandomizerLambda.startOffset,
    endOffset = makeRandomizerLambda.endOffset,
    type = pluginContext.irBuiltIns.functionN(1)
        .typeWith(
            randomContextAccessor.irType,
            makeRandomizerLambda.returnType,
        ),
    function = makeRandomizerLambda,
    origin = IrStatementOrigin.LAMBDA,
)
```
