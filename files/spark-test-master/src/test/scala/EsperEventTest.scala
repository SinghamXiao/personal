import esper.{BlackApple, BlackAppleListener}
import com.espertech.esper.client._

import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EsperEventTest extends FunSpec with Matchers {
  describe("Esper Event Test") {
    it("POJO Event Test") {
      val epService: EPServiceProvider = EPServiceProviderManager.getDefaultProvider
      val admin: EPAdministrator = epService.getEPAdministrator
      val runtime: EPRuntime = epService.getEPRuntime

      val event: String = classOf[BlackApple].getName
      val epl: String = "select avg(price) from " + event + ".win:length_batch(3)"
      val state: EPStatement = admin.createEPL(epl)
      state.addListener(new BlackAppleListener())

      val apple1 = new BlackApple(id = 1, price = 5)
      runtime.sendEvent(apple1)

      val apple2 = new BlackApple(2, 2)
      runtime.sendEvent(apple2)

      val apple3 = new BlackApple(3, 5)
      runtime.sendEvent(apple3)
    }

    it("Map Event Test") {
      val epService: EPServiceProvider = EPServiceProviderManager.getDefaultProvider
      val admin: EPAdministrator = epService.getEPAdministrator
      val runtime: EPRuntime = epService.getEPRuntime

      // Children定义
      val children = Map("name" -> classOf[String], "age" -> classOf[Int])
      import collection.JavaConversions.mapAsJavaMap
      val chs = mapAsJavaMap(children).asInstanceOf[java.util.Map[java.lang.String, java.lang.Object]]
      // 注册Children到Esper
      admin.getConfiguration.addEventType("Children", chs)

      // Address定义
      val address = new java.util.HashMap[String, AnyRef]
      address.put("road", classOf[String])
      address.put("street", classOf[String])
      address.put("houseNo", classOf[Int])
      // 注册Address到Esper
      admin.getConfiguration.addEventType("Address", address)

      // Person定义
      val person = new java.util.HashMap[String, AnyRef]
      person.put("name", classOf[String])
      person.put("age", classOf[Int])
      person.put("phones", classOf[String])
      person.put("children", "Children[]")
      person.put("address", "Address")
      // 注册Person到Esper
      admin.getConfiguration.addEventType("Person", person)

      // 新增一个gender属性
      person.put("gender", classOf[String])
      admin.getConfiguration.updateMapEventType("Person", person)

      val epl: String = "select * from Person where address.road=\"r1\""
      val state: EPStatement = admin.createEPL(epl, "epl")
      println("state: " + state.getName)
      state.addListener(new UpdateListener() {
        override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit =
          println("epl: " + newEvents(0).get("name"))
      })

      val epl2: String = "select * from Person where children[1].name=\"ch2\""
      val state2: EPStatement = admin.createEPL(epl2, "epl2")
      println("state2: " + state2.getName)
      state2.addListener(new UpdateListener() {
        override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit =
          println("epl2: " + newEvents(0).get("children"))
      })

      val epl3: String = "select * from Person"
      val state3: EPStatement = admin.createEPL(epl3, "epl3")
      println("state3: " + state3.getName)
      state3.addListener(new UpdateListener() {
        override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit =
          println("epl3: " + newEvents(0).getUnderlying)
      })

      val event: EventType = admin.getConfiguration.getEventType("Person")
      print("Person: ")
      event.getPropertyNames.foreach(elem => print(elem + " "))
      println("")

      val add = new java.util.HashMap[String, Any]
      add.put("road", "r1")
      add.put("houseNo", 2)
      add.put("street", "Landon Avenue")

      val ch1 = new java.util.HashMap[String, Any]
      ch1.put("name", "ch1")
      ch1.put("age", 2)

      val ch2 = new java.util.HashMap[String, Any]
      ch2.put("name", "ch2")
      ch2.put("age", 2)

      val child = new Array[java.util.HashMap[_, _]](2)
      child(0) = ch1
      child(1) = ch2

      val per = new java.util.HashMap[String, Any]
      per.put("name", "roger")
      per.put("gender", "male")
      per.put("age", 2)
      per.put("phones", "025-88888888")
      per.put("address", add)
      per.put("children", child)

      runtime.sendEvent(per, "Person")
    }

    it("Array Event Test") {
      val epService: EPServiceProvider = EPServiceProviderManager.getDefaultProvider
      val admin: EPAdministrator = epService.getEPAdministrator
      val runtime: EPRuntime = epService.getEPRuntime

      // Child定义
      val childPropNames: Array[String] = Array("name", "age")
      val childPropTypes: Array[AnyRef] = Array(classOf[String], classOf[Int])
      // 注册Child到Esper
      admin.getConfiguration.addEventType("Child_test", childPropNames, childPropTypes)

      // Address定义
      val addressPropNames: Array[String] = Array("road", "street", "houseNo")
      val addressPropTypes: Array[AnyRef] = Array(classOf[String], classOf[String], classOf[Int])
      // 注册Address到Esper
      admin.getConfiguration.addEventType("Address_test", addressPropNames, addressPropTypes)

      // Person定义
      val personPropNames: Array[String] = Array("name", "age", "children", "phones", "address")
      val personPropTypes: Array[AnyRef] = Array(classOf[String], classOf[Int], "Child_test[]", classOf[String], "Address_test")
      // 注册Person到Esper
      admin.getConfiguration.addEventType("Person_test", personPropNames, personPropTypes)

      // 新增一个gender属性
      admin.getConfiguration.updateObjectArrayEventType("Person_test", Array[String]("gender"), Array[AnyRef](classOf[Int]))

      val epl: String = "select * from Person_test where address.road=\"r1\""
      val state: EPStatement = admin.createEPL(epl, "epl11")
      println("state: " + state.getName)
      state.addListener(new UpdateListener() {
        override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit =
          println("epl: " + newEvents(0).get("name"))
      })

      val epl2: String = "select * from Person_test where children[1].name=\"ch2\""
      val state2: EPStatement = admin.createEPL(epl2, "epl12")
      println("state2: " + state2.getName)
      state2.addListener(new UpdateListener() {
        override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit =
          println("epl2: " + newEvents(0).getFragment("children"))
      })

      val epl3: String = "select * from Person_test"
      val state3: EPStatement = admin.createEPL(epl3, "epl13")
      println("state3: " + state3.getName)
      state3.addListener(new UpdateListener() {
        override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit =
          println("epl3: " + newEvents(0).get("address"))
      })

      val ch1: Array[Any] = Array("ch1", 5)
      val ch2: Array[Any] = Array("ch2", 25)
      val ch: Array[Any] = Array(ch1, ch2)
      val addr: Array[Any] = Array("r1", "s2", 100)
      val person: Array[AnyRef] = Array("roger", "male", ch, "025-66666666", addr, 3.asInstanceOf[AnyRef])

      runtime.sendEvent(person, "Person_test")
    }
  }
}