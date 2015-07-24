package com.example.dance_control.bmob_table;

import cn.bmob.v3.BmobObject;

public class regiment_music extends BmobObject {

	public String Intro;
	public String member_id;
	public String regiment_id;
	public Integer download_num;
	public String music_file_path;
	public String music_file_url;
	
	public regiment_music(String Intro,String mem_id,String regiment_id,Integer download_num,String music_file_path,String music_file_url)
	{
	
		this.Intro = Intro;
		this.member_id = mem_id;
		this.download_num = download_num;//下载次数用来更新praise_num
		this.regiment_id = regiment_id;
		this.music_file_path = music_file_path;
		this.music_file_url = music_file_url;
	}
	
	public regiment_music()
	{
		
	}
}
