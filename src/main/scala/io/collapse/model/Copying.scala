package io.collapse.model

trait Copying[T] {
  def copyWith(changes: (String, AnyRef)*): T = {
    val clas = getClass
    val constructor = clas.getDeclaredConstructors.head
    val argumentCount = constructor.getParameterTypes.size
    val fields = clas.getDeclaredFields
    val arguments = (0 until argumentCount) map { i =>
      val fieldName = fields(i).getName
      changes.find(_._1 == fieldName) match {
        case Some(change) => change._2
        case None => {
          val getter = clas.getMethod(fieldName)
            getter.invoke(this)
        }
      }
    }
    constructor.newInstance(arguments: _*).asInstanceOf[T]
  }
}
