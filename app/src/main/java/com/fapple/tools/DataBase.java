package com.fapple.tools;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.util.*;

public abstract class DataBase
{
	private DataBaseHelper databasehelper;
	private SQLiteDatabase database;

	private String sql = "";
	private String log = "";
	private int level = -1;

	public abstract void addLogCallBack(String content);

	public DataBase(Context main, String DATABASENAME)
	{
		databasehelper = new DataBaseHelper(main, DATABASENAME);
	}

	public SQLiteDatabase getReadableDatabase()
	{
		database = databasehelper.getReadableDatabase();
		return database;
	}

	public SQLiteDatabase getWritableDatabase()
	{
		database = databasehelper.getWritableDatabase();
		return database;
	}
	public void setTransactionSuccessful()
	{
		if (database != null && database.isOpen() && !database.isReadOnly()) {
			database.setTransactionSuccessful();
		}
	}
	public void addBDUSS(String uid, String name, String BDUSS, long signTime)
	{
		if (database != null && database.isOpen() && !database.isReadOnly()) {
			sql = "insert or replace into Account(uid, name, BDUSS, time) values('" + uid + "', '" + name + "', '" + BDUSS + "', '" + String.valueOf(signTime) + "')";//插入操作的SQL语句
			database.execSQL(sql);//执行SQL语句
		}
	}
	public String getBDUSS()
	{
		log = "";
		Cursor c = database.rawQuery("select * from Account", null);
		if (c.moveToFirst()) {//判断游标是否为空
			while (!c.isAfterLast()) {
				log += "(";
				log += c.getString(c.getColumnIndex("uid"));
				log += ",";
				log += c.getString(c.getColumnIndex("name"));
				log += ",";
				log += c.getString(c.getColumnIndex("BDUSS"));
				log += ",";
				log += c.getString(c.getColumnIndex("time"));
				log += ")\n";
				c.moveToNext();
			}
		}
		return log.length() > 0 ?log.substring(0, log.length() - 1): log;
	}
	public String getBDUSS(String uid)
	{
		log = "";
		Cursor c = database.rawQuery("select * from Account where uid = '" + uid + "'", null);
		if (c.moveToFirst()) {//判断游标是否为空
			if (!c.isAfterLast()) {
				log += c.getString(c.getColumnIndex("BDUSS"));
			}
		}
		return log;
	}
	public String getLastSignTime(String uid)
	{
		log = "";
		Cursor c = database.rawQuery("select * from Account where uid = '" + uid + "'", null);
		if (c.moveToFirst()) {//判断游标是否为空
			if (!c.isAfterLast()) {
				log += c.getString(c.getColumnIndex("time"));
			}
		}
		return log;
	}
	public int getUidLevel(String uid)
	{
		level = -1;
		Cursor c = database.rawQuery("select * from user where uid = '" + uid + "'", null);
		if (c.moveToFirst()) {//判断游标是否为空
			if (!c.isAfterLast()) {
				level = Integer.valueOf(c.getString(c.getColumnIndex("level")));
			}
		}
		return level;
	}
	public void addLog(int type, String content)
	{
		if (database != null && database.isOpen() && !database.isReadOnly()) {
			//database.beginTransaction();
			sql = "insert into log(time, type, content) values ('" + System.currentTimeMillis() + "', '" + type + "', '" + content + "')";//插入操作的SQL语句
			database.execSQL(sql);//执行SQL语句
			if (sql.length() > 50) {
				addLogCallBack(sql.substring(46, sql.length() - 2).replace("', '", ""));
			}
			//database.setTransactionSuccessful();
			//database.endTransaction();
		}
	}
	public String getLog()
	{
		log = "";
		Cursor c = database.rawQuery("select * from log", null);
		if (c.moveToFirst()) {//判断游标是否为空
			while (!c.isAfterLast()) {
				log += "(";
				log += Tool.currentToStrTime(Long.valueOf(c.getString(c.getColumnIndex("time"))));
				log += ",";
				log += c.getString(c.getColumnIndex("content"));
				log += ")\n";
				c.moveToNext();
			}
		}
		return log.length() > 0 ?log.substring(0, log.length() - 1): log;
	}
	public void clearLog()
	{
		sql = "delete from log";
		database.execSQL(sql);
	}
	public void execSQL(String SQL)
	{
		database.execSQL(SQL);
	}
	public void closeDatabase()
	{
		database.close();
	}
}
