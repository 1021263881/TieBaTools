package com.fapple.tools;
import android.content.*;
import android.database.sqlite.*;

public class DataBaseHelper extends SQLiteOpenHelper
{
	private static final int DB_VERSION = 1;
	//public static final String TABLE_NAME = "Orders";

	public DataBaseHelper(Context context, String DB_NAME)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase)
	{
		// create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
		String sql = "";

		//判断forum表是否存在，若不存在则创建forum表
		sql = "create table if not exists forum "
			+ "(tid text not null primary key, title text not null, content text not null, auid text not null, ctime text not null, luid text, ltime text)";
		sqLiteDatabase.execSQL(sql);

		//判断floor表是否存在，若不存在则创建floor表
		sql = "create table if not exists floor "
			+ "(tid text not null, pid text not null primary key, uid text not null, content text not null, floor text not null, time text not null)";
		sqLiteDatabase.execSQL(sql);

		//判断lzl表是否存在，若不存在则创建lzl表
		//sql = "create table if not exists lzl (tid text not null, pid text not null, spid text primary key, uid text not null, content text not null, time text not null)";
		//sqLiteDatabase.execSQL(sql);

		//判断user表是否存在，若不存在则创建user表
		sql = "create table if not exists user "
			+ "(uid text primary key, name text not null, nickname text not null, head text, level text)";
		sqLiteDatabase.execSQL(sql);
		
		//判断log表是否存在，若不存在则创建log表
		sql = "create table if not exists log "
			+ "(time text primary key, type int not null, content text not null)";
		sqLiteDatabase.execSQL(sql);
		
		//判断Account表是否存在，若不存在则创建Account表
		sql = "create table if not exists Account "
			+ "(uid text primary key, name text not null, BDUSS text not null, time text not null)";
		sqLiteDatabase.execSQL(sql);
		
		//判断ban表是否存在，若不存在则创建ban表
		sql = "create table if not exists ban "
			+ "(uid text primary key, time text not null, lasttime text)";
		sqLiteDatabase.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
	{
		//String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		//sqLiteDatabase.execSQL(sql);
		onCreate(sqLiteDatabase);
	}
}
