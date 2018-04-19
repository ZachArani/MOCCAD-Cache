package edu.ou.oudb.cacheprototypeapp.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

public class ProcessedQueryDbHelper extends SQLiteOpenHelper{
	
	//Database version
    private static final int DATABASE_VERSION = 1;
 
    // Database name
    private static final String DATABASE_NAME = "AndroidCachePrototypeDatabase.db";
 
    // Table queries
    private static final String TABLE_QUERIES = "QUERIES";
    
    // columns names
    private static final String KEY_QUERY_ID = "query_id";
    private static final String KEY_RELATION = "relation";
    
    
    // Table predicates
    private static final String TABLE_PREDICATES = "PREDICATES";
    
    // columns names
    private static final String KEY_OPERAND_LEFT = "operand_left";
    private static final String KEY_OPERATOR = "operator";
    private static final String KEY_OPERAND_RIGHT = "operand_right";
    private static final String KEY_P_FOREIGN_QUERY_ID = "fk_query_id";

	// Table attributes
	private static final String TABLE_ATTRIBUTES = "ATTRIBUTES";

	// columns names
	private static final String KEY_ATTRIBUTE = "attribute";
	private static final String KEY_A_FOREIGN_QUERY_ID = "fk_query_id";
 
    public ProcessedQueryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        
    	String CREATE_QUERY_TABLE = "CREATE TABLE " + TABLE_QUERIES + "("
                + KEY_QUERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
        		+ KEY_RELATION + " TEXT NOT NULL"
                + ");";
    	
    	String CREATE_PREDICATE_TABLE = "CREATE TABLE " + TABLE_PREDICATES + "("
    			+ KEY_OPERAND_LEFT + " TEXT NOT NULL,"
    			+ KEY_OPERATOR + " TEXT NOT NULL,"
    			+ KEY_OPERAND_RIGHT + " TEXT NOT NULL,"
    			+ KEY_P_FOREIGN_QUERY_ID + " INTEGER,"
    			+ " FOREIGN KEY(" + KEY_P_FOREIGN_QUERY_ID + ") REFERENCES " + TABLE_QUERIES + "(" + KEY_QUERY_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
    			+ ");";

    	String CREATE_ATTRIBUTE_TABLE = "CREATE TABLE " + TABLE_ATTRIBUTES + "("
				+ KEY_ATTRIBUTE + " TEXT NOT NULL,"
				+ KEY_A_FOREIGN_QUERY_ID + " INTEGER,"
				+ " FOREIGN KEY(" + KEY_A_FOREIGN_QUERY_ID + ") REFERENCES " + TABLE_QUERIES + "(" + KEY_QUERY_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
				+ ");";
    	

    	db.execSQL(CREATE_QUERY_TABLE);
        db.execSQL(CREATE_PREDICATE_TABLE);
        db.execSQL(CREATE_ATTRIBUTE_TABLE);
        
        Log.i("data_base path", db.getPath());
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	// delete previous db
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUERIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREDICATES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTRIBUTES);
 
        // recreate the table
        onCreate(db);
    }
    
    
    /////////////////////QUERY///////////////////////
    public boolean addQuery(Query query) {
    	boolean inserted = false;
    	
    		
    	if( ! isProcessedQuery(query.getId()) )
    	{
    			
    		SQLiteDatabase db = this.getWritableDatabase();
    		
	        ContentValues queryValues = new ContentValues();
	        queryValues.put(KEY_RELATION, query.getRelation()); 
	        
	        db.beginTransaction();
	        
	        long insertedID=-1;
	        
	        // Line insertion        
	        if ((insertedID = db.insert(TABLE_QUERIES, null, queryValues)) != -1)
	        {
	            Log.i("insert_database", "query inserted : " + query.toSQLString());
	            
	            Iterator<Predicate> it = query.getPredicates().iterator();
	            
	            inserted = true;
	            
	            Predicate curPredicate = null;
	            while(it.hasNext() && inserted)
	            {
	            	curPredicate = it.next();
	            	
	            	ContentValues predicateValues = new ContentValues();
	            	predicateValues.put(KEY_OPERAND_LEFT, curPredicate.getSerializedLeftOperand());
	            	predicateValues.put(KEY_OPERATOR, curPredicate.getOperator());
	            	predicateValues.put(KEY_OPERAND_RIGHT, curPredicate.getSerializedRightOperand());
	            	predicateValues.put(KEY_P_FOREIGN_QUERY_ID, insertedID);
	            	
	            	if (db.insert(TABLE_PREDICATES, null, predicateValues) != -1)
	    	        {
	    	            Log.i("insert_database", "predicate inserted : " + curPredicate);
	    	        }
	            	else
	            	{
	            		inserted = false;
	            	}
	            }
				Iterator<String> itA = query.getAttributes().iterator();

				String curAttribute = null;

				while(itA.hasNext() && inserted) {
					curAttribute = itA.next();

					ContentValues attributeValues = new ContentValues();
					attributeValues.put(KEY_ATTRIBUTE, curAttribute);
					attributeValues.put(KEY_P_FOREIGN_QUERY_ID, insertedID);

					if (db.insert(TABLE_ATTRIBUTES, null, attributeValues) != -1) {
						Log.i("insert_database", "attribute inserted : " + curAttribute);
					} else {
						inserted = false;
					}
				}
	            
	            if (inserted)
	            {
	            	query.setId(insertedID);
	            	db.setTransactionSuccessful();
	            }
	            
	        }
	        else
	        {
	        	inserted = false;
	        }
	        
	        db.endTransaction();
	    	db.close();
		}
    	
    	
    	
    	return inserted;
    }
    
    
    public List<Predicate> getAllPredicates( int queryID ) throws IllegalArgumentException
    {
    	StringBuilder selectQuerySb = new StringBuilder();
    	selectQuerySb.append("SELECT ");
    	selectQuerySb.append(KEY_OPERAND_LEFT + ',');
    	selectQuerySb.append(KEY_OPERATOR + ',');
    	selectQuerySb.append(KEY_OPERAND_RIGHT);
    	selectQuerySb.append(" FROM ");
    	selectQuerySb.append(TABLE_PREDICATES);
    	selectQuerySb.append(" WHERE ");
    	selectQuerySb.append(KEY_P_FOREIGN_QUERY_ID + '=');
    	selectQuerySb.append(queryID);
    	
    	List<Predicate> predicates = new ArrayList<Predicate>();
    	
    	SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuerySb.toString(), null);
 
        // loop on all the lines and making of all the predicate list
        if (cursor.moveToFirst()) {
            do {
            	Predicate curPredicate = null;
				try {
					curPredicate = PredicateFactory.createPredicate(cursor.getString(0), cursor.getString(1), cursor.getString(2));
					// add the predicate for the cursor position on the list
	                predicates.add(curPredicate);
				} catch (TrivialPredicateException | InvalidPredicateException e) {
					throw new IllegalArgumentException("database contains errors !!!!!");
				}
            } while (predicates != null && cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return predicates;
    }

	public List<String> getAllAttributes( int queryID ) throws IllegalArgumentException
	{
		StringBuilder selectQuerySb = new StringBuilder();
		selectQuerySb.append("SELECT ");
		selectQuerySb.append(KEY_ATTRIBUTE);
		selectQuerySb.append(" FROM ");
		selectQuerySb.append(TABLE_ATTRIBUTES);
		selectQuerySb.append(" WHERE ");
		selectQuerySb.append(KEY_P_FOREIGN_QUERY_ID + '=');
		selectQuerySb.append(queryID);

		List<String> attributes = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuerySb.toString(), null);

		// loop on all the lines and making of all the predicate list
		if (cursor.moveToFirst()) {
			do {
				String curAttribute = null;
				curAttribute = cursor.getString(0);
				// add the predicate for the cursor position on the list
				attributes.add(curAttribute);
			} while (attributes != null && cursor.moveToNext());
		}

		cursor.close();
		db.close();

		return attributes;
	}
    
    
    
    public boolean isProcessedQuery(long queryID)
    {
    	boolean ret = false;
    	StringBuilder selectQuerySb = new StringBuilder();
    	selectQuerySb.append("SELECT COUNT(*)");
    	selectQuerySb.append(" FROM ");
    	selectQuerySb.append(TABLE_QUERIES);
    	selectQuerySb.append(" WHERE ");
    	selectQuerySb.append(KEY_QUERY_ID + '=');
    	selectQuerySb.append(queryID);
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	Cursor cursor = db.rawQuery(selectQuerySb.toString(), null);
    		
    	if (cursor != null)
        {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            
            if ( count > 0)
            {
            	 ret = true;
            }
            
            cursor.close();
        }
        
        
        db.close();
    	
    	return ret;
    }

     
    public List<Query> getAllProcessedQueries() throws IllegalArgumentException{
        List<Query> queries = new ArrayList<Query>();

		String selectQuery = "SELECT * FROM " + TABLE_QUERIES;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // loop on all the lines and construction of the list
        
        
        if (cursor.moveToFirst()) {
        	
        	List<Predicate> predicates = null;
        	
            do {
            	
            	Query query = new Query(cursor.getString(1));

            	query.addAttributes(getAllAttributes(cursor.getInt(0)));
            	
            	predicates = getAllPredicates(cursor.getInt(0));

            	query.addPredicates(predicates);
            	
            	queries.add(query);
            	
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
 
        return queries;
    }
    
    /*public int updateProcessedQuery(Query query) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues queryValues = new ContentValues();
        queryValues.put(KEY_QUERY_ID, query.getId()); 
        queryValues.put(KEY_RELATION, query.getRelation()); 
        
        db.beginTransaction();
        
        // update the line
        int ret = db.update(TABLE_QUERIES, queryValues, KEY_QUERY_ID + " = ?",
                new String[] { String.valueOf(query.getId()) });
        
        if (ret == 1) // successful
        {
        	Iterator<Predicate> it = query.getPredicates().iterator();
        	
        	Predicate curPredicate = null;
        	
        	while(it.hasNext() && ret > 0)
        	{
        		curPredicate = it.next();
        		ContentValues predicateValues = new ContentValues();
            	predicateValues.put(KEY_OPERAND_LEFT, curPredicate.getSerializedLeftOperand());
            	predicateValues.put(KEY_OPERATOR, curPredicate.getOperator());
            	predicateValues.put(KEY_OPERAND_RIGHT, curPredicate.getSerializedRightOperand());
            	predicateValues.put(KEY_P_FOREIGN_QUERY_ID, query.getId());
        		
            	ret = db.update(TABLE_PREDICATES, predicateValues, KEY_P_FOREIGN_QUERY_ID + " = ?",
                        new String[] { String.valueOf(query.getId()) });
        		
        	}
        	
        	if ( ret > 0 )
        	{
        		db.setTransactionSuccessful();
        	}
        }
        
        db.endTransaction();
        db.close();
        
        return ret;
    }*/
    
    public boolean deleteProcessedQuery(Query query) {
    	boolean deleted = false;
    	
    	if ( isProcessedQuery(query.getId()))
    	{
    		SQLiteDatabase db = this.getWritableDatabase();
    		// Line deletion        
            if ( db.delete(TABLE_QUERIES, KEY_QUERY_ID + " = ?",
                new String[] { String.valueOf(query.getId()) }) 
                > 0 )
            {
                Log.i("insert_database", "processed query deleted: " + query.toSQLString());
            }
            // the on delete cascade will automatically delete the corresponding predicates
            
            db.close();
            deleted = true;
    	}
    	
    	return deleted;
    }

    public int getProcessedQueriesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_QUERIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        
        int ret = cursor.getCount();
        cursor.close();
        
        db.close();
        
        return ret;
    }
}
