package com.bus.huyma.hbus.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper{
    String DB_PATH = null;
    private static String DB_NAME = "BusData.db";
    private final Context myContext ;
    private SQLiteDatabase myDataBase;

    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,1);
        this.myContext=context;
        DB_PATH="/data/data/"+context.getPackageName()+"/"+"databases/";
    }

    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if(dbExist){
            //do notthing
        }else{
            this.getReadableDatabase();
            try{
                copyDataBase();
            }catch(IOException e){
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB=SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        if(checkDB!= null){
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        //Mo csdl voi input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while((length = myInput.read(buffer))>0){
            myOutput.write(buffer,0,length);
        }

        //Close stream
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase()throws SQLiteException {
        //open database
        String myPath = DB_PATH + DB_NAME;
        myDataBase=SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close(){
        if(myDataBase!=null) myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor rawQuery(String sql, String[] selectionArgs){
        return myDataBase.rawQuery(sql,selectionArgs);
    }
}
