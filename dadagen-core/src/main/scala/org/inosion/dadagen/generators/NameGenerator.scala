package org.inosion.dadagen.generators

import java.util.Random
import org.inosion.dadagen.Context
import org.inosion.dadagen.lists.{ListManager, ListConfigSupport}


/**
 * First names
 * @author rbuckland
 */
case class FirstNameGenerator(name:String,
                              genderFieldName:Option[String] = None,
                              listName:String = FirstNameGenerator.keyNameMedium
                               )(implicit rand: Random) extends Generator[String] {

  ListConfigSupport.importConfigListData(listName)

  /*
   * Pre execution derived list of dependencies that this field is waiting on
   */
  override def dependencies: List[String] = genderFieldName.iterator.toList

  override def internalGenerate(context: Context)(implicit rand: Random): String = {
    genderFieldName match {
      case None => ListManager.getRandomValue(listName)
      case Some(field) => context.dataFieldState(field).toString.charAt(0) match {
        case 'F' => ListManager.getRandomValue(listName, Some("F"))
        case 'M' => ListManager.getRandomValue(listName, Some("M"))
        case  _  => ListManager.getRandomValue(listName)
      }
    }
  }
}

object FirstNameGenerator extends Described {
  val keyNameMassive = "person.firstname"   // takes a VERY long time to load
  val keyNameSmall = "person.firstname2000"  // 1 sec
  val keyNameMedium = "person.firstname4000" // 2 seconds
  val description = "From an internal list of supplied names, the first name could be double barrelled." +
      " It is optionally bound to gender where you must specify the gender field name (i.e. sex or gender) - This will then" +
      " find names that are Male or Female matched to the current row's gender"


}

/**
 * Surnames
 */
case class SurnameGenerator(name:String,
                            listName:String=SurnameGenerator.keyNameMedium)
                           (implicit rand: Random) extends Generator[String] {

  ListConfigSupport.importConfigListData(listName)

  override def internalGenerate(context: Context)(implicit rand: Random): String = ListManager.getRandomValue(listName)

}

object SurnameGenerator extends Described {
  val keyNameMassive = "person.surname"   // takes a VERY long time to load
  val keyNameSmall = "person.surname2000"  // 1 sec
  val keyNameMedium = "person.surname4000" // 2 seconds
  val description = "From an internal list of supplied surnames, the surname could be double barrelled," +
      " hyphenated or normal"

}

/**
 * Full names
 * @param genderFieldName
 */
case class FullNameGenerator(name:String, genderFieldName:Option[String] = None) extends Generator[String] {

  override def internalGenerate(context: Context)(implicit rand: Random): String = {

    val gender = genderFieldName match {
      // if they didn't supply a gender, we will use one so title and firstname match
      case None => RandomUtil.randomFromList(Seq("M","F"))
      case Some(field) => context.dataFieldState(field).toString.charAt(0).toString
    }

    ListManager.getRandomValue(TitleGenerator.keyName, Some(gender)) +
    " " + ListManager.getRandomValue(FirstNameGenerator.keyNameMedium, Some(gender)) +
    " " + ListManager.getRandomValue(SurnameGenerator.keyNameMedium)
  }

}

object FullNameGenerator extends Described {
  val keyNames = List(SurnameGenerator.keyNameMedium,FirstNameGenerator.keyNameMedium,TitleGenerator.keyName)
  for (keyname <- keyNames) ListConfigSupport.importConfigListData(keyname)
  val description: String = "A full name like 'Michael Brown' or 'Sara MacDouglas'." +
    " It is optionally bound be gender where you must specify the gender field name (i.e. sex or gender) - This will then" +
    " find names that are Male or Female matched to the current row's gender"


}

case class TitleGenerator(name:String,genderFieldName:Option[String] = None) extends Generator[String] {

  override def internalGenerate(context: Context)(implicit rand: Random): String = {
    import TitleGenerator._
    genderFieldName match {
      case None => ListManager.getRandomValue(TitleGenerator.keyName)
      case Some(field) => context.dataFieldState(field).toString.charAt(0) match {
        case 'F' => ListManager.getRandomValue(TitleGenerator.keyName, Some("F"))
        case 'M' => ListManager.getRandomValue(TitleGenerator.keyName, Some("M"))
        case  _  => ListManager.getRandomValue(TitleGenerator.keyName)
      }
    }
  }

  override def dependencies: List[String] = genderFieldName.toList
}


object TitleGenerator extends Described {
  val keyName = "person.title"
  ListConfigSupport.importConfigListData(keyName)
  val description = "A Title like (Mrs, Mr, Miss, Ms) and, optionally gender specific if you tag it to" +
    " the gender field"


}
