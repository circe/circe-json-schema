package io.circe.schema

import cats.data.Validated
import io.circe.{ Decoder, Json }
import java.io.File
import org.scalatest.flatspec.AnyFlatSpec

case class SchemaTestCase(description: String, data: Json, valid: Boolean)
case class SchemaTest(description: String, schema: Json, tests: List[SchemaTestCase])

object SchemaTestCase {
  implicit val decodeSchemaTestCase: Decoder[SchemaTestCase] = io.circe.generic.semiauto.deriveDecoder
}

object SchemaTest {
  implicit val decodeSchemaTest: Decoder[SchemaTest] = io.circe.generic.semiauto.deriveDecoder
}

class TestSuiteTests(path: String) extends AnyFlatSpec {
  val tests: List[SchemaTest] = io.circe.jawn
    .decodeFile[List[SchemaTest]](new File(path))
    .getOrElse(
      throw new Exception(s"Unable to load test file: $path")
    )

  tests.foreach {
    case SchemaTest(description, schema, tests) =>
      tests.foreach {
        case SchemaTestCase(caseDescription, data, valid) =>
          val expected = if (valid) "validate successfully" else "fail to validate"
          s"$description: $caseDescription" should expected in {
            val errors = Schema.load(schema).validate(data)

            if (valid) {
              assert(errors == Validated.valid(()))
            } else {
              assert(errors.isInvalid)
            }
          }

          it should s"$expected when schema is loaded from a string" in {
            val errors = Schema.loadFromString(schema.noSpaces).get.validate(data)

            if (valid) {
              assert(errors == Validated.valid(()))
            } else {
              assert(errors.isInvalid)
            }
          }
      }
  }
}

class AdditionalItemsTestSuiteTests extends TestSuiteTests("tests/tests/draft7/additionalItems.json")
class AdditionalPropertiesTestSuiteTests extends TestSuiteTests("tests/tests/draft7/additionalProperties.json")
class AllOfTestSuiteTests extends TestSuiteTests("tests/tests/draft7/allOf.json")
class AnyOfTestSuiteTests extends TestSuiteTests("tests/tests/draft7/anyOf.json")
class BooleanSchemaTestSuiteTests extends TestSuiteTests("tests/tests/draft7/boolean_schema.json")
class ConstTestSuiteTests extends TestSuiteTests("tests/tests/draft7/const.json")
class ContainsTestSuiteTests extends TestSuiteTests("tests/tests/draft7/contains.json")
class DefaultTestSuiteTests extends TestSuiteTests("tests/tests/draft7/default.json")
//class DefinitionsTestSuiteTests extends TestSuiteTests("tests/tests/draft7/definitions.json")
class EnumTestSuiteTests extends TestSuiteTests("tests/tests/draft7/enum.json")
class ExclusiveMaximumTestSuiteTests extends TestSuiteTests("tests/tests/draft7/exclusiveMaximum.json")
class ExclusiveMinimumTestSuiteTests extends TestSuiteTests("tests/tests/draft7/exclusiveMinimum.json")
class FormatTestSuiteTests extends TestSuiteTests("tests/tests/draft7/format.json")
class IfThenElseTestSuiteTests extends TestSuiteTests("tests/tests/draft7/if-then-else.json")
class ItemsTestSuiteTests extends TestSuiteTests("tests/tests/draft7/items.json")
class MaximumTestSuiteTests extends TestSuiteTests("tests/tests/draft7/maximum.json")
class MaxItemsTestSuiteTests extends TestSuiteTests("tests/tests/draft7/maxItems.json")
class MaxLengthTestSuiteTests extends TestSuiteTests("tests/tests/draft7/maxLength.json")
class MaxPropertiesTestSuiteTests extends TestSuiteTests("tests/tests/draft7/maxProperties.json")
class MinimumTestSuiteTests extends TestSuiteTests("tests/tests/draft7/minimum.json")
class MinItemsTestSuiteTests extends TestSuiteTests("tests/tests/draft7/minItems.json")
class MinLengthTestSuiteTests extends TestSuiteTests("tests/tests/draft7/minLength.json")
class MinPropertiesTestSuiteTests extends TestSuiteTests("tests/tests/draft7/minProperties.json")
class MultipleOfTestSuiteTests extends TestSuiteTests("tests/tests/draft7/multipleOf.json")
class NotTestSuiteTests extends TestSuiteTests("tests/tests/draft7/not.json")
class OneOfTestSuiteTests extends TestSuiteTests("tests/tests/draft7/oneOf.json")
class PatternTestSuiteTests extends TestSuiteTests("tests/tests/draft7/pattern.json")
class PatternPropertiesTestSuiteTests extends TestSuiteTests("tests/tests/draft7/patternProperties.json")
class PropertyNamesTestSuiteTests extends TestSuiteTests("tests/tests/draft7/propertyNames.json")
// Not currently running remote tests.
//class RefTestSuiteTests extends TestSuiteTests("tests/tests/draft7/ref.json")
//class RefRemoteTestSuiteTests extends TestSuiteTests("tests/tests/draft7/refRemote.json")
class RequiredTestSuiteTests extends TestSuiteTests("tests/tests/draft7/required.json")
class TypeTestSuiteTests extends TestSuiteTests("tests/tests/draft7/type.json")
class UniqueItemsTestSuiteTests extends TestSuiteTests("tests/tests/draft7/uniqueItems.json")
