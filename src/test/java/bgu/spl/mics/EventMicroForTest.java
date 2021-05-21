package bgu.spl.mics;
import bgu.spl.mics.MicroService;
import test.java.bgu.spl.mics.DefenceEventForTest;

public class EventMicroForTest extends MicroService {

    // indicates all microService allowed to be terminated;
    private boolean terminateAllowed;

    public EventMicroForTest(String name) {
        super(name);
        terminateAllowed = false;
    }

    @Override
    protected void initialize() {
        System.out.println("The MicroService named:"+getName() + ",who gets Events, is starting");
        subscribeEvent(DefenceEventForTest.class, (DefenceEventForTest c)->{
            System.out.println("I'm " + getName() + "defending with" + c.getWeapon());
            complete(c, "Event was handled by " + getName());
            // in the actual project will be changed only when all micros are ready to terminate;
            terminateAllowed = true;
            if (terminateAllowed)
                terminate();
        });
    }

}