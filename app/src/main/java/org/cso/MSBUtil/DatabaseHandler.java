package org.cso.MSBUtil;

/**
 * Created by admin on 2/17/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Thirumalaivelu on 23-12-2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";
    Context context;
    // Logger log = CLogger.getInstance().getLogger(DatabaseHandler.class);

    /*************************************
     * CREATE DB (CONSTRUCTER)
     *********************************************/
    public DatabaseHandler(Context context, String DATABASE_NAME, int DATABASE_VERSION) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /************************************************************************************************************/
    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub

    }

    /************************************************************************************************************/
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    /***************************************
     * CREATE TABLE
     *********************************************************/
    public void createTable(String TABLE_NAME, Map<String, String> COLUMN_NAME_TYPES) {

        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sb = new StringBuilder();
        String QUERY = null;

        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        Iterator<Map.Entry<String, String>> it = COLUMN_NAME_TYPES.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, String> row = (Map.Entry<String, String>) it.next();
            String KEY = row.getKey();
            String VALUE = row.getValue();

            if (it.hasNext()) {
                sb.append("`" + KEY + "` " + VALUE + ", ");
            } else {
                sb.append("`" + KEY + "` " + VALUE);
            }

        }
        sb.append(")");

        QUERY = sb.toString();
        db.execSQL(QUERY);
    }

    /***************************************
     * CREATE TABLE
     *********************************************************/
    public void createTable(String sqlquery) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlquery);
    }

    /******************************************
     * POPULATE TABLE
     ****************************************************/
    public void populateTable(String TABLE_NAME, List<Map<String, Object>> COLUMN_NAME_VALUE) {

        SQLiteDatabase db = this.getWritableDatabase();

        Iterator<Map<String, Object>> list_of_rows_it = COLUMN_NAME_VALUE.iterator();
        while (list_of_rows_it.hasNext()) {
            //ContentValues values = new ContentValues();
            Map<String, Object> row_map = list_of_rows_it.next();
            StringBuilder insertq1_col = new StringBuilder();
            StringBuilder insertq2_val = new StringBuilder();
            insertq1_col.append("INSERT INTO " + TABLE_NAME + "(");
            insertq2_val.append("VALUES(");
            Iterator<Map.Entry<String, Object>> row_map_it = row_map.entrySet().iterator();
            while (row_map_it.hasNext()) {
                Map.Entry<String, Object> row = (Map.Entry<String, Object>) row_map_it.next();

                String KEY = row.getKey();
                Object value = row.getValue();

                if (value.getClass().equals(Integer.class)) {
                    //values.put("`"+KEY+"`", ((Integer) value).intValue());
                    insertq1_col.append("`" + KEY + "`");
                    insertq2_val.append(((Integer) value).intValue());
                    if (row_map_it.hasNext()) {
                        insertq1_col.append(", ");
                        insertq2_val.append(", ");
                    }

                } else if (value.getClass().equals(Long.class)) {
                    insertq1_col.append("`" + KEY + "`");
                    insertq2_val.append(((Long) value).longValue());
                    if (row_map_it.hasNext()) {
                        insertq1_col.append(", ");
                        insertq2_val.append(", ");
                    }
                } else if (value.getClass().equals(String.class)) {
                    //values.put("`"+KEY+"`", "\""+(String)value+"\"");
                    insertq1_col.append("`" + KEY + "`");
                    insertq2_val.append("\"" + (String) value + "\"");
                    if (row_map_it.hasNext()) {
                        insertq1_col.append(", ");
                        insertq2_val.append(", ");
                    }
                } else if (value.getClass().equals(Double.class)) {
                    //values.put("`"+KEY+"`", 12.98f);
                    insertq1_col.append("`" + KEY + "`");
                    insertq2_val.append(((Double) value).doubleValue());
                    if (row_map_it.hasNext()) {
                        insertq1_col.append(", ");
                        insertq2_val.append(", ");
                    }
                }
            }//first row populated
            insertq1_col.append(")");
            insertq2_val.append(")");
            insertq1_col.append(insertq2_val.toString());
            db.execSQL(insertq1_col.toString());
        }//all rows completed
    }

    public void insertIntoDb(String insertQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertQuery);
        Log.i(TAG, "Insert Query " + insertQuery + " inserted successfully");
    }

    public void insertIntoDb(String tableName, ContentValues insertVal) {
        SQLiteDatabase db = this.getWritableDatabase();
        long recid = db.insert(tableName, null, insertVal);
        Log.i(TAG, "TIMER OGH BLE COMM MOD: Insert Query : " + insertVal + " inserted with row id " + recid);

    }



    public void setIntoDb(String setQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(setQuery);
        Log.i(TAG, "Set Query " + setQuery + " set successfully");
    }
    public void setIntoDb(String tableName, String colName, String wherearg , String recid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(colName, "0");
        db.update(tableName, values, wherearg + " = ?", new String[]{recid});
        System.out.println("Set Query set successfully");
    }
    public Cursor getFromDb(String query, String[] args) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(query, args);
        return cursor;
    }

    public Cursor getFromDb(String query) {
        return getFromDb(query, null);
    }

    public Cursor getDateFromDb(String query, String udpm, String energydate){
        System.out.println("Query is : " + query);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=null;
        cursor = db.rawQuery(query, new String[]{String.valueOf(udpm), String.valueOf(energydate)});
        return cursor;
    }
/*This method is used to delete the  table by given parm table name*/
    public void deleteFromDb(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.delete(tableName, "1", null);
        System.out.println("Data in table deleted successfully");
    }

    public Cursor getServerStatusFromDb(String query, String[] args){
        System.out.println("Query is : " + query);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=null;
        cursor = db.rawQuery(query, args);
        return cursor;
    }
    public Cursor getDateFromDateBetween(String query, String oghid, String energyDateFrom, String energyDateTo){
        System.out.println(" power " +
                "Query is : " + query);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=null;
        cursor = db.rawQuery(query, new String[]{String.valueOf(oghid), String.valueOf(energyDateFrom), String.valueOf(energyDateTo)});
        return cursor;
    }
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}


