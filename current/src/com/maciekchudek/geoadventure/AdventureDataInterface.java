package com.maciekchudek.geoadventure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.maciekchudek.geoadventure.DatabaseInterface.ColumnsAdventure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdventureDataInterface extends SQLiteOpenHelper {

	private enum SqlQueries {
		SELECT_TRIGGERED(ColumnsAdventure.TRIGGERED.s + " IS 1"), 
		SELECT_TRIGGERED_IDS ("SELECT " + ColumnsAdventure.ID.s + " FROM "+ TABLE_NAME_ADVENTURE + " WHERE "+ SqlQueries.SELECT_TRIGGERED.s), 
		SELECT_VIABLE(ColumnsAdventure.TRIGGERED.s + " IS 0 AND ( "	+ ColumnsAdventure.REQUIRED.s + " IS NULL OR "+ ColumnsAdventure.REQUIRED.s + " IN ("+ SqlQueries.SELECT_TRIGGERED_IDS.s + ")) AND ( "+ ColumnsAdventure.PROHIBITED.s + " IS NULL OR "+ ColumnsAdventure.PROHIBITED.s + " NOT IN ("+ SqlQueries.SELECT_TRIGGERED_IDS.s + "))");

		String s;

		private SqlQueries(String string) {
			this.s = string;
		}
		
		public String s(int id){			
			return this.s + " AND " + ColumnsAdventure.METAID.s + " IS " + Integer.toString(id);
		}

	}
	
	
	private int meta_id;
	private static final String TABLE_NAME_ADVENTURE = "adventure_message_data";
	private static final String DATABASE_NAME = "geoadventure.db";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase database;	
	
	private GameActivity parent;
	
	private String meta_query;
	
	String[] allColsAdventure;
	
	public AdventureDataInterface (GameActivity parent, int id){
		super(parent, DATABASE_NAME, null, DATABASE_VERSION);
		this.meta_id = id;
		this.database = this.getWritableDatabase();
		this.parent = parent;
		
		allColsAdventure = new String[ColumnsAdventure.values().length];
		int i = 0;
		for (ColumnsAdventure c : ColumnsAdventure.values()) {
			allColsAdventure[i++] = c.s;
		}
		
		meta_query = ColumnsAdventure.METAID.s + " IS " + Integer.toString(meta_id);
		
	}
	
	// ADVENTURE METHODS
	public void trigger(int id) {
		ContentValues vals = new ContentValues();
		vals.put("triggered", "1");
		database.update(TABLE_NAME_ADVENTURE, vals, meta_query + " AND " + ColumnsAdventure.ID.s +" IS " + id, null);
	}

	public void read(int id) {
		ContentValues vals = new ContentValues();
		vals.put("read", "1");
		database.update(TABLE_NAME_ADVENTURE, vals, meta_query + " AND " + ColumnsAdventure.ID.s +" IS " + id, null);
	}

	public List<Message> getAllMessages() {
		Cursor cursor = database.query(TABLE_NAME_ADVENTURE, allColsAdventure,
				meta_query, null, null, null, null);
		return cursorToMessages(cursor);
	}

	public List<Message> getTriggeredMessages() {
		Cursor cursor = database.query(TABLE_NAME_ADVENTURE, allColsAdventure,
				SqlQueries.SELECT_TRIGGERED.s(meta_id), null, null, null, null);
		return cursorToMessages(cursor);
	}

	public Set<Integer> getTriggeredIDs(ArrayList<Message> messages) {
		Set<Integer> ids = new TreeSet<Integer>();
		for (Message m : messages) {
			ids.add(m.id);
		}
		return ids;
	}

	public List<Message> getViableMessages() {
		Cursor cursor = database.query(TABLE_NAME_ADVENTURE, allColsAdventure,
				SqlQueries.SELECT_VIABLE.s(meta_id), null, null, null, null);
		return cursorToMessages(cursor);
	}

	private List<Message> cursorToMessages(Cursor cursor) {
		List<Message> messages = new ArrayList<Message>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Message message = new Message(parent, cursor);
			messages.add(message);
			cursor.moveToNext();
		}

		cursor.close();
		return messages;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void sleep(){
		database.close();
	}

	public void wake(){
		database = this.getWritableDatabase();
	}

	
	
}
