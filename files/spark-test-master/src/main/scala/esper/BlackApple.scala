package esper

import scala.beans.BeanProperty

class BlackApple(@BeanProperty var id: Int = 0,
                 @BeanProperty var price: Int = 0)