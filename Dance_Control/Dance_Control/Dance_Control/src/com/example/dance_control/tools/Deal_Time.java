package com.example.dance_control.tools;

import java.text.SimpleDateFormat;

public class Deal_Time {

	private final static long Time_Seconde = 1000;
	private final static long Time_Minute = Time_Seconde*60;
	private final static long Time_Hour = Time_Minute*60;
	private final static long Time_Day = Time_Hour*24;
	
	public static String When_Happen(long now)
	{
		long now_time = System.currentTimeMillis();
		long which_time = now;
		long differ_time = now_time - which_time;
		if(differ_time < Time_Seconde)
		{
			return "刚才";
		}
		if(differ_time > Time_Seconde && differ_time < Time_Minute)
		{
			return differ_time/Time_Seconde + "秒前！";
		}
		
		if(differ_time > Time_Minute && differ_time < Time_Hour)
		{
			return differ_time/Time_Minute + "分钟前！";
		}
		
		if(differ_time > Time_Hour && differ_time < Time_Day)
		{
			return differ_time/Time_Hour + "小时前！";
		}
		
		if(differ_time > Time_Day && differ_time < 3*Time_Day)
		{
			return differ_time/Time_Day +"天前";
		}
		
		SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd");
		String result = Format.format(now);
		return result;
	}
}
