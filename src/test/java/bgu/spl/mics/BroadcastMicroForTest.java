package bgu.spl.mics;

public class BroadcastMicroForTest extends MicroService {

    private boolean terminateAllowed;

    public BroadcastMicroForTest(String name) {
        super(name);
        terminateAllowed = false;
    }

    @Override
    protected void initialize() {
        System.out.println("The MicroService named:"+getName() + ",who gets Broadcast, is starting");
        subscribeBroadcast(BroadcastForTest.class, (BroadcastForTest c) -> {
            System.out.println("I'm " + getName() + "got the message: " + c.getBattleAnnouncement());
            // in the actual project will be changed only when all micros are ready to terminate;
            terminateAllowed = true;
            if (terminateAllowed)
                terminate();
        });
    }
}