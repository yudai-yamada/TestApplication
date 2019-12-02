package com.example.testapplication;

import java.lang.reflect.Array;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    static final String DATABASE_NAME = "result.db";
    static final int DATABASE_VERSION = 1;


    public static final String TABLE_NAME = "result";
    public static final String COL_ID = "_id";
    public static final String COL_DATE = "DATE";
    public static final String COL_MACINE = "MACINE";
    public static final String COL_INN_AMT = "INN_AMT";
    public static final String COL_OUT_AMT = "OUT_AMT";
    public static final String COL_HOLE = "HOLE";
    public static final String COL_LASTUPDATE = "lastupdate";
    public static final String COL_TOTAL = "TOTAL";
    public static final String COL_COUNT = "COUNT";
    public static final String COL_YEAR = "YEAR";

    protected final Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public DBAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }


    //機種ＤＢ
    public static final String MC_TABLE_NAME = "machine";
    public static final String COL_KIND = "KIND";


    //
    // SQLiteOpenHelper
    //

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_NAME + " ("
                            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COL_DATE + " TEXT NOT NULL,"
                            + COL_MACINE + " INTEGER NOT NULL,"
                            + COL_INN_AMT + " INTEGER NOT NULL,"
                            + COL_OUT_AMT + " INTEGER NOT NULL,"
                            + COL_HOLE + " TEXT,"
                            + COL_LASTUPDATE + " TEXT NOT NULL);");
            db.execSQL(
                    "CREATE TABLE " + MC_TABLE_NAME + " ("
                            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COL_MACINE + " TEXT NOT NULL ,"
                            + COL_KIND + " INTEGER NOT NULL ,"
                            + COL_LASTUPDATE + " TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(
                SQLiteDatabase db,
                int oldVersion,
                int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MC_TABLE_NAME);
            onCreate(db);
        }

    }

    //
    // Adapter Methods
    //

    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }


    public void close(){
        dbHelper.close();
    }


    //
    // App Methods
    //


    public boolean deleteAllNotes(){
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    public boolean deleteNote(String id){
        return db.delete(TABLE_NAME, "_id = ?", new String[]{id}) > 0;
    }

    public Cursor getAllNotes(){
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor getDayNotes(String str){
        return db.query(TABLE_NAME, null, "DATE = ?", new String[]{ "" + str },  null, null, null);
    }


    public Cursor getMonthResult(String str){
        return db.rawQuery("SELECT "+ COL_DATE + ",SUM(" + COL_INN_AMT + ") ,SUM(" +COL_OUT_AMT + ") FROM " + TABLE_NAME + " WHERE " + COL_DATE + " LIKE '"+ str +"%' GROUP BY "+ COL_DATE,null);
    }

    public  Cursor getMonthTotal(String str){
        return db.rawQuery("SELECT SUM(" + COL_INN_AMT + ") ,SUM(" +COL_OUT_AMT + ") FROM " + TABLE_NAME + " WHERE " + COL_DATE + " LIKE '"+ str +"%'",null);
    }

    public  Cursor getMachineName(int id) {
        String str = "SELECT "+ COL_MACINE + ", " + COL_KIND + " FROM " + MC_TABLE_NAME + " WHERE "+ COL_ID + " = "+ id;
        return db.rawQuery(str,null);
    }

    public Cursor getALLMacine(){
        String str = "SELECT * FROM " + MC_TABLE_NAME;
        return db.rawQuery(str,null);
    }

    public Cursor getPCMachine(){
        String str = "SELECT * FROM " + MC_TABLE_NAME + " WHERE " + COL_KIND + " = 0";
        return db.rawQuery(str,null);
    }
    public Cursor getSLMachine(){
        String str = "SELECT * FROM " + MC_TABLE_NAME + " WHERE " + COL_KIND + " = 1";
        return db.rawQuery(str,null);
    }
    //機種毎結果取得
    //kind = 0:パチンコ,　=1:パチスロ
    public Cursor getResultByMachine(int kind){
        String CON = "";
        if (kind == 0){
            CON = "WHERE b.kind = 0";
        }else if (kind == 1){
            CON = "WHERE b.kind = 1";
        }
        String str = "SELECT count(a.MACINE) COUNT,b.MACINE,b.KIND,SUM(out_AMT) - SUM(INN_AMT) TOTAL,SUM(out_AMT) OUT_AMT,SUM(INN_AMT) INN_AMT from result a left join machine b on a.MACINE = b._id " + CON + " GROUP by a.MACINE";
        return db.rawQuery(str,null);
    }

    //年別結果取得
    public  Cursor getYearResult(){
        String str = "select substr(date,1,4) YEAR, count(INN_AMT) COUNT,SUM(OUT_AMT) - SUM(INN_AMT) TOTAL, SUM(INN_AMT) INN_AMT, SUM(OUT_AMT) OUT_AMT from result GROUP BY YEAR";
        return db.rawQuery(str,null);
    }

    //結果更新処理
    public  void updateResult(String date,String macine, int inn_amt, int out_amt, String hole,int kind,int id){

        String str = "SELECT "+ COL_ID + " FROM " + MC_TABLE_NAME + " WHERE "+ COL_MACINE + " = '"+ macine +"' AND " + COL_KIND + " = " + kind;
        Cursor c = db.rawQuery(str,null);

        //機種登録済みの場合は結果を更新
        if (c.getCount() > 0){
            //結果ＤＢを更新
            Date dateNow = new Date ();
            ContentValues values = new ContentValues();
            if(c.moveToFirst()){
                do {
                    values.put(COL_ID, id);
                    values.put(COL_DATE, date);
                    values.put(COL_MACINE, c.getInt(c.getColumnIndex(DBAdapter.COL_ID)));
                    values.put(COL_INN_AMT, inn_amt);
                    values.put(COL_OUT_AMT, out_amt);
                    values.put(COL_HOLE, hole);
                    values.put(COL_LASTUPDATE, dateNow.toLocaleString());
                }while(c.moveToNext());
            }
            db.replaceOrThrow(TABLE_NAME, null, values);

            //機種が未登録の場合は機種登録後に結果を更新
        }else {
            //機種ＤＢに登録
            Date dateNow = new Date ();
            ContentValues MCvalues = new ContentValues();
            MCvalues.put(COL_MACINE, macine);
            MCvalues.put(COL_KIND, kind);
            MCvalues.put(COL_LASTUPDATE, dateNow.toLocaleString());
            db.insertOrThrow(MC_TABLE_NAME, null, MCvalues);

            //結果ＤＢを更新
            Cursor c2 = db.rawQuery(str,null);
            ContentValues values = new ContentValues();
            if(c2.moveToFirst()){
                do {
                    values.put(COL_ID, id);
                    values.put(COL_DATE, date);
                    values.put(COL_MACINE, c2.getInt(c2.getColumnIndex(DBAdapter.COL_ID)));
                    values.put(COL_INN_AMT, inn_amt);
                    values.put(COL_OUT_AMT, out_amt);
                    values.put(COL_HOLE, hole);
                    values.put(COL_LASTUPDATE, dateNow.toLocaleString());
                }while(c2.moveToNext());
            }
            db.replaceOrThrow(TABLE_NAME, null, values);

        }
    }

    //結果登録処理
    public void saveResult(String date,String macine, int inn_amt, int out_amt, String hole,int kind){

        String str = "SELECT "+ COL_ID + " FROM " + MC_TABLE_NAME + " WHERE "+ COL_MACINE + " = '"+ macine +"' AND " + COL_KIND + " = " + kind;
        Cursor c = db.rawQuery(str,null);

        //機種登録済みの場合は結果を登録
        if (c.getCount() > 0){
            //結果ＤＢに登録
            Date dateNow = new Date ();
            ContentValues values = new ContentValues();
            if(c.moveToFirst()){
                do {
                    values.put(COL_DATE, date);
                    values.put(COL_MACINE, c.getInt(c.getColumnIndex(DBAdapter.COL_ID)));
                    values.put(COL_INN_AMT, inn_amt);
                    values.put(COL_OUT_AMT, out_amt);
                    values.put(COL_HOLE, hole);
                    values.put(COL_LASTUPDATE, dateNow.toLocaleString());
                }while(c.moveToNext());
            }
            db.insertOrThrow(TABLE_NAME, null, values);

        //機種が未登録の場合は機種登録後に結果を登録
        }else {
            //機種ＤＢに登録
            Date dateNow = new Date ();
            ContentValues MCvalues = new ContentValues();
            MCvalues.put(COL_MACINE, macine);
            MCvalues.put(COL_KIND, kind);
            MCvalues.put(COL_LASTUPDATE, dateNow.toLocaleString());
            db.insertOrThrow(MC_TABLE_NAME, null, MCvalues);

            //結果ＤＢに登録
            Cursor c2 = db.rawQuery(str,null);
            ContentValues values = new ContentValues();
            if(c2.moveToFirst()){
                do {
                    values.put(COL_DATE, date);
                    values.put(COL_MACINE, c2.getInt(c2.getColumnIndex(DBAdapter.COL_ID)));
                    values.put(COL_INN_AMT, inn_amt);
                    values.put(COL_OUT_AMT, out_amt);
                    values.put(COL_HOLE, hole);
                    values.put(COL_LASTUPDATE, dateNow.toLocaleString());
                }while(c2.moveToNext());
            }
            db.insertOrThrow(TABLE_NAME, null, values);

        }




    }


}
