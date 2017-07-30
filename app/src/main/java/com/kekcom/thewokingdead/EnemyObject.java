package com.kekcom.thewokingdead;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class EnemyObject extends GameObject{

    public static final int SPEED = 1;

    Context mContext;

    long fps;

    private long timeThisFrame;

    float walkPerSecond = 250;

    private int frameWidth = 80;
    private int frameHeight = 110;

    private int frameCount = 3;

    private int currentFrame = 0;

    private long lastFrameChangeTime = 0;

    private int frameLengthInMilliseconds = 100;

    private Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);

    private RectF whereToDraw = new RectF(this.getX(), this.getY(), this.getX() + frameWidth, frameHeight + this.getY());

    private int direction;
    private boolean isMoving;

    private float mScreenDensity;

    private long moveTime = 5000;
    private long captureTime;

    private boolean timeToChangeDirection;

    private long lastTimeAttacked;
    private long attackDelay = 5000;


    public EnemyObject(Context context, int drawable, float mScreenDensity) {
        super(context, drawable);

        this.mContext = context;

        this.setBitmap(Bitmap.createScaledBitmap(this.getBitmap(), (frameWidth) * frameCount, (frameHeight*4), false));
        this.mWidth = frameWidth;
        this.mHeight = frameHeight;

        this.mScreenDensity = mScreenDensity;

        this.captureTime = System.currentTimeMillis();

        //this.direction = 1;
    }

    public void setFrameToDraw(Rect frameToDraw) {
        this.frameToDraw = frameToDraw;
    }

    public void setWhereToDraw() {
        whereToDraw.set(this.getX(), this.getY(), this.getX() + frameWidth, frameHeight + this.getY());
    }

    public Rect getFrameToDraw() {
        return frameToDraw;
    }

    public RectF getWhereToDraw() {
        return whereToDraw;
    }

    public void getCurrentFrame(boolean isMoving, int direction){
        long time = System.currentTimeMillis();
        if(isMoving){
            if(time > lastFrameChangeTime + frameLengthInMilliseconds){
                lastFrameChangeTime = time;
                currentFrame++;

                if(currentFrame >= frameCount){
                    currentFrame = 0;
                }
            }
        }

        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;

        if(direction == 1){
            frameToDraw.top = frameHeight * 2;
            frameToDraw.bottom = frameHeight * 3;
        }
        else if (direction == 2){
            frameToDraw.top = frameHeight * 3;
            frameToDraw.bottom = frameHeight * 4;
        }
        else if (direction == 3){
            frameToDraw.top = frameHeight;
            frameToDraw.bottom = frameHeight * 2;
        }
        else if (direction == 4){
            frameToDraw.top = 0;
            frameToDraw.bottom = frameHeight;
        }

    }

    public void setDirection(int direction){
        this.direction = direction;
    }

    public int getDirection(){
        return direction;
    }

    public void setIsMoving(boolean isMoving){
        this.isMoving = isMoving;
    }

    public boolean isMoving(){
        if(isMoving){
            return true;
        }
        else{
            return false;
        }
    }

    public void setCaptureTime(long captureTime){
        this.captureTime = captureTime;
    }


    public boolean timeToMove(){
        return isMoving;

    }

    public void setTimeToChangeDirection(boolean timeToChangeDirection){
        this.timeToChangeDirection = timeToChangeDirection;
    }

    public boolean timeToChangeDirection(){
        return timeToChangeDirection;
    }

    public void setLastTimeAttacked(long lastTimeAttacked){
        this.lastTimeAttacked = lastTimeAttacked;
    }

    public boolean timeToAttack(){
        if(System.currentTimeMillis() < lastTimeAttacked + attackDelay){
            return false;
        }
        else{
            return true;
        }
    }
}
