package com.yu.drysister.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;

public class SisterOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "sister.db";  //数据库名
    private static final int DB_VERSION = 1;    //数据库版本号
    private static final String TAG = "SisterDBHelper";
    private static String path = "/data/data/com.yu.drysister/databases/"+DB_NAME;//数据库路径
    public SisterOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
           String createTableSql = "CREATE TABLE IF NOT EXISTS " + TableDefine.TABLE_FULI
                   + " ( "
                   + TableDefine.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + TableDefine.COLUMN_FULI_ID + " TEXT, "
                   + TableDefine.COLUMN_FULI_CREATEAT + " TEXT, "
                   + TableDefine.COLUMN_FULI_DESC + " TEXT, "
                   + TableDefine.COLUMN_FULI_PUBLISHEDAT + " TEXT, "
                   + TableDefine.COLUMN_FULI_SOURCE + " TEXT, "
                   + TableDefine.COLUMN_FULI_TYPE + " TEXT, "
                   + TableDefine.COLUMN_FULI_URL + " TEXT, "
                   + TableDefine.COLUMN_FULI_USED + " BOOLEAN, "
                   + TableDefine.COLUMN_FULI_WHO + " TEXT ,"
                   + TableDefine.COLUMN_FULI_FLAG + " TEXT "
                   + ")";
           db.execSQL(createTableSql);
           Log.e(TAG,"数据库创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
