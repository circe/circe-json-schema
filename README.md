# circe-json-schema

[![Build](https://github.com/circe/circe-json-schema/workflows/Continuous%20Integration/badge.svg)](https://github.com/circe/circe-json-schema/actions)
[![Coverage status](https://img.shields.io/codecov/c/github/circe/circe-json-schema/master.svg)](https://codecov.io/github/circe/circe-json-schema)
[![Gitter](https://img.shields.io/badge/gitter-join%20chat-green.svg)](https://gitter.im/circe/circe)
[![Maven Central](https://img.shields.io/maven-central/v/io.circe/circe-json-schema_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/io.circe/circe-json-schema_2.13)

This project provides some basic tools for performing [JSON Schema][json-schema] validation with [Circe][circe].

The current version of the library is a wrapper for the [Everit JSON Schema Validator][everit], although it does
not expose any `org.everit` or `org.json` types in its public API. Future releases will drop the Everit dependency,
although we don't currently have an exact timeline for when this will happen.

The library only supports Draft 7 of the JSON Schema specification.

We are currently testing against the non-`ref` cases provided in the [JSON Schema Test Suite][test-suite].

## Setup

Note that this library currently depends on the most recent version of the Everit validator, which
is not published to Maven Central. You'll need to add the [Jitpack][jitpack] resolver to your build:

```scala
resolvers += "jitpack".at("https://jitpack.io")
```

See the [Everit documentation][everit] for the equivalent Maven configuration.

Once you've configured the resolver, you can add this project to your
dependencies:
```scala
libraryDependencies += "io.circe" %% "circe-json-schema" % "0.1.0"
```
And the appropriate Everit version will be pulled in transitively.

## Usage

The `io.circe.schema` package contains just two types (`Schema` and `ValidationError`), each of which
has just a few methods. You can load a schema from either a `Json` value or a string.

```scala
import io.circe.literal._
import io.circe.schema.Schema

val polygonSchema: Schema = Schema.load(
  json"""
    {
      "type": "object",
      "properties": {
        "type": {
          "type": "string",
          "const": "Polygon"
        },
        "coordinates": {
          "type": "array",
          "items": {
            "type": "array",
            "minItems": 2,
            "maxItems": 3
          }
        }
      }
    }
  """
)
```

The `validate` method on `Schema` instances returns either nothing (as `Valid(())`) or a non-empty
list of validation errors, so if we have some values like this:

```scala
val good = json"""{"type": "Polygon", "coordinates": [[0, 0], [1, 0], [1, 1], [0, 1]] }"""
val bad1 = json"""true"""
val bad2 = json"""{"type": "Shape", "coordinates": [[0, 0], [1, 0], [1, 1], [0, 1]] }"""
val bad3 = json"""{"type": "Polygon", "coordinates": [[0], [1, 0], [1, 2, 3, 4], [0, 1]] }"""
val bad4 = json"""{"type": "Polygon", "coordinates": [1, 0] }"""
```

The results look like this:

```scala
scala> polygonSchema.validate(good)
res0: cats.data.ValidatedNel[io.circe.schema.ValidationError,Unit] = Valid(())

scala> polygonSchema.validate(bad1).swap.map(_.toList).toList.flatten.map(_.getMessage).foreach(println)
#: expected type: JSONObject, found: Boolean

scala> polygonSchema.validate(bad2).swap.map(_.toList).toList.flatten.map(_.getMessage).foreach(println)
#/type: #: only 1 subschema matches out of 2
#/type:

scala> polygonSchema.validate(bad3).swap.map(_.toList).toList.flatten.map(_.getMessage).foreach(println)
#/coordinates: 2 schema violations found
#/coordinates/0: expected minimum item count: 2, found: 1
#/coordinates/2: expected maximum item count: 3, found: 4

scala> polygonSchema.validate(bad4).swap.map(_.toList).toList.flatten.map(_.getMessage).foreach(println)
#/coordinates: 2 schema violations found
#/coordinates/0: expected type: JSONArray, found: Integer
#/coordinates/1: expected type: JSONArray, found: Integer
```

The details of these errors and the error messages are subject to change.

## Contributors and participation

This project supports the Scala [code of conduct][code-of-conduct] and we want
all of its channels (Gitter, GitHub, etc.) to be welcoming environments for everyone.

Please see the [Circe contributors' guide][contributing] for details on how to submit a pull
request.

## License

circe-json-schema is licensed under the **[Apache License, Version 2.0][apache]**
(the "License"); you may not use this software except in compliance with the
License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[apache]: http://www.apache.org/licenses/LICENSE-2.0
[api-docs]: https://circe.github.io/circe-json-schema/api/io/circe/
[circe]: https://github.com/circe/circe
[code-of-conduct]: https://www.scala-lang.org/conduct.html
[contributing]: https://circe.github.io/circe/contributing.html
[everit]: https://github.com/everit-org/json-schema
[jitpack]: https://jitpack.io/
[json-schema]: https://json-schema.org/
[test-suite]: https://github.com/json-schema-org/JSON-Schema-Test-Suite
