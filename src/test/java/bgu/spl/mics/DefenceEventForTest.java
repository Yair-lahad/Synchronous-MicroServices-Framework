package test.java.bgu.spl.mics;

import bgu.spl.mics.Event;

public class DefenceEventForTest implements Event<String>{
    String weapon;

    public DefenceEventForTest(String weapon) {
        this.weapon = weapon;
    }

    public String getWeapon() {
        return weapon;
    }
}