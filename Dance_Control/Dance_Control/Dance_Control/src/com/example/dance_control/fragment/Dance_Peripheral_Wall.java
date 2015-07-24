package com.example.dance_control.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dance_control.R;
import com.example.dance_control.basefragment.Base_Fragment;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class Dance_Peripheral_Wall extends Base_Fragment {

	private SlidingMenu menu;
	private View headView;
	private ImageButton ReFresh;
	private ImageButton Back;
	private TextView Hint ;
	private ImageView peripheral_1;
	private ImageView peripheral_2;
	private ImageView peripheral_3;
	private String peripherals[] = {"http://yc-shuffle.taobao.com/","http://guiwushe.taobao.com/","http://item.taobao.com/item.htm?spm=a230r.1.14.284.DVofBe&id=44624448842&ns=1&abbucket=13#detail"};
	public Dance_Peripheral_Wall() {
		// TODO Auto-generated constructor stub
	}
	
	public Dance_Peripheral_Wall(SlidingMenu menu) {
		// TODO Auto-generated constructor stub
		this.menu = menu;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.peripheral_wall, null);
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		BuildView();
		SetOnclick();
	}
	
	/***
	 * @author nieyihe
	 * 创建当前视图
	 * */
	public void BuildView()
	{
		View parent = this.getView();
		headView = parent.findViewById(R.id.wall_head);
		this.Back = (ImageButton) headView.findViewById(R.id.Head_Back);
		this.Hint = (TextView) headView.findViewById(R.id.Head_Word_Hint);
		this.ReFresh = (ImageButton) headView.findViewById(R.id.Head_ReFresh);
		this.ReFresh.setVisibility(View.GONE);
		this.Hint.setText("外设墙");
		this.peripheral_1 = (ImageView) parent.findViewById(R.id.ad_1);
		this.peripheral_2 = (ImageView) parent.findViewById(R.id.ad_2);
		this.peripheral_3 = (ImageView) parent.findViewById(R.id.ad_3);
	}
	
	/***
	 * @author nieyihe
	 * 设置监听器
	 * */
	public void SetOnclick()
	{
		this.Back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				menu.toggle();
				
			}
		});
		
		this.peripheral_1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri u = Uri.parse(peripherals[0]);
				Intent i = new Intent(Intent.ACTION_VIEW,u);
				context.startActivity(i);
			}
		});
		
		this.peripheral_2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri u = Uri.parse(peripherals[1]);
				Intent i = new Intent(Intent.ACTION_VIEW,u);
				context.startActivity(i);
			}
		});
		
		this.peripheral_3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri u = Uri.parse(peripherals[2]);
				Intent i = new Intent(Intent.ACTION_VIEW,u);
				context.startActivity(i);
			}
		});
	}
}
