package com.yu.drysister.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yu.drysister.APP.okAPP;
import com.yu.drysister.Bean.ResultsBean;
import com.yu.drysister.Bean.Sister;

import java.util.ArrayList;
import java.util.List;
//数据库操作类
public class SisterDBHelper {
    private static final String TAG = "SisterDBHelper";
    private static SisterDBHelper dbHelper;
    private SisterOpenHelper sqlHelper;
    private SQLiteDatabase db;

    private SisterDBHelper() {
        sqlHelper = new SisterOpenHelper(okAPP.getContext());
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

    /**
     * 插入一个妹子
     * @param sister 妹子
     * @param i 索引
     * @param flag 收藏还是点赞
     * @return
     */
    public void insertSister(List<ResultsBean> sister, int i, String flag) {
        if (!getSisterFlagId(flag,sister.get(i).get_id())){
            db = getWritableDB();
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
            contentValues.put(TableDefine.COLUMN_FULI_FLAG,flag);
            db.insert(TableDefine.TABLE_FULI,null,contentValues);
            closeIO(null);
        }
    }


    /**
     * 插入一堆妹子(使用事务)
     * @param sisters 妹子
     * @param flag 收藏还是点赞
     * @return
     */
    public void insertSisters(List<ResultsBean> sisters,int flag) {
        db = getWritableDB();
        db.beginTransaction();
        try{
            for (ResultsBean sister: sisters) {
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
                contentValues.put(TableDefine.COLUMN_FULI_FLAG,flag);
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


    /**
     * 删除妹子(根据_id)
     * @param _id
     * @return
     */
    public void deleteSisterById(String _id) {
        db = getWritableDB();
        db.delete(TableDefine.TABLE_FULI,TableDefine.COLUMN_FULI_ID + " =?" ,new String[]{_id});
        closeIO(null);
    }

    /**
     * 删除妹子(根据flag,_id)
     * @param flag
     * @param _id
     * @return
     */
    public void deleteSisterByFlagId(String flag,String _id) {
        if(getSisterFlagId(flag,_id)){
            db = getWritableDB();
            db.delete(TableDefine.TABLE_FULI,TableDefine.COLUMN_FULI_FLAG + " =? AND "+ TableDefine.COLUMN_FULI_ID + " =? ",new String[]{flag,_id});
            closeIO(null);
        }
    }

    /**
     * 删除所有妹子
     * @return
     */
    public void deleteAllSisters() {
        db = getWritableDB();
        db.delete(TableDefine.TABLE_FULI,null,null);
        closeIO(null);
    }

    /**
     * 更新妹子信息(根据_id)
     * @param _id
     * @param sister
     * @param flag
     * @return
     */
    public void updateSister(String _id,ResultsBean sister,int flag) {
        db = getWritableDB();
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
        contentValues.put(TableDefine.COLUMN_FULI_FLAG,flag);
        db.update(TableDefine.TABLE_FULI,contentValues,"_id =?",new String[]{_id});
        closeIO(null);
    }
    /**
     *
     * 查询妹子信息(根据flag,_id)
     * @param flag
     * @param _id
     * @return 是否存在
     */
    public boolean getSisterFlagId(String flag,String _id){
        db = getReadableDB();
        List<ResultsBean> sisters = new ArrayList<>();
        if (db!=null){
            Cursor cursor = db.rawQuery("SELECT * FROM " + TableDefine.TABLE_FULI + " WHERE " + TableDefine.COLUMN_FULI_FLAG + " =? " +" AND " + TableDefine.COLUMN_FULI_ID  + " =? ",new String[]{flag,_id});
            while (cursor.moveToNext()) {
              return  true;
            }
            closeIO(cursor);
        }
       return false;
    }
    /**
     *
     * 查询妹子信息(根据flag)
     * @param flag
     * @return 是否存在
     */
    public List<ResultsBean> getSisterFlag(String flag){
        db = getReadableDB();
        List<ResultsBean> sisters = new ArrayList<>();
        if (db!=null){
            Cursor cursor = db.rawQuery("SELECT * FROM " + TableDefine.TABLE_FULI + " WHERE " + TableDefine.COLUMN_FULI_FLAG + " =? ",new String[]{flag});
            while (cursor.moveToNext()) {
                ResultsBean sister = new ResultsBean();
                sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
                sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
                sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
                sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
                sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
                sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
                sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
                sister.setUsed(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
                sister.setWho(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_WHO)));
                sisters.add(sister);
            }
            closeIO(cursor);
        }
        return sisters;
    }
    /**
     *  查询当前表中有多少个妹子
     * @return
     */
    public int getSistersCount() {
        db = getReadableDB();
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TableDefine.TABLE_FULI,null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        Log.v(TAG,"count：" + count);
        closeIO(cursor);
        return count;
    }


    /**
     * 分页查询妹子，参数为当前页和每一个的数量，页数从0开始算
     * @param curPage 当前页
     * @param limit 数量
     * @param flag 标识
     * @return
     */
    public List<ResultsBean> getSistersLimit(int curPage,int limit,String flag) {
        db = getReadableDB();
        List<ResultsBean> sisters = new ArrayList<>();
        String startPos = String.valueOf(curPage * limit);  //数据开始位置
        if(db != null) {
            /**
             * table="表命",
             * columns="要查询的列名",
             * selection="查询 条件",
             * selectionArgs="条件中用了占位符的参数",
             * groupBy="数据分组",
             * having="分组后的条件",
             * orderBy="排序方式",
             * limit="分页查询"; 
             **/
            Cursor cursor = db.query(TableDefine.TABLE_FULI,new String[] {
                    TableDefine.COLUMN_FULI_ID, TableDefine.COLUMN_FULI_CREATEAT,
                    TableDefine.COLUMN_FULI_DESC, TableDefine.COLUMN_FULI_PUBLISHEDAT,
                    TableDefine.COLUMN_FULI_SOURCE, TableDefine.COLUMN_FULI_TYPE,
                    TableDefine.COLUMN_FULI_URL, TableDefine.COLUMN_FULI_USED,
                    TableDefine.COLUMN_FULI_WHO,
            },TableDefine.COLUMN_FULI_FLAG+" =?",new String[]{flag},null,null,TableDefine.COLUMN_ID,startPos + "," + limit);//"5,9",第6行开始,返回9行数据
            while (cursor.moveToNext()) {
                ResultsBean sister = new ResultsBean();
                sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
                sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
                sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
                sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
                sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
                sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
                sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
                sister.setUsed(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
                sister.setWho(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_WHO)));
                sisters.add(sister);
            }
            closeIO(cursor);
        }
        return sisters;
    }


    /**
     * 查询所有妹子
     * @return List<Sister.ResultsBean>
     */
    public List<ResultsBean> getAllSisters() {
        db = getReadableDB();
        List<ResultsBean> sisters = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TableDefine.TABLE_FULI,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            ResultsBean sister = new ResultsBean();
            sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
            sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
            sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
            sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
            sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
            sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
            sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
            sister.setUsed(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
            sister.setWho(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_WHO)));
            sisters.add(sister);
        }
        closeIO(cursor);
        return sisters;
    }

    /** 获得可写数据库的方法 */
    private SQLiteDatabase getWritableDB() {
        return sqlHelper.getWritableDatabase();
    }

    /** 获得可读数据库的方法 */
    private SQLiteDatabase getReadableDB() {
        return sqlHelper.getReadableDatabase();
    }
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
