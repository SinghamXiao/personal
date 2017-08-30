import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.espertech.esper.client.*;

public class ArrayEventTest {
    public static void main (String[]args) {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        // Child定义
        String[] childPropNames = {"name", "age"};
        Object[] childPropTypes = {String.class, int.class};
        // 注册Child到Esper
        admin.getConfiguration().addEventType("Child", childPropNames, childPropTypes);

        // Address定义
        String[] addressPropNames = {"road", "street", "houseNo"};
        Object[] addressPropTypes = {String.class, String.class, int.class};
        // 注册Address到Esper
        admin.getConfiguration().addEventType("Address", addressPropNames, addressPropTypes);

        // Person定义
        String[] personPropNames = {"name", "age", "children", "phones", "address"};
        Object[] personPropTypes = {String.class, int.class, "Child[]", String.class, "Address"};
        // 注册Person到Esper
        admin.getConfiguration().addEventType("Person", personPropNames, personPropTypes);

        // 新增一个gender属性
        admin.getConfiguration().updateObjectArrayEventType("Person", new String[]{"gender"}, new Object[]{int.class});

        String epl = "select * from Person where address.road=\"r1\"";
        EPStatement state = admin.createEPL(epl, "epl");
        System.out.println("state: " + state.getName());
        state.addListener((newEvents, oldEvents) ->
                System.out.println("epl: " + newEvents[0].get("name")));

        String epl2 = "select * from Person where children[1].name=\"ch2\"";
        EPStatement state2 = admin.createEPL(epl2, "epl2");
        System.out.println("state2: " + state2.getName());
        state2.addListener((newEvents, oldEvents) ->
                System.out.println("epl2: " + JSON.toJSONString(newEvents[0].get("children"))));

        String epl3 = "select * from Person";
        EPStatement state3 = admin.createEPL(epl3, "epl3");
        System.out.println("state3: " + state3.getName());
        state3.addListener((newEvents, oldEvents) ->
                System.out.println("epl3: " + JSON.toJSONString(newEvents[0])));

        EventType event = admin.getConfiguration().getEventType("Person");
        System.out.println("Person props: " + Arrays.asList(event.getPropertyNames()));

        Object[] ch1 = {"ch1", 5};
        Object[] ch2 = {"ch2", 25};
        Object[] ch = {ch1, ch2};
        Object[] addr = {"r1", "s2", 100};
        Object[] person = {"roger", "male", ch, "025-66666666", addr, 3};

        runtime.sendEvent(person, "Person");
    }
}
