package bgu.spl.mics.application.passiveObjects;
import java.sql.SQLOutput;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */

public class Ewoks {
    private Ewok[] ewoks;

    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks();
    }

    private Ewoks() {
    }

    public static Ewoks getInstance() {
        return Ewoks.SingletonHolder.instance;
    }

    public void initialize(int size) {
        ewoks = new Ewok[size + 1];
        for (int i = 1; i <= size; i = i + 1) {
            Ewok ewok_tmp = new Ewok(i);
            ewoks[i] = ewok_tmp;
        }
    }

    public void getEwoks(List<Integer> requiredEwoks) {
        for (int i : requiredEwoks) {
            Ewok e = ewoks[i];
            try {
                // sync specific ewok to allow other threads who needs different ewoks to run
                synchronized (e) {
                    while (!e.isAvailable())
                        e.wait();
                    e.acquire();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void releaseEwoks(List<Integer> requiredEwoks) {
        for (int i : requiredEwoks){
            synchronized (ewoks[i]) {
                ewoks[i].release();
                ewoks[i].notifyAll();
            }
        }
    }
}