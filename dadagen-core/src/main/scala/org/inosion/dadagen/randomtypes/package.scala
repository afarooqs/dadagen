package org.inosion.dadagen

import java.util.Random

/**
 * Created by rbuckland on 08/05/2015.
 */
package object randomtypes {

  // The generators in this package need a random to load up lists as their defaults.
  // the higher level rand is used for the main generators
  implicit val rand: Random = org.inosion.dadagen.rand
}
