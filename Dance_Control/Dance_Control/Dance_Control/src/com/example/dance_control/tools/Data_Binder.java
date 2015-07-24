package com.example.dance_control.tools;

public class Data_Binder {

	public String DownLoadPath;
	public String MusicName;
	
	public Data_Binder(String DownLoadPath,String MusicName)
	{
		this.DownLoadPath = DownLoadPath;
		this.MusicName = MusicName;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		Data_Binder db = null;
		if(o != null) 
		{
			db = (Data_Binder)o;
			return this.DownLoadPath.equals(db.DownLoadPath)&& this.MusicName.equals(db.MusicName);
		}
		
		return false;
	}
	
	
}
