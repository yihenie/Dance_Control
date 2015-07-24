package com.example.dance_control.bmob_table;

import cn.bmob.v3.BmobObject;

public class regiment_member extends BmobObject {

	public String member_name;
	public String member_ture_name;
	public String regiment_id;
	public String member_sex;
	public String member_host;
	public String member_password;
	public String member_power;//order,member,游客不注册自动为visiter//不存在visiter
	public String member_head_path;
	public String member_head_url;
	public Integer praise_num;
	
	public regiment_member(String head_path,String head_url,String name,String regiment_id , String sex,String host,String power,String password,String ture_name,Integer praise_num)
	{
		this.member_head_path = head_path;
		this.member_head_url = head_url;
		this.member_name = name;
		this.regiment_id = regiment_id;
		this.member_sex = sex;
		this.member_host = host;
		this.member_power = power;
		this.member_password = password;
		this.member_ture_name = ture_name;
		this.praise_num = praise_num;
	}
	
	public regiment_member(){}
	
}
