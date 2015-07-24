package com.example.dance_control.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dance_control.R;
import com.example.dance_control.basefragment.Base_Fragment;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class Dance_Menu extends Base_Fragment{

	private Base_Fragment bf;
	private Base_Fragment reg;
	private Base_Fragment wall;
	private ArrayList<Options> Selecter;
	
	private SlidingMenu m ; 
	private Myadapter adapter;
	private ListView l;//menu的listview
	private MyHandler handler ;
	private FragmentManager fg;
	@Override
	public View onCreateView(LayoutInflater l ,ViewGroup v ,Bundle savedInstanceState)
	{
		fg = Dance_Menu.this.getActivity().getSupportFragmentManager();
		return l.inflate(R.layout.show_menu, null);
	}
	
	public Dance_Menu(SlidingMenu m,Dance_Show bf,Dance_Regiment reg,Dance_Peripheral_Wall wall)
	{
		this.m = m;
		this.bf = bf;
		this.reg = reg;
		this.wall = wall;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		
		this.initViews();
	}
	/**
	 * @author nieyihe
	 * @作用:用于初始化menu界面
	 * */
	public void initViews()
	{
		this.handler = new MyHandler();
		
		ini_Options();
		View parent = this.getView();
		l = (ListView) parent.findViewById(R.id.Menu_list_1);
		adapter = new Myadapter(context,Selecter);
		l.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				switch(pos)
				{
				case 0:
				{
					Message m = new Message();
					Bundle b = new Bundle();
					b.putString("Select", "First");
					m.setData(b);
					handler.sendMessage(m);
					break;
				}
				case 1:
				{
					Message m = new Message();
					Bundle b = new Bundle();
					b.putString("Select", "Second");
					m.setData(b);
					handler.sendMessage(m);
					break;
				}
				case 2:
				{
					Message m = new Message();
					Bundle b = new Bundle();
					b.putString("Select", "Third");
					m.setData(b);
					handler.sendMessage(m);
					break;
				}
				}
			}
			
		});
	
		l.setAdapter(adapter);
	}
		
	public void ini_Options()
	{
		Selecter = new ArrayList<Options>();
		Selecter.add(new Options(R.drawable.study_center,"视频点播榜"));
		Selecter.add(new Options(R.drawable.show,"舞团展示"));
		Selecter.add(new Options(R.drawable.adwall,"外设墙"));
		
	}
	/**
	 * @author nieyihe
	 * @作用:为listview设置个适配器以此来添加对应的数据，与图标
	 * */
	private class Myadapter extends BaseAdapter
	{
		private Context context;
		private LayoutInflater inflater;
		private ArrayList<Options> Selecter;
		public Myadapter(Context context,ArrayList<Options> Selecter)
		{
			this.context = context;
			this.inflater = LayoutInflater.from(this.context);
			this.Selecter = Selecter;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.Selecter.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Bindle bind ;
			if(convertView == null)
			{
			   convertView = this.inflater.inflate(R.layout.menu_option, null);
			   bind = new Bindle();
			   bind.icon = (ImageView) convertView.findViewById(R.id.icon);
			   bind.Option = (TextView) convertView.findViewById(R.id.option_string);
			   convertView.setTag(bind);
			  
			}
			else
			{
				bind = (Bindle)convertView.getTag();
			}
			
			Options s = Myadapter.this.Selecter.get(position);
			bind.icon.setImageResource(s.path);
			bind.Option.setText(s.select);
			
			return convertView;
		}
		
		
	}
	
	
	/**
	 * @author nieyihe
	 * @作用:用于适配器使用
	 * */
	public class Bindle
	{
		public ImageView icon;
		public TextView Option;

	}
	
	/**
	 * @author nieyihe
	 * @作用:建立一个消息循环，交互信息用于修改ui界面
	 * */
	public class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message s)
		{
			super.handleMessage(s);
			Bundle b = s.getData();
			
			if(b.containsKey("Select"))
			{
				String Order = b.getString("Select");
				if(Order.equals("First"))
				{
					//Toast.makeText(context, "最新资讯", Toast.LENGTH_LONG).show();
					
						fg.beginTransaction().hide(reg).hide(wall).show(bf).commit();
						m.toggle();
					
					/*FragmentTransaction transaction = .beginTransaction().setCustomAnimations(
		                    android.R.anim.fade_in, R.anim.slide_out)*/
				}
				if(Order.equals("Second"))
				{
				//	Toast.makeText(context, "显示所有舞团", Toast.LENGTH_LONG).show();
					if(!reg.isAdded())
					{
						fg.beginTransaction().add(R.id.main_content,reg).hide(bf).hide(wall).show(reg).commit();
						m.toggle();
					}
					else
					{
						fg.beginTransaction().hide(bf).hide(wall).show(reg).commit();
						m.toggle();
					}
				}
				if(Order.equals("Third"))
				{
					if(!wall.isAdded())
					{
						fg.beginTransaction().add(R.id.main_content,wall).hide(bf).hide(reg).show(wall).commit();
						m.toggle();
					}
					else{
						fg.beginTransaction().hide(bf).hide(reg).show(wall).commit();
						m.toggle();
					}
				}
				
			}
		}
	}
	/**
	 * @author nieyihe
	 * @作用：此类用于辅助适配器	
	 * */
	public class Options {

		public int path;
		public String select;
		
		
		public Options(int path,String select)
		{
			this.path = path;
			this.select = select;
		
		}
	}
}
