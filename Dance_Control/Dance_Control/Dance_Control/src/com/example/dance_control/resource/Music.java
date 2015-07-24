package com.example.dance_control.resource;

public class Music {

	public String music_path;
	public String music_url;
	public String Music_id;
	public String Music_member_id;
	public Integer download_num;
	public String intro;
	
	public Music(String music_path,String music_url,String music_id,String music_mem_id,Integer download_num,String intro)
	{
		this.music_path = music_path;
		this.music_url = music_url;
		this.Music_id = music_id;
		this.Music_member_id = music_mem_id;
		this.download_num = download_num;
		this.intro = intro;
	}
	
	public Music(String music_id,String music_mem_id,Integer download_num,String intro)
	{
		this.Music_id = music_id;
		this.Music_member_id = music_mem_id;
		this.download_num = download_num;
		this.intro = intro;
	}
}
