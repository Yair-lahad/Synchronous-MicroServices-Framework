package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Comparator;
import java.util.List;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }

    @Override
    protected void initialize() {
        // gets diary instance
        Diary diary = Diary.getInstance();
        subscribeEvent(AttackEvent.class, (AttackEvent ae)->{
            // gets all the Ewoks required for this attack
            List<Integer> requiredEwoks = ae.getSerials();
            requiredEwoks.sort(Comparator.comparingInt((Integer o) -> o));
            Ewoks ewoks = Ewoks.getInstance();
            ewoks.getEwoks(requiredEwoks);
            // sleep for duration:
            int duration = ae.getDuration();
            try {
                Thread.sleep(duration);
            }catch (InterruptedException ignored){}
            // release the resources
            ewoks.releaseEwoks(requiredEwoks);
            //complete:
            complete(ae,true);
            // update diary
            diary.setHanSoloFinish(System.currentTimeMillis());
            diary.updateTotalAttacks();
        });
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->{
            terminate();
            // update diary
            diary.setHanSoloTerminate(System.currentTimeMillis());
        });
        Main.latch.countDown();

    }
}
