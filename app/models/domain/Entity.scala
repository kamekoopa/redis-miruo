package models.domain

/**
 * @author kamekoopa
 */
trait Entity[E <: Entity[E]] extends Cloneable {

  val identifier: Id

  override def hashCode = identifier.hashCode()

  override def equals(that: Any): Boolean = {
    that match {
      case e: Entity[E] => identifier == e.identifier
      case _ => false
    }
  }

  override final def clone: E = {
    super.clone.asInstanceOf[E]
  }
}
