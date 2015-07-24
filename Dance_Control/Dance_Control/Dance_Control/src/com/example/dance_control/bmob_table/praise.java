package com.example.dance_control.bmob_table;

import cn.bmob.v3.BmobObject;

public class praise extends BmobObject {

	public String audio_id;
	public String mem_id;
	public String Create_Mem_Id;
	public String type;
	public long time;
	
	public praise(String audio_id,String mem_id,long time,String Create_Mem_Id,String type)
	{
		this.audio_id = audio_id;
		this.mem_id = mem_id;
		this.time = time;
		this.Create_Mem_Id = Create_Mem_Id;
		this.type = type;
	}
	
	public praise() {
		// TODO Auto-generated constructor stub
	}
}
