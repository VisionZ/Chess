package Engine;

import java.util.concurrent.TimeUnit;

/**
 * Timer class used to time how long an {@link AI} can
 * spend searching. 
 * @author Will
 */
public final class SearchTimer {
    
    private final Countdown countdown;
    
    private volatile boolean terminated = false;
    private volatile boolean timing = false;
    private volatile boolean override = false;
    
    private volatile long nanosecondsCountdown;
    private volatile long startTime;
    private volatile long nanosecondsElapsed;

    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    SearchTimer(int seconds, String timerName) {
        if (timerName == null) {
            throw new NullPointerException("SearchTimer name cannot be null.");
        }
        nanosecondsCountdown = TimeUnit.NANOSECONDS.convert(seconds, TimeUnit.SECONDS);
        (countdown = new Countdown(timerName)).start();
    }
        
    /**
     * Sets the name of this SearchTimer. If the given name is null, this method
     * returns immediately.
     * @param timerName The new name of this SearchTimer.
     * @throws SecurityException If the given name cannot replace
     * the current SearchTimer name.
     */
    public void setName(String timerName) throws SecurityException {
        if (timerName != null) {
            countdown.setName(timerName);
        }
    }
    
    /**
     * Gets the current name of this SearchTimer.
     * @return The current name of this SearchTimer.
     */
    public String getName() {
        return countdown.getName();
    }
    
    /**
     * Sets the countdown time (in seconds) of this SearchTimer.
     * @param seconds The new countdown time (in seconds).
     */
    public void setCountdownTime(int seconds) { //dont reinvent the wheel
        nanosecondsCountdown = TimeUnit.NANOSECONDS.convert(seconds, TimeUnit.SECONDS);
    }
    
    /**
     * Gets the current countdown time (in seconds) of this SearchTimer.
     * @return The current countdown time (in seconds) of this SearchTimer.
     */
    public int getCountdownTime() {
        return (int) TimeUnit.SECONDS.convert(nanosecondsCountdown, TimeUnit.NANOSECONDS);
    }

    /**
     * Starts the countdown of this SearchTimer from the beginning.
     */
    public void startTiming() {
        nanosecondsElapsed = 0;
        startTime = System.nanoTime();
        timing = true;
        override = false;
    }

    /**
     * Forcibly stops the countdown of this SearchTimer.
     */
    public void stopTiming() {
        timing = false;
    }
    
    /**
     * Gets the number of seconds that has elapsed since this SearchTimer
     * has started its countdown.
     * @return The number of seconds that has elapsed since this SearchTimer
     * has started its countdown.
     */
    public int timeElapsed() {
        return (int) TimeUnit.SECONDS.convert(nanosecondsElapsed, TimeUnit.NANOSECONDS);
    }

    /**
     * Determines whether or not this SearchTimer is active.
     * @return {@code true} if this SearchTimer is not counting down, {@code false}
     * otherwise.
     */
    public boolean timeOver() {
        return override ? true : !timing;
    }
    
    public void setOverride(boolean over) {
        override = over;
    }
    
    /**
     * Permanently disables this SearchTimer and terminates its 
     * internal thread.
     */
    public void disable() {
        terminated = true;
    }
    
    /**
     * Countdown helper thread class to hide the {@link run()} method
     * from outside callers.
     */
    private final class Countdown extends Thread {
        
        private Countdown(String threadName) {
            super(threadName);
        }

        @Override
        public final void run() {
            try {
                while (!terminated) { //loop forever
                    if (timing) { //if timer is running
                        //update time
                        if ((nanosecondsElapsed = (System.nanoTime() - startTime)) >= nanosecondsCountdown) {
                            timing = false; //if time is reached, then stop running.
                            //System.out.println(secondsElapsed);
                        }
                    }
                    else {
                        TimeUnit.MILLISECONDS.sleep(1000); //was 100
                    }
                }
            }
            catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    /**
     * This method always throws a {@link CloneNotSupportedException} because a
     * SearchTimer cannot be properly cloned as it uses an internal {@link Thread} for
     * concurrent timing.
     *
     * @return Never
     * @throws CloneNotSupportedException
     */
    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /*
    public static void main(String[] args) {
        SearchTimer timer = new SearchTimer(3, "Test Timer");
        timer.startTiming();
        System.out.println(timer.getTime());
        while (!timer.timeOver()) {
            System.out.println(timer.secondsElapsed());
        }
        System.out.println(timer.secondsElapsed());
        timer.disable();
    }
     */
}