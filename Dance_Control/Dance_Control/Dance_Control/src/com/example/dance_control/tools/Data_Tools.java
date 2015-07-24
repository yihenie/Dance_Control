package com.example.dance_control.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Data_Tools {
	
	private Data_Helper Helper = null;
	
	public Data_Tools(Context context)
	{
		this.Helper = new Data_Helper(context);
	}
	
	public void Update_Talk(String Talk_id,String Praise_Num,String []WhereArgs)
	{
		SQLiteDatabase data = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		/*values.put("talk_id", Talk_id);*/
		values.put("num", Praise_Num);
		data.update("talk_praise", values, "talk_id=?", WhereArgs);
		data.close();
	}
	
	public void Update_Pic(String Pic_id,String Praise_Num,String []WhereArgs)
	{
		SQLiteDatabase data = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		/*values.put("talk_id", Talk_id);*/
		values.put("num", Praise_Num);
		data.update("pic_praise", values, "pic_id=?", WhereArgs);
		data.close();
	}
	

	public void Update_Movie(String Movie_id,String Praise_Num,String []WhereArgs)
	{
		SQLiteDatabase data = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		/*values.put("talk_id", Talk_id);*/
		values.put("num", Praise_Num);
		data.update("movie_praise", values, "movie_id=?", WhereArgs);
		data.close();
	}
	

	public void Update_Music(String Music_id,String Praise_Num,String []WhereArgs)
	{
		SQLiteDatabase data = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		/*values.put("talk_id", Talk_id);*/
		values.put("num", Praise_Num);
		data.update("music_praise", values, "music_id=?", WhereArgs);
		data.close();
	}
	
	public void Add_Talk(String Talk_id,String Praise_Num)
	{
		SQLiteDatabase data = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("talk_id", Talk_id);
		values.put("num", Praise_Num);
		data.insert("talk_praise", null, values);
		data.close();
	}
	
	public void Add_Pic(String Pic_id,String Praise_Num)
	{
		SQLiteDatabase data = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("pic_id", Pic_id);
		values.put("num", Praise_Num);
		data.insert("pic_praise", null, values);
		data.close();
	}
	
	public void Add_Movie(String Movie_id,String Praise_Num)
	{
		SQLiteDatabase data = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("movie_id", Movie_id);
		values.put("num", Praise_Num);
		data.insert("movie_praise", null, values);
		data.close();
	}
	
	public void Add_Music(String Music_id,String Praise_Num)
	{
		SQLiteDatabase data = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("music_id", Music_id);
		values.put("num", Praise_Num);
		data.insert("music_praise", null, values);
		data.close();
	}
	
	public HashMap<String,String> Select_Talk()
	{
		HashMap<String,String> map = new HashMap<String,String>();
		SQLiteDatabase data = Helper.getReadableDatabase();
		Cursor c = null;
		c = data.query("talk_praise", null, null, null, null, null, null);
		
		while(c != null && c.moveToNext())
		{
			String id = c.getString(c.getColumnIndex("talk_id"));
			String num_praise = c.getString(c.getColumnIndex("num"));
			map.put(id, num_praise);
		}
		if(c == null)
		{
			return null;
		}
		c.close();
		data.close();
		
		return map;
	}
	
	public HashMap<String,String> Select_Pic()
	{
		HashMap<String,String> map = new HashMap<String,String>();
		SQLiteDatabase data = Helper.getReadableDatabase();
		Cursor c = null;
		c = data.query("pic_praise", null, null, null, null, null, null);
		
		while(c != null && c.moveToNext())
		{
			String id = c.getString(c.getColumnIndex("pic_id"));
			String num_praise = c.getString(c.getColumnIndex("num"));
			map.put(id, num_praise);
			
		}
		if(c == null)
		{
			return null;
		}
		c.close();
		data.close();
		
		return map;
	}
	
	public HashMap<String,String> Select_Movie()
	{
		HashMap<String,String> map = new HashMap<String,String>();
		SQLiteDatabase data = Helper.getReadableDatabase();
		Cursor c = null;
		c = data.query("movie_praise", null, null, null, null, null, null);
		
		while(c != null && c.moveToNext())
		{
			String id = c.getString(c.getColumnIndex("movie_id"));
			String num_praise = c.getString(c.getColumnIndex("num"));
			map.put(id, num_praise);
			
		}
		if(c == null)
		{
			return null;
		}
		c.close();
		data.close();
		
		return map;
	}
	
	
	public HashMap<String,String> Select_Music()
	{
		HashMap<String,String> map = new HashMap<String,String>();
		SQLiteDatabase data = Helper.getReadableDatabase();
		Cursor c = null;
		c = data.query("music_praise", null, null, null, null, null, null);
		
		while(c != null && c.moveToNext())
		{
			String id = c.getString(c.getColumnIndex("music_id"));
			String num_praise = c.getString(c.getColumnIndex("num"));
			map.put(id, num_praise);
			
		}
		if(c == null)
		{
			return null;
		}
		c.close();
		data.close();
		
		return map;
	}
	
	
	
	private void Delete_Talk()
	{
		String Delete_All_Sql = "Delete from talk_praise";
		SQLiteDatabase data = Helper.getWritableDatabase();
		data.execSQL(Delete_All_Sql);
	}
	
	public void Delete_Movie()
	{
		 String Delete_Sql = "Delete from movie_praise";
		 SQLiteDatabase data = Helper.getWritableDatabase();
		 data.execSQL(Delete_Sql);
	}
	
	public void Delete_Pic()
	{
		String Delete_Sql = "Delete from pic_praise";
		SQLiteDatabase data = Helper.getWritableDatabase();
		data.execSQL(Delete_Sql);
	}
	
	public void Delete_Music()
	{
		 
		 String Delete_Sql = "Delete from music_praise";
		 SQLiteDatabase data = Helper.getWritableDatabase();
		 data.execSQL(Delete_Sql);
	}
	
	public void Insert_Talk(HashMap<String,String> Talk_map)
	{
		this.Delete_Talk();//删除所有的数据,准备重新装入数据
		SQLiteDatabase data = Helper.getWritableDatabase();
		Iterator iter_talk = Talk_map.entrySet().iterator();
		while (iter_talk.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter_talk.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			ContentValues values = new ContentValues();
			values.put("talk_id", key);
			values.put("num", val);
			data.insert("talk_praise", null, values);
		}
		
		data.close();
	}
	
	public void Insert_Pic(HashMap<String,String> Pic_map)
	{
		Delete_Pic();
		SQLiteDatabase data = Helper.getWritableDatabase();
		Iterator iter_pic = Pic_map.entrySet().iterator();
		while (iter_pic.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter_pic.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			ContentValues values = new ContentValues();
			values.put("pic_id", key);
			values.put("num", val);
			data.insert("pic_praise", null, values);
		}
		
	}
	
	public void Insert_Movie(HashMap<String,String> Movie_map)
	{
		Delete_Movie();
		SQLiteDatabase data = Helper.getWritableDatabase();
		Iterator iter_movie = Movie_map.entrySet().iterator();
		while (iter_movie.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter_movie.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			ContentValues values = new ContentValues();
			values.put("movie_id", key);
			values.put("num", val);
			data.insert("movie_praise", null, values);
		}
	}
	
	public void Insert_Music(HashMap<String,String> Music_map)
	{
		Delete_Music();
		SQLiteDatabase data = Helper.getWritableDatabase();
		Iterator iter_music = Music_map.entrySet().iterator();
		while (iter_music.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter_music.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			ContentValues values = new ContentValues();
			values.put("music_id", key);
			values.put("num", val);
			data.insert("music_praise", null, values);
		}
		
		data.close();
	}
	
	class Data_Helper extends SQLiteOpenHelper 
	{
		private final static String DataBaseName = "Regiment_Praise.db";
		private final static int Version = 1;
		private final String Create_talk_Sql = "create table talk_praise(id int identity(1,1) primary key, talk_id varchar(20) ,num varchar(20))";
		private final String Create_pic_Sql = "create table pic_praise(id int identity(1,1) primary key, pic_id varchar(20) ,num varchar(20))";
		private final String Create_movie_Sql = "create table movie_praise(id int identity(1,1) primary key, movie_id varchar(20) ,num varchar(20))";
		private final String Create_music_Sql = "create table music_praise(id int identity(1,1) primary key, music_id varchar(20) ,num varchar(20))";
		
		public Data_Helper(Context context) {
			super(context, DataBaseName, null, Version);
			// TODO Auto-generated constructor stub
		}
	
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(this.Create_movie_Sql);
			db.execSQL(this.Create_pic_Sql);
			db.execSQL(this.Create_talk_Sql);
			db.execSQL(this.Create_music_Sql);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}
}
