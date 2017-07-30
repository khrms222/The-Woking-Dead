package com.kekcom.thewokingdead;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

public class FloorBase extends GameBaseObject {
    public static final int TYPE_EMPTY = 0;
    public static final int TYPE_OBSTACLE = 1;
    public static final int TYPE_DANGEROUS = 2;
    public static final int TYPE_EXIT = 3;

    private int mKey = 0;
    private int mType = TYPE_EMPTY;

    private boolean mVisible = true;

    private Rect mCollisionRect = null;

    public FloorBase(Context context, Point point) {
        super(context);

        this.mX = point.x;
        this.mY = point.y;
    }

    public FloorBase(Context context, int drawable, Point point) {
        super(context, drawable);

        this.mX = point.x;
        this.mY = point.y;
    }

    public boolean isDangerous() {
        return (this.mType == TYPE_DANGEROUS);
    }

    public boolean getCollision(float x, float y, int width, int height) {
        if (this.mCollisionRect == null) {
            this.mCollisionRect = new Rect((int) x + 10, (int) y, ((int) x + width - 10), ((int) y + height - 30));
        } else {
            this.mCollisionRect.set((int) x + 10, (int) y, ((int) x + width - 10), ((int) y + height - 30));
        }

        return (this.mCollisionRect.intersects(this.mX, this.mY, (this.mX + getWidth()), (this.mY + getHeight())));
    }

    public boolean getCollision(GameObject gameObject) {
        return (gameObject.getRect().intersects(this.mX, this.mY, (this.mX + mWidth), (this.mY + mHeight)));
    }

    public int getKey() {
        return this.mKey;
    }

    public void setKey(int key) {
        this.mKey = key;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getX() {
        return this.mX;
    }

    public void setX(int x) {
        this.mX = x;
    }

    public int getY() {
        return this.mY;
    }

    public void setY(int y) {
        this.mY = y;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public void setVisible(boolean visible) {
        this.mVisible = visible;
    }

    public boolean isCollisionTile() {
        return ((this.mType != FloorBase.TYPE_EMPTY) && this.mVisible);
    }

    public boolean isBlockerTile() {
        return this.mType != FloorBase.TYPE_EMPTY;

    }
}
