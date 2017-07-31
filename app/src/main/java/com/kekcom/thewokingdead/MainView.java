package com.kekcom.thewokingdead;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kekcom.thewokingdead.data.GameLevelData;
import com.kekcom.thewokingdead.data.GameStageData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Marcus on 7/29/2017.
 */

public class MainView extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int STATE_RUNNING = 1;
    public static final int STATE_PAUSED = 2;
    private static final int UI_PADDING = 50;
    private static final int START_STAGE = 1;
    private static final int START_LEVEL = 1;
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;
    private static final int DIRECTION_LEFT = 3;
    private static final int DIRECTION_RIGHT = 4;
    private int mScreenXMax = 0;
    private int mScreenYMax = 0;
    private int mScreenXCenter = 0;
    private int mScreenYCenter = 0;
    private int mScreenXOffset = 0;
    private int mScreenYOffset = 0;

    private float mScreenDensity;

    private Context mGameContext;
    private GameActivity mGameActivity;
    private SurfaceHolder mGameSurfaceHolder = null;

    private boolean updatingGameTiles = false;

    private GameLevelData mGameTileData = null;
    private GameStageData mGameLevelTileData = null;

    private PlayerObject mPlayerUnit = null;

    private int mPlayerStage = START_STAGE;
    private int mPlayerLevel = START_LEVEL;

    private Bitmap mBackgroundImage = null;

    private int mGameState;

    private boolean mGameRun = true;

    private boolean mPlayerMoving = false;
    private int mPlayerVerticalDirection = 0;
    private int mPlayerHorizontalDirection = 0;

    private int mPlayerDirection = 0;

    private MainUi mCtrlUpArrow = null;
    private MainUi mCtrlDownArrow = null;
    private MainUi mCtrlLeftArrow = null;
    private MainUi mCtrlRightArrow = null;

    private Paint mUiTextPaint = null;
    private String mLastStatusMessage = "";

    private int mNumOfEnemies;
    private List<EnemyObject> mEnemyList = new ArrayList<EnemyObject>();
    private EnemyMoveTimer mEnemyMoveTimer;
    private HashMap<Integer, ArrayList<Integer>> mGameTileTemplates = null;

    private HashMap<Integer, Bitmap> mGameTileBitmaps = new HashMap<Integer, Bitmap>();
    private List<FloorBase> mGameTiles = new ArrayList<FloorBase>();

    private int mPlayerStartTileX = 0;
    private int mPlayerStartTileY = 0;

    private int mTileWidth = 0;
    private int mTileHeight = 0;
    private GameThread thread;

    private WeaponObject playerWeapon;

    private List<PlayerLife> playerLives;

    private MainUi fireButton = null;

    private int enemyKillCount = 0;

    public MainView(Context context, GameActivity activity, int stage, int level, float screenDensity) {
        super(context);

        mGameContext = context;
        mGameActivity = activity;

        mScreenDensity = screenDensity;

        mPlayerStage = stage;
        mPlayerLevel = level;

        mGameTileData = new GameLevelData(context);
        mGameLevelTileData = new GameStageData(context);

        mGameTileTemplates = mGameTileData.getLevelData();

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new GameThread(holder, context, null);

        setFocusable(true);

        mUiTextPaint = new Paint();
        mUiTextPaint.setStyle(Paint.Style.FILL);
        mUiTextPaint.setColor(Color.YELLOW);
        mUiTextPaint.setAntiAlias(true);

        //Typeface uiTypeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Molot.otf");
        Typeface uiTypeface = Typeface.createFromAsset(activity.getAssets(), "fonts/CHINESETAKEAWAY.ttf");
        if (uiTypeface != null) {
            mUiTextPaint.setTypeface(uiTypeface);
        }
        mUiTextPaint.setTextSize(mGameContext.getApplicationContext().getResources().getDimensionPixelSize(R.dimen.ui_text_size));

        startLevel();
        thread.doStart();
    }
    private void drawGameTiles(Canvas canvas) {
        int gameTilesSize = mGameTiles.size();
        //boolean playerPerception = mPlayerUnit.getX() + mPlayerUnit.getWidth() > mGameTiles.get(gameTilesSize-1).getX() - mScreenXOffset || mPlayerUnit.getY() + mPlayerUnit.getHeight() > mGameTiles.get(gameTilesSize-1).getY() - mScreenYOffset;
        if (mPlayerUnit != null)
            //canvas.drawBitmap(mPlayerUnit.getBitmap(), mPlayerUnit.getX(), mPlayerUnit.getY(), null);

            mPlayerUnit.setWhereToDraw();
        mPlayerUnit.getCurrentFrame(mPlayerMoving, mPlayerDirection);
        canvas.drawBitmap(mPlayerUnit.getBitmap(), mPlayerUnit.getFrameToDraw(), mPlayerUnit.getWhereToDraw(), mUiTextPaint);

        if (mEnemyList.size() > 0) {
            for (EnemyObject enemy : mEnemyList) {
                enemy.setX(enemy.getX() - mScreenXOffset);
                enemy.setY(enemy.getY() - mScreenYOffset);

                enemy.setWhereToDraw();
                enemy.getCurrentFrame(enemy.isMoving(), enemy.getDirection());
                canvas.drawBitmap(enemy.getBitmap(), enemy.getFrameToDraw(), enemy.getWhereToDraw(), mUiTextPaint);
            }
        }

        for (int i = 0; i < gameTilesSize; i++) {
            if (mGameTiles.get(i) != null) {
                mGameTiles.get(i).setX(
                        mGameTiles.get(i).getX() - mScreenXOffset);
                mGameTiles.get(i).setY(
                        mGameTiles.get(i).getY() - mScreenYOffset);
                if (mGameTiles.get(i).isVisible()) {
                    canvas.drawBitmap(mGameTiles.get(i).getBitmap(),
                            mGameTiles.get(i).getX(), mGameTiles.get(i)
                                    .getY(), null);
                }
            }
        }
    }

    private void drawControls(Canvas canvas) {
        canvas.drawBitmap(mCtrlUpArrow.getBitmap(), mCtrlUpArrow.getX(), mCtrlUpArrow.getY(), null);
        canvas.drawBitmap(mCtrlDownArrow.getBitmap(), mCtrlDownArrow.getX(), mCtrlDownArrow.getY(), null);
        canvas.drawBitmap(mCtrlLeftArrow.getBitmap(), mCtrlLeftArrow.getX(), mCtrlLeftArrow.getY(), null);
        canvas.drawBitmap(mCtrlRightArrow.getBitmap(), mCtrlRightArrow.getX(), mCtrlRightArrow.getY(), null);
    }

    private void drawFireButton(Canvas canvas){
        canvas.drawBitmap(fireButton.getBitmap(), fireButton.getX(), fireButton.getY(), null);
    }

    private void drawPlayerLives(Canvas canvas){
        for(PlayerLife playerLife : playerLives){
            canvas.drawBitmap(playerLife.getBitmap(), playerLife.getX(), playerLife.getY(), null);
        }
    }

    private void updatePlayerUnit() {
        FloorBase collisionTile = null;

        if (mPlayerMoving) {
            int differenceX = 0;
            int differenceY = 0;
            int newX = mPlayerUnit.getX();
            int newY = mPlayerUnit.getY();

            if (mPlayerHorizontalDirection != 0) {
                differenceX = (mPlayerHorizontalDirection == DIRECTION_RIGHT) ? getPixelValueForDensity(PlayerObject.SPEED) : getPixelValueForDensity(-PlayerObject.SPEED);
                newX = (mPlayerUnit.getX() + differenceX);
                //newX = (int)(mPlayerUnit.getX() + (differenceX / mPlayerUnit.getFps()));
            }

            if (mPlayerVerticalDirection != 0) {
                differenceY = (mPlayerVerticalDirection == DIRECTION_DOWN) ? getPixelValueForDensity(PlayerObject.SPEED) : getPixelValueForDensity(-PlayerObject.SPEED);
                newY = (mPlayerUnit.getY() + differenceY);
                //newY = (int)(mPlayerUnit.getY() + (differenceY / mPlayerUnit.getFps()));
            }

            collisionTile = getCollision(newX, newY, mPlayerUnit.getWidth(), mPlayerUnit.getHeight());

            				/*
				Iterator<EnemyUnit> iter = mEnemyList.iterator();
				while (iter.hasNext()) {
					EnemyUnit enemy = iter.next();

					if (mPlayerUnit.getRect().intersect(enemy.getRect())) {
						iter.remove();
					}
				}
				*/

            if ((collisionTile != null)
                    && collisionTile.isBlockerTile()) {
                handleTileCollision(collisionTile);
            } else {
                mPlayerUnit.setX(newX);
                mPlayerUnit.setY(newY);
            }
        }
    }
    
    private void updateEnemyUnit() {

        for (EnemyObject enemy : mEnemyList) {

            if (mEnemyMoveTimer.getTimeToMove()) {
                //enemy.setDirection(new Random().nextInt(4) + 1);
                enemy.setIsMoving(true);
                //enemy.setCaptureTime(System.currentTimeMillis());

                if (enemy.timeToChangeDirection()) {
                    enemy.setDirection(new Random().nextInt(4) + 1);
                    enemy.setTimeToChangeDirection(false);

                }

                FloorBase collisionTile = null;

                if (enemy.timeToMove()) {
                    //int direction = new Random().nextInt(4) + 1;
                    //int direction = 2;
                    //enemy.setDirection(direction);

                    int differenceX = 0;
                    int differenceY = 0;
                    int newX = enemy.getX();
                    int newY = enemy.getY();

                    if (enemy.getDirection() == DIRECTION_UP) {
                        differenceY = getPixelValueForDensity(EnemyObject.SPEED);
                        newY = (enemy.getY() - differenceY);
                    } else if (enemy.getDirection() == DIRECTION_RIGHT) {
                        differenceX = getPixelValueForDensity(EnemyObject.SPEED);
                        newX = (enemy.getX() + differenceX);
                    } else if (enemy.getDirection() == DIRECTION_DOWN) {
                        differenceY = getPixelValueForDensity(-EnemyObject.SPEED);
                        newY = (enemy.getY() - differenceY);
                    } else if (enemy.getDirection() == DIRECTION_LEFT) {
                        differenceX = getPixelValueForDensity(-EnemyObject.SPEED);
                        newX = (enemy.getX() + differenceX);
                    }

                    collisionTile = getCollision(newX, newY, enemy.getWidth(), enemy.getHeight());

                    Iterator<PlayerLife> iter = playerLives.iterator();
                    while (iter.hasNext()) {
                        iter.next();
                        if (enemy.getRect().intersect(mPlayerUnit.getRect()) && enemy.timeToAttack()) {
                            enemy.setLastTimeAttacked(System.currentTimeMillis());
                            iter.remove();

                            mLastStatusMessage = "Enemy hit you!";
                        }
                    }

                    if(playerLives.size() == 0){
                        mGameRun = false;

                        Intent i = new Intent(mGameContext, GameOverActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mGameContext.startActivity(i);
                    }

                    if ((collisionTile != null) && collisionTile.isBlockerTile()) {
                        handleTileCollision(collisionTile);

                    } else {
                        enemy.setX(newX);
                        enemy.setY(newY);
                    }
                }
            } else {
                enemy.setIsMoving(false);
                enemy.setTimeToChangeDirection(true);
            }

        }
    }

    public void updatePlayerWeapon(){

        FloorBase collisionTile = null;

        if(playerWeapon.isFiring()){
            int differenceX = 0;
            int differenceY = 0;
            int newX = playerWeapon.getX();
            int newY = playerWeapon.getY();

            if(playerWeapon.getDirection() == DIRECTION_UP){
                differenceY = getPixelValueForDensity(WeaponObject.SPEED);
                newY = (playerWeapon.getY() - differenceY);
            }
            else if(playerWeapon.getDirection() == DIRECTION_DOWN){
                differenceY = getPixelValueForDensity(-WeaponObject.SPEED);
                newY = (playerWeapon.getY() - differenceY);
            }
            else if(playerWeapon.getDirection() == DIRECTION_LEFT){
                differenceX = getPixelValueForDensity(-WeaponObject.SPEED);
                newX = (playerWeapon.getX() + differenceX);
            }
            else if(playerWeapon.getDirection() == DIRECTION_RIGHT){
                differenceX = getPixelValueForDensity(WeaponObject.SPEED);
                newX = (playerWeapon.getX() + differenceX);
            }

            collisionTile = getCollision(newX, newY, playerWeapon.getWidth(), playerWeapon.getHeight());

            Iterator<EnemyObject> iter = mEnemyList.iterator();

            while (iter.hasNext()) {
                EnemyObject enemy = iter.next();

                if (playerWeapon.getRect().intersect(enemy.getRect())) {
                    iter.remove();
                    playerWeapon.setFiring(false);
                    enemyKillCount++;
                }
            }


			for (EnemyObject enemy : mEnemyList) {
				if (playerWeapon.getRect().intersect(enemy.getRect())) {
					mEnemyList.remove(enemy);
					playerWeapon.setFiring(false);
					Log.d("enemyhit", "" + enemy.getId());
				}
			}

            if ((collisionTile != null) && collisionTile.isBlockerTile()) {
                handleTileCollision(collisionTile);
                playerWeapon.setFiring(false);
            } else {
                playerWeapon.setX(newX);
                playerWeapon.setY(newY);
            }
        }
    }

    private FloorBase getCollision(int x, int y, int width, int height) {
        FloorBase gameTile = null;

        int gameTilesSize = mGameTiles.size();
        for (int i = 0; i < gameTilesSize; i++) {
            gameTile = mGameTiles.get(i);
            if ((gameTile != null) && gameTile.isCollisionTile()) {
                // Make sure tiles don't collide with themselves
                if ((gameTile.getX() == x) && (gameTile.getY() == y)) {
                    continue;
                }

                if (gameTile.getCollision(x, y, width, height)) {
                    return gameTile;
                }
            }
        }
        return null;
    }

    private void handleTileCollision(FloorBase gameTile) {
        if (gameTile != null) {
            switch (gameTile.getType()) {
                case FloorBase.TYPE_DANGEROUS:
                    //handleDangerousTileCollision();
                    break;
                case FloorBase.TYPE_EXIT:
                    handleExitTileCollision();
                    break;
                default:
                    //mLastStatusMessage = "Collision with regular tile";
            }
        }
    }
    private void handleDangerousTileCollision() {
        mLastStatusMessage = "Collision with dangerous tile";
    }

    private void handleExitTileCollision() {
        mLastStatusMessage = "You go up the stairs";
        mPlayerLevel++;
        thread.pause();
        startLevel();
    }

    public GameThread getThread() {
        return thread;
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        thread.setSurfaceSize(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {

        if (thread.getState() == Thread.State.TERMINATED) {
            thread = new GameThread(holder, getContext(), new Handler());
            thread.setRunning(true);
            thread.start();
            thread.doStart();
            startLevel();
        } else {
            thread.setRunning(true);
            thread.start();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e("Tile Game Example", e.getMessage());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:

                if (mGameState == STATE_RUNNING) {
                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if(fireButton.getImpact(x, y)){
                        mLastStatusMessage = "Fire!";

                        if(!playerWeapon.isFiring()) {
                            playerWeapon.setDirection(mPlayerDirection);
                            playerWeapon.setFiring(true);
                            playerWeapon.setX(mPlayerUnit.getX());
                            playerWeapon.setY(mPlayerUnit.getY());

                            return true;
                        }
                    }

                    if (y < mScreenYCenter / 2) {
                        Log.d("Tile Game Example", "Pressed up arrow");
                        mLastStatusMessage = "Moving up";
                        mPlayerVerticalDirection = DIRECTION_UP;

                        mPlayerDirection = DIRECTION_UP;

                        mPlayerMoving = true;
                    } else if (y > 3 * mScreenYCenter / 2) {
                        Log.d("Tile Game Example", "Pressed down arrow");
                        mLastStatusMessage = "Moving down";
                        mPlayerVerticalDirection = DIRECTION_DOWN;

                        mPlayerDirection = DIRECTION_DOWN;

                        mPlayerMoving = true;
                    } else if ((y > mScreenYMax / 4 && y < 3 * mScreenYMax / 4) && x < mScreenXCenter) {
                        Log.d("Tile Game Example", "Pressed left arrow");
                        mLastStatusMessage = "Moving left";
                        mPlayerHorizontalDirection = DIRECTION_LEFT;

                        mPlayerDirection = DIRECTION_LEFT;

                        mPlayerMoving = true;
                    } else if ((y > mScreenYMax / 4 && y < 3 * mScreenYMax / 4) && x > mScreenXCenter) {
                        Log.d("Tile Game Example", "Pressed right arrow");
                        mLastStatusMessage = "Moving right";
                        mPlayerHorizontalDirection = DIRECTION_RIGHT;

                        mPlayerDirection = DIRECTION_RIGHT;

                        mPlayerMoving = true;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPlayerMoving = false;
                mPlayerVerticalDirection = 0;
                mPlayerHorizontalDirection = 0;
                break;
        }

        return true;
    }

    /*
    private void setControlsStart() {
        if (mCtrlDownArrow == null) {
            mCtrlDownArrow = new MainUi(mGameContext, R.drawable.ctrl_down_arrow);

            mCtrlDownArrow.setX(mScreenXMax - ((mCtrlDownArrow.getWidth() * 2) + getPixelValueForDensity(CONTROLS_PADDING)));
            mCtrlDownArrow.setY(mScreenYMax - (mCtrlDownArrow.getHeight() + getPixelValueForDensity(CONTROLS_PADDING)));
        }

        if (mCtrlUpArrow == null) {
            mCtrlUpArrow = new MainUi(mGameContext, R.drawable.ctrl_up_arrow);
            mCtrlUpArrow.setX(mCtrlDownArrow.getX());
            mCtrlUpArrow.setY(mCtrlDownArrow.getY() - (mCtrlUpArrow.getHeight() * 2));
        }

        if (mCtrlLeftArrow == null) {
            mCtrlLeftArrow = new MainUi(mGameContext, R.drawable.ctrl_left_arrow);
            mCtrlLeftArrow.setX(mCtrlDownArrow.getX() - mCtrlLeftArrow.getWidth());
            mCtrlLeftArrow.setY(mCtrlDownArrow.getY() - mCtrlLeftArrow.getHeight());
        }

        if (mCtrlRightArrow == null) {
            mCtrlRightArrow = new MainUi(mGameContext, R.drawable.ctrl_right_arrow);

            mCtrlRightArrow.setX(mScreenXMax - (mCtrlLeftArrow.getWidth() + getPixelValueForDensity(CONTROLS_PADDING)));
            mCtrlRightArrow.setY(mCtrlLeftArrow.getY());
        }
    }
    */

    private void setFireButton(){
        if(fireButton == null){
            fireButton = new MainUi(mGameContext, R.drawable.wpbf);
            fireButton.setX(mScreenXMax - fireButton.getWidth());
            fireButton.setY(mScreenYMax - fireButton.getHeight());
        }
    }

    private void setPlayerStart() {
        if (mPlayerUnit == null) {
            mPlayerUnit = new PlayerObject(mGameContext, R.drawable.walk_left_right_up_down);
        }

        int playerStartX = (mPlayerStartTileX * 100);
        int playerStartY = (mPlayerStartTileY * 100);

        Log.d("logging", "X: " + mPlayerUnit.getWidth() + " Y: " + mPlayerUnit.getHeight());
        playerStartX = getPixelValueForDensity(100);
        playerStartY = getPixelValueForDensity(100);

        Log.d("Tile Game Example", "Player unit starting at X: " + playerStartX + ", Y: " + playerStartY);

        mPlayerUnit.setX(playerStartX);
        mPlayerUnit.setY(playerStartY);
        mPlayerUnit.setUnmodifiedX(0);
        mPlayerUnit.setUnmodifiedY(0);


        playerWeapon = new WeaponObject(mGameContext, R.drawable.wok);

        playerLives = new ArrayList<PlayerLife>();
        for(int x = 1; x < mPlayerUnit.getNumOfLives()+1; x++){
            PlayerLife playerLife = new PlayerLife(mGameContext, R.drawable.heart);
            playerLife.setX(mScreenXMax - (playerLife.getWidth()*x));
            playerLives.add(playerLife);
        }
    }

    private void setEnemyStart() {
        mEnemyList.removeAll(mEnemyList);

        mNumOfEnemies = (int) (Math.random() * 5) + 1;

        mEnemyMoveTimer = new EnemyMoveTimer(5000, 10000);

        if (mEnemyList.size() == 0) {
            for (int x = 0; x < mNumOfEnemies; x++) {
                EnemyObject enemy = new EnemyObject(mGameContext, R.drawable.z_animations_chinese, mScreenDensity);
                enemy.setX(getPixelValueForDensity(100));
                enemy.setY(getPixelValueForDensity(150));

                enemy.setIsMoving(true);
                enemy.setDirection(new Random().nextInt(4) + 1);

                mEnemyList.add(enemy);
            }
        }

        mEnemyMoveTimer.start();
    }

    private void parseGameLevelData() {
        updatingGameTiles = true;

        ArrayList<String> gameLevelData = mGameLevelTileData.getGameStageData(mPlayerStage, mPlayerLevel);

        String levelTileData = gameLevelData.get(GameStageData.FIELD_ID_TILE_DATA);

        if (levelTileData == null) {
            return;
        }

        mPlayerStartTileX = GameStageData.getFieldIdPlayerStartTileX();
        mPlayerStartTileY = GameStageData.getFieldIdPlayerStartTileY();

        mGameTiles.clear();

        String[] tileLines = levelTileData.split(GameStageData.TILE_DATA_LINE_BREAK);

        Bitmap bitmap = null;
        Point tilePoint = new Point(0, 0);
        int tileX = 0;
        int tileY = 0;

        int tileKey = 0;

        for (String tileLine : tileLines) {
            tileX = 0;

            String[] tiles = tileLine.split(",");

            for (String tile : tiles) {
                ArrayList<Integer> tileData = mGameTileTemplates.get(Integer.parseInt(tile));

                if ((tileData != null)
                        && (tileData.size() > 0)
                        && (tileData.get(GameLevelData.FIELD_ID_DRAWABLE) > 0)) {
                    tilePoint.x = tileX;
                    tilePoint.y = tileY;
                    FloorBase gameTile = new FloorBase(mGameContext, tilePoint);

                    bitmap = setAndGetGameTileBitmap(tileData.get(GameLevelData.FIELD_ID_DRAWABLE));
                    gameTile.setBitmap(bitmap);

                    gameTile.setType(tileData.get(GameLevelData.FIELD_ID_TYPE));

                    if (tileData.get(GameLevelData.FIELD_ID_VISIBLE) == 0) {
                        gameTile.setVisible(false);
                    }

                    gameTile.setKey(tileKey);

                    if (mTileWidth == 0) {
                        mTileWidth = gameTile.getWidth();
                    }
                    if (mTileHeight == 0) {
                        mTileHeight = gameTile.getHeight();
                    }

                    mGameTiles.add(gameTile);

                    tileKey++;
                }

                tileX += mTileWidth;
            }

            tileY += mTileHeight;
        }

        updatingGameTiles = false;
    }

    private void setGameStartState() {
        //setControlsStart();
        setPlayerStart();
        setEnemyStart();
        setFireButton();
    }

    private void startLevel() {
        parseGameLevelData();
        setPlayerStart();
        setEnemyStart();

        thread.unpause();
    }

    private Bitmap setAndGetGameTileBitmap(int resourceId) {
        if (!mGameTileBitmaps.containsKey(resourceId)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeResource(mGameContext
                    .getResources(), resourceId);

            if (bitmap != null) {
                mGameTileBitmaps.put(resourceId, bitmap);
            }
        }

        return mGameTileBitmaps.get(resourceId);
    }

    private int getPixelValueForDensity(int pixels) {
        return (int) (pixels * mScreenDensity);
    }

    class GameThread extends Thread {
        public GameThread(SurfaceHolder surfaceHolder, Context context,
                          Handler handler) {
            mGameSurfaceHolder = surfaceHolder;
            mGameContext = context;

            Resources res = context.getResources();

            mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.canvas_bg_01);

            Display display = mGameActivity.getWindowManager().getDefaultDisplay();
            mScreenXMax = display.getWidth();
            mScreenYMax = display.getHeight();
            mScreenXCenter = (mScreenXMax / 2);
            mScreenYCenter = (mScreenYMax / 2);

            setGameStartState();
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mGameSurfaceHolder) {
                mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
                        width, height, true);
            }
        }

        public void setRunning(boolean run) {
            mGameRun = run;
        }

        public void doStart() {
            setState(STATE_RUNNING);
        }

        public void setState(int state) {
            mGameState = state;
        }

        @Override
        public void run() {
            while (mGameRun) {
                Canvas c = null;
                try {
                    c = mGameSurfaceHolder.lockCanvas(null);
                    synchronized (mGameSurfaceHolder) {
                        long startTimeFrame = System.currentTimeMillis();

                        if (mGameState == STATE_RUNNING) {
                            updatePlayerUnit();
                            updateEnemyUnit();
                            updatePlayerWeapon();
                        }

                        doDraw(c);
                        mPlayerUnit.setTimeThisFrame(System.currentTimeMillis() - startTimeFrame);
                        if (mPlayerUnit.getTimeThisFrame() >= 1) {
                            mPlayerUnit.setFps(1000 / mPlayerUnit.getTimeThisFrame());
                        }
                    }
                } finally {
                    if (c != null) {
                        mGameSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }

            return;
        }

        public void pause() {
            synchronized (mGameSurfaceHolder) {
                if (mGameState == STATE_RUNNING) {
                    setState(STATE_PAUSED);
                }
            }
        }

        public void unpause() {
            synchronized (mGameSurfaceHolder) {
                if (mGameState != STATE_RUNNING) {
                    setState(STATE_RUNNING);
                }
            }
        }

        private void centerView() {
            mPlayerUnit.setUnmodifiedX(mPlayerUnit.getX() + mScreenXCenter);
            mPlayerUnit.setUnmodifiedY(mPlayerUnit.getY() + mScreenYCenter);

            mScreenXOffset = (mPlayerUnit.getX() - mScreenXCenter);
            mScreenYOffset = (mPlayerUnit.getY() - mScreenYCenter);

            mPlayerUnit.setX(mScreenXCenter);
            mPlayerUnit.setY(mScreenYCenter);
        }

        private void doDraw(Canvas canvas) {
            centerView();

            if (canvas != null) {
                canvas.drawBitmap(mBackgroundImage, 0, 0, null);

                if (!updatingGameTiles) {
                    drawGameTiles(canvas);
                }

                //drawControls(canvas);

                if(playerWeapon.isFiring()){
                    playerWeapon.setX(playerWeapon.getX() - mScreenXOffset);
                    playerWeapon.setY(playerWeapon.getY() - mScreenYOffset);

                    playerWeapon.setWhereToDraw();
                    playerWeapon.getCurrentFrame(playerWeapon.getDirection());
                    canvas.drawBitmap(playerWeapon.getBitmap(), playerWeapon.getFrameToDraw(), playerWeapon.getWhereToDraw(), mUiTextPaint);
                }

                mUiTextPaint.setColor(Color.argb(255, 0, 0, 0));
                Path path = new Path();
                path.addCircle(mPlayerUnit.getX() + mPlayerUnit.getWidth()/2, mPlayerUnit.getY() + mPlayerUnit.getHeight()/2, mPlayerUnit.getWidth()*3, Path.Direction.CW);
                path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
                //canvas.drawPath(path, mUiTextPaint);

                drawPlayerLives(canvas);

                mUiTextPaint.setColor(Color.argb(255, 255, 0, 0));
                mUiTextPaint.setTextSize(100);
                canvas.drawText(mLastStatusMessage, getPixelValueForDensity(100), getPixelValueForDensity(100), mUiTextPaint);
                canvas.drawText("Enemies Killed: " + enemyKillCount, 0, mScreenYMax - getPixelValueForDensity(200), mUiTextPaint);

                drawFireButton(canvas);
            }
        }
    }
}
