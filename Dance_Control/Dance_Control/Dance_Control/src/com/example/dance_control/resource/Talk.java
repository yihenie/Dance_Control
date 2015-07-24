package com.example.dance_control.resource;

public class Talk {

	public String Talk_Id;
	public String talk_head_path;
	public String talk_head_url;
	public String talk_name;
	public long talk_time;
	public String talk_word;
	public Integer talk_praise_num;
	public String talk_member_id;
	public String talk_img_path;
	public String talk_img_url;
	
	public Talk(String head_path,String head_url,String name,long time,String word,Integer praise_num,String id,String talk_member_id,String img_path,String img_url)
	{
		this.talk_head_path = head_path;
		this.talk_head_url  = head_url;
		this.talk_name = name;
		this.talk_time = time;
		this.talk_word = word;
		this.talk_praise_num = praise_num;
		this.Talk_Id = id ;  
		this.talk_member_id = talk_member_id;
		this.talk_img_path = img_path;
		this.talk_img_url = img_url;
	}

	//重写
	//用于ArrayList判断是否存在
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Talk) {   
			Talk u = (Talk) obj;   
            return this.Talk_Id.equals(u.Talk_Id);  
        } 
		
		return super.equals(obj);
	}
	
	
	

}
