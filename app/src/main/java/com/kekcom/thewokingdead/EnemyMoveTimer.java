package com.kekcom.thewokingdead;

public class EnemyMoveTimer extends Thread implements Runnable{

    public boolean timeToMove;
    private long timeFrame;
    private long stopFrame;

    public EnemyMoveTimer(long timeFrame, long stopFrame) {
        super();
        this.timeFrame = timeFrame;
        this.stopFrame = stopFrame;
    }

    public void setTimeFrame(long timeFrame){
        this.timeFrame = timeFrame;
    }

    public void setStopFrame(long stopFrame){
        this.stopFrame = stopFrame;
    }

    public boolean getTimeToMove() {
        return timeToMove;
    }

    @Override
    public void run() {
        while(true) {
            long currentTime = System.currentTimeMillis();
            long timeFrame = this.timeFrame;

            long stopTime = System.currentTimeMillis();
            long stopFrame = this.stopFrame;

            while (System.currentTimeMillis() < stopTime + stopFrame) {
                while (System.currentTimeMillis() < currentTime + timeFrame) {
                    timeToMove = true;

                    try {
                        this.sleep(timeFrame);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                timeToMove = false;

                try {
                    this.sleep(stopFrame);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
