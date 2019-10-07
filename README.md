# circe-json-schema

[![Build status](https://img.shields.io/travis/circe/circe-json-schema/master.svg)](https://travis-ci.org/circe/circe-json-schema)
[![Coverage status](https://img.shields.io/codecov/c/github/circe/circe-json-schema/master.svg)](https://codecov.io/github/circe/circe-json-schema)
[![Gitter](https://img.shields.io/badge/gitter-join%20chat-green.svg)](https://gitter.im/circe/circe)
[![Maven Central](https://img.shields.io/maven-central/v/io.circe/circe-json-schema_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/io.circe/circe-json-schema_2.13)

This project provides some basic tools for performing [JSON Schema][json-schema] validation with [Circe][circe].

The current version of the library is a wrapper for the [Everit JSON Schema Validator][everit], although it does
not expose any `org.everit` or `org.json` types in its public API. Future releases will drop the Everit dependency,
although we don't currently have an exact timeline for when this will happen.

The library only supports Draft 7 of the JSON Schema specification.

We are currently testing against the non-`ref` cases provided in the [JSON Schema Test Suite][test-suite].

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
[json-schema]: https://json-schema.org/
[test-suite]: https://github.com/json-schema-org/JSON-Schema-Test-Suite