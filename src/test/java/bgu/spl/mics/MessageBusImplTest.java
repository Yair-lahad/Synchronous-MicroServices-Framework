package bgu.spl.mics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.java.bgu.spl.mics.DefenceEventForTest;
import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    // Micros:
    static EventMicroForTest droidEvent;
    static BroadcastMicroForTest droidBroadcast;
    static SenderMicroForTest droidSenderB;
    static SenderMicroForTest droidSenderE;
    // Events:
    static DefenceEventForTest event1;
    static BroadcastForTest broadcast1;
    // Bus:
    static MessageBusImpl MB;

    @BeforeAll
    static void setUp() {
        droidEvent = new EventMicroForTest("Obi1");
        droidBroadcast = new BroadcastMicroForTest("yoda");
        // Sender Droids:
        String[] args = new String[1];
        args[0]="broadcast";
        droidSenderB = new SenderMicroForTest("finn1",args);
        args[0]="event";
        droidSenderE = new SenderMicroForTest("finn2",args);

        // testDefenceEvent is a simple event to check
        event1 = new DefenceEventForTest("fire");
        broadcast1 = new BroadcastForTest("we killed 50% of the storm troopers");

        // create message bus
        MB = MessageBusImpl.getInstance();
        // register, initialize and subscribe all micros:
        // sender Micro:
        MB.register(droidSenderB);
        //droidSenderB.initialize();
        MB.register(droidSenderE);
        //droidSenderE.initialize();
        // Event Micro:
        MB.register(droidEvent);
        //droidEvent.initialize();
        MB.subscribeEvent(DefenceEventForTest.class,droidEvent);
        //
        MB.register(droidBroadcast);
        //droidBroadcast.initialize();
        MB.subscribeBroadcast(BroadcastForTest.class,droidBroadcast);
    }

    @Test
    public void testComplete() {
        Future<String> event1F = MB.sendEvent(event1);
        String result = "event is handled successfully";
        try{
            MB.complete(event1,result);
        }
        catch (Exception e){
            fail("executing complete method failed");
        }
        assertEquals(result,event1F.get());
        assertTrue(event1F.isDone());
    }

    @Test
    public void testSendBroadcast() {
        try {
            MB.sendBroadcast(broadcast1);
        }
        catch (Exception e){
            fail("executing sendBroadcast method failed");
        }
        try {
            assertEquals(broadcast1, MB.awaitMessage(droidBroadcast));
        }
        catch (Exception e){
            fail("sendBroadcast failed");
        }
    }

    @Test
    public void testSendEvent() {
        try {
            MB.sendEvent(event1);
        }
        catch (Exception e){
            fail("executing sendEvent method failed");
        }
        try {
            assertEquals(event1, MB.awaitMessage(droidEvent));
        }
        catch (Exception e){
            fail("sendEvent failed");
        }
    }

    @Test
    void testAwaitMessage() {
        try {
            Message tmp = MB.awaitMessage(droidEvent);
            assertTrue(tmp instanceof Event);
        }
        catch (Exception E){
            fail("executing awaitMessage method failed");
        }
    }
}