[![CircleCI branch](https://img.shields.io/circleci/project/github/leftshiftone/implicit/master.svg?style=flat-square)](https://circleci.com/gh/leftshiftone/implicit)
[![GitHub tag (latest SemVer)](https://img.shields.io/github/tag/leftshiftone/implicit.svg?style=flat-square)](https://github.com/leftshiftone/implicit/tags)
[![Bintray](https://img.shields.io/badge/dynamic/json.svg?label=bintray&query=name&style=flat-square&url=https%3A%2F%2Fapi.bintray.com%2Fpackages%2Fleftshiftone%2Fimplicit%2Fone.leftshift.implicit.implicit%2Fversions%2F_latest)](https://bintray.com/leftshiftone/implicit/one.leftshift.implicit.implicit/_latestVersion)


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
| Min            | Target value must be greater equals the configured value        |
| Max            | Target value must be greater equals the configured value        |
| NotNull        | Target must not be null                                         |
| NotBlank       | Target must not be blank                                        |
| NotEmpty       | Target (string or collection) must not be empty                 |
| ContentNotNull | Target content (map/collection entries) must not be null        |
| Between        | Target value must be between the min and max value              |
| Pattern        | Target value must match the regex pattern                       |
| Email          | Target value must match the email regex pattern                 |
| URL            | Target value must match the url regex pattern                   |
| Docker         | Target value must match the docker image regex pattern          |
| UUID           | Target value must match the uuid regex pattern          |

````
interface IPojo {
    fun setPartitionKey(@NotBlank str: String)
    fun setSortingKey(@NotBlank str: String)
}
````

## Alias
The @Alias annotation can be used to create an alias method which delegates to the annotated method.
The @Alias annotation can be embedded in an other annotation.

````
interface Entity {
   @Alias("partitionKey") // creates a 'getPartitionKey' method which delegates to 'getIdentityId'
   fun getIdentityId():String
}
````

## Mixin
The @Mixin annotation can be used to add a mixin class to the class of the annotated method on the fly.
The @Mixin annotation can be embedded in an other annotation.

````
interface PartitionKeyAware {
   fun getPartitionKey()
}

interface Entity {
   @Alias("partitionKey")
   @Mixin(PartitionKeyAware::class)
   fun getIdentityId():String
}
````

## EqualsHashCode
The @EqualsHashCode annotation can be used to define which fields should be used for the equals and hashCode generation.
The @EqualsHashCode annotation can be embedded in an other annotation.

````
interface Entity {
   @EqualsHashCode
   fun getIdentityId():String
}
````


## ToString
The @ToString annotation can be used to define which fields should be used for the toString generation.
The @ToString annotation can be embedded in an other annotation.

````
interface Entity {
   @ToString
   fun getIdentityId():String
}
````

## Default
The @Default annotation can be used on getter methods to define a default value when the getter method would return null.
The @Default annotation can be embedded in an other annotation.

This mechanism supports all standard datatypes (int, float, string, ...) as well as
list and map structures.

````
interface Entity {
   @Default("defaultValue")
   fun getStringVal():String?
   @Default("0")
   fun getFloatVal():Float?
   @Default("true")
   fun getBooleanVal():Boolean?
   @Default
   fun getListVal():List<String>?
   @Default
   fun getMapVal():Map<String, String>?
}
````

## GenericType
The @GenericType annotation can be used on methods to let implicit-engine know the type of object that is contained in a collection.


````
interface EntityA {
    fun getContentList(): List<EntityB>
    @GenericType(EntityB::class)
    fun setContentList(content: List<EntityB>)
}

interface EntityB {
    fun getPartitionKey(): String
    fun setPartitionKey(str: String)
}

````
**Use case:** Object instantiation from a Map, where there are fields with collections of nested objects 
e.g.

````
....
fun instantiateEntityA(){
    val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
    val function = factory.getFunction(IPojoAlpha::class.java)
    val instance = function.apply(mapOf(
            "partitionKey" to "abc",
            "contentSet" to arrayOf(mapOf("partitionKey" to "ABC"), mapOf("partitionKey" to "DEF"))
    ))
}
````
## Development

### Release
Releases are triggered locally. Just a tag will be pushed and CI pipelines take care of the rest.

#### Major
Run `./gradlew final -x sendReleaseEmail -Prelease.scope=major` locally.

#### Minor
Run `./gradlew final -x sendReleaseEmail -Prelease.scope=minor` locally.

#### Patch
Run `./gradlew final -x sendReleaseEmail -Prelease.scope=patch` locally.
