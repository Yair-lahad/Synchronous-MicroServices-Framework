package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent bde)->{
            // sleep for duration:
            try {
                Thread.sleep(duration);
            }catch (InterruptedException ignored){}
            //complete:
            complete(bde,"bombed the star destroyer");
        });
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->{
            // gets diary instance
            Diary diary = Diary.getInstance();
            terminate();
            // update diary
            diary.setLandoTerminate(System.currentTimeMillis());
        });
        Main.latch.countDown();
    }
}
