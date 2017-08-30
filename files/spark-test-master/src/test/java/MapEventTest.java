import java.util.HashMap;
import java.util.Map;

import com.espertech.esper.client.*;
import com.alibaba.fastjson.JSON;

public class MapEventTest {
    public static void main(String[] args) {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        Map<String, Object> children = new HashMap<String, Object>();
        children.put("name", String.class);
        children.put("age", int.class);
        admin.getConfiguration().addEventType("children_test", children);

        Map<String, Object> address = new HashMap<String, Object>();
        address.put("road", String.class);
        address.put("houseNo", int.class);
        address.put("street", String.class);
        admin.getConfiguration().addEventType("address_test", address);

        Map<String, Object> Person = new HashMap<String, Object>();
        Person.put("name", String.class);
        Person.put("age", int.class);
        Person.put("phones", String.class);
        Person.put("address", "address_test");
        Person.put("children", "children_test[]");
        admin.getConfiguration().addEventType("person_test", Person);

        // 新增一个gender属性
        Person.put("gender", String.class);
        admin.getConfiguration().updateMapEventType("person_test", Person);

        String epl = "select * from person_test where address.road=\"r1\"";
        EPStatement state = admin.createEPL(epl, "epl");
        System.out.println("state: " + state.getName());
        state.addListener(new UpdateListener() {
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                System.out.println("epl: " + newEvents[0].get("name"));
            }});

        String epl2 = "select * from person_test where children[1].name=\"ch2\"";
        EPStatement state2 = admin.createEPL(epl2, "epl2");
        System.out.println("state2: " + state2.getName());
        state2.addListener((newEvents, oldEvents) ->
                System.out.println("epl2: " + JSON.toJSONString(newEvents[0].get("children"))));

        String epl3 = "select * from person_test";
        EPStatement state3 = admin.createEPL(epl3, "epl3");
        System.out.println("state3: " + state3.getName());
        state3.addListener((newEvents, oldEvents) ->
                System.out.println("epl3: " + JSON.toJSONString(newEvents[0])));

        Map<String, Object> add = new HashMap<String, Object>();
        add.put("road", "r1");
        add.put("houseNo", 2);
        add.put("street", "Landon Avenue");

        Map<String, Object> ch1 = new HashMap<String, Object>();
        ch1.put("name", "ch1");
        ch1.put("age", 2);

        Map<String, Object> ch2 = new HashMap<String, Object>();
        ch2.put("name", "ch2");
        ch2.put("age", 2);

        Map[] child = new HashMap[2];
        child[0] = ch1;
        child[1] = ch2;

        Map<String, Object> per = new HashMap<String, Object>();
        per.put("name", "roger");
        per.put("gender", "male");
        per.put("age", 2);
        per.put("phones", "025-88888888");
        per.put("address", add);
        per.put("children", child);

        runtime.sendEvent(per, "person_test");
    }
}
