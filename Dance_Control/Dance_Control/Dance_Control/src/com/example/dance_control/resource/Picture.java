package com.example.dance_control.resource;

public class Picture {

	public String pic_id;
	public String pic_path;
	public String pic_url;
	public Integer pic_praise_num;
	public String pic_member_id;
	
	public Picture(String id,String pic_path,String pic_url,Integer pic_praise_num,String pic_member_id)
	{
		this.pic_id = id;
		this.pic_path = pic_path;		
		this.pic_url = pic_url;
		this.pic_member_id = pic_member_id;
		this.pic_praise_num = pic_praise_num;
	}
}
