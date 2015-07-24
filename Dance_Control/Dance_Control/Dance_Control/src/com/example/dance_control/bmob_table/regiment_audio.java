package com.example.dance_control.bmob_table;

import cn.bmob.v3.BmobObject;

public class regiment_audio extends BmobObject {

	public String img_path;
	public String img_url;
	public String url;
	public String intro;
	public String regiment_id;
	public Integer praise_num;
	public String member_id;
	public String type;
	
	public regiment_audio(String img_path,String img_url,String regiment_id,Integer num,String mem_id,String intro,String url,String type)
	{
		this.img_path = img_path;
		this.img_url = img_url;
		this.regiment_id = regiment_id;
		this.praise_num = num; 		
		this.member_id = mem_id;
		this.url = url;
		this.intro = intro;
		this.type = type ;
	}
	public regiment_audio(){}
}
