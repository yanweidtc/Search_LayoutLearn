package scut.carson_ho.george;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/1/18.
 */

public class WeiSQLiteOpenHelper extends SQLiteOpenHelper {

    private static String name = "wei.db";
    private static Integer version = 1;

    public WeiSQLiteOpenHelper (Context context){
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 打开数据库 & 建立了一个叫weirecords的表， 里面只有一列name来存储历史记录：
        db.execSQL("create table weirecords(id integer primary key autoincrement,name varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
