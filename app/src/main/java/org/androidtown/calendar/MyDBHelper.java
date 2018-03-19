package org.androidtown.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by changjun on 2017-06-05.
 */

public class MyDBHelper extends SQLiteOpenHelper{

    public MyDBHelper(Context context) {
        super(context, "myCalendar.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table myCalendar(CalendarDate char(10) primary key, content varchar(500));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
