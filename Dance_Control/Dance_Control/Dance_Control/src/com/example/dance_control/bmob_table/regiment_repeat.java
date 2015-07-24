package com.example.dance_control.bmob_table;

import cn.bmob.v3.BmobObject;

public class regiment_repeat extends BmobObject{

	public String repeat_head_path;
	public String repeat_head_url;
	public long repeat_time;
	public String repeat_name;
	public String repeat_word;
	public String repeat_talk_id;
	public String Talk_Create_Mm_Id;
	
	public regiment_repeat(String repeat_head_path,String repeat_head_url,long time,String name,String word,String id,String Talk_Create_Mm_Id)
	{
		this.repeat_head_path = repeat_head_path;
		this.repeat_head_url = repeat_head_url;
		this.repeat_time = time;
		this.repeat_name = name;
		this.repeat_talk_id = id;
		this.repeat_word = word;
		this.Talk_Create_Mm_Id = Talk_Create_Mm_Id;
	}
	
	public regiment_repeat() {
		// TODO Auto-generated constructor stub
	}
}
