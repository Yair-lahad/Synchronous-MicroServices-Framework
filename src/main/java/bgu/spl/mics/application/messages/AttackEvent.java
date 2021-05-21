package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import java.util.List;


public class AttackEvent implements Event<Boolean> {
    private List<Integer> serials;
    private int duration;

    public AttackEvent(List<Integer> serials,int duration){
        this.serials = serials;
        this.duration = duration;
    }

    public List<Integer> getSerials() {
        return serials;
    }

    public int getDuration() {
        return duration;
    }
}
