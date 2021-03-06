package com.apps.amit.lawofattraction.sqlitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apps.amit.lawofattraction.utils.PrivateWishesUtils;

import java.util.ArrayList;
import java.util.List;

public class WishDataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "privateWish";
    private static final String TABLE_WISH = "myPrivateWish";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_WISH = "wish";
    private static final String KEY_DATE = "date";

    public WishDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_WISH + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_WISH + " TEXT,"
                + KEY_DATE + " TEXT" + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISH);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addWish(PrivateWishesUtils wishDB) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, wishDB.getUserName()); // User Name
        values.put(KEY_WISH, wishDB.getUserWish()); // User Wish
        values.put(KEY_DATE, wishDB.getUserDate()); // User Date

        // Inserting Row
        db.insert(TABLE_WISH, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all contacts in a list view
    public List<PrivateWishesUtils> getAllWishes() {

        List<PrivateWishesUtils> wishList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WISH;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
             cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PrivateWishesUtils wishDB = new PrivateWishesUtils();
                    wishDB.setUserName(cursor.getString(1));
                    wishDB.setUserWish(cursor.getString(2));
                    wishDB.setUserDate(cursor.getString(3));
                    // Adding contact to list
                    wishList.add(wishDB);
                } while (cursor.moveToNext());
            }
        } finally {

            if(cursor!=null) {
                cursor.close();
            }
        }


        // return contact list
        return wishList;

    }

    public void removeDuplicates() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "DELETE FROM "+TABLE_WISH+" WHERE "
                +KEY_ID+" NOT IN ( SELECT MIN("+KEY_ID+") FROM "+TABLE_WISH+" GROUP BY "+KEY_DATE+")";

        db.execSQL(selectQuery);

        /*
       DELETE FROM myPrivateWish
WHERE id NOT IN (
  SELECT MIN(id)
  FROM myPrivateWish
  GROUP BY name
)

        */
    }
}
