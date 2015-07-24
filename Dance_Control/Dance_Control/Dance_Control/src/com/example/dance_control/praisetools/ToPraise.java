package com.example.dance_control.praisetools;


public interface ToPraise {

	public void UpdateMemPraise(String memString,int Num); 
	public void InputPraise(final String audio_id,final String Createmem_id,final String mem_id,final String type);
	public void DeletePraise(final String audio_id,final String CreateMemId);
}
