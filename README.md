# Implicit

Implicit is a runtime code generation library which can be used to generate POJO classes out of interface definitions.
The idea of this library is, that starting from an interface definition, the entire POJO class definition including
validation and mapping features is created and so reducing the amount of boilerplate code.

With regards to performance the library is designed to have no performance losses in contrast to native object
instantiation.

The generated pojo class inherits all annotations of the interface definition class or method level.

## Usage
````
interface IPojo {
    fun getPartitionKey(): String
    fun setPartitionKey(str: String)

    fun getSortingKey(): String
    fun setSortingKey(str: String)
}


val factory = Implicit { "custom.package.${it.simpleName}" }
val pojo = factory.instantiate(IPojo::class.java)

pojo.setPartitionKey(UUID.randomUUID().toString())
pojo.setSortingKey(UUID.randomUUID().toString())
````

## Map initialization
Implicit also supports a pojo initialization by using a map argument.

````
val factory = Implicit { "custom.package.${it.simpleName}" }
val pojo = factory.instantiate(IPojo::class.java, mapOf("partitionKey" to "A", "sortingKey" to "B")
````

## Validation
By using annotations it is possible to declare validation rules for class methods.
Implicit has the following built-in annotations:

| name           | description                                                     |
|----------------|-----------------------------------------------------------------|        
| GreaterThan    | Target value must be greater than the configured value          |
| LowerThan      | Target value must be lower than the configured value            |
| GreaterEquals  | Target value must be greater equals the configured value        |
| LowerEquals    | Target value must be lower equals the configured value          |
| MaxLength      | Target value length must be lower equals the configured value   |
| MinLength      | Target value length must be greater equals the configured value |
| NotNull        | Target must not be null                                         |
| NotBlank       | Target must not be blank                                        |
| ContentNotNull | Target content (map/collection entries) must not be bzkk        |

````
interface IPojo {
    fun setPartitionKey(@NotBlank str: String)
    fun setSortingKey(@NotBlank str: String)
}
````
