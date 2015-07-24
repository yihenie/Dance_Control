package com.example.dance_control.resource;

public class member {

	public Integer praise_num;
	public String name;
	public String regiment_id;
	public String sex;
	public String member_id;
	public String member_host;
	public String member_password;
	public String member_power;
	public String member_true_name;
	public String head_path;
	public String head_url;
	
	public member(String head_path,String head_url,String regiment_id , String member_id,String name,String sex,String member_power,String host,String password,String true_name,Integer praise_num)
	{
		this.head_path = head_path;
		this.head_url = head_url;
		this.regiment_id = regiment_id;
		this.member_id = member_id;
		this.name = name ; 
		this.sex = sex ; 
		this.member_power = member_power;
		this.member_host = host;
		this.member_password = password;
		this.member_true_name = true_name;
		this.praise_num = praise_num;
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof member)
		{
			member m = (member)o;
			if(m.member_id.equals(this.member_id))
				return true;
		}
		
		return false;
	}
}
