package com.kekcom.thewokingdead;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class WeaponObject extends GameObject{

    public static final int SPEED = 8;

    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;
    private static final int DIRECTION_LEFT = 3;
    private static final int DIRECTION_RIGHT = 4;

    private boolean isFiring;

    private int frameCount = 4;

    private int frameWidth = 100;
    private int frameHeight = 68;

    private Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);

    private RectF whereToDraw = new RectF(this.getX(), this.getY(), this.getX() + frameWidth, frameHeight + this.getY());

    private int direction;

    public WeaponObject(Context context, int drawable) {
        super(context, drawable);

        this.setBitmap(Bitmap.createScaledBitmap(this.getBitmap(), (frameWidth) * frameCount, (frameHeight), false));
        this.mWidth = frameWidth;
        this.mHeight = frameHeight;

        isFiring = false;
    }

    public boolean isFiring(){
        return isFiring;
    }

    public Bitmap getBitmap(int direction) {
        return super.getBitmap();
    }

    public void setWhereToDraw() {
        whereToDraw.set(this.getX(), this.getY(), this.getX() + frameWidth, frameHeight + this.getY());
    }

    public void getCurrentFrame(int direction){

        if(direction == DIRECTION_UP){
            frameToDraw.left = 2 * frameWidth;
            frameToDraw.right = frameToDraw.left + frameWidth;
        }
        else if (direction == DIRECTION_DOWN){
            frameToDraw.left = 3 * frameWidth;
            frameToDraw.right = frameToDraw.left + frameWidth;
        }
        else if (direction == DIRECTION_LEFT){
            frameToDraw.left = 0;
            frameToDraw.right = frameToDraw.left + frameWidth;
        }
        else if (direction == DIRECTION_RIGHT){
            frameToDraw.left = frameWidth;
            frameToDraw.right = frameToDraw.left + frameWidth;
        }

    }

    public Rect getFrameToDraw() {
        return frameToDraw;
    }

    public RectF getWhereToDraw() {
        return whereToDraw;
    }

    public void setFiring(boolean firing){
        this.isFiring = firing;
    }

    public void setDirection(int direction){
        this.direction = direction;
    }

    public int getDirection(){
        return direction;
    }
}
