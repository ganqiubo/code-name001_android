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
            + "conversation_from varchar(20) , "
            + "conversation_owner varchar(20) not null, "
            + "conversation_photo varchar(80) not null, "
            + "conversation_last_chat varchar(180) not null, "
            + "unread_message int(4) not null default 0, "
            + "conversation_type int(4) not null default 0, " // 1: 好友聊天消息; 2: 群组聊天消息; 3: 标签消息回复【公开】; 4:标签消息回复【私密】
            + "conversation_uid varchar(40) not null default '', "
            + "conversation_last_chattime varchar(20) not null)";

    private static final String CREATE_UPLOAD_PIC_TABLE = "create table upload_pic("
            + "id integer not null primary key AUTOINCREMENT, " //comment '上传图片id', "
            + "user_name char(15) not null, " // COMMENT '用户名', "
            + "upload_pic_type int(4) not null, " // COMMENT '上传图片类型：1 普通图片; 2 位置图片', "
            + "is_delete int(4) not null default 1, " // COMMENT '是否已删除：1: 未被删除; 2: 已被删除', "
            + "uplod_pic_theme char(15), " // COMMENT '图片主题', "
            + "uplod_pic_label varchar(100), " // COMMENT '图片标签', "
            + "upload_pic_country char(15), " // COMMENT '所在国家', "
            + "upload_pic_province char(15), " // COMMENT '所在省份', "
            + "upload_pic_city char(15), " // COMMENT '所在城市', "
            + "upload_pic_district char(15), " // COMMENT '所在县/区', "
            + "upload_pic_addr char(15), " // COMMENT '所在详细地址', "
            + "upload_pic_longitude char(15), " // COMMENT '经度', "
            + "upload_pic_latitude char(15), " // COMMENT '纬度', "
            + "upload_pic_altitude char(15), " // COMMENT '海拔', "
            + "upload_pic_locnote char(15), " // COMMENT '位置描述', "
            + "upload_pic_time char(20) not null, " // COMMENT '图片上传日期', "
            + "pic_time char(20), " // COMMENT '图片拍摄日期,暂不做处理', "
            + "pic_loc_type int(4), " // COMMENT '位置类型：0: 未知; 1 GPS; 2 网络', "
            + "pic_loc_upload_status int(4), " // COMMENT '位置类型：0: 未上传; 1 上传成功; 2 上传失败', "
            + "upload_pic_locshow int(4))"; //COMMENT '显示类型：1 模糊显示; 2 精确显示'

    private static final String CREATE_PIC_TABLE = "create table pic("
            + "id integer not null primary key AUTOINCREMENT, " // COMMENT '图片id', "
            + "upload_pic_id integer not null, " // COMMENT '上传图片id', "
            + "upload_pic_url varchar(120) not null, " // COMMENT '图片地址', "
            + "is_delete int(4) not null default 1)"; //COMMENT '是否已删除：1: 未被删除; 2: 已被删除')";

    /*private static final String CREATE_ITEM_PRIV_USERS = "create table item_priv_users ("
            + "from_name varchar(20) not null, "
            + "from_nick_name varchar(20) not null, "
            + "to_name varchar(20) , "
            + "tag_mess_uid varchar(40) not null, "
            + "sex int(4) not null, "
            + "age int(4) not null, "
            + "unread_message int(4) not null default 0, "
            + "photo varchar(120) not null default '', "
            + "conversation_last_chattime varchar(20) not null)";*/

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
        db.execSQL(CREATE_UPLOAD_PIC_TABLE);
        db.execSQL(CREATE_PIC_TABLE);
        //db.execSQL(CREATE_ITEM_PRIV_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
