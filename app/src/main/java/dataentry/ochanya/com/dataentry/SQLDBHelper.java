package dataentry.ochanya.com.dataentry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ephraim on 1/10/2020.
 */

public class SQLDBHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME="dataentry2.db";
    private static int DATABASE_VERSION=1;
    private SQLiteDatabase db;

    String createUsers="create table users(user_id TEXT PRIMARY KEY, designation_id TEXT, designation_type TEXT, " +
            "class TEXT, fullname TEXT, gender TEXT, disability TEXT, parent_guide_name TEXT, parent_guide_phone TEXT, " +
            "dob TEXT, phone TEXT, shop_number TEXT, address TEXT, bvn TEXT, acct_name TEXT, " +
            "acct_number TEXT, business_type TEXT, nok_name TEXT, nok_number TEXT, agent_id TEXT, reg_date TEXT, user_image BLOB, role TEXT)";

    String createLogin="create table login(username TEXT PRIMARY KEY, password TEXT, role TEXT, status TEXT, designation TEXT, designation_id TEXT)";

    String createSync="create table sync(serial TEXT PRIMARY KEY, sync_date TEXT, status TEXT)";

    String createMessages="create table messages(sn INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, message TEXT, " +
            "sender TEXT, receiver TEXT, datet TIMESTAMP, readstatus TEXT, solvedstatus TEXT, msgstatus TEXT)";

    public static String USER_TABLE="users";
    public static String USER_LOGIN="login";
    public static String USER_SYNC="sync";
    public static String MESSAGES="messages";

    public SQLDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db=sqLiteDatabase;
        sqLiteDatabase.execSQL(createUsers);
        sqLiteDatabase.execSQL(createLogin);
        sqLiteDatabase.execSQL(createSync);
        sqLiteDatabase.execSQL(createMessages);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + USER_TABLE);
        sqLiteDatabase.execSQL("drop table" + USER_LOGIN);
        sqLiteDatabase.execSQL("drop table" + USER_SYNC);
        sqLiteDatabase.execSQL("drop table" + MESSAGES);
        onCreate(sqLiteDatabase);
    }
}
