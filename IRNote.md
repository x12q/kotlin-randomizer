# Using DeclarationIrBuilder

## create an IR const

To create a literal value IR

```kotlin
val irConstInt = 123.toIrConst(pluginContext.irBuiltIns.intType)
```
