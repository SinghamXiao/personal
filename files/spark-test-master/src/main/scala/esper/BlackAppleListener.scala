package esper

import com.espertech.esper.client.{EventBean, UpdateListener}

class BlackAppleListener extends UpdateListener {
  def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit = {
    if (newEvents != null) {
      println("NewEvents Length: " + newEvents.length)
      val price: Double = newEvents(0).get("avg(price)").asInstanceOf[Double]
      println("Apple's average price is " + price)
    }
  }
}
