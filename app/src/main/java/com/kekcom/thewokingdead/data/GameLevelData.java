package com.kekcom.thewokingdead.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static android.provider.BaseColumns._ID;

public class GameLevelData extends GameDAO {
    public static final String TABLE_NAME = "gameLevelData";

    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String DRAWABLE = "drawable";
    public static final String VISIBLE = "visible";

    public static final int FIELD_ID_ID = 0;
    public static final int FIELD_ID_NAME = 1;
    public static final int FIELD_ID_TYPE = 2;
    public static final int FIELD_ID_DRAWABLE = 3;
    public static final int FIELD_ID_VISIBLE = 4;

    public GameLevelData(Context ctx) {
        super(ctx);
    }

    public HashMap<Integer, ArrayList<Integer>> getLevelData() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] from = {_ID, NAME, TYPE, DRAWABLE, VISIBLE};
        Cursor cursor = db.query(TABLE_NAME, from, null, null, null, null, null);

        HashMap<Integer, ArrayList<Integer>> level = new HashMap<Integer, ArrayList<Integer>>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();

                arrayList.add(cursor.getInt(FIELD_ID_ID));
                arrayList.add(cursor.getInt(FIELD_ID_NAME));
                arrayList.add(cursor.getInt(FIELD_ID_TYPE));
                arrayList.add(cursor.getInt(FIELD_ID_DRAWABLE));
                arrayList.add(cursor.getInt(FIELD_ID_VISIBLE));

                level.put(cursor.getInt(FIELD_ID_ID), arrayList);
            }
            cursor.close();
        }

        db.close();

        return level;
    }
}