package com.example.dance_control.resource;

public class Regiment {

	public String regiment_id;
	public String regiment_img_path;
	public String regiment_img_url;
	public String regiment_name;
	public Integer regiment_number;
	public String regiment_order;
	public String regiment_addr;
	public String regiment_secretary;
	public String regiment_sec_order;
	public String regiment_intro;
	public String regiment_informs;
	
	public Regiment(String id,String img_path,String img_url,String name,Integer number,String order,String addr,String regiment_secretary,String regiment_sec_order,String regiment_intro,String regiment_informs)
	{
		this.regiment_id = id;
		this.regiment_img_path = img_path;
		this.regiment_img_url = img_url;
		this.regiment_name = name;
		this.regiment_number = number;
		this.regiment_order = order;
		this.regiment_addr = addr;
		this.regiment_secretary = regiment_secretary;
		this.regiment_sec_order = regiment_sec_order;
		this.regiment_intro = regiment_intro;
		this.regiment_informs = regiment_informs;
	}
}
