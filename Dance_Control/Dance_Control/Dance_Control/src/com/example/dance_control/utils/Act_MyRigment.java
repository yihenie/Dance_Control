package com.example.dance_control.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dance_control.R;
import com.example.dance_control.fragment.Dance_My_Info;
import com.example.dance_control.fragment.Dance_My_Menu;
import com.lxh.slidingmenu.lib.SlidingMenu;

public class Act_MyRigment extends FragmentActivity{

	private SlidingMenu menu;
	private String regiment_id = "";
	private String member_id = "";
	private String power;
	private Long backTimeLong = 0l;
	
	@Override
	public void onCreate(Bundle s)
	{
		super.onCreate(s);
		this.setContentView(R.layout.main_activity_1);
		Get_Bundle();
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.menu_shadow_width);
		menu.setShadowDrawable(R.drawable.menu_shadow);
		menu.setBehindOffsetRes(R.dimen.menu_offset);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		menu.setFadeDegree(0.35f);
		menu.setBehindOffset(dm.widthPixels*50/100);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		
		menu.setMode(SlidingMenu.LEFT);//这里模式设置为左右都有菜单，RIGHT显示右菜单，LEFT显示左菜单，LEFT_RIGH显示左右菜单
		
		menu.setMenu(R.layout.main_menu_1);
		menu.setContent(R.layout.main_content_1);
		
		Dance_My_Info info = new Dance_My_Info(menu,regiment_id,power);
		
		this.getSupportFragmentManager()
		.beginTransaction()
		.add(R.id.main_menu_1, new Dance_My_Menu(menu,member_id,power,regiment_id,info)).commit();
		
		this.getSupportFragmentManager()
		.beginTransaction()
		.add(R.id.main_content_1, info).commit();
		
		//Show_Welcome();
		
	}
	
	public void Get_Bundle()
	{
		Bundle b = this.getIntent().getExtras();
		regiment_id = b.getString("regiment_id");
		member_id = b.getString("member_id");
		power = b.getString("member_power");
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		/*super.onBackPressed();*/
		long now = System.currentTimeMillis();
		if((now - backTimeLong) > 2000)
		{
			Toast.makeText(Act_MyRigment.this, "确认退出可在两秒内再次点击", Toast.LENGTH_SHORT).show();
			backTimeLong = now ;
		}
		else {
			finish();
		}
	}
}
