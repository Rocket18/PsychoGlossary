package com.psychoglossary.psychoglossary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Rocke on 10/11/2015.
 */
public  class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TERM_NAME = "Dictionary";
    public static final String ID = BaseColumns._ID;
    public static final String TERM = "term";
    public static final String DESC = "description";
    public static final String SUB = "subject";
    public static final String SUB_NAME = "Subjects";
    public static final String S_ID = BaseColumns._ID;
    public static final String S_NAME = "subname";
    private static final String DATABASE_NAME = "dict.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TERM_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TERM + " TEXT NOT NULL,"
                + DESC + " TEXT,"
                + SUB + " TEXT"
                + ");");

        db.execSQL("create table " + SUB_NAME + " ("
                + S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + S_NAME + " TEXT NOT NULL"
                + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TERM_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + SUB_NAME + ";");
        onCreate(db);
    }
    @SuppressLint("NewApi")
    private void validateData(ContentValues values) throws DatabaseException{
        if(!values.containsKey(S_NAME)){
            if(!values.containsKey(TERM) || values.getAsString(TERM) == null || values.getAsString(TERM).isEmpty())
                throw new DatabaseException("Назву не вказано!");
            if(!values.containsKey(DESC) || values.getAsString(DESC) == null || values.getAsString(DESC).isEmpty())
                throw new DatabaseException("Визначення не вказано!");
            if(!values.containsKey(SUB) || values.getAsString(SUB) == null || values.getAsString(SUB).isEmpty())
                throw new DatabaseException("Категорію не вказано!");
        }
        else if (!values.containsKey(S_NAME) || values.getAsString(S_NAME) == null || values.getAsString(S_NAME).isEmpty())
            throw new DatabaseException("Категорію не вказано!");
    }
    public long insert(String tableName, ContentValues values) throws DatabaseException{
        validateData(values);
        return getWritableDatabase().insert(tableName, null, values);
    }
    public int update(String tableName, long id, ContentValues values) throws DatabaseException {
        validateData(values);
        String selection = ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return getWritableDatabase().update(tableName, values, selection, selectionArgs);
    }
    public int delete(String tableName, long id) {
        String selection = ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return getWritableDatabase().delete(tableName, selection, selectionArgs);
    }
    public int delete(String tableName, String subject) {
        String selection = SUB + " = ?";
        String[] selectionArgs = {subject};
        return getWritableDatabase().delete(tableName, selection, selectionArgs);
    }
    public Cursor query(String tableName, String subject, String orderedBy) {
        String[] projection = {ID, TERM, DESC, SUB};
        String selection = SUB + " like ?";
        String[] selectionArgs = {subject};
        return getReadableDatabase().query(tableName, projection, selection, selectionArgs, null, null, orderedBy);
    }
    public Cursor subquery(String tableName, String orderedBy) {
        String[] projection = {S_ID, S_NAME};
        return getReadableDatabase().query(tableName, projection, null, null, null, null, orderedBy);
    }
    public Cursor search(String tableName, String column, String value, String subject,  String orderBy){
        return getReadableDatabase().rawQuery("select * from " + tableName+ " where " + column + " like '%" + value + "%' and subject like '%" + subject + "%' order by " + orderBy, null);
    }
    public static class DatabaseException extends Throwable {

        private static final long serialVersionUID = 1L;

        public DatabaseException(String detailMessage) {
            super(detailMessage);
        }
    }
}
