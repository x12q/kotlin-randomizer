## Be careful when...
- Using ArrayList with randomizer factory function because:
  - The function may infer the type in an unexpected way
    - Eg: ConstantClassRandomizer.of(ArrayList(listOf(123))) -> ArrayList<Int!>, not ArrayList<Int>
  - To fix this: specify the type explicitly like this:
    - ConstantClassRandomizer.of(ArrayList<Int>(listOf(123)))
    - ConstantClassRandomizer.of<ArrayList<Int>>(ArrayList(listOf(123)))
