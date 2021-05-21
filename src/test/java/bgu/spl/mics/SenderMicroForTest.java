package bgu.spl.mics;
import java.util.concurrent.TimeUnit;
import test.java.bgu.spl.mics.DefenceEventForTest;

public class SenderMicroForTest extends MicroService {

    private String type;
    private boolean terminateAllowed;

    public SenderMicroForTest(String name, String[] args) {
        super(name);
        terminateAllowed = false;
        if ( args.length != 1 || !(args[0].equals("broadcast")|args[0].equals("event")) ) {
            throw new IllegalArgumentException("I'm sending only Event or Broadcast");
        }
        type = args[0];
    }

    @Override
    protected void initialize() {
        System.out.println("The MicroService named:"+getName() + ",who sends Events, is starting");
        if (type.equals("event")) {
            Future<String> eventF = (Future<String>)sendEvent(new DefenceEventForTest("ice"));
            if (eventF != null) {
                String result = eventF.get(100, TimeUnit.MILLISECONDS);
                if (result != null) {
                    System.out.println("The Event was handled successfully with result: " + result);
                } else {
                    System.out.println("timeout");
                }
            } else{
                    System.out.println("send Event failed. no MicroService has registered to handle this event");
                }
        } else {
            sendBroadcast(new BroadcastForTest("battle continues"));
            System.out.println("The Broadcast was sent by " + getName());
        }
        // in the actual project will be changed only when all micros are ready to terminate;
        terminateAllowed = true;
        if (terminateAllowed)
            terminate();
    }

}
