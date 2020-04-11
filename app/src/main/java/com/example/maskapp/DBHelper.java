package com.example.maskapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    Context context;

    public DBHelper(Context context){
        super(context, "MaskDB", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* 이름(name), 남은수량(remain_stat), 주소(addr), 입고시간(stock_at), 위도(lat), 경도(lng) */
        db.execSQL("CREATE TABLE MaskTBL (name CHAR(30) PRIMARY KEY, remain STRING, address STRING, stock STRING, lat DOUBLE, lng DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS MaskTBL");
        onCreate(db);
    }
}
