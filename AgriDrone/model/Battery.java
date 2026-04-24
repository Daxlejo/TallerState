package AgriDrone.model;

import java.util.Timer;
import java.util.TimerTask;

public class Battery {

    private static final int MAX_LEVEL = 100;
    private static final int LOW_THRESHOLD = 15;
    private static final int DRAIN_PER_PHOTO = 16;
    private static final int DISCHARGE_INTERVAL_MS = 5000;
    private static final int DISCHARGE_AMOUNT = 1;

    private int level;
    private Timer dischargeTimer;

    public Battery() {
        this.level = MAX_LEVEL;
    }

    public void startDischarge() {
        stopDischarge();
        dischargeTimer = new Timer(true);
        dischargeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (level > 0) {
                    level = Math.max(0, level - DISCHARGE_AMOUNT);
                }
            }
        }, DISCHARGE_INTERVAL_MS, DISCHARGE_INTERVAL_MS);
    }

    public void stopDischarge() {
        if (dischargeTimer != null) {
            dischargeTimer.cancel();
            dischargeTimer = null;
        }
    }

    public void drain() {
        this.level = Math.max(0, this.level - DRAIN_PER_PHOTO);
    }

    public boolean isLow() {
        return this.level <= LOW_THRESHOLD;
    }

    public void recharge() {
        this.level = MAX_LEVEL;
    }

    public int getLevel() {
        return level;
    }
}
