package tl.pojul.com.fastim.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static MySQLiteHelper mySQLiteHelper;
    private static String DATABASE_NAME = "fastim.db";
    private static int DATABASE_VERSION = 3;
    private static final String CREATE_CONVERSATION_TABLE = "create table conversation ("
            + "conversation_name varchar(20) not null, "
            + "conversation_from varchar(20) primary key, "
            + "conversation_owner varchar(20) not null, "
            + "conversation_photo varchar(80) not null, "
            + "conversation_last_chat varchar(80) not null, "
            + "unread_message int(4) not null default 0, "
            + "conversation_type int(4) not null default 0, "
            + "conversation_last_chattime varchar(20) not null)";

    private MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }
    public static MySQLiteHelper Instance(Context context) {
        if (mySQLiteHelper == null) {
            synchronized (MySQLiteHelper.class) {
                if (mySQLiteHelper == null) {
                    mySQLiteHelper = new MySQLiteHelper(context);
                }
            }
        }
        return mySQLiteHelper;
    }

    public static MySQLiteHelper getInstance() {
        return mySQLiteHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONVERSATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
