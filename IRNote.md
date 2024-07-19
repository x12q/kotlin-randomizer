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
