package com.example.dance_control.praisetools;

import java.util.HashMap;

import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.dance_control.R;
import com.example.dance_control.bmob_table.praise;
import com.example.dance_control.bmob_table.regiment_member;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


public class PraiseHelperOnclicker implements OnClickListener ,ToPraise
{
	private Context context;
	private boolean isPraise;
	private String audio_id;
	private String Createmem_id;
	private String mem_id;
	private String type;
	private HashMap<String, String> praiseHashMap;
	
	public PraiseHelperOnclicker(Context context,boolean ispraise,String audio_id,String Createmem_id,String mem_id,String InputPraiseType,HashMap<String, String> praiseHashMap) {
	// TODO Auto-generated constructor stub
		this.context = context;
		this.isPraise = ispraise;
		this.audio_id = audio_id ;
		this.Createmem_id = Createmem_id;
		this.mem_id = mem_id;
		this.type = InputPraiseType;
		this.praiseHashMap = praiseHashMap;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(!isPraise)
		{
			((ImageView)v).setImageResource(R.drawable.praise_end);
			InputPraise(audio_id,Createmem_id,mem_id,type);
			isPraise = true; 
		}
		else
		{
			((ImageView)v).setImageResource(R.drawable.praise_before);
			DeletePraise(audio_id,Createmem_id);
			isPraise = false;
		}
	}

	@Override
	public void UpdateMemPraise( String memString, int Num) {
		// TODO Auto-generated method stub
		regiment_member mem = new regiment_member();
		mem.increment("praise_num", Num);
		mem.update(context, memString, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	
	@Override
	public void DeletePraise( final String audio_id,
			final String CreateMemId) {
		// TODO Auto-generated method stub
		praise pra = new praise();
		pra.delete(context, praiseHashMap.get(audio_id),new DeleteListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				UpdateMemPraise(CreateMemId,-1);
				praiseHashMap.remove(audio_id);
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void InputPraise( final String audio_id,
			final String Createmem_id, String mem_id, String type) {
		// TODO Auto-generated method stub
		final praise praise = new praise(audio_id, mem_id, System.currentTimeMillis(), Createmem_id,type);
		praise.save(context, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				UpdateMemPraise(Createmem_id,1);
				praiseHashMap.put(audio_id,praise.getObjectId());
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
}
