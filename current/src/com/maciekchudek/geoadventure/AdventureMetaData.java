package com.maciekchudek.geoadventure;

import android.database.Cursor;

public class AdventureMetaData {

	public int id;
	public String name;
	public String version;
	public String author;
	public String special_message = null;
	
	public AdventureMetaData(Cursor cursor) {
		this.id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsMeta.ID.s));
		this.name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsMeta.NAME.s));
		this.version = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsMeta.VERSION.s));
		this.author = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseInterface.ColumnsMeta.AUTHOR.s));
	}

	public AdventureMetaData(int id, String name, String message) {
		this.id = id;
		this.name = name;
		this.special_message = message; 
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public String htmlHeader(){
		if (null != special_message){
			return name;
		} else {
			return name
					+ "<small> <font color=\"cc0000\">(version "
					+ version
					+ ")</font> </small><i> by "
					+ author
					+ "</i>";
		}
	}
}
