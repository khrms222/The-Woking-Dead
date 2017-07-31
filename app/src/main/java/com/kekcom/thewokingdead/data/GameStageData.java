package com.kekcom.thewokingdead.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;


public class GameStageData extends GameDAO {
    public static final String TABLE_NAME = "gameLevelTileData";

    public static final String STAGE = "stage";
    public static final String LEVEL = "level";
    public static final String PLAYER_START_TILE_X = "playerStartTileX";
    public static final String PLAYER_START_TILE_Y = "playerStartTileY";
    public static final String TILE_DATA = "tileData";

    public static final int FIELD_ID_ID = 0;
    public static final int FIELD_ID_TILE_DATA = 5;
    public static final String TILE_DATA_LINE_BREAK = "//";
    public static int FIELD_ID_STAGE = 1;
    public static int FIELD_ID_LEVEL = 2;
    private static int FIELD_ID_PLAYER_START_TILE_X = 3;
    private static int FIELD_ID_PLAYER_START_TILE_Y = 4;
    private static int size = 7;

    private static int a = 3;
    private static int b = 3;
    public GameStageData(Context ctx) {
        super(ctx);
    }

    public static int getFieldIdPlayerStartTileX() {

        return a;
    }

    public static int getFieldIdPlayerStartTileY() {

        return b;
    }

    private static String[][] RNGenie(int max) {
        String[][] gw = new String[max][max];
        for (int i = 0; i < gw.length; i++) {
            for (int j = 0; j < gw[0].length; j++) {
                if (i == 0 || i == (gw.length - 1) || j == 0 || j == (gw[0].length - 1))
                    gw[i][j] = "01";
                else if (i % 2 == 0 && j % 2 == 0)
                    gw[i][j] = "01";
                else
                    gw[i][j] = "00";
            }
        }
        return gw;
    }

    public static ArrayList<String> parse() {
        ArrayList<String> yoooo = new ArrayList<>();
        for (int k = 1; k <= 10; k++) {
            size = size + (k * 2);
            String[][] gw = RNGenie(size);
            boolean check1 = true;
            while (check1) {
                int x = (int) (Math.random() * size);
                int y = (int) (Math.random() * size);
                if (gw[x][y] == "00") {
                    gw[x][y] = "03";
                    check1 = false;
                }
            }
            boolean check2 = true;
            while (check2) {
                int x = (int) (Math.random() * size);
                int y = (int) (Math.random() * size);
                if (gw[x][y] == "00") {
                    a = x;
                    b = y;
                    check2 = false;
                }
            }
            String command = "INSERT INTO " + GameStageData.TABLE_NAME + " VALUES "
                    + "(null," + FIELD_ID_STAGE + "," + k + "," + a + "," + b + ",\"";
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (j == size - 1)
                        command += gw[i][j];
                    else
                        command += gw[i][j] + ",";
                }
                command += GameStageData.TILE_DATA_LINE_BREAK;
            }
            command += "\");";
            yoooo.add(command);
        }
        return yoooo;
    }

    public ArrayList<String> getGameStageData(int stage, int level) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] from = {_ID, STAGE, LEVEL, PLAYER_START_TILE_X, PLAYER_START_TILE_Y, TILE_DATA};
        String where = STAGE + " = " + stage + " AND " + LEVEL + " = " + level;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        ArrayList<String> levelData = new ArrayList<String>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                levelData.add(cursor.getString(FIELD_ID_ID));
                levelData.add(cursor.getString(FIELD_ID_STAGE));
                levelData.add(cursor.getString(FIELD_ID_LEVEL));
                levelData.add(cursor.getString(FIELD_ID_PLAYER_START_TILE_X));
                levelData.add(cursor.getString(FIELD_ID_PLAYER_START_TILE_Y));
                levelData.add(cursor.getString(FIELD_ID_TILE_DATA));
            }
            cursor.close();
        }

        db.close();
        return levelData;
    }
}