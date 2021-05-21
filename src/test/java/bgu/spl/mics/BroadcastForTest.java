package bgu.spl.mics;

public class BroadcastForTest implements Broadcast {

    private String battleAnnouncement;

    public BroadcastForTest(String BA) {
        this.battleAnnouncement = BA;
    }

    public String getBattleAnnouncement() {
        return battleAnnouncement;
    }

}
