package com.example.dance_control.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.example.dance_control.R;
import com.example.dance_control.bmob_table.praise;
import com.example.dance_control.bmob_table.regiment_audio;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.bmob_table.regiment_music;
import com.example.dance_control.bmob_table.regiment_repeat;
import com.example.dance_control.bmob_table.regiment_table;
import com.example.dance_control.bmob_table.regiment_talk;
import com.example.dance_control.resource.Movie;
import com.example.dance_control.resource.Music;
import com.example.dance_control.resource.Picture;
import com.example.dance_control.resource.Talk;
import com.example.dance_control.resource.member;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.example.dance_control.tools.Data_Tools;
import com.example.dance_control.tools.NotificationLocalFactory;
import com.example.dance_control.tools.Utility;

public class Ac_Dital_Info extends Activity {

		private String member_id; 
		private String regiment_id;
		private member me;
		private Bitmap headBitmap;
		private ImageView me_head;
		private ImageButton me_set;
		private TextView me_host;
		private TextView me_name;
		private TextView me_virtural_name;
		private ImageView me_sex;
		private ImageView me_power;
		//表示是否有动态
		private ImageView me_newsimg;
		//更换成员
		private Button me_change;
		//签到的按钮
		private ImageButton Assagin;
		
		private RelativeLayout me_set_host_password;
		private RelativeLayout me_set_news;
		private String ImagePath;
		private TextView Host;
		
		private ImageButton Btu_Movie;
		private ImageButton Btu_Talk;
		private ImageButton Btu_Pic;
		private ImageButton Btu_Music;
		
		//每一个标签上显示的最新数字
		private TextView Text_Movie_Trend_Num;
		private TextView Text_Music_Trend_Num;
		private TextView Text_Pic_Trend_Num;
		private TextView Text_Talk_Trend_Num;
		
		//每一个显示动态界面
		private RelativeLayout Show_Movie_Trend;
		private RelativeLayout Show_Music_Trend;
		private RelativeLayout Show_Pic_Trend;
		private RelativeLayout Show_Talk_Trend;
		
		//表示每一界面上总共变化的hots值
		private TextView Movie_Hots;
		private TextView Music_Hots;
		private TextView Pic_Hots;
		private TextView Talk_Hots;
		
		//表示每一界面上显示的hots是上涨还是下降
		private ImageView Movie_Hot_Icon; 
		private ImageView Music_Hot_Icon;
		private ImageView Pic_Hot_Icon;
		private ImageView Talk_Hot_Icon;
		
		//表示每一个界面到底变化了多少  正还是负
		private int Movies_Change_Hots = 0;
		private int Musics_Change_Hots = 0;
		private int Pics_Change_Hots = 0;
		private int Talks_Change_Hots = 0;
		
		private RelativeLayout[] Slide_Layout ;
		private RelativeLayout.LayoutParams[] params;
		private int[] Original_Margin_Top;
		//消息回调函数
		private MyHandler handler = new MyHandler();
		//判断是否正在下滑
		private boolean SlidingDown = false;
		
		private HashMap<String,Bitmap> pictureBitmaps = new HashMap<String,Bitmap>();
		//位图列表
		private ListView Movies = null;
		private ListView Musics = null;
		private ListView Pics = null;
		private ListView Talks = null;
		
		private Adapter_Movie Mov_Ada;
		private Adapter_Music Mus_Ada;
		private Adapter_Talk  Tal_Ada;
		private Adapter_Pic   Pic_Ada;
		
		/*表示是否有新的动态*/ 
		private boolean Has = false;
		
		/*保存Time*/
		private SharedPreferences Save_Time;
		//保存是否已经签到
		private SharedPreferences Sign;
		//保存上次更新的赞
		/*private SharedPreferences Trends;*/
		/*表示Tag距离底部的高度*/
		private int Height_y;
		private int Scrren_Height = 0;
		
		/*代表动态的数量*/
		private int Music_Trends = 0;
		private int Movie_Trends = 0;
		private int Pictrue_Trends = 0;
		private int Talk_Trends = 0;
	
		//本地保存的赞的数目
		private HashMap<String ,String > Local_Movie_Praise ;
		private HashMap<String ,String > Local_Talk_Praise ;
		private HashMap<String ,String > Local_Music_Praise ;
		private HashMap<String ,String > Local_Pic_Praise ;
		
		/***
		 * *
		 * 最新动态的数据连接
		 * 通过保存的有动态的对象集中的id
		 * 来获取对应赞 或者 是repeat数*/
		//保存所有有动态的 数据
		private ArrayList<Movie> Movie_Trends_Ever = new ArrayList<Movie>();
		private ArrayList<Talk> Talk_Trends_Ever = new ArrayList<Talk>();
		private ArrayList<Picture> Pic_Trends_Ever = new ArrayList<Picture>();
		private ArrayList<Music> Music_Trends_Ever = new ArrayList<Music>();
		
		//保存有赞的每一个id
		//ArrayList<String> Music_HasPraises = new ArrayList<String>();
		
		//保存每一个id对应的赞数,id来自保存的对象集中了
		private HashMap<String,String> movie_id_praise = new HashMap<String,String>();
		private HashMap<String,String> talk_id_praise = new HashMap<String,String>();
		private HashMap<String,String> pic_id_praise = new  HashMap<String,String>();
		private HashMap<String,String> music_id_praise = new HashMap<String,String>();
		
		//保存每一个id对应的最新评论数,id来自保存的对象集中
		private HashMap<String ,String> Talk_id_repeat_num = new HashMap<String,String>();
		
		/*请求int值*/
		private static int Get_Image = 1;
		//用于表示是否已经点击下降了,此时已经设置好了down的MarginTop数值,用于保存当系统极易关闭activity时.
		private  boolean Is_Touch_Down = false;
		//数据库辅助类
		Data_Tools tools = new Data_Tools(Ac_Dital_Info.this);
		
		@Override
		public void onCreate(Bundle b)
		{
			super.onCreate(b);
			this.setContentView(R.layout.regiment_me);
			BuildDitalInfoView();
			GetBundle();
			
			if(!member_id.equals(""))
				Get_Info();
			
		}
		
		/**
		 * @author nieyihe
		 * 获取本人的成员id
		 * */
		public void GetBundle()
		{
			String id = this.getIntent().getExtras().getString("member_id");
			String regiment_idString = this.getIntent().getExtras().getString("regiment_id");
			this.member_id = id;
			this.regiment_id = regiment_idString;
		}
		
		/**
		 * 布局加载完成
		 * */
		@Override
		public void onWindowFocusChanged(boolean hasFocus) {
			// TODO Auto-generated method stub
			super.onWindowFocusChanged(hasFocus);
			if(hasFocus && !Is_Touch_Down)
			{
				ImageView line = (ImageView) this.findViewById(R.id.regiment_me_line_3);
				int []pos = new int[2];
				line.getLocationOnScreen(pos);
				Height_y = pos[1];//获取横线下面的高度
				for(int i = 0 ;i<4;i++)
				{
					//设置高度;
					params[i].height += Scrren_Height - Height_y;
				    //将背景Layoutparams的TopMargin抬高
					params[i].topMargin += - (Scrren_Height - Height_y);
					//保存TopMargin
					Original_Margin_Top[i] = params[i].topMargin;
					
					//将设置好的params设置到Layout中
					Slide_Layout[i].setLayoutParams(params[i]);
				}
				Is_Touch_Down = true;//点击下降了
				
			}
			
		}


		@Override
		protected void onSaveInstanceState(Bundle outState) {
			// TODO Auto-generated method stub
			super.onSaveInstanceState(outState);
			outState.putBoolean("Is_TouchDown", Is_Touch_Down);
			
		}
		
		

		@Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onRestoreInstanceState(savedInstanceState);
			Is_Touch_Down = savedInstanceState.getBoolean("Is_TouchDown");
			
		}

		/**
		 * @author nieyihe
		 * @作用：主页布局的细节设置
		 * 
		 * */
		@SuppressWarnings("deprecation")
		public void BuildDitalInfoView()
		{
			
			WindowManager wm = Ac_Dital_Info.this.getWindowManager();
			Scrren_Height = wm.getDefaultDisplay().getHeight();
			
			Save_Time = this.getSharedPreferences("Time",Context.MODE_PRIVATE);
			Sign = this.getSharedPreferences("Sign", Context.MODE_PRIVATE);
			
			Slide_Layout = new RelativeLayout[4];
			params = new RelativeLayout.LayoutParams[4];
			Original_Margin_Top = new int[4];
			for(int i = 0 ; i < 4 ; i++)
			{
				Slide_Layout[i] = new RelativeLayout(Ac_Dital_Info.this);
			}
					
			me_set_host_password =  (RelativeLayout) this.findViewById(R.id.regiment_me_row_2);
			me_set_news = (RelativeLayout) this.findViewById(R.id.regiment_me_row_3);
			me_change = (Button) this.findViewById(R.id.regiment_me_change);
			Assagin = (ImageButton) this.findViewById(R.id.regiment_me_assgin);
			Btu_Movie = (ImageButton) this.findViewById(R.id.me_inner_show_movie);
			Btu_Talk = (ImageButton) this.findViewById(R.id.me_inner_show_talk);
			Btu_Pic = (ImageButton) this.findViewById(R.id.me_inner_show_pic);
			Btu_Music = (ImageButton) this.findViewById(R.id.me_inner_show_music);
			
			Btu_Movie.setOnClickListener(new OnClick_Btu());
			Btu_Talk.setOnClickListener(new OnClick_Btu());
			Btu_Pic.setOnClickListener(new OnClick_Btu());
			Btu_Music.setOnClickListener(new OnClick_Btu());
		
			Slide_Layout[0] = (RelativeLayout) this.findViewById(R.id.regiment_me_inner_show_title_1);
			Slide_Layout[1] = (RelativeLayout) this.findViewById(R.id.regiment_me_inner_show_title_2);
			Slide_Layout[2] = (RelativeLayout) this.findViewById(R.id.regiment_me_inner_show_title_3);
			Slide_Layout[3] = (RelativeLayout) this.findViewById(R.id.regiment_me_inner_show_title_4);
			
			Show_Movie_Trend = (RelativeLayout) this.findViewById(R.id.regiment_me_inner_show_1);
			Show_Music_Trend = (RelativeLayout) this.findViewById(R.id.regiment_me_inner_show_4);
			Show_Pic_Trend = (RelativeLayout) this.findViewById(R.id.regiment_me_inner_show_3);
			Show_Talk_Trend = (RelativeLayout) this.findViewById(R.id.regiment_me_inner_show_2);
			
			Movies = (ListView) this.findViewById(R.id.regiment_me_movie_list);
			Musics = (ListView) this.findViewById(R.id.regiment_me_music_list);
			Pics = (ListView) this.findViewById(R.id.regiment_me_pic_list);
			Talks = (ListView) this.findViewById(R.id.regiment_me_talk_list);
			
			Pic_Ada = new Adapter_Pic(Ac_Dital_Info.this);
			Pics.setAdapter(Pic_Ada);
			Mov_Ada = new Adapter_Movie(Ac_Dital_Info.this);
			Movies.setAdapter(Mov_Ada);
			Mus_Ada = new Adapter_Music(Ac_Dital_Info.this);
			Musics.setAdapter(Mus_Ada);
			Tal_Ada = new Adapter_Talk(Ac_Dital_Info.this);
			Talks.setAdapter(Tal_Ada);
			
			Talks.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					// TODO Auto-generated method stub
					Talk talk = Talk_Trends_Ever.get(pos);;
					
					Bundle b = new Bundle();
					b.putString("Talk_id", talk.Talk_Id);
					b.putString("member_id", member_id);
					Intent i = new Intent();
					i.setClass(Ac_Dital_Info.this, Ac_Repeat.class);
					i.putExtras(b);
					Ac_Dital_Info.this.startActivity(i);
				}
				
			});
			
			Movie_Hots = (TextView) this.findViewById(R.id.regiment_me_movie_Change_num);
			Music_Hots = (TextView) this.findViewById(R.id.regiment_me_music_Change_num);
			Pic_Hots = (TextView) this.findViewById(R.id.regiment_me_pic_Change_num);
			Talk_Hots = (TextView) this.findViewById(R.id.regiment_me_talk_Change_num);
			
			Movie_Hot_Icon = (ImageView) this.findViewById(R.id.regiment_me_movie_UpDown_pic);
			Music_Hot_Icon = (ImageView) this.findViewById(R.id.regiment_me_music_UpDown_pic);
			Pic_Hot_Icon   = (ImageView) this.findViewById(R.id.regiment_me_pic_UpDown_pic);
			Talk_Hot_Icon  = (ImageView) this.findViewById(R.id.regiment_me_talk_UpDown_pic);
			
			Host = (TextView) this.findViewById(R.id.regiment_me_host);
			
			for(int i = 0; i <4 ; i++)
			{
				//初始化params
				params[i] = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams) Slide_Layout[i].getLayoutParams());
			}
			//params[i]= (LayoutParams) 
			
			me_head = (ImageView) this.findViewById(R.id.regiment_me_head_img);
			me_set = (ImageButton) this.findViewById(R.id.regiment_me_set_info);
			me_host = (TextView) this.findViewById(R.id.regiment_me_host_text);
			me_name = (TextView) this.findViewById(R.id.regiment_me_name);
			me_virtural_name = (TextView) this.findViewById(R.id.regiment_me_virtural_name);
			me_sex = (ImageView) this.findViewById(R.id.regiment_me_sex);
			me_power = (ImageView) this.findViewById(R.id.regiment_me_power_sym);
			me_newsimg = (ImageView) this.findViewById(R.id.regiment_me_new_icon);
			
			Text_Movie_Trend_Num = (TextView) this.findViewById(R.id.me_inner_show_movie_num);
			Text_Music_Trend_Num = (TextView) this.findViewById(R.id.me_inner_show_music_num);
			Text_Pic_Trend_Num = (TextView) this.findViewById(R.id.me_inner_show_pic_num);
			Text_Talk_Trend_Num = (TextView) this.findViewById(R.id.me_inner_show_talk_num);
			
			Show_Movie_Trend.setVisibility(View.INVISIBLE);
			Show_Music_Trend.setVisibility(View.INVISIBLE);
			Show_Talk_Trend.setVisibility(View.INVISIBLE);
			Show_Pic_Trend.setVisibility(View.INVISIBLE);
			
		}
		
		/**
		 * @author nieyihe
		 * 展示自己的详细信息
		 * */
		public void ShowMeDitalInfo()
		{
			if(me != null)
			{	
				Ini_Treads();
				
				me_name.setText(me.member_true_name);
				me_virtural_name.setText(me.name);
				me_host.setText(me.member_host+"");
				Host.setText(me.praise_num +"");//设置人气度
				//设置是男是女//
				if(me.sex.equals("男"))
					me_sex.setImageResource(R.drawable.icon_pop_qz_boy);
				else if(me.sex.equals("女")) 
					me_sex.setImageResource(R.drawable.icon_pop_qz_girl_1);
				
				//设置权限头像
				if(me.member_power.equals("order"))
					me_power.setImageResource(R.drawable.icon_manager);
				else if(me.member_power.equals("member"))
					me_power.setImageResource(R.drawable.icon_group);
				
				if(me.member_power.equals("order"))
					me_change.setOnClickListener(new Onclick());
				else {
					me_change.setVisibility(View.GONE);
				}
				me_set_host_password.setOnClickListener(new Onclick());
				//设置账号密码
				me_set_news.setOnClickListener(new Onclick());
				//查看本人动态
				me_head.setOnClickListener(new Onclick());
				//设置头像
				me_set.setOnClickListener(new Onclick());
				//设置本人信息
				
				Calendar ca = Calendar.getInstance();
				if(Sign.getInt("Sign",ca.get(Calendar.DATE) - 1) != ca.get(Calendar.DATE))
				//当没有签到,则设置签到
					Assagin.setOnClickListener(new Onclick());
				else 
					Assagin.setImageResource(R.drawable.assgin_over);
				//设置签到
				
				//获取本地的赞
				Local_Movie_Praise = tools.Select_Movie();
				Local_Music_Praise = tools.Select_Music();
				Local_Pic_Praise   = tools.Select_Pic();
				Local_Talk_Praise = tools.Select_Talk();
				
				
			}
		
		}
		
		/**
		 * @author nieyihe
		 * @作用：初始化最新动态显示
		 * */
		public void Ini_Treads()
		{
			Has_Trends();			
		}
		
		
		/**
		 * @author nieyihe
		 * @作用：检测是否有最新的动态
		 * */
		public void Has_Trends()
		{
			
			if(Save_Time == null) 
				Save_Time = this.getSharedPreferences("Time",Context.MODE_PRIVATE);
			if(Save_Time!= null)
			{
				long music_time = Save_Time.getLong("music_time",System.currentTimeMillis());
				if(Math.abs(music_time - System.currentTimeMillis()) < 10)
					SaveTime("music_time");
				BmobQuery<praise> query_music = new BmobQuery<praise>();
				query_music.addWhereGreaterThanOrEqualTo("time", music_time);
				query_music.addWhereEqualTo("Create_Mem_Id", member_id);
				query_music.addWhereEqualTo("type", "Music");
				query_music.count(Ac_Dital_Info.this, praise.class, new CountListener()
				{

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(int arg0) {
						// TODO Auto-generated method stub
						if(arg0 > 0)
						{
							Music_Trends += arg0;
							Has = true;
							me_newsimg.setVisibility(View.VISIBLE);
						}
						Text_Music_Trend_Num.setText(Music_Trends+"");
					}
					
				});
				
				long pic_time = Save_Time.getLong("pic_time",System.currentTimeMillis());
				if(Math.abs(pic_time - System.currentTimeMillis()) < 10)
					SaveTime("pic_time");
				BmobQuery<praise> query_pic = new BmobQuery<praise>();
				query_pic.addWhereGreaterThanOrEqualTo("time", pic_time);
				query_pic.addWhereEqualTo("Create_Mem_Id", member_id);
				query_pic.addWhereEqualTo("type", "Pic");
				query_pic.count(Ac_Dital_Info.this, praise.class, new CountListener()
				{

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(int arg0) {
						// TODO Auto-generated method stub
						if(arg0 > 0)
						{
							Pictrue_Trends += arg0;
							Has = true;
							me_newsimg.setVisibility(View.VISIBLE);
						}
						Text_Pic_Trend_Num.setText(Pictrue_Trends+"");
					}
					
				});
				
				long movie_time = Save_Time.getLong("movie_time",System.currentTimeMillis());
				if(Math.abs(movie_time - System.currentTimeMillis()) < 10)
					SaveTime("movie_time");
				BmobQuery<praise> query_movie = new BmobQuery<praise>();
				query_movie.addWhereGreaterThanOrEqualTo("time", movie_time);
				query_movie.addWhereEqualTo("Create_Mem_Id", member_id);
				query_movie.addWhereEqualTo("type", "Movie");
				query_movie.count(Ac_Dital_Info.this, praise.class, new CountListener()
				{

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(int arg0) {
						// TODO Auto-generated method stub
						if(arg0 > 0)
						{
							Movie_Trends += arg0;
							Has = true;
							me_newsimg.setVisibility(View.VISIBLE);
						}
						Text_Movie_Trend_Num.setText(Movie_Trends+"");
					}
					
					
					
				});
				
				
				long talk_time = Save_Time.getLong("talk_time",System.currentTimeMillis());
				if(Math.abs(talk_time - System.currentTimeMillis()) < 10)
					SaveTime("talk_time");
				BmobQuery<praise> query_talk = new BmobQuery<praise>();
				query_talk.addWhereGreaterThanOrEqualTo("time", talk_time);
				query_talk.addWhereEqualTo("Create_Mem_Id", member_id);
				query_talk.addWhereEqualTo("type", "Talk");
				query_talk.count(Ac_Dital_Info.this, praise.class, new CountListener()
				{

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(int arg0) {
						// TODO Auto-generated method stub
						if(arg0 > 0)
						{
							Talk_Trends += arg0;
							Has = true;
							me_newsimg.setVisibility(View.VISIBLE);
						}
						
						Text_Talk_Trend_Num.setText(Talk_Trends+"");
						
					}
					
				});
				
				BmobQuery<regiment_repeat> query_repeat = new BmobQuery<regiment_repeat>();
				query_repeat.addWhereGreaterThanOrEqualTo("time", talk_time);
				query_repeat.addWhereEqualTo("Talk_Create_Mm_Id", member_id);
				query_repeat.count(Ac_Dital_Info.this, regiment_repeat.class, new CountListener()
				{

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(int arg0) {
						// TODO Auto-generated method stub
						if(arg0 > 0)
						{
							Talk_Trends += arg0;
							Has = true;
							me_newsimg.setVisibility(View.VISIBLE);
						}

						Text_Talk_Trend_Num.setText(Talk_Trends+"");
						
					}
					
				});
				
			}
				
		}
		
		
		/**
		 * @author nieyihe
		 * @作用：获取最新的动态的详细情况
		 * */
		public void Get_Dital_Movie_Trends()
		{
			//点击获取最新电影的动态d
			//movie_praise
			long movie_time = Save_Time.getLong("music_time",System.currentTimeMillis());
			BmobQuery<praise> query = new BmobQuery<praise>();
			query.addWhereGreaterThanOrEqualTo("time", movie_time);
			query.addWhereEqualTo("Create_Mem_Id", member_id);
			query.addWhereEqualTo("type", "Movie");
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.findObjects(Ac_Dital_Info.this, new FindListener<praise>()
					{

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(List<praise> list) {
							// TODO Auto-generated method stub
							Movie_Trends_Ever.clear();
							movie_id_praise.clear();
							
							ArrayList<String> Movie_HasPraises = new ArrayList<String>();
							Movie_HasPraises.clear();
							for(int pos = 0;pos < list.size();pos ++)
							{
								praise temp  = list.get(pos);
								String id = temp.audio_id;
								if(Movie_HasPraises.contains(id))
									//当存在这个id时就什么都不做;
									;
								else 
								{
									//否则先将这个id装入ids中
									Movie_HasPraises.add(id);
									//,以及每一个id对应的详细movie;
									Add_Bmob_Movie(id);
									//将这个id 获取用于提取最新的赞数
									Get_Movie_Id_Praise(id,pos,list.size() - 1);
								}
							}
						}
				
					});
		}
		
		/**
		 * @author nieyihe
		 * @zuoyong :通过提供的id获取对应的movie
		 * */
		public void Add_Bmob_Movie(String id)
		{
			BmobQuery<regiment_audio> query_inner = new BmobQuery<regiment_audio>();
			query_inner.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query_inner.getObject(Ac_Dital_Info.this, id, new GetListener<regiment_audio>()
					{

						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(final regiment_audio m) {
							// TODO Auto-generated method stub
							//String url,String Movie_id,BmobFile img,String intro,String Movie_member_id,String praise_num
							Movie_Trends_Ever.add(new Movie(m.url,m.getObjectId(),m.img_path,m.img_url,m.intro,m.regiment_id,m.praise_num));
							SendMessage("ReFreshMovie", "");
						}
				
					});
		}
		
		
		/**
		 * @author nieyihe
		 * @作用：通过movie_id获取最新的赞数目
		 * */
		
		public void Get_Movie_Id_Praise(final String id,final int pos,final int total)
		{
			//movie_id_praise.put(id, value)
			BmobQuery<praise> query = new BmobQuery<praise>();
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.addWhereEqualTo("audio_id", id);
			query.addWhereEqualTo("Create_Mem_Id", member_id);
			query.addWhereEqualTo("type", "Movie");
			query.count(Ac_Dital_Info.this, praise.class, new CountListener()
			{

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(int arg0) {
					// TODO Auto-generated method stub
					movie_id_praise.put(id, arg0+"");
					SendMessage("ReFreshMovie", "");
					SendMessage("SetStateMovie", "");
					if(pos == total)
						SendMessage("MovieSave", "");
				}
				
			});
		}
		
		/**
		 * @author nieyihe
		 * @作用：获取最新的动态的详细情况
		 * */
		public void Get_Dital_Pic_Trends()
		{
			//点击获取最新电影的动态
			//pic_praise
			//query_repeat.addWhereGreaterThanOrEqualTo("time", talk_time);
			long pic_time = Save_Time.getLong("pic_time",System.currentTimeMillis());
			BmobQuery<praise> query = new BmobQuery<praise>();
			query.addWhereGreaterThanOrEqualTo("time", pic_time);
			query.addWhereEqualTo("Create_Mem_Id", member_id);
			query.addWhereEqualTo("type", "Pic");
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.findObjects(Ac_Dital_Info.this, new FindListener<praise>()
					{

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(List<praise> list) {
							// TODO Auto-generated method stub
							
							Pic_Trends_Ever.clear();
							pic_id_praise.clear();
							
							ArrayList<String> Pic_HasPraises = new ArrayList<String>();
							Pic_HasPraises.clear();
							for(int pos = 0;pos < list.size();pos ++)
							{
								praise temp  = list.get(pos);
								String id = temp.audio_id;
								if(Pic_HasPraises.contains(id))
									//当存在这个id时就什么都不做;
									;
								else 
								{
									//否则先将这个id装入ids中
									Pic_HasPraises.add(id);
									//将这个id 获取用于提取最新的赞数
									
									Get_Pic_Id_Praise(id,pos,list.size() - 1);
									//,以及每一个id对应的详细movie;
									Add_Bmob_Pic(id);
								}
							}
						}
				
					});
		}
		/**
		 * @author nieyihe
		 * @zuoyong :获取当前id所对应的所有的赞的数
		 * */
		public void Get_Pic_Id_Praise(final String id,final int pos,final int total)
		{
			BmobQuery<praise> query = new BmobQuery<praise>();
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.addWhereEqualTo("audio_id", id);
			query.addWhereEqualTo("Create_Mem_Id", member_id);
			query.addWhereEqualTo("type", "Pic");
			query.count(Ac_Dital_Info.this, praise.class, new CountListener()
			{

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(int arg0) {
					// TODO Auto-generated method stub
					pic_id_praise.put(id, arg0+"");
					SendMessage("ReFreshPicture", "");
					SendMessage("SetStatePic", "");
					if(pos == total)//当为最后一个赞的设置时 才开始增加
						SendMessage("PicSave", "");
				}
				
			});
		}
		
		
		/**
		 * @author nieyihe
		 * @作用:获取图片的pictrue
		 * */
		public void Add_Bmob_Pic(String id)
		{
			BmobQuery<regiment_audio> query_inner = new BmobQuery<regiment_audio>();
			query_inner.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query_inner.getObject(Ac_Dital_Info.this, id, new GetListener<regiment_audio>()
					{

						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(final regiment_audio m) {
							// TODO Auto-generated method stub
							//String id,BmobFile pic,String pic_praise_num,String pic_member_id
							Pic_Trends_Ever.add(new Picture(m.getObjectId(),m.img_path,m.img_url,m.praise_num,m.member_id));
							
							SendMessage("ReFreshPicture", "");
							new Thread()
							{
								@Override
								public void run()
								{
									try {
										pictureBitmaps.put(m.getObjectId(),BitmapLocalFactory.CreateBitmap(Ac_Dital_Info.this,m.img_path,m.img_url,BitmapLocalFactory.NORMAL));
										SendMessage("ReFreshPicture", "");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}.start();
						}
				
					});
		}
		
		/**
		 * @author nieyihe
		 * @作用：获取最新的动态的详细情况
		 * */
		public void Get_Dital_Talk_Trends()
		{
			//点击获取最新电影的动态
			//talk_praise
			//query_repeat.addWhereGreaterThanOrEqualTo("time", talk_time);
			long talk_time = Save_Time.getLong("talk_time",System.currentTimeMillis());
			Log.w("TalkTime",talk_time +"");
			BmobQuery<praise> query = new BmobQuery<praise>();
			query.addWhereGreaterThanOrEqualTo("time", talk_time);
			query.addWhereEqualTo("Create_Mem_Id", member_id);
			query.addWhereEqualTo("type", "Talk");
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.findObjects(Ac_Dital_Info.this, new FindListener<praise>()
					{

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(List<praise> list) {
							// TODO Auto-generated method stub
							
							Talk_Trends_Ever.clear();
							talk_id_praise.clear();
							
							ArrayList<String> Talk_HasPraises = new ArrayList<String>();
							Talk_HasPraises.clear();
							for(int pos = 0;pos < list.size() ; pos ++)
							{
								praise temp  = list.get(pos);
								String id = temp.audio_id;
								if(Talk_HasPraises.contains(id))
									//当存在这个id时就什么都不做;
									;
								else 
								{
									//避免重复的获取同一个talk的赞
									//否则先将这个id装入ids中
									Talk_HasPraises.add(id);
									//以及每一个id对应的详细talk;
									Add_Bmob_talk(id);
									//将这个id 获取用于提取最新的赞数
									Get_Talk_Id_Praise(id,pos,list.size() - 1);
								}
							}
						}
				
					});
		}
		
		/**
		 * @author nieyihe
		 * @获取id对应的赞的数目
		 * */
		public void Get_Talk_Id_Praise(final String id,final int pos,final int total)
		{
			//保存最新的赞数
			//
			BmobQuery<praise> query = new BmobQuery<praise>();
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.addWhereEqualTo("audio_id", id);
			query.addWhereEqualTo("Create_Mem_Id", member_id);
			query.addWhereEqualTo("type", "Talk");
			query.count(Ac_Dital_Info.this, praise.class, new CountListener()
			{

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(int arg0) {
					// TODO Auto-generated method stub
					if(!talk_id_praise.containsKey(id))
						{
							talk_id_praise.put(id, arg0+"");
						
							SendMessage("ReFreshTalk", "");
							SendMessage("SetStateTalk", "");
						if(pos == total)
							SendMessage("TalkSave", "");
						}
					
				}
				
			});
		}
		
		//获取所有大于上次刷新时间的Repeat,获取对应的id从而获取Bmobobject以及新评价的数目
		public void Get_Repeat_Talk()
		{
			final long talk_time = Save_Time.getLong("talk_time",System.currentTimeMillis());
			BmobQuery<regiment_repeat> query = new BmobQuery<regiment_repeat>();
			query.addWhereGreaterThanOrEqualTo("repeat_time", talk_time);
			query.addWhereEqualTo("Talk_Create_Mm_Id", member_id);
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.findObjects(Ac_Dital_Info.this, new FindListener<regiment_repeat>()
					{

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(List<regiment_repeat> arg0) {
							// TODO Auto-generated method stub
							
							Talk_id_repeat_num.clear();
							
							ArrayList<String> Talk_HasRepeat = new ArrayList<String>();
							Talk_HasRepeat.clear();
							for(regiment_repeat repeat :arg0)
							{
								if(!Talk_HasRepeat.contains(repeat.repeat_talk_id))
								{
									Talk_HasRepeat.add(repeat.repeat_talk_id);
									
									//避免重复regiment_repeat中拥有统一个talk_id的repeat,只读取一个Talk_id;
									
									//并且只保存（id是talk_id的,且时间是超过上次刷新时间的）的repeat数目;
									Get_Talk_Repeat_Num(repeat.repeat_talk_id,talk_time);
									//并且在Talk_Trends_Ever中只保存一遍bmobobject
									Add_Bmob_talk(repeat.repeat_talk_id);
								}
							}
						}
				
					});
			//Talk_id_repeat_num.put(id,repeat_num);
			//通过最新的评论找到对应的id，避免重复
			//Add_Bmob_talk 
		}
		
		public void Get_Talk_Repeat_Num(final String id,long time)
		{
			BmobQuery<regiment_repeat> query = new BmobQuery<regiment_repeat>();
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.addWhereGreaterThanOrEqualTo("repeat_time", time);
			query.addWhereEqualTo("repeat_talk_id", id);
			query.addWhereEqualTo("Talk_Create_Mm_Id", member_id);
			query.count(Ac_Dital_Info.this, regiment_repeat.class, new CountListener()
			{

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(int arg0) {
					// TODO Auto-generated method stub
					if(!Talk_id_repeat_num.containsKey(id))
						{
							Talk_id_repeat_num.put(id, arg0+"");
							SendMessage("ReFreshTalk", "");
						}
				}
				
			});
		}
		
		public void Add_Bmob_talk(String id)
		{
			BmobQuery<regiment_talk> query = new BmobQuery<regiment_talk>();
			query.getObject(Ac_Dital_Info.this, id, new GetListener<regiment_talk>()
			{

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(regiment_talk arg0) {
					// TODO Auto-generated method stub
					//BmobFile head,String name,String time,String word,String praise_num,String id,String talk_member_id,BmobFile img
					Talk talk = new Talk(arg0.talk_head_path,arg0.talk_head_url,arg0.talk_order,arg0.talk_time,arg0.talk_word,arg0.talk_praise_num,arg0.getObjectId(),arg0.member_id,arg0.talk_img_path,arg0.talk_img_url);
					
					if(!Talk_Trends_Ever.contains(talk))
					{
						Talk_Trends_Ever.add(talk);
					}
					
					SendMessage("ReFreshTalk", "");
				}

				
			});
			/*if(!Talk_Trends_Ever.contains(object))*/
		}
		
		
		/**
		 * @author nieyihe
		 * @作用：获取最新的动态的详细情况
		 * */
		public void Get_Dital_Music_Trends()
		{
			//点击获取最新电影的动态
			//movie_praise
			//query_repeat.addWhereGreaterThanOrEqualTo("time", talk_time);
			//talk_praise,pic_praise
			long music_time = Save_Time.getLong("music_time",System.currentTimeMillis());
			BmobQuery<praise> query = new BmobQuery<praise>();
			query.addWhereGreaterThanOrEqualTo("time", music_time);
			query.addWhereEqualTo("Create_Mem_Id", member_id);
			query.addWhereEqualTo("type", "Music");
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			
			query.findObjects(Ac_Dital_Info.this, new FindListener<praise>()
					{

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(List<praise> list) {
							// TODO Auto-generated method stub
							
							Music_Trends_Ever.clear();
							music_id_praise.clear();
							
							ArrayList<String> Music_HasPraises = new ArrayList<String>();
							Music_HasPraises.clear();
							for(int pos = 0; pos < list.size() ; pos ++)
							{
								praise temp = list.get(pos);
								String id = temp.audio_id;
								if(Music_HasPraises.contains(id))
									//当存在这个id时就什么都不做;
									;
								else 
								{
									//否则先将这个id装入ids中
									Music_HasPraises.add(id);
									//将这个id 获取用于提取最新的赞数
									
									Get_Music_Id_Praise(id,pos,list.size() - 1);
									//,以及每一个id对应的详细movie;
									Add_Bmob_Music(id);
								}
							}
						}
				
					});
			
		}
		
		public void Get_Music_Id_Praise(final String id,final int pos , final int total)
		{
			BmobQuery<praise> query = new BmobQuery<praise>();
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.addWhereEqualTo("audio_id", id);
			query.addWhereEqualTo("Create_Mem_Id", member_id);
			query.addWhereEqualTo("type", "Music");
			query.count(Ac_Dital_Info.this, praise.class, new CountListener()
			{

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(int arg0) {
					// TODO Auto-generated method stub
					music_id_praise.put(id, arg0+"");
					SendMessage("ReFreshMusic", "");
					SendMessage("SetStateMusic", "");
					if( pos == total )
					{
						SendMessage("MusicSave", "");
					}
				}
				
			});
		}
		
		
		public void Add_Bmob_Music(String id)
		{
			BmobQuery<regiment_music> query_inner = new BmobQuery<regiment_music>();
			query_inner.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query_inner.getObject(Ac_Dital_Info.this, id, new GetListener<regiment_music>()
					{

						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(regiment_music m) {
							// TODO Auto-generated method stub
							//String music_id,String music_mem_id,Integer download_num,String intro
							Music_Trends_Ever.add(new Music(m.getObjectId(),m.member_id,m.download_num,m.Intro));
							SendMessage("ReFreshMusic", "");
						}
				
					});
		}
		
		/**
		 * @author nieyihe
		 * @作用：设计标签的触击监听器
		 * */
		private class OnClick_Btu implements View.OnClickListener
		{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId())
				{
					case R.id.me_inner_show_movie:
					{
						
						if(me!=null)//是团员
						{
							Get_Dital_Movie_Trends();
							//先通过保存的时间获取最新的动态
							//在更新 s最新时间 
							/**/
							//显示所有最新movie的动态
						}
						Show_Movie_Trend.setVisibility(View.VISIBLE);
						Show_Music_Trend.setVisibility(View.INVISIBLE);
						Show_Pic_Trend.setVisibility(View.INVISIBLE);
						Show_Talk_Trend.setVisibility(View.INVISIBLE);
					
						//执行增加总hots值
						
						//tools.Insert_Movie(movie_id_praise);
						break;
					}
					
					case R.id.me_inner_show_music:
					{
						if(me!=null)
						{
							Get_Dital_Music_Trends();
							
						}
						Show_Movie_Trend.setVisibility(View.INVISIBLE);
						Show_Music_Trend.setVisibility(View.VISIBLE);
						Show_Pic_Trend.setVisibility(View.INVISIBLE);
						Show_Talk_Trend.setVisibility(View.INVISIBLE);
						
						break;
					}
					
					case R.id.me_inner_show_pic:
					{

						if(me!=null)
						{
							Get_Dital_Pic_Trends();
	
							//显示所有最新picture的动态
						}
						Show_Movie_Trend.setVisibility(View.INVISIBLE);
						Show_Music_Trend.setVisibility(View.INVISIBLE);
						Show_Pic_Trend.setVisibility(View.VISIBLE);
						Show_Talk_Trend.setVisibility(View.INVISIBLE);
						
						//执行递增总hots值
						//tools.Insert_Pic(pic_id_praise);
						break;
					}
					
					case R.id.me_inner_show_talk:
					{
					
						if(me!=null)
						{
							Get_Repeat_Talk();
							//显示所有最新talk的动态
							Get_Dital_Talk_Trends();
						}
						
						Show_Movie_Trend.setVisibility(View.INVISIBLE);
						Show_Music_Trend.setVisibility(View.INVISIBLE);
						Show_Pic_Trend.setVisibility(View.INVISIBLE);
						Show_Talk_Trend.setVisibility(View.VISIBLE);
						
						
						//执行递增总hots值
						
						//tools.Insert_Pic(talk_id_praise);
						break;
					}
				}
			}

		
		}
		
	
		
		/**
		 * @author nieyihe
		 * @作用：设计一个点击监听器
		 * */
		private class Onclick implements View.OnClickListener
		{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch(arg0.getId())
				{
					case R.id.regiment_me_change:
					{
						 if(me == null) break;
						 
						 final Dialog dialog = new Dialog(Ac_Dital_Info.this,R.style.Item_Dialog);
						 View view = (View) LayoutInflater.from(Ac_Dital_Info.this).inflate(R.layout.dital_info_exchange_dital, null);
						 
						 final EditText newtuanzhang = (EditText) view.findViewById(R.id.dital_info_exchange_order);
						 final EditText newfutuanzhang  = (EditText) view.findViewById(R.id.dital_info_exchange_fuorder);
						 final EditText newtuanzhishu = (EditText) view.findViewById(R.id.dital_info_exchange_tuanzhishu); 	
						 
						 Button sure = (Button) view.findViewById(R.id.dital_info_exchange_dialog_sure);
						 Button cancel = (Button) view.findViewById(R.id.dital_info_exchange_dialog_cancel);
						 
						 dialog.setContentView(view);
						 dialog.setCanceledOnTouchOutside(false);
						 
						 sure.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String newtuanzhangString = newtuanzhang.getText().toString().trim();
								String newfutuanzhangString = newfutuanzhang.getText().toString().trim();
								String newtuanzhishuString = newtuanzhishu.getText().toString().trim();
								if(newtuanzhangString.equals("") || newfutuanzhangString.equals("") || newtuanzhishuString.equals(""))
									{
										Toast.makeText(Ac_Dital_Info.this, "没有设置完全", Toast.LENGTH_SHORT).show();
										return;
									}
								
								UpdateRegimentMember(newtuanzhangString,newtuanzhishuString,newfutuanzhangString);
								dialog.dismiss();
							}
						});
						 cancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						
						 dialog.show();
						 break;
						
					}
					
					 case  R.id.regiment_me_row_2:
					 {
						 //重新设置密码
						 if(me == null) break;
						 
						 final Dialog dialog = new Dialog(Ac_Dital_Info.this,R.style.Item_Dialog);
						 View view = (View) LayoutInflater.from(Ac_Dital_Info.this).inflate(R.layout.dital_info_password_dialog, null);
						 
						 final EditText oldpassword = (EditText) view.findViewById(R.id.dital_info_password_dialog_oldpassword);
						 final EditText newpassword  = (EditText) view.findViewById(R.id.dital_info_password_dialog_newpassword);
						 	
						 Button sure = (Button) view.findViewById(R.id.dital_info_password_dialog_sure);
						 Button cancel = (Button) view.findViewById(R.id.dital_info_password_dialog_cancel);
						 
						 dialog.setContentView(view);
						 dialog.setCanceledOnTouchOutside(false);
						 
						 sure.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String oldpasswordString = oldpassword.getText().toString().trim();
								String newpasswordString = newpassword.getText().toString().trim();
								if(newpasswordString.equals(""))
									{
										Toast.makeText(Ac_Dital_Info.this, "新密码没有设置", Toast.LENGTH_SHORT).show();
										return;
									}
								if(oldpasswordString.equals(""))
									{
										Toast.makeText(Ac_Dital_Info.this, "请输入原密码,以便核对", Toast.LENGTH_SHORT).show();
										return;
									}
								if(me.member_password.equals(oldpasswordString))
									{
										UpdatePassword(newpasswordString);
									}
								else{
										Toast.makeText(Ac_Dital_Info.this, "验证不对,请核实原密码", Toast.LENGTH_SHORT).show();
									}
								dialog.dismiss();
							}
						});
						 cancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						
						 dialog.show();
						 break;
						 
					 }
					 
					 case R.id.regiment_me_row_3:
					 {
						 
						 //点击查看最新动态
						 if(params[0].topMargin<=0)
						 {
							 SlidingDown = true;//设置可以下滑
							 new Slide_Down_Tag().start();//开始下滑
							 
						 }
						 else if(params[0].topMargin>=0)
						 {
							 SlidingDown = false;//设置此时不可以下滑
							 new Slide_Up_Tag().start();//开始上升
						 }
						 break;
						 
					 }
					 
					 case R.id.regiment_me_assgin:
					 {
						 UpdateSignHots();
						 //点击签到
						 break;
					 }
					 
					 case R.id.regiment_me_set_info:
					 {
						 if(me == null) break;
						 
						 //设置本人信息
						 Dialog dialog = new Dialog(Ac_Dital_Info.this,R.style.Item_Dialog);
						 RelativeLayout view = (RelativeLayout) LayoutInflater.from(Ac_Dital_Info.this).inflate(R.layout.dital_info_change_dialog, null);
						 
						 EditText truename = (EditText) view.findViewById(R.id.dital_info_change_dialog_truename);
						 TextView oldname  = (TextView) view.findViewById(R.id.dital_info_change_dialog_oldname);
						 	oldname.setText(me.member_true_name);
						 EditText yewuname = (EditText)view.findViewById(R.id.dital_info_change_dialog_yewuname);
						 TextView oldyewuname = (TextView) view.findViewById(R.id.dital_info_change_dialog_oldyewuname);
						 	oldyewuname.setText(me.name);
						 Button sure = (Button) view.findViewById(R.id.dital_info_change_dialog_sure);
						 Button cancel = (Button) view.findViewById(R.id.dital_info_change_dialog_cancel);
						 
						 dialog.setContentView(view);
						 dialog.setCanceledOnTouchOutside(false);
						 
						 sure.setOnClickListener(new Dialog_Onclick(truename,yewuname,dialog));
						 cancel.setOnClickListener(new Dialog_Onclick(null,null,dialog));
						
						 dialog.show();
						 break;
					 }
					 
					 case R.id.regiment_me_head_img:
					 {
						 //设置头像
						 Intent i = new Intent();
						 i.setType("image/*");
						 i.setAction(Intent.ACTION_PICK);
						 Ac_Dital_Info.this.startActivityForResult(i, Get_Image);
						 break;
					 }
				}
			}
			
			
		}
		
		/**
		 * @author nieyihe
		 * 更新密码
		 * */
		public void UpdatePassword(String password)
		{
			regiment_member mem = new regiment_member();
			mem.member_password = password;
			mem.update(Ac_Dital_Info.this, member_id, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Toast.makeText(Ac_Dital_Info.this, "更新完成", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		/**
		 * @author nieyihe
		 * 更新团员
		 * */
		public void  UpdateRegimentMember(String order,String tuanzhishu,String fuorder)
		{
			regiment_table regiment = new regiment_table();
			regiment.regiment_order = order ;
			regiment.regiment_sec_order = fuorder;
			regiment.regiment_secretary = tuanzhishu;
			regiment.update(Ac_Dital_Info.this, regiment_id, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Toast.makeText(Ac_Dital_Info.this, "更新成功", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		/**
		 * @author nieyihe
		 * 对话框保存名字监听器
		 * */
		public class Dialog_Onclick implements View.OnClickListener
		{
			EditText True_Name;
			EditText Yewu_Name;
			Dialog dialog;
			public Dialog_Onclick(EditText truename,EditText yewuname,Dialog dialog)
			{
				this.True_Name = truename;
				this.Yewu_Name = yewuname;
				this.dialog = dialog;
			}
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch(arg0.getId())
				{
					case R.id.dital_info_change_dialog_sure:
					{
						String nameString = True_Name.getText().toString().trim();
						String yewuString = Yewu_Name.getText().toString().trim();
						if(nameString.equals("") || yewuString.equals(""))
						{
							Toast.makeText(Ac_Dital_Info.this,"还有数据没有填写", Toast.LENGTH_LONG).show();
							break;
						}
						Update_Name(nameString,yewuString);
						me_name.setText(nameString);
						me_virtural_name.setText(yewuString);
						dialog.dismiss();
						break;
					} 
					
					case R.id.dital_info_change_dialog_cancel:
					{
						dialog.dismiss();
						break;
					}
				}
				
			}
			
		}		
		
		/**
		 * @author nieyihe
		 * 更新名字
		 * */
		public void Update_Name(String True_Name,String Yewu_Name)
		{
			regiment_member mem = new regiment_member();
			mem.member_ture_name = True_Name;
			mem.member_name = Yewu_Name;
			
			mem.update(Ac_Dital_Info.this, member_id, new UpdateListener()
			{

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess() 
				{
					// TODO Auto-generated method stub
					Toast.makeText(Ac_Dital_Info.this, "更新成功", Toast.LENGTH_LONG).show();
				}
				
			});
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			
			if(requestCode == Get_Image && resultCode == RESULT_OK && data != null)
			{
				Uri ImageUri = data.getData();
				String []proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(ImageUri,proj,null,null,null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(proj[0]);
				
	            ImagePath = cursor.getString(columnIndex);
	           
	            cursor.close();
	            
	            me_head.setImageBitmap(android.graphics.BitmapFactory.decodeFile(ImagePath));
	            Update_Img();
			}
			
		}
		
		/**
		 * @author nieyihe
		 * @作用：保存头像
		 * */
		public void Update_Img()
		{
			if(!ImagePath.equals(""))
			{
				final Notification notification = NotificationLocalFactory.NotificationViewCreate(Ac_Dital_Info.this, R.layout.notification_message, R.drawable.input, 0);
				NotificationLocalFactory.SetViewText(notification.contentView, "小控帮助更换头像中..", R.id.notification_word, notification, 0);
				
				BmobProFile.getInstance(Ac_Dital_Info.this).upload(ImagePath,new UploadListener() {
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onSuccess(String Path, String Url) {
						// TODO Auto-generated method stub
						Update_Mem_Head(Path,Url);
						Toast.makeText(Ac_Dital_Info.this, "更新成功", Toast.LENGTH_SHORT).show();
						NotificationLocalFactory.SetViewText(notification.contentView, "更新成功", R.id.notification_word, notification, 0);
						NotificationLocalFactory.Cancel(0);
					}
					
					@Override
					public void onProgress(int arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}
		
		/**
		 * @author nieyihe
		 * @zuoyong :上传更新头像
		 * */
		public void Update_Mem_Head(String img_path,String img_url)
		{
			regiment_member mem = new regiment_member();
			mem.member_head_path = img_path;
			mem.member_head_url  = img_url;
			mem.update(Ac_Dital_Info.this, member_id, new UpdateListener()
			{

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(Ac_Dital_Info.this, "更新头像失败", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Toast.makeText(Ac_Dital_Info.this, "更新头像成功", Toast.LENGTH_LONG).show();
				}
				
			});
		}
		

		/**
		 * @author nieyihe
		 * @作用：获取本人信息
		 * */
		
		private void Get_Info()
		{
			BmobQuery<regiment_member> info = new BmobQuery<regiment_member>();
			info.getObject(Ac_Dital_Info.this, member_id, new GetListener<regiment_member>()
					{

						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(regiment_member arg0) {
							// TODO Auto-generated method stub
							me = new member(arg0.member_head_path,arg0.member_head_url,arg0.regiment_id,arg0.getObjectId(),arg0.member_name,arg0.member_sex,arg0.member_power,arg0.member_host,arg0.member_password,arg0.member_ture_name,arg0.praise_num);
							SetHeadImg();
							ShowMeDitalInfo();
						}
				
					});
		}
		
		/***
		 * @author nieyihe
		 * @ 更新签到的
		 * */
		public void UpdateSignHots()
		{
			Assagin.setImageResource(R.drawable.assgin_over);
			
			new Thread(){
			
				@Override
				public void run()
				{
						Integer i = Integer.parseInt(Host.getText().toString());
						 /*****/
						++i;
						me.praise_num = i;
						
						SendMessage("Change",""+i);
						
						 Editor editer = Sign.edit();
						 
						 Calendar ca = Calendar.getInstance();
						 editer.putInt("Sign", ca.get(Calendar.DATE));
						 //设置已签到图标
						 editer.commit();
						 Assagin.setOnClickListener(null);
						 
						 /*****/
						 
						regiment_member mem = new regiment_member();
						mem.praise_num = i;
						mem.update(Ac_Dital_Info.this, member_id, new UpdateListener()
						{

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								
								 Assagin.setImageResource(R.drawable.assgin_over);
								 Editor editer = Sign.edit();
								 Calendar ca = Calendar.getInstance();
								 editer.putInt("Sign", ca.get(Calendar.DATE));
								 //设置已签到图标
								 editer.commit();
								 Assagin.setOnClickListener(null);
								 
							}
							
						});
					
				}
			}.start();
		}
	
		
	private class MyHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("MovieSave"))
			{
				UpdateNewInfo(Movie_Hots.getId());
			}
			if(b.containsKey("MusicSave"))
			{
				UpdateNewInfo(Music_Hots.getId());
			}
			if(b.containsKey("PicSave"))
			{
				UpdateNewInfo(Pic_Hots.getId());
			}
			if(b.containsKey("TalkSave"))
			{
				UpdateNewInfo(Talk_Hots.getId());
			}
			if(b.containsKey("ReFreshPicture"))
			{
				Pics_Change_Hots = 0;
				Pic_Ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(Pics, Ac_Dital_Info.this);
			}
			
			if(b.containsKey("ReFreshMovie"))
			{
				Movies_Change_Hots = 0;
				Mov_Ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(Movies, Ac_Dital_Info.this);
			}
			
			if(b.containsKey("ReFreshMusic"))
			{
				Musics_Change_Hots = 0;
				Mus_Ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(Musics, Ac_Dital_Info.this);
			}
			
			if(b.containsKey("ReFreshTalk"))
			{
				Talks_Change_Hots = 0;
				Tal_Ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(Talks, Ac_Dital_Info.this);
			}
			
			if(b.containsKey("SetStateMovie"))
			{
				if(Movies_Change_Hots >= 0)
				{
					if(Movie_Hots != null)
					Movie_Hots.setText(Math.abs(Movies_Change_Hots) + "");
					// 默认为上涨
					
				}
				
				Text_Movie_Trend_Num.setVisibility(View.VISIBLE);
			}
			
			if(b.containsKey("SetStateMusic"))
			{
				if(Musics_Change_Hots >= 0)
				{
					if(Music_Hots != null)
					Music_Hots.setText(Math.abs(Musics_Change_Hots) + "");
					// 默认为上涨
					
				}
				
				Text_Music_Trend_Num.setVisibility(View.VISIBLE);
				//显示所有最新music的动态
			}
			
			if(b.containsKey("SetStateTalk"))
			{
				if(Talks_Change_Hots >= 0)
				{
					Talk_Hots.setText(Math.abs(Talks_Change_Hots) + "");
					// 默认为上涨
				}
				
				
				Text_Talk_Trend_Num.setVisibility(View.VISIBLE);
			}
			
			if(b.containsKey("SetStatePic"))
			{
				
				if(Pics_Change_Hots >= 0)
				{
					Pic_Hots.setText(Math.abs(Pics_Change_Hots) + "");
					// 默认为上涨
				}
				
				Text_Pic_Trend_Num.setVisibility(View.VISIBLE);
			}
			
			if(b.containsKey("ShowHeadImg"))
			{
				me_head.setImageBitmap(headBitmap);
			}
			
			if(b.containsKey("SlidingDownTag"))
			{
				for(int i = 0; i < 4 ;i++)
				{
					int j = params[i].topMargin;
					j+=4;
					params[i].topMargin = j;
					Slide_Layout[i].setLayoutParams(params[i]);
				}
			}
			
			if(b.containsKey("SlidingUpTag"))
			{
				for(int j = 0; j < 4 ;j++)
				{
					int i = params[j].topMargin;
					i-=4;
					params[j].topMargin = i;
					Slide_Layout[j].setLayoutParams(params[j]);
				}
			}
			
			if(b.containsKey("SlidingDownLayout"))
			{
				int id = Integer.parseInt(b.get("SlidingDownLayout").toString());
				int i = params[id].topMargin;
				i +=2;
				params[id].topMargin = i;
				Slide_Layout[id].setLayoutParams(params[id]);
				 
			}
			
			if(b.containsKey("Change"))
			{
				//显示Host修改数字
				Host.setText(b.getString("Change"));
			}
			
			if(b.containsKey("" + R.id.regiment_me_talk_Change_num) )
			{
				int num = Integer.parseInt(b.getString("" + R.id.regiment_me_talk_Change_num));
				Talk_Hots.setText(num+"");
			}
			
			if(b.containsKey("" + R.id.regiment_me_music_Change_num))
			{
				int num = Integer.parseInt(b.getString("" + R.id.regiment_me_music_Change_num));
				Music_Hots.setText(num+"");
			}
			
			if(b.containsKey("" + R.id.regiment_me_pic_Change_num))
			{
				int num = Integer.parseInt(b.getString("" + R.id.regiment_me_pic_Change_num));
				Pic_Hots.setText(num+"");
			}
			
			if(b.containsKey(""+R.id.regiment_me_movie_Change_num))
			{
				int num = Integer.parseInt(b.getString("" + R.id.regiment_me_movie_Change_num));
				Movie_Hots.setText(num+"");
			}
		}
		
	}	
	
	/**
	 * @author nieyihe
	 * @作用：点击下降标签
	 * */
	private class Slide_Down_Tag extends Thread
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			while(params[0].topMargin <= 0 && SlidingDown)
			{
				try {
					Bundle b = new Bundle();
					b.putString("SlidingDownTag", "");
					Message msg = new Message();
					msg.setData(b);
					handler.sendMessage(msg);
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}	
	
	/**
	 * @author nieyihe
	 * @作用：点击上升标签
	 * */
	private class Slide_Up_Tag extends Thread
	{
	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				
				while(params[0].topMargin >= Original_Margin_Top[0] && !SlidingDown)
				{
					try {
						Bundle b = new Bundle();
						b.putString("SlidingUpTag", "");
						Message msg = new Message();
						msg.setData(b);
						handler.sendMessage(msg);
						Thread.sleep(15);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		
	}
	
	
	public class Adapter_Movie extends BaseAdapter
	{

		Context context;
		LayoutInflater l;
		public Adapter_Movie(Context context)
		{
			this.context = context ;
			l = LayoutInflater.from(context);
		}
		
		//Movie_Trends_Ever movie_id_praise
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Movie_Trends_Ever.size();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			return pos;
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Binder_Movie bind = null;
			if(arg1 == null)
			{
				arg1 = l.inflate(R.layout.me_movie_dital_list, null);
				bind = new Binder_Movie();
				bind.Num = (TextView) arg1.findViewById(R.id.me_movie_dital_list_Num);
				bind.UpDown = (ImageView) arg1.findViewById(R.id.me_movie_dital_list_UpDownimg);
				bind.Intro = (TextView) arg1.findViewById(R.id.me_movie_dital_list_intro);
				
				arg1.setTag(bind);
			}
			else
			{
				bind = (Binder_Movie)arg1.getTag();
			}
			
			Movie movie = null;
			if(Movie_Trends_Ever.size() > arg0)
			  movie = Movie_Trends_Ever.get(arg0);
			
			if(Local_Movie_Praise != null && Local_Movie_Praise.get(movie.Movie_id) != null)
			{
				String beforeString = Local_Movie_Praise.get(movie.Movie_id);
				String nowString = movie_id_praise.get(movie.Movie_id);
				
				int before = Integer.parseInt(beforeString);
				int now = Integer.parseInt(nowString);
				
				/*if(before > now)
				{
					bind.UpDown.setImageResource(R.drawable.icon_down);
				}*/
				//此处不再设置Hots的数值
				if(now > before){
					Movies_Change_Hots += now - before;//保存变化的Hots数值
					bind.Num.setText(Math.abs(before - now)+"");//设置差值的绝对值;
				}
				else {
					bind.Num.setText("0");
				}
			}
			//当本地赞保存了时就进行比较 
			//如没有保存就只可能上涨。
			else
			{
				String num = movie_id_praise.get(movie.Movie_id);
				
				//保存变化的Hots值
				Movies_Change_Hots += Integer.parseInt(num == null ? "0" : num);
				
				//显示赞数
				bind.Num.setText(num);
			}
		
			
			bind.Intro.setText(movie.intro);
			return arg1;
		}
		
	}
	
	public class Adapter_Music extends BaseAdapter
	{

		Context context;
		LayoutInflater l;
		public Adapter_Music(Context context)
		{
			this.context = context ;
			l = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Music_Trends_Ever.size();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			return pos;
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(int pos, View contentview, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Bindler_Music bind ;
			if(contentview == null)
			{
				contentview = l.inflate(R.layout.me_music_dital_list, null);
				bind = new Bindler_Music();
				bind.Music_Intro = (TextView) contentview.findViewById(R.id.me_music_dital_list_intro);
				bind.Pra_Num = (TextView) contentview.findViewById(R.id.me_music_dital_list_Num);
				bind.UpDown = (ImageView) contentview.findViewById(R.id.me_music_dital_list_UpDownimg);
				
				contentview.setTag(bind);
			}
			else
			{
				bind = (Bindler_Music)contentview.getTag();
			}
			Music music = null;
			if(Music_Trends_Ever.size() > pos)
				music = Music_Trends_Ever.get(pos);
			
			if(Local_Music_Praise != null && Local_Music_Praise.get(music.Music_id) != null)
			{
				String beforeString = Local_Music_Praise.get(music.Music_id);
				String nowString = music_id_praise.get(music.Music_id);
				
				int before = Integer.parseInt(beforeString == null ? "0" : beforeString);
				int now = Integer.parseInt(nowString == null ? "0" : nowString);
				
			/*	if(before > now)
				{
					bind.UpDown.setImageResource(R.drawable.icon_down);
				}*/
				
				//以下不再设置下降Hots的数值
				if(now > before)
				{
					Musics_Change_Hots += now - before;//保存变化的Hots数值
				
					bind.Pra_Num.setText(Math.abs(before - now)+"");//设置差值的绝对值;
				}
				else {
					bind.Pra_Num.setText("0");
				}
			}
			//当本地赞保存了时就进行比较 
			//如没有保存就只可能上涨。
			else
			{
				String num = music_id_praise.get(music.Music_id);
				
				//保存变化的Hots值
				Musics_Change_Hots += Integer.parseInt(num == null ? "0" : num );
				
				//显示赞数
				bind.Pra_Num.setText(num);
			}
			
			bind.Music_Intro.setText(music.intro);
			return contentview;
		}
		
	}
	
	public class Adapter_Talk extends BaseAdapter
	{


		Context context;
		LayoutInflater l;
		public Adapter_Talk(Context context)
		{
			this.context = context ;
			l = LayoutInflater.from(context);
		}
		
		//Talk_Trends_Ever talk_id_praise Talk_id_repeat_num
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Talk_Trends_Ever.size();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			return pos;
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Binder_Talk bind ;
			if(arg1 == null)
			{
				arg1  = l.inflate(R.layout.me_talk_dital_list, null);
				bind = new Binder_Talk();
				
				bind.Pra_Num = (TextView) arg1.findViewById(R.id.me_talk_dital_list_HostNum);
				bind.Repeat_Num = (TextView) arg1.findViewById(R.id.me_talk_dital_list_repeat_num);
				bind.Time = (TextView) arg1.findViewById(R.id.me_talk_dital_list_time);
				bind.UpDown = (ImageView) arg1.findViewById(R.id.me_talk_dital_list_UpDownimg);
				bind.Word = (TextView) arg1.findViewById(R.id.me_talk_dital_list_word);
				
				arg1.setTag(bind);
			}
			else
			{
				bind = (Binder_Talk)arg1.getTag();
			}
			 Talk talk = null;
			 
				talk = Talk_Trends_Ever.get(arg0);
			 
			 if(Local_Talk_Praise != null && talk != null && Local_Talk_Praise.get(talk.Talk_Id) != null)
				{
				 	String beforeString = Local_Talk_Praise.get(talk.Talk_Id);
				 	String nowString = talk_id_praise.get(talk.Talk_Id);
					int before = Integer.parseInt(beforeString == null ? "0" : beforeString);
					int now = Integer.parseInt(nowString == null ? "0" : nowString);
					
					/*if(before > now)
					{
						bind.UpDown.setImageResource(R.drawable.icon_down);
					}
					else ;//不用设置  默认为上涨*/
					
					//以下不再设置下降Hots的数值
					if(now > before)
					{
						Talks_Change_Hots += now - before;//保存变化的Hots数值
					
						bind.Pra_Num.setText(Math.abs(before - now)+"");//设置差值的绝对值;
					}
					else {
						bind.Pra_Num.setText("0");
					}
				}
			 else
				{
				 	String num = talk_id_praise.get(talk.Talk_Id);
				 	//保存变化的hots数值
				
				 	Talks_Change_Hots += Integer.parseInt(num == null ? "0" : num);
				 	//显示item变化的数值
					bind.Pra_Num.setText(num);
				}
			
			 bind.Time.setText(talk.talk_time +"");
			 bind.Word.setText(talk.talk_word);
			 
			 //设置新评论的数目
			 bind.Repeat_Num.setText(Talk_id_repeat_num.get(talk.Talk_Id));
			 
			return arg1;
		}
		
	}
	
	public class Adapter_Pic extends BaseAdapter
	{

		Context context;
		LayoutInflater l;
		public Adapter_Pic(Context context)
		{
			this.context = context ;
			l = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Pic_Trends_Ever.size();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			return pos;
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Bindler_Pic bind ;
			if(arg1 == null)
			{
				arg1 = l.inflate(R.layout.me_pic_dital_list, null);
				bind = new Bindler_Pic();
				bind.Pic_Img = (ImageView) arg1.findViewById(R.id.me_pic_dital_list_img);
				bind.Pra_Num = (TextView) arg1.findViewById(R.id.me_pic_dital_list_Num);
				bind.UpDown = (ImageView) arg1.findViewById(R.id.me_pic_dital_list_UpDownimg);
			
				arg1.setTag(bind);
			}
			else
			{
				bind = (Bindler_Pic)arg1.getTag();
			}
			
			Picture pic = null;
			Bitmap bitmap = null;
			if(Pic_Trends_Ever.size() > arg0)
				pic = Pic_Trends_Ever.get(arg0);
			
				bitmap = pictureBitmaps.get(pic.pic_id);
			
			if(bitmap != null) 
				bind.Pic_Img.setImageBitmap(bitmap);
				
			 if(Local_Pic_Praise != null && Local_Pic_Praise.get(pic.pic_id) != null)
				{
				 	String beforeString = Local_Pic_Praise.get(pic.pic_id);
				 	String nowString = pic_id_praise.get(pic.pic_id);
					int before = Integer.parseInt(beforeString == null ? "0" :beforeString);
					int now = Integer.parseInt(nowString == null ? "0" :nowString);
					
					/*if(before > now)
					{
						bind.UpDown.setImageResource(R.drawable.icon_down);
					}
					
					else ;//UpDown不用设置  默认为上涨
*/					
					//以下不再设置下降Hots的数值
					if(now > before){
						
						Pics_Change_Hots += now - before;//保存变化的Hots数值
						bind.Pra_Num.setText(Math.abs(before - now)+"");//设置差值的绝对值;
					}
					else {
						bind.Pra_Num.setText("0");
					}
				}
			 else
				{
				 	String num = pic_id_praise.get(pic.pic_id);
				 	//显示变换的hosts值
				 	Pics_Change_Hots += Integer.parseInt(num == null ? "0" : num);
				 	///显示item中的hosts值8
					bind.Pra_Num.setText(num);
				}
			
			return arg1;
		}
		
	}
		
	private class Binder_Movie
	{
		public TextView Num;
		public ImageView UpDown;
		public TextView Intro;
	}
	
	private class Binder_Talk
	{
		public TextView Pra_Num;
		public TextView Word;
		public TextView Repeat_Num;
		public TextView Time;
		public ImageView UpDown;
	}
	
	private class Bindler_Pic
	{
		public TextView Pra_Num;
		public ImageView UpDown;
		public ImageView Pic_Img;
	}
	
	private class Bindler_Music
	{
		public TextView Pra_Num;
		public ImageView UpDown;
		public TextView Music_Intro;
	}
	
	/**
	 * @author nieyihe
	 * 用于显示增加的hot值的 到Hosts中去
	 * *//*
	private class Increase extends Thread
	{
		TextView Start;//指示为当前打开的标签的host---TextView
		int Symbol;//用来标示是总体的hots 是递增 还是 递减 
		
		public Increase(TextView Start , int Symbol)
		{
			this.Start  = Start;
			this.Symbol = Symbol;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			synchronized(Lock_Increase)
			{	///保持同步只能有一个线程拥有这个Host对象
				
					int increase_num = Integer.parseInt(Start.getText().toString());
					int now			 = me.praise_num;
					
					while(true)
					{
					
						if(increase_num <= 0)
							//当递减到零时退出这个线程
							break;
						
						increase_num -- ;//执行自减
						if(Symbol > 0)
							now ++;//为递增
						else 
							now -- ;//为递减
						Message msg = new Message();
						Bundle b = new Bundle();
						
						b.putString(Start.getId()+"", "" + increase_num );
						//Start.setText( );
						String str = "" + now;
						b.putString("Change", str);
						
						msg.setData(b);
						handler.sendMessage(msg);
						
						try {
							Thread.sleep(40);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					Update_Hots(now,Start.getId());//now为最终的人气值
					
					//更新人气值
					//并且保存最新的更新时间
				
					
				}
		}
		
	}*/
	
	/**
	 * @author nieyihe
	 * 保存时间与最近的赞
	 * */
	public void UpdateNewInfo(final int id)
	{
			switch(id) 
				{
					case  R.id.regiment_me_pic_Change_num:
					{
						SaveTime("pic_time");
						tools.Insert_Pic(pic_id_praise);
						break;
					}
					
					case R.id.regiment_me_music_Change_num:
					{
						SaveTime("music_time");
						tools.Insert_Music(music_id_praise);
						break;
					}
					
					case R.id.regiment_me_talk_Change_num:
					{
						SaveTime("talk_time");
						tools.Insert_Talk(talk_id_praise);
						break;
					}
					
					case R.id.regiment_me_movie_Change_num:
					{
						SaveTime("movie_time");
						tools.Insert_Movie(movie_id_praise);
						break;
					}
				}
		/*	}
			
		});*/
	}
	
	
	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	public void SendMessage(String key ,String word)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString(key, word);
		msg.setData(b);
		handler.sendMessage(msg);
	}

	
	/**
	 * @author nieyihe
	 * 保存
	 * */
	public void SaveTime(String where)
	{
		Editor editer =  Save_Time.edit();
		editer.putLong(where,System.currentTimeMillis());
		editer.commit();
	}
	
	/**
	 * @author nieyihe
	 * 设置头像
	 * */
	public void SetHeadImg()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				try {
					headBitmap = BitmapLocalFactory.CreateBitmap(Ac_Dital_Info.this, me.head_path, me.head_url,BitmapLocalFactory.SMALL);
					if(headBitmap!= null)
						SendMessage("ShowHeadImg", "");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
}
