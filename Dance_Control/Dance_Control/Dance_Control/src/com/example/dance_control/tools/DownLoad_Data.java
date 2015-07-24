package com.example.dance_control.tools;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DownLoad_Data {
	private DataHelp Control;
	
	public DownLoad_Data(Context context)
	{
		this.Control = new DataHelp(context);
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong 保存每一个线程的信息
	 * */
	public void Save_Thread_Info(String musicid,int threadid, int DownLoadLength ,int TotalLength)
	{
		SQLiteDatabase data = Control.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("musicid", musicid);//音乐的名字
		values.put("threadid", threadid);//线程的id
		values.put("downlength", DownLoadLength);//下载了长度.
		values.put("totallength", TotalLength);//本文件总长度
		 data.insert("Download", null, values);
		 data.close();
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong 用音乐的名字来标示每一个音乐的下载
	 * @ 更新每一个线程的状态
	 * */
	public void Update_Thread_Info(String musicid,String threadid,int DownLoadLength)
	{
		String[] str = {musicid,threadid};
		SQLiteDatabase data = Control.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("downlength", DownLoadLength);
		data.update("Download", values, "musicid = ? and threadid = ?", str);
		data.close();
	}
	
	/**
	 * @author nieyihe
	 * @ 删除当前音乐的所有线程下载的记录
	 * */
	public void Delete_Music_Info(String musicid)
	{
		String []str = {musicid};
		SQLiteDatabase data = Control.getWritableDatabase();
		data.delete("Download","musicid = ?", str);
		data.close();
	}
	
	/**
	 * @author nieyihe
	 * @ 获取数据库里单一的某一个音乐的下载情况
	 * */
	public HashMap<String ,String> Get_SingleDownload_Info(String musicid)
	{
		HashMap<String,String> threadinfo = new HashMap<String,String>();
		String arg[] = {musicid};
		SQLiteDatabase data = Control.getReadableDatabase();
		Cursor c = null;
		c = data.query("Download", null, "musicid = ?", arg, null, null, null);
		
		if(c == null) return null;
		
		if(!c.moveToNext()) return null;
		
		while(c != null)
		{
			threadinfo.put(c.getString(c.getColumnIndex("threadid")), c.getString(c.getColumnIndex("downlength")));
			if(!c.moveToNext()) break;
		}
		
		data.close();
		c.close();
		
		return threadinfo;
	}
	
	/**
	 * @author nieyihe
	 * @作用 获取现在保存的下载的状态
	 * *///Data_Binder 
	public HashMap<String,HashMap<String,String>> Get_NoFinish_Info(HashMap<String ,String > filesizes)
	{
		
		HashMap<String ,HashMap<String,String>> Music_DownLoad = new HashMap<String ,HashMap<String,String>>();
		HashMap<String,String> Thread_Info = null;
		SQLiteDatabase data = Control.getReadableDatabase();
		
		Cursor c = null;
		c = data.query("Download",null, null, null, null, null, null);
				
		if(!c.moveToNext()) return null;
		
		while(c != null)
		{
			/*Data_Binder bind = new Data_Binder(c.getString(c.getColumnIndex("loadpath")),c.getString(c.getColumnIndex("musicname")));*/
			String musicid = c.getString(c.getColumnIndex("musicid"));
			//loadpath varchar(500) , threadid varchar(5) , downlength varchar(100)
			if(Music_DownLoad.containsKey(musicid) && Thread_Info != null)
			{	//若本音乐已经保存过一次
				//保存每一个线程的下载量;
				Thread_Info.put(c.getString(c.getColumnIndex("threadid")), c.getString(c.getColumnIndex("downlength")));
			
			}
			else
			{	//若没有保存过
				//则初始化线程信息
				Thread_Info = new HashMap<String,String>();
				//将新建的Thread_map保存到map中
				Music_DownLoad.put(musicid, Thread_Info);
				//保存每一个线程的下载量;
				Thread_Info.put(c.getString(c.getColumnIndex("threadid")), c.getString(c.getColumnIndex("downlength")));
				//获取文件的总大小
				filesizes.put(musicid, c.getString(c.getColumnIndex("totallength")));
			}
			
			if(!c.moveToNext()) break;//假如向下移动失败,则退出
		}
		
		if(c == null)
			return null;
		
		c.close();
		data.close();
		
		return Music_DownLoad;
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong 处理数据库的类
	 * */
	private class DataHelp extends SQLiteOpenHelper
	{
		private final static String DbName = "DownLoad.db";
		private final static int Version = 1;
		private final String Create = "create table Download(id int identity(1,1) primary key,musicid varchar(100), threadid varchar(5) , downlength varchar(100),totallength varchar(100))";
		
		public DataHelp(Context context) {
			super(context, DbName, null, Version);
			// TODO Auto-generated constructor stub
			
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			// TODO Auto-generated method stub
			arg0.execSQL(Create);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}
}
