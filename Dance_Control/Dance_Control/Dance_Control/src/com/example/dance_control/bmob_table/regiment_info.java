package com.example.dance_control.bmob_table;

import cn.bmob.v3.BmobObject;

public class regiment_info extends BmobObject {

	public String img_path;
	public String img_url;
	public String brief ;
	public String word;
	public String title;
	public Integer hit_num;
	public String time;
	public String regiment_id;
	
	public regiment_info(String regiment_id , String img_path,String img_url,String word ,String brief,String title,Integer hit_num,String time)
	{
		this.regiment_id = regiment_id;
		this.img_path = img_path ;
		this.img_url = img_url;
		this.brief = brief ;
		this.word = word;
		this.title = title;
		this.hit_num = hit_num;
		this.time = time;
	}
	
	public regiment_info(){}
}
