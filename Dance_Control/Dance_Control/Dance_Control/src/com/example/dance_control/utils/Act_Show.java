package com.example.dance_control.utils;

import cn.bmob.v3.Bmob;

import com.example.dance_control.R;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.fragment.Dance_Menu;
import com.example.dance_control.fragment.Dance_My_Movie;
import com.example.dance_control.fragment.Dance_Peripheral_Wall;
import com.example.dance_control.fragment.Dance_Regiment;
import com.example.dance_control.fragment.Dance_Show;
import com.example.dance_control.fragment.Dance_Talk;
import com.lxh.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.Toast;

public class Act_Show extends FragmentActivity {

	SlidingMenu menu ;
	private Long backTimeLong = 0l;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_act__show);
		BmobCreate();
		
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
		
		menu.setMenu(R.layout.main_menu);//设置左边的menu
		menu.setContent(R.layout.main_content);//此处设置中间的view是content
		
		
		Dance_Show bf = new Dance_Show(menu);
		Dance_Regiment dr = new Dance_Regiment(menu);
		Dance_Peripheral_Wall wall = new Dance_Peripheral_Wall(menu);
		//Dance_Talk tal = new Dance_Talk("",null);
		//Dance_My_Movie mov = new Dance_My_Movie("","");
		
		this.getSupportFragmentManager()
		.beginTransaction()
		.add(R.id.main_menu, new Dance_Menu(menu,bf,dr,wall))
		.commit();//设置左菜单内容
		
		getSupportFragmentManager()
		.beginTransaction()
		.add(R.id.main_content, bf)
		.commit();//设置主界面内容

	}


	public void BmobCreate()
	{
		Bmob.initialize(Act_Show.this, "fb77cf30562c036cd21b69543db2805f");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		/*super.onBackPressed();*/
		long now = System.currentTimeMillis();
		if((now - backTimeLong) > 2000)
		{
			Toast.makeText(Act_Show.this, "确认退出可在两秒内再次点击", Toast.LENGTH_SHORT).show();
			backTimeLong = now ;
		}
		else {
			finish();
		}
	}
}
