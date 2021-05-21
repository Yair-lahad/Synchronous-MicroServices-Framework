package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private Future[] attacksF;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        attacksF = new Future[attacks.length];
    }

    @Override
    protected void initialize() {
        // gets diary instance
        Diary diary = Diary.getInstance();
        try {
            Main.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int i = 0;
        for (Attack a : attacks) {
            AttackEvent ae = new AttackEvent(a.getSerials(), a.getDuration());
            attacksF[i] = this.sendEvent(ae);
            i = i + 1;
        }
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast tb) -> {
            terminate();
            // update diary
            diary.setLeiaTerminate(System.currentTimeMillis());
        });
        /*// checks all attack events resolved */
        for (Future fa : attacksF)
            fa.get();
        DeactivationEvent de = new DeactivationEvent();
        Future deactivateF = sendEvent(de);
        // checks deactivation completed
        deactivateF.get();
        BombDestroyerEvent be = new BombDestroyerEvent();
        Future bombF = sendEvent(be);
        //checks if bombed
        bombF.get();
        TerminateBroadcast tb = new TerminateBroadcast();
        sendBroadcast(tb);
    }
}