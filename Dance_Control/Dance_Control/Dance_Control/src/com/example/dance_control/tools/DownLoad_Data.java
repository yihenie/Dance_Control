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
	 * @zuoyong ����ÿһ���̵߳���Ϣ
	 * */
	public void Save_Thread_Info(String musicid,int threadid, int DownLoadLength ,int TotalLength)
	{
		SQLiteDatabase data = Control.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("musicid", musicid);//���ֵ�����
		values.put("threadid", threadid);//�̵߳�id
		values.put("downlength", DownLoadLength);//�����˳���.
		values.put("totallength", TotalLength);//���ļ��ܳ���
		 data.insert("Download", null, values);
		 data.close();
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong �����ֵ���������ʾÿһ�����ֵ�����
	 * @ ����ÿһ���̵߳�״̬
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
	 * @ ɾ����ǰ���ֵ������߳����صļ�¼
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
	 * @ ��ȡ���ݿ��ﵥһ��ĳһ�����ֵ��������
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
	 * @���� ��ȡ���ڱ�������ص�״̬
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
			{	//���������Ѿ������һ��
				//����ÿһ���̵߳�������;
				Thread_Info.put(c.getString(c.getColumnIndex("threadid")), c.getString(c.getColumnIndex("downlength")));
			
			}
			else
			{	//��û�б����
				//���ʼ���߳���Ϣ
				Thread_Info = new HashMap<String,String>();
				//���½���Thread_map���浽map��
				Music_DownLoad.put(musicid, Thread_Info);
				//����ÿһ���̵߳�������;
				Thread_Info.put(c.getString(c.getColumnIndex("threadid")), c.getString(c.getColumnIndex("downlength")));
				//��ȡ�ļ����ܴ�С
				filesizes.put(musicid, c.getString(c.getColumnIndex("totallength")));
			}
			
			if(!c.moveToNext()) break;//���������ƶ�ʧ��,���˳�
		}
		
		if(c == null)
			return null;
		
		c.close();
		data.close();
		
		return Music_DownLoad;
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong �������ݿ����
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
