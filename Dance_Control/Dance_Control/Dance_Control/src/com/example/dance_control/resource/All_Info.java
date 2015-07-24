package com.example.dance_control.resource;

import java.util.ArrayList;

public class All_Info {
	public String inform_id;
	public String Inform;
	public ArrayList<Info> infos;
	
	public All_Info(String Inform,ArrayList<Info> infos)
	{
		this.Inform = Inform;
		this.infos = infos;
	}
	
	public All_Info()
	{
		this.infos = new ArrayList<Info>();
		this.Inform = "";
	}
}
