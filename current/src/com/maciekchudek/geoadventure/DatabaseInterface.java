package com.maciekchudek.geoadventure;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseInterface extends SQLiteOpenHelper {

	private static final String TABLE_NAME_META = "adventure_meta_data";
	private static final String TABLE_NAME_ADVENTURE = "adventure_message_data";
	private static final String DATABASE_NAME = "geoadventure.db";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase database;
	private Context parent;
	
	public enum ColumnsMeta {
		ID("id", " integer primary key autoincrement"), NAME("name",
				" TEXT NOT NULL"), VERSION("version", " TEXT NOT NULL"), AUTHOR(
				"author", " TEXT NOT NULL");

		public String s;
		public String type;

		private ColumnsMeta(String name, String type) {
			this.s = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return this.s;
		}
	}

	public enum ColumnsAdventure {
		REALID("_id", " integer primary key"), 
		ID("id", " integer"), 
		METAID("meta_id"," integer NOT NULL"), 
		TITLE("title"," TEXT NOT NULL DEFAULT (' ')"), 
		MESSAGE("message"," TEXT NOT NULL DEFAULT (' ')"), 
		REQUIRED("required"," INTEGER"), 
		PROHIBITED("prohibited", " INTEGER"), 
		TRIGGERED("triggered", " INTEGER NOT NULL DEFAULT (0)"), 
		LOCATION("location"," TEXT"), 
		READ("read", " INTEGER NOT NULL DEFAULT (0)"), 
		IMAGE("image", " TEXT DEFAULT ('')"), 
		PROXIMITY("proximity", " INTEGER");

		public String s;
		public String type;

		private ColumnsAdventure(String name, String type) {
			this.s = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return this.s;
		}
	}

	

	String[] allColsMeta;
	String[] allColsAdventure;

	public DatabaseInterface(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		parent = context;
		database = this.getWritableDatabase();

		allColsMeta = new String[ColumnsMeta.values().length];
		int i = 0;
		for (ColumnsMeta c : ColumnsMeta.values()) {
			allColsMeta[i++] = c.s;
		}

		allColsAdventure = new String[ColumnsAdventure.values().length];
		i = 0;
		for (ColumnsAdventure c : ColumnsAdventure.values()) {
			allColsAdventure[i++] = c.s;
		}
	}

	// METHODS FOR BOTH TABLES

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create the meta and adventure tables
		StringBuilder sb_meta = new StringBuilder(200);
		StringBuilder sb_adventure = new StringBuilder(200);
		sb_meta.append("create table IF NOT EXISTS ");
		sb_adventure.append("create table IF NOT EXISTS ");
		sb_meta.append(TABLE_NAME_META);
		sb_adventure.append(TABLE_NAME_ADVENTURE);
		sb_meta.append("(");
		sb_adventure.append("(");
		String prefix = "";
		for (ColumnsMeta c : ColumnsMeta.values())// ADD each column
		{
			sb_meta.append(prefix);
			sb_meta.append(c.s);
			sb_meta.append(c.type);
			prefix = ",";
		}

		prefix = "";
		for (ColumnsAdventure c : ColumnsAdventure.values())// ADD each column
		{
			sb_adventure.append(prefix);
			sb_adventure.append(c.s);
			sb_adventure.append(c.type);
			prefix = ",";
		}

		sb_meta.append(");");
		sb_adventure.append(");");
		db.execSQL(sb_meta.toString());
		db.execSQL(sb_adventure.toString());

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// we probably shouldn't do this... or we'll destroy everyone's data
	}

	public void deleteAdventure(int id) {
		database.delete(TABLE_NAME_META, ColumnsMeta.ID.s + " = " + id, null);
		database.delete(TABLE_NAME_ADVENTURE, ColumnsAdventure.METAID.s + " = "
				+ id, null);
	}
	
	public void resetDatabase() {
		database.close();
		parent.deleteDatabase(DATABASE_NAME);
		database = this.getWritableDatabase();
	}



	public void sleep(){
		database.close();
	}

	public void wake(){
		database = this.getWritableDatabase();
	}









	// META METHODS

	public AdventureMetaData getSingleAdventureMetaData(int id) {
		
		Cursor c = database.query(TABLE_NAME_META,
				allColsMeta, ColumnsMeta.ID.s + " = " + id, null, null, null,
				null);
		c.moveToFirst();
		return new AdventureMetaData(c);
	}

	public List<AdventureMetaData> getAdventuresMetaData() {
		Cursor cursor = database.query(TABLE_NAME_META, allColsMeta, null,
				null, null, null, null);
		List<AdventureMetaData> adventures = new ArrayList<AdventureMetaData>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AdventureMetaData adventure = new AdventureMetaData(cursor);
			adventures.add(adventure);
			cursor.moveToNext();
		}
		cursor.close();
		return adventures;
	}

	public String getWhereClause(AdventureMetaData adventure) {
		StringBuilder sb = new StringBuilder(200);
		String prefix = "";
		for (ColumnsMeta c : ColumnsMeta.values()) {
			sb.append(prefix);
			prefix = " AND ";
			sb.append(c.s);
			sb.append(" = '");
			try {
				sb.append(adventure.getClass().getField(c.s).get(adventure));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			sb.append("' ");
		}
		return sb.toString();
	}

	public long addAdventureMeta(String name, String version, String author) {
		// check if an adventure with these features already exists
		String where = ColumnsMeta.NAME.s + " = '" + name + "' AND "
				+ ColumnsMeta.VERSION.s + " = '" + version + "' AND "
				+ ColumnsMeta.AUTHOR.s + " = '" + author + "'";
		Cursor cursor = database.query(TABLE_NAME_META, allColsMeta, where,
				null, null, null, null);
		if (cursor.getCount() > 0)
			return -2;

		// if not, add one
		ContentValues c = new ContentValues(3);
		c.put(ColumnsMeta.NAME.s, name);
		c.put(ColumnsMeta.VERSION.s, version);
		c.put(ColumnsMeta.AUTHOR.s, author);
		return database.insert(TABLE_NAME_META, null, c);
	}

	public int getNextID() {
		
		
		ContentValues c = new ContentValues(3);
		c.put(ColumnsMeta.NAME.s, "TEMPORARY STRING");
		c.put(ColumnsMeta.VERSION.s, "TEMPORARY STRING");
		c.put(ColumnsMeta.AUTHOR.s, "TEMPORARY STRING");
		database.beginTransaction();
		long nextID = database.insert(TABLE_NAME_META, null, c);
		database.endTransaction(); //this should invalidate the transaction				
		return (int)nextID;
	}



	public boolean addAdventureData(int metaid, String[] cols,
			List<String[]> rows) {
		ContentValues vals;
		boolean error = false;
		database.beginTransaction();
		for (String[] row : rows) {
			vals = new ContentValues();
			vals.put(ColumnsAdventure.METAID.s, metaid);
			for (int i = 0; i < cols.length; i++) {
				vals.put(cols[i], row[i]);
			}
			if (database.insert(TABLE_NAME_ADVENTURE, null, vals) == -1)
				error = true;
		}
		if (false == error)
			database.setTransactionSuccessful();
		database.endTransaction();
		return !error;
	}

}
