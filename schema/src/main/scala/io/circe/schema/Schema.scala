package io.circe.schema

import cats.data.{ Validated, ValidatedNel }
import io.circe.{ Json, JsonNumber, JsonObject }
import org.everit.json.schema.{ Schema => EveritSchema, ValidationException }
import org.everit.json.schema.loader.SchemaLoader
import org.json.{ JSONArray, JSONObject, JSONTokener }
import scala.collection.JavaConverters._
import scala.util.Try

trait Schema {
  def validate(value: Json): ValidatedNel[ValidationError, Unit]
}

object Schema {
  def load(value: Json): Schema = new EveritSchemaImpl(
    SchemaLoader.builder().schemaJson(fromCirce(value)).draftV7Support().build().load().build()
  )

  def loadFromString(value: String): Try[Schema] = Try(
    new EveritSchemaImpl(
      SchemaLoader.builder().schemaJson(new JSONTokener(value).nextValue).draftV7Support().build().load().build()
    )
  )

  private[this] class EveritSchemaImpl(schema: EveritSchema) extends Schema {
    def validate(value: Json): ValidatedNel[ValidationError, Unit] =
      try {
        schema.validate(fromCirce(value))
        Validated.valid(())
      } catch {
        case e: ValidationException => Validated.invalid(ValidationError.fromEverit(e))
      }
  }

  private[this] val fromCirceVisitor: Json.Folder[Object] = new Json.Folder[Object] {
    def onNull: Object = JSONObject.NULL
    def onBoolean(value: Boolean): Object = Predef.boolean2Boolean(value)
    def onString(value: String): Object = value
    def onNumber(value: JsonNumber): Object = value.toInt
      .map(Predef.int2Integer)
      .orElse(value.toLong.map(Predef.long2Long))
      .orElse(value.toBigDecimal.map(_.underlying))
      .getOrElse(Predef.double2Double(value.toDouble))
    def onArray(value: Vector[Json]): Object = new JSONArray(value.map(_.foldWith(this)).toArray)
    def onObject(value: JsonObject): Object = new JSONObject(value.toMap.mapValues(_.foldWith(this)).asJava)
  }

  private[this] def fromCirce(value: Json): Object = value.foldWith(fromCirceVisitor)
}
