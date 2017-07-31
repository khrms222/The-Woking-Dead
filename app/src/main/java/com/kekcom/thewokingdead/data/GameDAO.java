package com.kekcom.thewokingdead.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kekcom.thewokingdead.FloorBase;
import com.kekcom.thewokingdead.R;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;

public class GameDAO extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gamedata.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_GAME_TILES = "CREATE TABLE " + GameLevelData.TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY, "
            + GameLevelData.NAME + " STRING,"
            + GameLevelData.TYPE + " INTEGER DEFAULT 0,"
            + GameLevelData.DRAWABLE + " INTEGER DEFAULT 0,"
            + GameLevelData.VISIBLE + " INTEGER DEFAULT 1"
            + ");";

    private static final String CREATE_TABLE_GAME_LEVEL_TILES = "CREATE TABLE " + GameStageData.TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + GameStageData.STAGE + " INTEGER DEFAULT 0,"
            + GameStageData.LEVEL + " INTEGER DEFAULT 0,"
            + GameStageData.PLAYER_START_TILE_X + " INTEGER DEFAULT 0,"
            + GameStageData.PLAYER_START_TILE_Y + " INTEGER DEFAULT 0,"
            + GameStageData.TILE_DATA + " TEXT NOT NULL"
            + ");";

    private static final String[] POPULATE_TABLE_GAME_TILES = {
            "INSERT INTO " + GameLevelData.TABLE_NAME + " VALUES "
                    + "(1,\"Tile 01\"," + FloorBase.TYPE_OBSTACLE + "," + R.drawable.tile_01 + ",1);",

            "INSERT INTO " + GameLevelData.TABLE_NAME + " VALUES "
                    + "(2,\"Dangerous Tile 01\"," + FloorBase.TYPE_DANGEROUS + "," + R.drawable.tile_danger_01 + ",1);",

            "INSERT INTO " + GameLevelData.TABLE_NAME + " VALUES "
                    + "(3,\"Exit Tile\"," + FloorBase.TYPE_EXIT + "," + R.drawable.zst41r30typ3 + ",1);"
    };

    private static ArrayList<String> POPULATE_TABLE_GAME_LEVEL_TILES = GameStageData.parse();

    public GameDAO(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("Tile Game Example", "Creating DB tables");

        db.execSQL(CREATE_TABLE_GAME_TILES);
        db.execSQL(CREATE_TABLE_GAME_LEVEL_TILES);

        Log.d("Tile Game Example", "Populating DB tables");

        for (String query : POPULATE_TABLE_GAME_TILES) {
            db.execSQL(query);
        }

        for (String query : POPULATE_TABLE_GAME_LEVEL_TILES) {
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GameLevelData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GameStageData.TABLE_NAME);

        onCreate(db);
    }


}