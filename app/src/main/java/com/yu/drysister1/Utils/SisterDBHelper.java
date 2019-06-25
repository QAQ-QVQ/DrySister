package com.yu.drysister1.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yu.drysister1.Bean.Sister;

import java.util.ArrayList;
import java.util.List;
//数据库操作类
public class SisterDBHelper {

    private static final String TAG = "SisterDBHelper";
    private static final String DB_NAME = "sister.db";  //数据库名
    private static final int DB_VERSION = 1;    //数据库版本号
    private String path = "/data/data/com.yu.drysister/databases/"+DB_NAME;//数据库路径
    private static SisterDBHelper dbHelper;
    private SQLiteDatabase db;
    private boolean flag = true;
    private SisterDBHelper() {
        db = SQLiteDatabase.openOrCreateDatabase(path,null);
        while (flag){
            onCreate(db);
            flag = false;
        }
    }
    //创建数据库
    private void onCreate(SQLiteDatabase db){
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
                + TableDefine.COLUMN_FULI_WHO + " TEXT "
                + ")";
        db.execSQL(createTableSql);
        Log.e(TAG,"数据库创建成功");
    }
    /** 单例 */
    public static SisterDBHelper getInstance() {
        if(dbHelper == null) {
            synchronized (SisterDBHelper.class) {
                if(dbHelper == null) {
                    dbHelper = new SisterDBHelper();
                }
            }
        }
        return dbHelper;
    }

    /** 插入一个妹子 */
    public void insertSister(List<Sister.ResultsBean> sister, int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableDefine.COLUMN_FULI_ID,sister.get(i).get_id());
        contentValues.put(TableDefine.COLUMN_FULI_CREATEAT,sister.get(i).getCreatedAt());
        contentValues.put(TableDefine.COLUMN_FULI_DESC,sister.get(i).getDesc());
        contentValues.put(TableDefine.COLUMN_FULI_PUBLISHEDAT,sister.get(i).getPublishedAt());
        contentValues.put(TableDefine.COLUMN_FULI_SOURCE,sister.get(i).getSource());
        contentValues.put(TableDefine.COLUMN_FULI_TYPE,sister.get(i).getType());
        contentValues.put(TableDefine.COLUMN_FULI_URL,sister.get(i).getUrl());
        contentValues.put(TableDefine.COLUMN_FULI_USED,sister.get(i).isUsed());
        contentValues.put(TableDefine.COLUMN_FULI_WHO,sister.get(i).getWho());
        db.insert(TableDefine.TABLE_FULI,null,contentValues);
        closeIO(null);
    }

    /** 插入一堆妹子(使用事务) */
    public void insertSisters(List<Sister.ResultsBean> sisters) {
        db.beginTransaction();
        try{
            for (Sister.ResultsBean sister: sisters) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TableDefine.COLUMN_FULI_ID,sister.get_id());
                contentValues.put(TableDefine.COLUMN_FULI_CREATEAT,sister.getCreatedAt());
                contentValues.put(TableDefine.COLUMN_FULI_DESC,sister.getDesc());
                contentValues.put(TableDefine.COLUMN_FULI_PUBLISHEDAT,sister.getPublishedAt());
                contentValues.put(TableDefine.COLUMN_FULI_SOURCE,sister.getSource());
                contentValues.put(TableDefine.COLUMN_FULI_TYPE,sister.getType());
                contentValues.put(TableDefine.COLUMN_FULI_URL,sister.getUrl());
                contentValues.put(TableDefine.COLUMN_FULI_USED,sister.isUsed());
                contentValues.put(TableDefine.COLUMN_FULI_WHO,sister.getWho());
                db.insert(TableDefine.TABLE_FULI,null,contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            if(db != null && db.isOpen()) {
                db.endTransaction();
                closeIO(null);
            }
        }
    }

    /** 删除妹子(根据_id) */
    public void deleteSister(String _id) {
        db.delete(TableDefine.TABLE_FULI,"_id =?",new String[]{_id});
        closeIO(null);
    }

    /** 删除所有妹子 */
    public void deleteAllSisters() {
        db.delete(TableDefine.TABLE_FULI,null,null);
        closeIO(null);
    }

    /** 更新妹子信息(根据_id) */
    public void updateSister(String _id,Sister.ResultsBean sister) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableDefine.COLUMN_FULI_ID,sister.get_id());
        contentValues.put(TableDefine.COLUMN_FULI_CREATEAT,sister.getCreatedAt());
        contentValues.put(TableDefine.COLUMN_FULI_DESC,sister.getDesc());
        contentValues.put(TableDefine.COLUMN_FULI_PUBLISHEDAT,sister.getPublishedAt());
        contentValues.put(TableDefine.COLUMN_FULI_SOURCE,sister.getSource());
        contentValues.put(TableDefine.COLUMN_FULI_TYPE,sister.getType());
        contentValues.put(TableDefine.COLUMN_FULI_URL,sister.getUrl());
        contentValues.put(TableDefine.COLUMN_FULI_USED,sister.isUsed());
        contentValues.put(TableDefine.COLUMN_FULI_WHO,sister.getWho());
        db.update(TableDefine.TABLE_FULI,contentValues,"_id =?",new String[]{_id});
        closeIO(null);
    }

    /** 查询当前表中有多少个妹子 */
    public int getSistersCount() {
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TableDefine.TABLE_FULI,null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        Log.v(TAG,"count：" + count);
        closeIO(cursor);
        return count;
    }

    /** 分页查询妹子，参数为当前页和每一个的数量，页数从0开始算 */
    public List<Sister.ResultsBean> getSistersLimit(int curPage,int limit) {
        List<Sister.ResultsBean> sisters = new ArrayList<>();
        String startPos = String.valueOf(curPage * limit);  //数据开始位置
        if(db != null) {
            Cursor cursor = db.query(TableDefine.TABLE_FULI,new String[] {
                    TableDefine.COLUMN_FULI_ID, TableDefine.COLUMN_FULI_CREATEAT,
                    TableDefine.COLUMN_FULI_DESC, TableDefine.COLUMN_FULI_PUBLISHEDAT,
                    TableDefine.COLUMN_FULI_SOURCE, TableDefine.COLUMN_FULI_TYPE,
                    TableDefine.COLUMN_FULI_URL, TableDefine.COLUMN_FULI_USED,
                    TableDefine.COLUMN_FULI_WHO,
            },null,null,null,null,TableDefine.COLUMN_ID,startPos + "," + limit);
            while (cursor.moveToNext()) {
                Sister.ResultsBean sister = new Sister.ResultsBean();
                sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
                sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
                sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
                sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
                sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
                sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
                sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
                sister.setUsed(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
                sisters.add(sister);
            }
            closeIO(cursor);
        }
        return sisters;
    }

    /** 查询所有妹子 */
    public List<Sister.ResultsBean> getAllSisters() {
        List<Sister.ResultsBean> sisters = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TableDefine.TABLE_FULI,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            Sister.ResultsBean sister = new Sister.ResultsBean();
            sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
            sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
            sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
            sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
            sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
            sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
            sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
            sister.setUsed(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
            sisters.add(sister);
        }
        closeIO(cursor);
        return sisters;
    }

//    /** 获得可写数据库的方法 */
//    private SQLiteDatabase getWritableDB() {
//        return sqlHelper.getWritableDatabase();
//    }
//
//    /** 获得可读数据库的方法 */
//    private SQLiteDatabase getReadableDB() {
//        return sqlHelper.getReadableDatabase();
//    }
//
    /** 关闭cursor和数据库的方法 */
    private void closeIO(Cursor cursor) {
        if(cursor != null) {
            cursor.close();
        }
        if(db != null) {
            db.close();
        }
    }
}
