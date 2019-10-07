package io.circe.schema

import cats.data.NonEmptyList
import org.everit.json.schema.ValidationException
import scala.collection.mutable.Builder

sealed abstract class ValidationError(
  val keyword: String,
  val location: String,
  val schemaLocation: Option[String]
) extends Exception {
  final override def fillInStackTrace(): Throwable = this
}

object ValidationError {
  def apply(
    keyword: String,
    message: String,
    location: String,
    schemaLocation: Option[String] = None
  ): ValidationError =
    new ValidationError(keyword, location, schemaLocation) {
      final override def getMessage: String = message
    }

  private[this] def fromEveritOne(e: ValidationException): ValidationError =
    apply(e.getKeyword, e.getMessage, e.getPointerToViolation, Option(e.getSchemaLocation))

  private[this] def fromEveritRecursive(
    e: ValidationException,
    builder: Builder[ValidationError, List[ValidationError]]
  ): Unit = {
    val nested = e.getCausingExceptions
    val iter = nested.iterator

    while (iter.hasNext) {
      builder += fromEveritOne(iter.next)
    }

    val iterRecursive = nested.iterator

    while (iterRecursive.hasNext) {
      fromEveritRecursive(iterRecursive.next, builder)
    }
  }

  private[schema] def fromEverit(e: ValidationException): NonEmptyList[ValidationError] = {
    val builder = List.newBuilder[ValidationError]
    fromEveritRecursive(e, builder)
    NonEmptyList(fromEveritOne(e), builder.result)
  }
}
