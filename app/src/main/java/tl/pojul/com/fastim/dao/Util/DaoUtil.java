package tl.pojul.com.fastim.dao.Util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pojul.fastIM.entity.BaseEntity;
import com.pojul.objectsocket.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.dao.MySQLiteHelper;

public class DaoUtil {

    private static final String TAG = "DaoUtil";

    public static void closeDb(SQLiteDatabase db, Cursor cursor) {
        try {
            if(cursor != null) {
                cursor.close();
            }
            if(db != null) {
                db.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.i(TAG, e.toString());
        }
    }

    public static <T> List<T> executeQuery(String sql, Class<?> c){
        SQLiteDatabase db = MySQLiteHelper.getInstance().getWritableDatabase();
        if(db == null) {
            return null;
        }
        List<T> entitys = new ArrayList<T>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while(cursor.moveToNext()){
                BaseEntity entity = (BaseEntity) c.newInstance();
                entity.setBySql(cursor);
                entitys.add((T) entity);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }finally {
            closeDb(db, cursor);
        }
        return entitys;
    }

    public static int executeUpdate(String sql, boolean lastInsert) {
        SQLiteDatabase db = MySQLiteHelper.getInstance().getWritableDatabase();
        if(db == null) {
            return -1;
        }
        try {
            db.execSQL(sql);
            if(lastInsert){
                return getLastInsertId(db);
            }else{
                return 0;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.i(TAG, e.toString());
            //e.printStackTrace();
            return -1;
        }finally {
            closeDb(db, null);
        }
    }

    public static int getLastInsertId(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select last_insert_rowid() ", null);
        int lastInsertId = 0;
        if (cursor.moveToFirst()){
            lastInsertId = cursor.getInt(0);
        }
        return lastInsertId;
    }
}
