package datalog

case class Type(name: String) {
  override def toString: String = name
}
object Type {
  val addressType: Type = Type("address")
  val integerType: Type = Type("int")
  val uintType: Type = Type("uint")
  val any: Type = Type("any")
}

sealed abstract class Parameter {
  def _type: Type
  def name: String
  override def toString: String = name
}

case class Constant(_type: Type, name: String) extends Parameter
case class Variable(_type: Type, name: String) extends Parameter

sealed abstract class Relation {
  def name: String
  def sig: List[Type]
}
object Relation {
  val reservedRelations: Set[Relation] = Set(
    SingletonRelation("msgSender", List(Type.addressType))
  )
}

case class SimpleRelation(name: String, sig: List[Type]) extends Relation
case class SingletonRelation(name: String, sig: List[Type]) extends Relation

case class Literal(relation: Relation, fields: List[Parameter]) {
  override def toString: String = {
    val fieldStr = fields.mkString(",")
    s"${relation.name}($fieldStr)"
  }
}

case class Rule(head: Literal, body: Set[Literal], functors: Set[Functor], aggregators: Set[Aggregator]) {
  override def toString: String = {
    val litStr = body.map(_.toString)
    val functorStr = functors.map(_.toString)
    val aggStr = aggregators.map(_.toString)
    val bodyStr = (litStr++functorStr++aggStr).mkString(",")
    s"$head :- $bodyStr."
  }
}

case class Interface(relation: Relation, inputTypes: List[Type], returnType: Option[Type]) {
  override def toString: String = {
    val inputStr = inputTypes.mkString(",")
    val retStr = returnType match {
      case Some(rt) => s": $rt"
      case None => s""
    }
    s"${relation.name}($inputStr)" + retStr
  }
}
case class Program(rules: Set[Rule], interfaces: Set[Interface], relationIndices: Map[Relation, Int]) {
  override def toString: String = {
    var ret: String = s""
    ret += "Interfaces:\n" + interfaces.mkString("\n") + "\n"
    ret += "Indices:\n" + relationIndices.mkString("\n") + "\n"
    ret += "Rules:\n" + rules.mkString("\n")
    ret
  }
}
