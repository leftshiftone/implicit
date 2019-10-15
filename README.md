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
| NotNull        | Target must not be null                                         |
| NotBlank       | Target must not be blank                                        |
| ContentNotNull | Target content (map/collection entries) must not be bzkk        |
| Between        | Target value must be between the min and max value              |

````
interface IPojo {
    fun setPartitionKey(@NotBlank str: String)
    fun setSortingKey(@NotBlank str: String)
}
````

## Development

### Release
Releases are triggered locally. Just a tag will be pushed and CI pipelines take care of the rest.

#### Major
Run `./gradlew final -x bintrayUpload -Prelease.scope=major` locally.

#### Minor
Run `./gradlew final -x bintrayUpload -Prelease.scope=minor` locally.

#### Patch
Run `./gradlew final -x bintrayUpload -Prelease.scope=patch` locally.
