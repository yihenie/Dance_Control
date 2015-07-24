package com.example.dance_control.resource;

public class Repeat {

	public String repeat_name;
	public String repeat_word;
	public long repeat_time;
	public String repeat_head_path;
	public String repeat_head_url;
	
	public Repeat(String name,String word,long time,String head_path,String head_url)
	{
		this.repeat_name = name;
		this.repeat_word = word;
		this.repeat_time = time;
		this.repeat_head_path = head_path;
		this.repeat_head_url  = head_url;
	}
}
