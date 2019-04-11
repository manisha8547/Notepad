package com.example.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String TAG = Database.class.getSimpleName();
    private static DatabaseHandler sqlhelper = null;
    private static SQLiteDatabase database = null;
    private static Context context = null;


    private Database() {
    }

    /**
     * Initiates the Database for access
     *
     * @param context Application context
     */
    public static void initiate(Context context) {
        if (sqlhelper == null)
            sqlhelper = new DatabaseHandler(context);

        //if (Database.context == null)
        Database.context = context;
    }


    /**
     * Opens the database for reading
     *
     * @throws SQLException if the database cannot be opened for reading
     */
    public static void openReadable() throws SQLException {

        try {


            if (sqlhelper == null)
                sqlhelper = new DatabaseHandler(context);

            if (database == null)
                database = sqlhelper.getReadableDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the database for writing
     * Defaults to Foreign Keys Constraint ON
     *
     * @throws SQLException if the database cannot be opened for writing
     */
    public static void openWritable() throws SQLException {

        if (sqlhelper == null)
            sqlhelper = new DatabaseHandler(context);

        if ((database == null) ? true : database.isReadOnly()) {
            openWritable(true);
        }
    }

    public static void destroy(Context context) {

        sqlhelper.destroy(context);

    }

    /**
     * Opens the database for writing
     *
     * @param foreignKeys State of Foreign Keys Constraint, true = ON, false = OFF
     * @throws SQLException if the database cannot be opened for writing
     */
    public static void openWritable(boolean foreignKeys) throws SQLException {

        if (sqlhelper == null)
            sqlhelper = new DatabaseHandler(context);

        database = sqlhelper.getWritableDatabase();

        if (foreignKeys) {
            database.execSQL("PRAGMA foreign_keys = ON;");
        } else {
            database.execSQL("PRAGMA foreign_keys = OFF;");
        }
    }

    /**
     * Closes the database
     */
    public static void close() {
        if (database != null) {
            database.close();
            database = null;
        }
        if (sqlhelper != null) {
            sqlhelper.close();
            sqlhelper = null;
        }
    }


    public static void addToDoItem(ToDo toDo) {

        openWritable();

        sqlhelper.addToDoItem(toDo);

    }

    public static ToDo getlistData() {

        openReadable();

        return sqlhelper.getlistData();

    }

    public static List<ToDo> getList() {

        openReadable();

        return sqlhelper.getList("");

    }

    public static void deleteQuery(String tableName, String condition) {

        openWritable();

        sqlhelper.deleteQuery(tableName, condition);

    }

    public static List<ToDo> getDeletedTask() {

        openReadable();

        return sqlhelper.getDeletedTask();
    }

    public static int exeUpdateQuery(String employees, ContentValues values, String s) {

        openWritable();

        return sqlhelper.exeUpdateQuery(employees, values, s);

    }


}
