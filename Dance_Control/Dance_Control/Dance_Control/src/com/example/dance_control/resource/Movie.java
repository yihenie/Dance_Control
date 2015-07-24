package com.example.dance_control.resource;

import cn.bmob.v3.datatype.BmobFile;

public class Movie {

	public String img_path;
	public String img_url;
	public String url;
	public String Movie_id;
	public String Movie_member_id;
	public Integer praise_num;
	public String intro;
	
	public Movie(String url,String Movie_id,String img_path,String img_url,String intro,String Movie_member_id,Integer praise_num)
	{
		this.url = url;
		this.Movie_id = Movie_id;
		this.img_path = img_path;
		this.img_url  = img_url;
		this.intro = intro;
		this.Movie_member_id = Movie_member_id;
		this.praise_num = praise_num;
	}
}
