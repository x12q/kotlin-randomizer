:construction: :construction: :construction: Work in progress :construction: :construction: :construction:
## Be careful when...
- Using ArrayList with randomizer factory function because:
  - The function may infer the type in an unexpected way
    - Eg: ConstantClassRandomizer.of(ArrayList(listOf(123))) -> ArrayList<Int!>, not ArrayList<Int>
  - To fix this: specify the type explicitly like this:
    - ConstantClassRandomizer.of(ArrayList<Int>(listOf(123)))
    - ConstantClassRandomizer.of<ArrayList<Int>>(ArrayList(listOf(123)))
# Support built-in types:
  - Int, Long, Byte, Short
  - UInt, ULong, UShort, UByte
  - Float, Double, Number
  - Boolean, Char
  - String, Unit, Any
  - List, Map, Set
  - ArrayList
  - HashMap
  - LinkedHashMap
  - HashSet
  - LinkedHashSet
  - Array
