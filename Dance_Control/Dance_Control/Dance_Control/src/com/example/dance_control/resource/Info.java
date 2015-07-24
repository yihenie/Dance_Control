package com.example.dance_control.resource;

public class Info {
	public String img_path;
	public String img_url;
	public String brief;
	public String id;
	public String word;
	public String title;
	public Integer hit_num;
	public String time;
	public Info(String img_path,String img_url,String word,String id,String brief,String title,Integer hit_num,String time)
	{
		this.img_path = img_path ;
		this.img_url  = img_url ;
		this.word = word ;
		this.id = id;
		this.brief = brief;
		this.title = title;
		this.hit_num = hit_num;
		this.time = time;
	}
	
}
