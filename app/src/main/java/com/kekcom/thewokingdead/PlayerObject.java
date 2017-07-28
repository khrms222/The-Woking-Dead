package com.kekcom.thewokingdead;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class PlayerObject extends GameObject{
    public static final int SPEED = 6;

    Context mContext;

    private int mUnmodifiedX = 0;
    private int mUnmodifiedY = 0;



    long fps;

    private long timeThisFrame;

    //boolean isMoving = false;
    float walkPerSecond = 250;

    private int frameWidth = 80;
    private int frameHeight = 110;

    private int frameCount = 3;

    private int currentFrame = 0;

    private long lastFrameChangeTime = 0;

    private int frameLengthInMilliseconds = 100;

    private Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);

    private RectF whereToDraw = new RectF(this.getX(), this.getY(), this.getX() + frameWidth, frameHeight + this.getY());

    public PlayerObject(Context context, int drawable)
    {
        super(context, drawable);
        this.mContext = context;

        this.setBitmap(Bitmap.createScaledBitmap(this.getBitmap(), (frameWidth) * frameCount, (frameHeight*4), false));
        this.mWidth = frameWidth;
        this.mHeight = frameHeight;

        Log.d("testlog", "" + Bitmap.createScaledBitmap(this.getBitmap(), frameWidth * frameCount, frameHeight, false).getWidth());
    }

    public void setUnmodifiedX(int unmodifiedX)
    {
        this.mUnmodifiedX = unmodifiedX;
    }

    public void setUnmodifiedY(int unmodifiedY)
    {
        this.mUnmodifiedY = unmodifiedY;
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

    public void setTimeThisFrame(long timeThisFrame) {
        this.timeThisFrame = timeThisFrame;
    }

    public long getTimeThisFrame() {
        return timeThisFrame;
    }

    public void setFps(long fps) {
        this.fps = fps;
    }
}
