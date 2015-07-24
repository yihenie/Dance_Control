package com.example.dance_control.bmob_table;

import cn.bmob.v3.BmobObject;

public class regiment_talk extends BmobObject{
	
	public String talk_head_path;
	public String talk_head_url;
	public String talk_word;
	public String talk_order;
	public Long talk_time;
	public Integer talk_praise_num;
	public String regiment_id;
	public String member_id;
	public String talk_img_path;
	public String talk_img_url;
	
	public regiment_talk(String regiment_id,String word,String order,long time,String head_path,String head_url,Integer num,String member_id,String talk_img_path,String talk_img_url)
	{
		this.regiment_id = regiment_id;
		this.talk_word = word;
		this.talk_order = order;
		this.talk_time = time;
		this.talk_head_path = head_path;
		this.talk_head_url  = head_url;
		this.talk_praise_num = num;
		this.member_id = member_id;
		this.talk_img_path = talk_img_path;
		this.talk_img_url = talk_img_url;
	}
	
	public regiment_talk(String regiment_id,String word,String order,long time,String head_path,String head_url,Integer num,String member_id)
	{
		this.regiment_id = regiment_id;
		this.talk_word = word;
		this.talk_order = order;
		this.talk_time = time;
		this.talk_head_path = head_path;
		this.talk_head_url  = head_url;
		this.talk_praise_num = num;
		this.member_id = member_id;
		this.talk_img_path = "";
		this.talk_img_url = "";
	}
	
	public regiment_talk(){}
	
}
