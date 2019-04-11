package com.example.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;



import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION    =   1;

    private static final String name    =   "todo_list.db";

    private static final String TAG = DatabaseHandler.class.getSimpleName();

    private Map<String, String[]> dbVersionUpdates = new HashMap<String, String[]>();
    private Map<String, String[]> dbVersionDeletes = new HashMap<String, String[]>();

    Gson gson = new Gson();


    public void setDbVersionDeletes() {

    }

    public void setDbVersionUpdates() {

        }

    String CREATE_TODO_TABLE     =   "CREATE TABLE IF NOT EXISTS toDo(" +
            " id INTEGER PRIMARY KEY," +
            " title TEXT," +
            " status INTEGER" +

            ")";

    public DatabaseHandler(Context context) {

        super(context, name, null, VERSION);
        setDbVersionUpdates();
        setDbVersionDeletes();

    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        setDbVersionUpdates();
        setDbVersionDeletes();
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        setDbVersionUpdates();
        setDbVersionDeletes();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {


            db.execSQL(CREATE_TODO_TABLE);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "******* ******* ***** upgrading database");
        Log.d(TAG, oldVersion + " ===> " + newVersion);

        try {
            if (newVersion != oldVersion) {

                if (dbVersionDeletes.size() > 0) {

                    Iterator it = dbVersionDeletes.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();

                        String tableName = (String) pair.getKey();

                        String[] columns = (String[]) pair.getValue();

                        for (int i = 0; i < columns.length; i++) {

                            try {
                                db.execSQL("ALTER TABLE " + tableName + " DROP COLUMN " + columns[i]);
                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        }

                        it.remove(); // avoids a ConcurrentModificationException
                    }

                }

                if (dbVersionUpdates.size() > 0) {

                    Iterator it = dbVersionUpdates.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();

                        String tableName = (String) pair.getKey();

                        String[] columns = (String[]) pair.getValue();

                        for (int i = 0; i < columns.length; i++) {

                            try {
                                db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columns[i]);
                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        }

                        it.remove(); // avoids a ConcurrentModificationException
                    }


                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public int getCount(String tableName, String condition) {

        int count = 0;

        if (!checkTableExists(tableName)) {
            createTable(tableName);
        }


        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT count(*) from " + tableName + " " + condition;

        Log.d(TAG,"query:"+selectQuery);

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
            }

        } catch (Exception e) {

            //Log.d("BackgroundProcessThread", "ERROR 1"+ e.toString());
            e.printStackTrace();

        }


        try {
            cursor.close();
        } catch (Exception ex) {
            //Log.d("BackgroundProcessThread", "ERROR "+ ex.toString());
        }

        return count;

    }



    public void destroy(Context context) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT name FROM sqlite_master WHERE type='table'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d(TAG, cursor.getCount() + " Total Tables");

        try {

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                do {

                    Log.d(TAG, "TABLE NAME => " + cursor.getString(0));

                    if (!cursor.getString(0).matches("sqlite_sequence")) {
                        try {
                            db.execSQL("DELETE FROM " + cursor.getString(0));
                            Log.d(TAG, "TABLE EMPTIED => " + cursor.getString(0));
                        } catch (Exception emptyError) {
                            Log.d(TAG, "TABLE EMPTIED FAILED => " + cursor.getString(0));
                            emptyError.printStackTrace();
                        }

                        try {
                            db.execSQL("DROP TABLE IF EXISTS '" + cursor.getString(0) + "'");
                            Log.d(TAG, "TABLE DROPPED => " + cursor.getString(0));
                        } catch (Exception ex) {
                            Log.d(TAG, "TABLE DROPPED FAILED => " + cursor.getString(0));
                            ex.printStackTrace();
                        }
                    }

                } while (cursor.moveToNext());

            }

            cursor.close();

        } catch (Exception e) {

            e.printStackTrace();

            try {
                cursor.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        try {
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public boolean checkTableExists(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount() > 0) {

                try {
                    cursor.close();
                } catch (Exception ex) {

                }

                try {
                    db.endTransaction();
                } catch (Exception endEx) {

                }

                return true;

            } else {

                createTable(tableName);

                try {
                    cursor.close();
                } catch (Exception ex) {

                }

                try {
                    db.endTransaction();
                } catch (Exception e) {

                }

                try {
                    db.endTransaction();
                } catch (Exception endEx) {

                }

                return false;

            }
        } catch (Exception e) {

            try {
                cursor.close();
            } catch (Exception ex) {

            }

            try {
                db.endTransaction();
            } catch (Exception endEx) {

            }

            return false;
        }
    }

    public int exeUpdateQuery(String tableName, ContentValues values, String condition) {


        int responseId = -1;

        if (!checkTableExists(tableName)) {
            createTable(tableName);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            responseId = db.update(tableName, values, condition, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            db.endTransaction();
        } catch (Exception endEx) {

        }

        return responseId;

    }

    public int deleteQuery(String tableName, String condition) {

        if (tableName.equals("kidsConnect")) {

            Log.d(TAG + ":KIDS", "************ DELETE");
            Log.d(TAG + ":KIDS", "KIDS CONNECT");
            Log.d(TAG + ":KIDS", condition);

        }

        int responseId = -1;

        if (!checkTableExists(tableName)) {
            createTable(tableName);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            responseId = db.delete(tableName, condition, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            db.endTransaction();
        } catch (Exception endEx) {

        }

        return responseId;

    }

    private void createTable(String tableName) {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            switch (tableName) {

                case "toDo":

                    db.execSQL(CREATE_TODO_TABLE);

                    break;

                    default:

                    break;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void addToDoItem(ToDo toDo) {

        try {

            if (!checkTableExists("toDo")) {
                createTable("toDo");
            }


            SQLiteDatabase db = this.getWritableDatabase();
            String sql =  " INSERT OR REPLACE INTO toDo ( title, status) VALUES (?,?);";

            SQLiteStatement statement = db.compileStatement(sql);

            db.beginTransaction();

            statement.clearBindings();


            statement.bindString(1, toDo.getTask());
            statement.bindLong(2, toDo.getStatus());


            Log.d("member data", ""+statement.toString());

            statement.execute();

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public List<ToDo> getList(String s) {

        if (!checkTableExists("toDo")) {
            createTable("toDo");
        }

        List<ToDo> toDoList = new ArrayList<ToDo>();

        SQLiteDatabase db = this.getReadableDatabase();

        String condition = "";

        String selectQuery = "SELECT id, title, status FROM toDo WHERE status = 0 ORDER BY id ASC ";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {

                do {

                    ToDo toDoObj = new ToDo();

                    toDoObj.setId(cursor.getInt(0));
                    toDoObj.setTask(cursor.getString(1));
                    toDoObj.setStatus(cursor.getInt(2));

                    toDoList.add(toDoObj);

                } while (cursor.moveToNext());

            }

        }

        try {
            cursor.close();
        } catch (Exception ex) {

        }

        try {
            db.endTransaction();
        } catch (Exception endEx) {

        }

        return toDoList;

    }


    public ToDo getlistData(){

        if (!checkTableExists("toDo")) {

            createTable("toDo");
        }

        ToDo toDo = null;

        SQLiteDatabase db = this.getReadableDatabase();

        String condition = "";

        String selectQuery  =   "SELECT  id, title, status FROM toDo ";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {

                do {

                    toDo = new ToDo();

                    toDo.setId(cursor.getInt(0));
                    toDo.setTask(cursor.getString(1));
                    toDo.setStatus(cursor.getInt(2));

                } while (cursor.moveToNext());

            }

        }
        try {
            cursor.close();
        } catch (Exception ex) {

        }

        try {
            db.endTransaction();
        } catch (Exception endEx) {

        }

        return toDo;

    }

    public List<ToDo> getDeletedTask() {

        if (!checkTableExists("toDo")) {
            createTable("toDo");
        }

        String condition = "where status = "+1;


        List<ToDo> toDoList = new ArrayList<ToDo>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = " SELECT  id ,title, status FROM toDo "+ condition +" ORDER BY id ASC" ;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {

                do {

                    ToDo toDo = new ToDo();
                    toDo.setId(cursor.getInt(0));
                    toDo.setTask(cursor.getString(1));
                    toDo.setStatus(cursor.getInt(2));

                    toDoList.add(toDo);

                } while (cursor.moveToNext());

            }

        }
        try {
            cursor.close();
        } catch (Exception ex) {

        }

        try {
            db.endTransaction();
        } catch (Exception endEx) {

        }
        return toDoList;
    }




}
