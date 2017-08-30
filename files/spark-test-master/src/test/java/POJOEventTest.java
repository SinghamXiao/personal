import esper.Apple;
import esper.AppleListener;

import com.espertech.esper.client.*;

public class POJOEventTest {
    public static void main(String[] args) throws InterruptedException {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        String event = Apple.class.getName();
        String epl = "select avg(price) from " + event + ".win:length_batch(3)";
        EPStatement state = admin.createEPL(epl, "epl");
        state.addListener(new AppleListener());

        Apple apple1 = new Apple();
        apple1.setID(1);
        apple1.setPrice(5);
        runtime.sendEvent(apple1);

        Apple apple2 = new Apple();
        apple2.setID(2);
        apple2.setPrice(2);
        runtime.sendEvent(apple2);

        Apple apple3 = new Apple();
        apple3.setID(3);
        apple3.setPrice(5);
        runtime.sendEvent(apple3);
    }
}
