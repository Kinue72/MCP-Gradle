package tech.mania.core.util.legit;

import java.util.concurrent.ThreadLocalRandom;

public class LegitCPSTimer {

    private int remainder = 0;
    private long time = System.currentTimeMillis();

    public int getClicks(double targetedCPS) {
        if (ThreadLocalRandom.current().nextDouble(1.0) > Math.sqrt(remainder + 4.0) / 3.0) {
            remainder++;
        }
        int ticks = Math.round((float) (System.currentTimeMillis() - time) / (int) (1000.0 / (targetedCPS + remainder)));
        time += (long) (ticks * (1000.0 / (targetedCPS + remainder)));
        return System.currentTimeMillis() > time ? 1 : 0;
    }

    public void reset(double targetedCPS) {
        time = System.currentTimeMillis() + (long) (1000.0 / targetedCPS);
        remainder = 0;
    }
}
