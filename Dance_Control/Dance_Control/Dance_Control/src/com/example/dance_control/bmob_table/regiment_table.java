package com.example.dance_control.bmob_table;

import cn.bmob.v3.BmobObject;

public class regiment_table extends BmobObject{

	public String regiment_name;
	public Integer regiment_num;
	public String regiment_time;
	public String regiment_order;
	public String regiment_addr;
	public String regiment_head_img_path;
	public String regiment_head_img_url;
	public String regiment_secretary;
	public String regiment_sec_order;
	public String regiment_intro;
	public String regiment_informs;
	
	public regiment_table(String name,Integer num,String time,String order ,String addr,String img_path,String img_url,String regiment_secretary,String regiment_sec_order,String regiment_intro,String regiment_informs)
	{
		this.regiment_head_img_path = img_path;
		this.regiment_head_img_url = img_url;
		this.regiment_name = name;
		this.regiment_num = num;
		this.regiment_order = order;
		this.regiment_addr = addr;
		this.regiment_time = time;
		this.regiment_secretary = regiment_secretary;
		this.regiment_sec_order = regiment_sec_order;
		this.regiment_intro = regiment_intro;
		this.regiment_informs = regiment_informs;
	}
	
	public regiment_table() {
		// TODO Auto-generated constructor stub
	}
}
