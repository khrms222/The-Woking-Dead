package com.kekcom.thewokingdead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.content.Context;
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

public class MainView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final int CONTROLS_PADDING = 10;

    private static final int START_STAGE = 1;
    private static final int START_LEVEL = 1;

    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;
    private static final int DIRECTION_LEFT = 3;
    private static final int DIRECTION_RIGHT = 4;

    public static final int STATE_RUNNING = 1;
    public static final int STATE_PAUSED = 2;

    private int mScreenXMax = 0;
    private int mScreenYMax = 0;
    private int mScreenXCenter = 0;
    private int mScreenYCenter = 0;
    private int mScreenXOffset = 0;
    private int mScreenYOffset = 0;

    private float mScreenDensity;

    private Context mGameContext;
    private Start mGameActivity;
    private SurfaceHolder mGameSurfaceHolder = null;

    private boolean updatingFloorBase = false;

    private GameLevelData mGameLevelData = null;
    private GameStageData mGameStageData = null;

    private PlayerObject mPlayerObject = null;

    private int mPlayerStage = START_STAGE;
    private int mPlayerLevel = START_LEVEL;

    private Bitmap mBackgroundImage = null;

    private int mGameState;

    private boolean mGameRun = true;

    private boolean mPlayerMoving = false;
    private int mPlayerVerticalDirection = 0;
    private int mPlayerHorizontalDirection = 0;

    private int mPlayerDirection = 0;

    private MainUI mCtrlUpArrow = null;
    private MainUI mCtrlDownArrow = null;
    private MainUI mCtrlLeftArrow = null;
    private MainUI mCtrlRightArrow = null;

    private Paint mUiTextPaint = null;
    private String mLastStatusMessage = "";

    private int mNumOfEnemies;
    private List<EnemyObject> mEnemyList = new ArrayList<EnemyObject>();
    private EnemyMoveTimer mEnemyMoveTimer;

    private HashMap<Integer, ArrayList<Integer>> mFloorBaseTemplates = null;

    private HashMap<Integer, Bitmap> mFloorBaseBitmaps = new HashMap<Integer, Bitmap>();

    private List<FloorBase> mFloorBase = new ArrayList<FloorBase>();

    private int mPlayerStartTileX = 0;
    private int mPlayerStartTileY = 0;

    private int mTileWidth = 0;
    private int mTileHeight = 0;

    private WeaponObject playerWeapon;

    private List<PlayerLife> playerLives;

    class GameThread extends Thread
    {
        public GameThread(SurfaceHolder surfaceHolder, Context context,
                          Handler handler)
        {
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

        public void setSurfaceSize(int width, int height)
        {
            synchronized (mGameSurfaceHolder)
            {
                mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
                        width, height, true);
            }
        }

        public void setRunning(boolean run)
        {
            mGameRun = run;
        }

        public void doStart()
        {
            setState(STATE_RUNNING);
        }

        public void setState(int state)
        {
            mGameState = state;
        }

        @Override
        public void run()
        {
            while (mGameRun)
            {
                Canvas c = null;
                try
                {
                    c = mGameSurfaceHolder.lockCanvas(null);
                    synchronized (mGameSurfaceHolder)
                    {
                        long startTimeFrame = System.currentTimeMillis();

                        if (mGameState == STATE_RUNNING)
                        {
                            updatePlayerObject();
                            updateEnemyObject();
                            updatePlayerWeapon();
                        }

                        doDraw(c);

                        mPlayerObject.setTimeThisFrame(System.currentTimeMillis() - startTimeFrame);
                        if(mPlayerObject.getTimeThisFrame() >= 1){
                            mPlayerObject.setFps(1000 / mPlayerObject.getTimeThisFrame());
                        }
                    }
                } finally
                {
                    if (c != null)
                    {
                        mGameSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }

            return;
        }

        public void pause()
        {
            synchronized (mGameSurfaceHolder)
            {
                if (mGameState == STATE_RUNNING)
                {
                    setState(STATE_PAUSED);
                }
            }
        }

        public void unpause()
        {
            synchronized (mGameSurfaceHolder)
            {
                if (mGameState != STATE_RUNNING)
                {
                    setState(STATE_RUNNING);
                }
            }
        }

        private void centerView()
        {
            mPlayerObject.setUnmodifiedX(mPlayerObject.getX() + mScreenXCenter);
            mPlayerObject.setUnmodifiedY(mPlayerObject.getY() + mScreenYCenter);

            mScreenXOffset = (mPlayerObject.getX() - mScreenXCenter);
            mScreenYOffset = (mPlayerObject.getY() - mScreenYCenter);

            mPlayerObject.setX(mScreenXCenter);
            mPlayerObject.setY(mScreenYCenter);
        }

        private void doDraw(Canvas canvas)
        {
            centerView();

            if (canvas != null && mGameRun)
            {
                canvas.drawBitmap(mBackgroundImage, 0, 0, null);

                if (!updatingFloorBase)
                {
                    drawFloorBase(canvas);
                }

                if (mPlayerObject != null)

                    mPlayerObject.setWhereToDraw();
                mPlayerObject.getCurrentFrame(mPlayerMoving, mPlayerDirection);
                canvas.drawBitmap(mPlayerObject.getBitmap(), mPlayerObject.getFrameToDraw(), mPlayerObject.getWhereToDraw(), mUiTextPaint);
            }

            if(mEnemyList.size() > 0){
                for(EnemyObject enemy : mEnemyList){
                    enemy.setX(enemy.getX() - mScreenXOffset);
                    enemy.setY(enemy.getY() - mScreenYOffset);

                    enemy.setWhereToDraw();
                    enemy.getCurrentFrame(enemy.isMoving(),enemy.getDirection());
                    canvas.drawBitmap(enemy.getBitmap(), enemy.getFrameToDraw(), enemy.getWhereToDraw(), mUiTextPaint);
                }
            }

            if(playerWeapon.isFiring()){
                playerWeapon.setX(playerWeapon.getX() - mScreenXOffset);
                playerWeapon.setY(playerWeapon.getY() - mScreenYOffset);

                playerWeapon.setWhereToDraw();
                playerWeapon.getCurrentFrame(playerWeapon.getDirection());
                canvas.drawBitmap(playerWeapon.getBitmap(), playerWeapon.getFrameToDraw(), playerWeapon.getWhereToDraw(), mUiTextPaint);
            }


            mUiTextPaint.setColor(Color.argb(255, 0, 0, 0));

            Path path = new Path();
            path.addCircle(mPlayerObject.getX() + mPlayerObject.getWidth()/2, mPlayerObject.getY() + mPlayerObject.getHeight()/2, mPlayerObject.getWidth()*3, Path.Direction.CW);
            path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
            canvas.drawPath(path, mUiTextPaint);

            drawControls(canvas);
            drawPlayerLives(canvas);

            canvas.drawText(mLastStatusMessage, 30, 50, mUiTextPaint);
        }
    }

    private void drawFloorBase(Canvas canvas)
    {
        int floorBaseSize = mFloorBase.size();
        for (int i = 0; i < floorBaseSize; i++)
        {
            if (mFloorBase.get(i) != null)
            {
                mFloorBase.get(i).setX(
                        mFloorBase.get(i).getX() - mScreenXOffset);
                mFloorBase.get(i).setY(
                        mFloorBase.get(i).getY() - mScreenYOffset);

                if (mFloorBase.get(i).isVisible())
                {
                    canvas.drawBitmap(mFloorBase.get(i).getBitmap(),
                            mFloorBase.get(i).getX(), mFloorBase.get(i)
                                    .getY(), null);
                }
            }
        }
    }


    private void drawControls(Canvas canvas)
    {
        canvas.drawBitmap(mCtrlUpArrow.getBitmap(), mCtrlUpArrow.getX(), mCtrlUpArrow.getY(), null);
        canvas.drawBitmap(mCtrlDownArrow.getBitmap(), mCtrlDownArrow.getX(), mCtrlDownArrow.getY(), null);
        canvas.drawBitmap(mCtrlLeftArrow.getBitmap(), mCtrlLeftArrow.getX(), mCtrlLeftArrow.getY(), null);
        canvas.drawBitmap(mCtrlRightArrow.getBitmap(), mCtrlRightArrow.getX(), mCtrlRightArrow.getY(), null);
    }

    private void drawPlayerLives(Canvas canvas){
        for(PlayerLife playerLife : playerLives){
            canvas.drawBitmap(playerLife.getBitmap(), playerLife.getX(), playerLife.getY(), null);
        }
    }

    private void updatePlayerObject()
    {
        FloorBase collisionTile = null;

        if (mPlayerMoving)
        {
            int differenceX = 0;
            int differenceY = 0;
            int newX = mPlayerObject.getX();
            int newY = mPlayerObject.getY();

            if (mPlayerHorizontalDirection != 0)
            {
                differenceX = (mPlayerHorizontalDirection == DIRECTION_RIGHT) ? getPixelValueForDensity(PlayerObject.SPEED) : getPixelValueForDensity(-PlayerObject.SPEED);
                newX = (mPlayerObject.getX() + differenceX);
            }

            if (mPlayerVerticalDirection != 0)
            {
                differenceY = (mPlayerVerticalDirection == DIRECTION_DOWN) ? getPixelValueForDensity(PlayerObject.SPEED) : getPixelValueForDensity(-PlayerObject.SPEED);
                newY = (mPlayerObject.getY() + differenceY);
            }

            collisionTile = getCollisionTile(newX, newY, mPlayerObject.getWidth(), mPlayerObject .getHeight());

            if ((collisionTile != null)
                    && collisionTile.isBlockerTile())
            {
                handleTileCollision(collisionTile);
            } else
            {
                mPlayerObject.setX(newX);
                mPlayerObject.setY(newY);
            }
        }
    }

    private void updateEnemyObject(){

        for(EnemyObject enemy : mEnemyList){

            if(mEnemyMoveTimer.getTimeToMove()){
                enemy.setIsMoving(true);

                if(enemy.timeToChangeDirection()){
                    enemy.setDirection(new Random().nextInt(4) + 1);
                    enemy.setTimeToChangeDirection(false);

                }

                FloorBase collisionTile = null;

                if(enemy.timeToMove()) {
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

                    collisionTile = getCollisionTile(newX, newY, enemy.getWidth(), enemy.getHeight());


                    Iterator<PlayerLife> iter = playerLives.iterator();
                    while (iter.hasNext()) {
                        iter.next();
                        if (enemy.getRect().intersect(mPlayerObject.getRect()) && enemy.timeToAttack()) {
                            enemy.setLastTimeAttacked(System.currentTimeMillis());
                            iter.remove();
                        }
                    }

                    if(playerLives.size() == 0){
                        mGameRun = false;
                    }

                    if ((collisionTile != null) && collisionTile.isBlockerTile()) {
                        handleTileCollision(collisionTile);

                    } else {
                        enemy.setX(newX);
                        enemy.setY(newY);
                    }
                }
            }
            else{
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

            collisionTile = getCollisionTile(newX, newY, playerWeapon.getWidth(), playerWeapon.getHeight());

            Iterator<EnemyObject> iter = mEnemyList.iterator();

            while (iter.hasNext()) {
                EnemyObject enemy = iter.next();

                if (playerWeapon.getRect().intersect(enemy.getRect())) {
                    iter.remove();
                    playerWeapon.setFiring(false);
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

    private FloorBase getCollisionTile(int x, int y, int width, int height)
    {
        FloorBase floorBase = null;

        int floorBaseSize = mFloorBase.size();
        for (int i = 0; i < floorBaseSize; i++) {
            floorBase = (FloorBase) mFloorBase.get(i);
            if ((floorBase != null) && floorBase.isCollisionTile()) {
                if ((floorBase.getX() == x) && (floorBase.getY() == y)) {
                    continue;
                }

                if (floorBase.getCollision(x, y, width, height)) {
                    return floorBase;
                }
            }
        }
        return null;
    }

    private void handleTileCollision(FloorBase floorBase)
    {
        if (floorBase != null)
        {
            switch (floorBase.getType())
            {
                case FloorBase.TYPE_DANGEROUS:
                    break;
                case FloorBase.TYPE_EXIT:
                    break;
                default:
            }
        }
    }

    private void handleDangerousTileCollision()
    {
        mLastStatusMessage = "Collision with dangerous tile";
    }

    private void handleExitTileCollision()
    {
        mLastStatusMessage = "Collision with exit tile";
    }

    private GameThread thread;


    public MainView(Context context, Start activity, int stage, int level, float screenDensity)
    {
        super(context);

        mGameContext = context;
        mGameActivity = activity;

        mScreenDensity = screenDensity;

        mPlayerStage = stage;
        mPlayerLevel = level;

        mGameLevelData = new GameLevelData(context);
        mGameStageData = new GameStageData(context);

        mFloorBaseTemplates = mGameLevelData.getGameLevelData();

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new GameThread(holder, context, null);

        setFocusable(true);

        mUiTextPaint = new Paint();
        mUiTextPaint.setStyle(Paint.Style.FILL);
        mUiTextPaint.setColor(Color.YELLOW);
        mUiTextPaint.setAntiAlias(true);

        Typeface uiTypeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Molot.otf");
        if (uiTypeface != null)
        {
            mUiTextPaint.setTypeface(uiTypeface);
        }
        mUiTextPaint.setTextSize(mGameContext.getApplicationContext().getResources().getDimensionPixelSize(R.dimen.ui_text_size));

        startLevel();
        thread.doStart();
    }


    public GameThread getThread()
    {
        return thread;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height)
    {
        thread.setSurfaceSize(width, height);
    }


    public void surfaceCreated(SurfaceHolder holder)
    {
        if (thread.getState() == Thread.State.TERMINATED)
        {
            thread = new GameThread(holder, getContext(), new Handler());
            thread.setRunning(true);
            thread.start();
            thread.doStart();
            startLevel();
        }
        else
        {
            thread.setRunning(true);
            thread.start();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        thread.setRunning(false);
        while (retry)
        {
            try
            {
                thread.join();
                retry = false;
            } catch (InterruptedException e)
            {
                Log.e("Tile Game Example", e.getMessage());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int eventAction = event.getAction();

        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:

                if (mGameState == STATE_RUNNING)
                {
                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if (mCtrlUpArrow.getImpact(x, y))
                    {
                        Log.d("Tile Game Example", "Pressed up arrow");
                        mLastStatusMessage = "Moving up";
                        mPlayerVerticalDirection = DIRECTION_UP;

                        mPlayerDirection = DIRECTION_UP;

                        mPlayerMoving = true;

                    }
                    else if (mCtrlDownArrow.getImpact(x, y))
                    {
                        Log.d("Tile Game Example", "Pressed down arrow");
                        mLastStatusMessage = "Moving down";
                        mPlayerVerticalDirection = DIRECTION_DOWN;

                        mPlayerDirection = DIRECTION_DOWN;

                        mPlayerMoving = true;

                    }
                    else if (mCtrlLeftArrow.getImpact(x, y))
                    {
                        Log.d("Tile Game Example", "Pressed left arrow");
                        mLastStatusMessage = "Moving left";
                        mPlayerHorizontalDirection = DIRECTION_LEFT;

                        mPlayerDirection = DIRECTION_LEFT;

                        mPlayerMoving = true;

                    }
                    else if (mCtrlRightArrow.getImpact(x, y))
                    {
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

    private void setControlsStart()
    {
        if (mCtrlDownArrow == null)
        {
            mCtrlDownArrow = new MainUI(mGameContext, R.drawable.ctrl_down_arrow);

            mCtrlDownArrow.setX(mScreenXMax - ((mCtrlDownArrow.getWidth() * 2) + getPixelValueForDensity(CONTROLS_PADDING)));
            mCtrlDownArrow.setY(mScreenYMax - (mCtrlDownArrow.getHeight() + getPixelValueForDensity(CONTROLS_PADDING)));
        }

        if (mCtrlUpArrow == null)
        {
            mCtrlUpArrow = new MainUI(mGameContext, R.drawable.ctrl_up_arrow);

            mCtrlUpArrow.setX(mCtrlDownArrow.getX());
            mCtrlUpArrow.setY(mCtrlDownArrow.getY() - (mCtrlUpArrow.getHeight() * 2));
        }

        if (mCtrlLeftArrow == null)
        {
            mCtrlLeftArrow = new MainUI(mGameContext, R.drawable.ctrl_left_arrow);
            mCtrlLeftArrow.setX(mCtrlDownArrow.getX() - mCtrlLeftArrow.getWidth());
            mCtrlLeftArrow.setY(mCtrlDownArrow.getY() - mCtrlLeftArrow.getHeight());
        }

        if (mCtrlRightArrow == null)
        {
            mCtrlRightArrow = new MainUI(mGameContext, R.drawable.ctrl_right_arrow);

            mCtrlRightArrow.setX(mScreenXMax - (mCtrlLeftArrow.getWidth() + getPixelValueForDensity(CONTROLS_PADDING)));
            mCtrlRightArrow.setY(mCtrlLeftArrow.getY());
        }
    }

    private void setPlayerStart()
    {
        if (mPlayerObject == null)
        {
            mPlayerObject = new PlayerObject(mGameContext, R.drawable.walk_left_right_up_down);
        }

        int playerStartX = (mPlayerStartTileX * mPlayerObject.getWidth());
        int playerStartY = (mPlayerStartTileY * mPlayerObject.getHeight());

        Log.d("logging", "X: " + mPlayerObject.getWidth() + " Y: " + mPlayerObject.getHeight());

        Log.d("Tile Game Example", "Player unit starting at X: " + playerStartX + ", Y: " + playerStartY);

        mPlayerObject.setX(playerStartX);
        mPlayerObject.setY(playerStartY);
        mPlayerObject.setUnmodifiedX(0);
        mPlayerObject.setUnmodifiedY(0);

        playerWeapon = new WeaponObject(mGameContext, R.drawable.wok);




        playerLives = new ArrayList<PlayerLife>();
        for(int x = 1; x < mPlayerObject.getNumOfLives()+1; x++){
            PlayerLife playerLife = new PlayerLife(mGameContext, R.drawable.heart);
            playerLife.setX(mScreenXMax - (playerLife.getWidth()*x));
            playerLives.add(playerLife);
        }
    }

    private void setEnemyStart(){

        mNumOfEnemies = 4;

        mEnemyMoveTimer = new EnemyMoveTimer(5000, 10000);

        if(mEnemyList.size() == 0){
            for(int x = 0; x < mNumOfEnemies; x ++) {
                EnemyObject enemy = new EnemyObject(mGameContext, R.drawable.z_animations, mScreenDensity);
                enemy.setX(770);
                enemy.setY(330);

                enemy.setIsMoving(true);
                enemy.setDirection(new Random().nextInt(4) + 1);
                enemy.setLastTimeAttacked(System.currentTimeMillis());

                mEnemyList.add(enemy);
            }
        }

        mEnemyMoveTimer.start();
    }

    private void parseGameLevelData()
    {
        updatingFloorBase = true;

        ArrayList<String> gameLevelData = mGameStageData.getStageData(mPlayerStage, mPlayerLevel);

        String levelTileData = gameLevelData.get(GameStageData.FIELD_ID_TILE_DATA);

        if (levelTileData == null)
        {
            return;
        }

        mPlayerStartTileX = Integer.parseInt(gameLevelData.get(GameStageData.FIELD_ID_PLAYER_START_TILE_X));
        mPlayerStartTileY = Integer.parseInt(gameLevelData.get(GameStageData.FIELD_ID_PLAYER_START_TILE_Y));

        mFloorBase.clear();

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
                ArrayList<Integer> tileData = mFloorBaseTemplates.get(Integer.parseInt(tile));

                if ((tileData != null)
                        && (tileData.size() > 0)
                        && (tileData.get(GameLevelData.FIELD_ID_DRAWABLE) > 0)) {
                    tilePoint.x = tileX;
                    tilePoint.y = tileY;

                    FloorBase floorBase = new FloorBase(mGameContext, tilePoint);

                    bitmap = setAndGetFloorBaseBitmap(tileData.get(GameLevelData.FIELD_ID_DRAWABLE));
                    floorBase.setBitmap(bitmap);

                    floorBase.setType(tileData.get(GameLevelData.FIELD_ID_TYPE));

                    if (tileData.get(GameLevelData.FIELD_ID_VISIBLE) == 0) {
                        floorBase.setVisible(false);
                    }

                    floorBase.setKey(tileKey);

                    if (mTileWidth == 0) {
                        mTileWidth = floorBase.getWidth();
                    }
                    if (mTileHeight == 0) {
                        mTileHeight = floorBase.getHeight();
                    }

                    mFloorBase.add(floorBase);

                    tileKey++;
                }

                tileX += mTileWidth;
            }

            tileY += mTileHeight;
        }

        updatingFloorBase = false;
    }

    private void setGameStartState()
    {
        setControlsStart();
        setPlayerStart();
        setEnemyStart();
    }

    private void startLevel()
    {
        parseGameLevelData();
        setPlayerStart();
        setEnemyStart();

        thread.unpause();
    }


    private Bitmap setAndGetFloorBaseBitmap(int resourceId)
    {
        if (!mFloorBaseBitmaps.containsKey(resourceId))
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeResource(mGameContext
                    .getResources(), resourceId);

            if (bitmap != null)
            {
                mFloorBaseBitmaps.put(resourceId, bitmap);
            }
        }

        return mFloorBaseBitmaps.get(resourceId);
    }

    private int getPixelValueForDensity(int pixels)
    {
        return (int) (pixels * mScreenDensity);
    }
}