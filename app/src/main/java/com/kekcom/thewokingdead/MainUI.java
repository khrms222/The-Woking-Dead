package com.kekcom.thewokingdead;

import android.app.Activity;
import android.view.MenuItem;
import android.util.DisplayMetrics;

import android.view.Menu;
import android.content.Intent;
import android.view.MenuInflater;

import android.os.Bundle;
import android.content.Context;
import android.view.Window;

/**
 * Created by Marcus on 7/29/2017.
 */

public class MainUI extends GameObject{

    private Context context = null;
    private boolean isVisible = true;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_INACTIVE = 2;
    public static final int STATE_ACTIVE = 3;
    public static final int STATE_READY = 4;
    private int state = STATE_NORMAL;

    private int drawableStateNormal = 0;
    private int drawableStateReady = 0;
    private int drawableStateActive = 0;
    private int drawableStateInactive = 0;




    public MainUI( Context context, int draw){
        super(context, draw);

        this.context = context;

        this.drawableStateNormal = draw;
    }



    // Checking game states whether inactive or normal
    public boolean isStateInactive() {
        return (this.state == STATE_INACTIVE);
    }

    public boolean isStateNormal() {
        return (this.state == STATE_NORMAL);
    }



    // Checking visibilities
    public boolean isVisible() {
        return isVisible;
    }
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }



    // Setting the different states of the game: inactive, ready, normal, active
    public void setStateInactive() {
        this.state = STATE_INACTIVE;
        if (this.drawableStateInactive > 0) {
            this.setDrawable(this.context, this.drawableStateInactive);
        }
    }

    public void setStateReady() {
        this.state = STATE_READY;
        if (this.drawableStateReady > 0) {
            this.setDrawable(this.context, this.drawableStateReady);
        }
    }

    public void setStateNormal() {
        this.state = STATE_NORMAL;
        if (this.drawableStateNormal > 0) {
            this.setDrawable(this.context, this.drawableStateNormal);
        }
    }

    public void setStateActive() {
        this.state = STATE_ACTIVE;
        if (this.drawableStateActive > 0) {
            this.setDrawable(this.context, this.drawableStateActive);
        }
    }



    // Getters and Setters for drawable
    public int getDrawableStateNormal() {
        return this.drawableStateNormal;
    }

    public void setDrawableStateNormal(int mDrawableStateNormal) {
        this.drawableStateNormal = drawableStateNormal;
    }

    public int getDrawableStateInactive() {
        return this.drawableStateInactive;
    }

    public void setDrawableStateInactive(int mDrawableStateInactive) {
        this.drawableStateInactive = drawableStateInactive;
    }

    public int getDrawableStateActive() {
        return this.drawableStateActive;
    }

    public void setDrawableStateActive(int mDrawableStateActive) {
        this.drawableStateActive = drawableStateActive;
    }

    public int getDrawableStateReady() {
        return this.drawableStateReady;
    }

    public void setDrawableStateReady(int mDrawableStateReady) {
        this.drawableStateReady = drawableStateReady;
    }

}
