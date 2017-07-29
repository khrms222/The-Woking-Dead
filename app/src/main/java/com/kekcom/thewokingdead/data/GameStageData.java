package com.kekcom.thewokingdead.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;

public class GameStageData extends GameDAO {
    public static final String TABLE_NAME = "gameStageData";

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
    public static int FIELD_ID_PLAYER_START_TILE_X = 1;
    public static int FIELD_ID_PLAYER_START_TILE_Y = 1;
    private static int size = 5 + (2 * FIELD_ID_LEVEL);

    public GameStageData(Context ctx) {
        super(ctx);
    }

    private static String[][] RNGenie() {
        String[][] gw = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == 0 || i == size - 1 || j == 0 || j == size - 1)
                    gw[i][j] = "01";
                else if (i % 2 == 0 && j % 2 == 0)
                    gw[i][j] = "01";
                else
                    gw[i][j] = "00";
            }
        }
        Boolean startingPos = true;
        while (startingPos) {
            int x = (int) (Math.random() * size);
            int y = (int) (Math.random() * size);
            if (gw[x][y] == "00") {
                FIELD_ID_PLAYER_START_TILE_X = x;
                FIELD_ID_PLAYER_START_TILE_Y = y;
                startingPos = false;
            }
        }
        return gw;
    }

    public static ArrayList<String> parse() {
        String[][] gw = RNGenie();
        ArrayList<String> parsing = new ArrayList<>(size);
        String command = "INSERT INTO " + GameStageData.TABLE_NAME + " VALUES "
                + "(null,1,1,1,1,\"";
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
        parsing.add(command);
        return parsing;
    }

    public ArrayList<String> getGameStageData(int stage, int level) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] from = {_ID, STAGE, LEVEL, PLAYER_START_TILE_X, PLAYER_START_TILE_Y, TILE_DATA};
        String where = STAGE + " = " + stage + " AND " + LEVEL + " = " + level;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        ArrayList<String> stageData = new ArrayList<String>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                stageData.add(cursor.getString(FIELD_ID_ID));
                stageData.add(cursor.getString(FIELD_ID_STAGE));
                stageData.add(cursor.getString(FIELD_ID_LEVEL));
                stageData.add(cursor.getString(FIELD_ID_PLAYER_START_TILE_X));
                stageData.add(cursor.getString(FIELD_ID_PLAYER_START_TILE_Y));
                stageData.add(cursor.getString(FIELD_ID_TILE_DATA));
            }
            cursor.close();
        }

        db.close();
        return stageData;
    }
}