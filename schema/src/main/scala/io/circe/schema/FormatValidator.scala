package io.circe.schema

import org.everit.json.schema.{ FormatValidator => EveritFormatValidator }

trait FormatValidator extends EveritFormatValidator {
  def name: String

  override def formatName(): String = name
}
