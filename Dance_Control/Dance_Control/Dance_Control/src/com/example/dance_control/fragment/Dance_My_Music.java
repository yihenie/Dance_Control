package com.example.dance_control.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio.Media;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.example.dance_control.R;
import com.example.dance_control.activity.GainAllMusic;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.bmob_table.praise;
import com.example.dance_control.bmob_table.regiment_music;
import com.example.dance_control.praisetools.PraiseHelperOnclicker;
import com.example.dance_control.resource.Music;
import com.example.dance_control.tools.Utility;
import com.example.updownview.DownFreshLinear;
import com.example.updownview.DownFreshLinear.FreshListener;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class Dance_My_Music extends Base_Fragment {

	private ImageButton update ;
	private ListView music_list;
	private ArrayList<Music> musics = new ArrayList<Music>();
	private String regiment_id = "";
	private String member_id = "";
	private String member_power = "";
	
	private MediaPlayer media = new MediaPlayer();
	//媒体播放器
	private String broadcastMusicString  = ""; 
	//现在播放的音乐的名字
	private ImageButton Back;
	//返回
	private TextView Hint;
	//提示
	private ImageButton ReFresh;

	private View headView;
	//显示的Head
	
	private ArrayList<String> isExits = new ArrayList<String>();
	
	//消息循环
	private ListennerHandler handler = new ListennerHandler();
	//播放音乐的layout
	private LinearLayout Play_Layout = null;
	//layout的Param
	private FrameLayout.LayoutParams Play_Params;
	//默认的高度
	private int Play_Layout_Height = 1;
	//播放音乐界面的Bar
	private SeekBar Play_Bar;
	//播放音乐界面的音乐名字
	private TextView Play_Name;
	//播放音乐界面的hide按钮
	private ImageButton Hide;
	//是否正在显示MusicLayout
	private boolean isShowIng = false;
	//下拉刷新Linear
	private DownFreshLinear Linear;
	
	private MyAdapter ada ;
	//适配器
	private Animation anim;
	
	private Integer ShowNum = 10;
	
	private Integer Last = 0;
	
	private Boolean isDowning = false;   
	
	//已经下载好的音乐
	private ArrayList<String> isDownList = new ArrayList<String>();
	//当前的下载队列
	private Integer isDownPercent = 0 ;
	//当前文件的进度
	private HashMap<String,String> praiseList = new HashMap<String, String>(); 
	
	private HashMap<String,String> pathToNameHashMap = new HashMap<String, String>();
	//每一个云端文件的名字对应的音乐的名字
	private HashMap<String, String> pathToMusicIdHashMap = new HashMap<String, String>();
	//路径对应的音乐id
	private String DownIngPath = "";
	//当前下载的音乐路径
	public Dance_My_Music(SlidingMenu menu,String regiment_id,String mem_id,String member_power)
	{
		this.menu = menu;
		this.regiment_id = regiment_id;
		this.member_id = mem_id;
		this.member_power = member_power;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		this.BuildMusicView();
		this.Get_Music_Info();
		this.GetIsPraiseList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.regiment_music, null);
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :初始化音乐界面
	 * **/
	private void BuildMusicView()
	{
		Toast.makeText(context, "若出现音乐无法下载,请尝试重新开机", Toast.LENGTH_LONG).show();
		//初始化操控数据库的对象
		View parent = this.getView();
		headView = parent.findViewById(R.id.regiment_music_head);
		Back = (ImageButton) headView.findViewById(R.id.Head_Back);
		Hint = (TextView) headView.findViewById(R.id.Head_Word_Hint);
		Hint.setText("音乐列表");
		ReFresh = (ImageButton) headView.findViewById(R.id.Head_ReFresh);
		ReFresh.setVisibility(View.VISIBLE);
		anim = AnimationUtils.loadAnimation(context, R.anim.btu_refresh_anim);  
		ReFresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				ReFresh.startAnimation(anim);
				Get_Music_Info();
				GetIsPraiseList();
			}
		});
		music_list = (ListView) parent.findViewById(R.id.regiment_music_list);
		update = (ImageButton) parent.findViewById(R.id.regiment_music_update);
		Play_Layout = (LinearLayout) parent.findViewById(R.id.regiment_music_play_layout);
		Play_Params = (FrameLayout.LayoutParams) Play_Layout.getLayoutParams();
		Play_Layout_Height = Play_Params.bottomMargin;
		Linear = (DownFreshLinear) parent.findViewById(R.id.regiment_music_layout_DownFresh);
		Linear.setOnFresh(3, new FreshListener(){

			@Override
			public boolean onFreshTop() {
				// TODO Auto-generated method stub
				GainNewSeriesMusic(Last);
				return true;
			}

			@Override
			public boolean onFreshButton() {
				// TODO Auto-generated method stub
				GainNextSeriesMusic(Last,ShowNum);
				return true;
			}

			});
		Play_Bar = (SeekBar) parent.findViewById(R.id.regiment_music_play_bar);
		Play_Name = (TextView) parent.findViewById(R.id.regiment_music_play_musicname);
		Hide = (ImageButton) parent.findViewById(R.id.regiment_music_hide);
		PrepareMedia();
		//获取更目录下music文件中所有的已经下载的音乐,以及获取当前未完成下载的音乐
		Get_Data();
		SetOnclick();
		ada = new MyAdapter(context);
		music_list.setAdapter(ada);
		
		Utility.setListViewHeightBasedOnChildren(music_list, context);
	}
	
	/**
	 * @author nieyihe
	 * 获取所有的赞
	 * */
	public void GetIsPraiseList()
	{
		BmobQuery<praise> query = new BmobQuery<praise>();
		query.addWhereEqualTo("mem_id", member_id);		
		query.addWhereEqualTo("type", "Music");
		query.findObjects(context, new FindListener<praise>() {
			
			@Override
			public void onSuccess(List<praise> list) {
				// TODO Auto-generated method stub
				praiseList.clear();
				for(praise praise : list){
						praiseList.put(praise.audio_id,praise.getObjectId());
						ada.notifyDataSetChanged();
						Utility.setListViewHeightBasedOnChildren(music_list, context);
					}
				
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	/**
	 * @author nieyihe
	 * 设置监听器
	 * */
	public void PrepareMedia()
	{
		Play_Bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//media.start();
				media.seekTo(seekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//media.pause();
				
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
			
			}
		});
		
		media.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "播放音乐出错", Toast.LENGTH_SHORT).show();
				media.reset();
				return false;
			}
		});
		
		media.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				
				Play_Bar.setMax(media.getDuration());
				
			}
		});
		
		media.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				
				mp.reset();
			}
		});
	}
	
	/**
	 * 从数据库与本地根目录获取已经下载和未完成下载的音乐
	 *@author nieyihe
	 *仅仅支持mp3 wav wma
	 * */
	public void Get_Data()
	{

		//初始化未完成下载的数据
		if(!Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(context, "内存卡不存在,请安装内存卡在点击下载.", Toast.LENGTH_LONG).show();
			return;
		}

		//初始化完成下载的音乐的名字
		File file = new File(context.getExternalCacheDir(),Environment.DIRECTORY_MUSIC + "/Download/");
		if(!file.exists()) 
			file.mkdirs();//创建文件夹
		
		File files[] = file.listFiles(); //获取当前文档下面的所有文件名字
		if(files != null)
			for(File f: files)
			{
				String filename = f.getName();
				if(filename.endsWith(".mp3") || filename.endsWith(".wav") || filename.endsWith(".wma"))
				{
					isExits.add(filename);
				}
			}
		
	}
	
	/**
	 * @author nieyihe
	 * 设置监听器
	 * */
	public void SetOnclick()
	{
		Hide.setOnClickListener(new HideOnClick());
		Back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				menu.toggle();
			}
		});
		
		
		if(member_power.equals("order") || member_power.equals("member"))
			update.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(context,GainAllMusic.class);
				Bundle b = new Bundle();
				b.putString("member_id", member_id);
				b.putString("regiment_id", regiment_id);
				i.putExtras(b);
				context.startActivity(i);
			}
		});
		else{
			update.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "游客只能观看", Toast.LENGTH_SHORT).show();
				}
			});
			
		} 
			
	}
	
	/***
	 * @author nieyihe
	 * 获取最新的音乐
	 * */
	public void GainNewSeriesMusic(int Limit)
	{
		BmobQuery<regiment_music> b = new BmobQuery<regiment_music>();
		//b.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		b.addWhereEqualTo("regiment_id", regiment_id);
		b.order("download_num");
		b.setLimit(Limit > ShowNum ? Limit : ShowNum);
		b.findObjects(context, new FindListener<regiment_music>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<regiment_music> arg0) {
						// TODO Auto-generated method stub
						musics.clear();
						pathToNameHashMap.clear();
						pathToMusicIdHashMap.clear();
						for(regiment_music music : arg0)
						{
							musics.add(new Music(music.music_file_path,music.music_file_url,music.getObjectId(),music.member_id,music.download_num,music.Intro));
							pathToNameHashMap.put(music.music_file_path,music.Intro);
							pathToMusicIdHashMap.put(music.music_file_path, music.getObjectId());
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(music_list, context);
						}
						
						Last = musics.size();
						
					}
			
				});
	}
	
	/**
	 * @author nieyihe
	 * 获取下一个系列的音乐
	 * */
	public void GainNextSeriesMusic(final int Skip , int Limit)
	{
		BmobQuery<regiment_music> b = new BmobQuery<regiment_music>();
		//b.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		b.addWhereEqualTo("regiment_id", regiment_id);
		b.order("download_num");
		b.setLimit(Limit);
		b.setSkip(Skip);
		b.findObjects(context, new FindListener<regiment_music>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<regiment_music> arg0) {
						// TODO Auto-generated method stub
						
						for(regiment_music music : arg0)
						{
							musics.add(new Music(music.music_file_path,music.music_file_url,music.getObjectId(),music.member_id,music.download_num,music.Intro));
							pathToNameHashMap.put(music.music_file_path,music.Intro);
							pathToMusicIdHashMap.put(music.music_file_path, music.getObjectId());
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(music_list, context);
						}
						
						Last = musics.size();
						
				}
			
				});
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :获取最新的音乐数据
	 * */
	public void Get_Music_Info()
	{
		musics.clear();
		//Links.clear();
		pathToNameHashMap.clear();
		pathToMusicIdHashMap.clear();
		BmobQuery<regiment_music> b = new BmobQuery<regiment_music>();
		//b.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		b.addWhereEqualTo("regiment_id", regiment_id);
		b.order("download_num");
		b.findObjects(context, new FindListener<regiment_music>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<regiment_music> arg0) {
						// TODO Auto-generated method stub
						for(regiment_music music : arg0)
						{
							musics.add(new Music(music.music_file_path,music.music_file_url,music.getObjectId(),music.member_id,music.download_num,music.Intro));
							pathToNameHashMap.put(music.music_file_path,music.Intro);
							pathToMusicIdHashMap.put(music.music_file_path, music.getObjectId());
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(music_list, context);
						}
						
						Last = musics .size();
						
						
					}
			
				});
	}
	
	
	private class MyAdapter extends BaseAdapter
	{
		private LayoutInflater i;
		private Context context;
		
		public MyAdapter(Context context)
		{
			this.context = context;
			this.i = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return musics.size();
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
		public View getView(int arg0, View contentview, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Bind bind ; 
			
			if(contentview == null)
			{
				contentview = i.inflate(R.layout.regiment_music_dital, null);
				bind = new Bind();
				bind.Praise = (ImageView)contentview.findViewById(R.id.regiment_music_dital_praise);
				bind.Intro = (TextView) contentview.findViewById(R.id.regiment_music_dital_intro);
				bind.Download = (ImageButton) contentview.findViewById(R.id.regiment_music_dital_download);
				bind.Down_Num = (TextView) contentview.findViewById(R.id.regiment_music_dital_num);
				bind.bar = (ProgressBar)contentview.findViewById(R.id.regiment_music_dital_progress);
				
				contentview.setTag(bind);
			}
			else
			{
				bind = (Bind)contentview.getTag();
			}
			
			final Music music = musics.get(arg0);
			String name = music.intro;
			String musicid = music.Music_id;
			if(member_power.equals("member") || member_power.equals("order"))
			{
				if(praiseList.containsKey(musicid))
				{
					bind.Praise.setOnClickListener(new PraiseHelperOnclicker(context, true, music.Music_id, music.Music_member_id, member_id, "Music", praiseList));
					bind.Praise.setImageResource(R.drawable.praise_end);
				}else {
					bind.Praise.setOnClickListener(new PraiseHelperOnclicker(context, false, music.Music_id, music.Music_member_id, member_id, "Music", praiseList));
					bind.Praise.setImageResource(R.drawable.praise_before);
				}
			}
			else {
				bind.Praise.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "游客不可以刷赞",Toast.LENGTH_SHORT).show();
					}
				});
			}
			
			bind.Intro.setText(name);
			bind.Down_Num.setText(music.download_num + "");
			
			if(isDownList.contains(music.music_path))
				{
					bind.Download.setImageResource(R.drawable.icon_pause);
					bind.Download.setEnabled(false);
				}
			else{
				bind.Download.setImageResource(R.drawable.icon_down);
			}
			
			if(isExits.contains(music.music_path))
			{
				String showString = "未下载完成：" +name;
				SpannableString string = new SpannableString(showString);
				string .setSpan(new ForegroundColorSpan(Color.RED), 0, showString.lastIndexOf("："), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				bind.Intro.setText(string);
			}
			
			if(!isExits.contains(music.intro))
			{
				bind.Download.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(isDowning)
							{
								((ImageButton)v).setImageResource(R.drawable.icon_pause);
								((ImageButton)v).setEnabled(false);
								isDownList.add(music.music_path);
								Toast.makeText(context, "已将:"+ music.intro+"加入到下载列表中,稍后下载.",Toast.LENGTH_SHORT).show();
							}
							else {
								((ImageButton)v).setImageResource(R.drawable.icon_pause);
								((ImageButton)v).setEnabled(false);
								isDownList.add(music.music_path);
								Toast.makeText(context, "开始下载,不可以暂停下载", Toast.LENGTH_SHORT).show();
								DownLoad(isDownList.get(0));
							}
						}
					});
			}else {
				bind.bar.setVisibility(View.GONE);
				bind.Download.setImageResource(R.drawable.icon_play);
				bind.Download.setEnabled(true);
				bind.Download.setOnClickListener(new PlayMusic(music.intro));
			}
			
			bind.bar.setMax(100);
			
			if(music.music_path.equals(DownIngPath))
				bind.bar.setProgress(isDownPercent);
			
			return contentview;
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :适配器的辅助类
	 * */
	private class Bind
	{
		public ImageView Praise;
		public TextView Intro;
		public ImageButton Download;
		public TextView Down_Num;
		public ProgressBar bar;
	}
	
	
	/**
	 * @author nieyihe
	 * @ 播放音乐
	 * */
	public class PlayMusic implements View.OnClickListener
	{
		private String path ;
		private String name;
		private boolean isPlay;
		public PlayMusic(String name)
		{
			this.name = name.substring(0, name.lastIndexOf("."));
			this.path = context.getExternalCacheDir().getAbsolutePath() + "/Music/Download/" + name;
		
			
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ImageButton vbtu = (ImageButton)v;
			Play_Name.setText(name);
			
			if(!isPlay){
				
				isPlay = true;
				vbtu.setImageResource(R.drawable.icon_pause);
				
				if(!broadcastMusicString.equals(path))
					PlayMusic_On(path);
				else 
					GoOnPlay();
			}
			else
			{
				isPlay = false;
				vbtu.setImageResource(R.drawable.icon_play);
				PausePlay();
			}
			Toast.makeText(context, "播放"+ name, Toast.LENGTH_LONG).show();
		}
		
	}

	/***
	 * @author nieyihe
	 * 更新本地音乐的名字
	 * */
	public void UpDateLocalMusicName(final String filepath,String name)
	{
		File oldfile = new File(filepath);
		String oldpath = filepath.substring(0, filepath.lastIndexOf("/"));
		File newfile = new File(oldpath +"/"+ name);
		oldfile.renameTo(newfile);
	}
	
	/**
	 * @author nieyihe
	 * 进行下载
	 * */
	public void DownLoad(final String fileName)
	{
		isDowning = true; 
		DownIngPath = fileName ; 
		File file = context.getExternalCacheDir();
		if(!file.exists())
			file.mkdirs();
		BmobConfiguration config = new BmobConfiguration.Builder(context).customExternalCacheDir("Music").build();
		BmobPro.getInstance(context).initConfig(config);
		BmobProFile.getInstance(context).download(fileName, new DownloadListener() {

            @Override
            public void onSuccess(String fullPath) {
                // TODO Auto-generated method stub
            	
            	isDowning = false ; 
            	UpDateLocalMusicName(fullPath,pathToNameHashMap.get(fileName));
            	
            	Toast.makeText(context, "" + pathToNameHashMap.get(fileName) +"下载完成！", Toast.LENGTH_SHORT).show();
            	isDownList.remove(fileName);
            	
            	if(isExits.contains(fileName))//未下载完成
            		isExits.remove(fileName);
            	isExits.add(pathToNameHashMap.get(fileName));
            	Update_DownLoad_Num(pathToMusicIdHashMap.get(fileName));
            	
            	ContentValues values = new ContentValues();
            	values.put(Media.DATA, fullPath);
            	values.put(Media.DISPLAY_NAME,pathToNameHashMap.get(fileName));
            	
            	context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
            	
            	ada.notifyDataSetChanged();
        		Utility.setListViewHeightBasedOnChildren(music_list, context);
        		
            	if(isDownList.size() > 0)
            		DownLoad(isDownList.get(0));
            }
            @Override
            public void onProgress(String localPath, int percent) {
                // TODO Auto-generated method stub
            		isDownPercent = percent ;
            		
            		ada.notifyDataSetChanged();
            		Utility.setListViewHeightBasedOnChildren(music_list, context);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
               Toast.makeText(context, "错误",Toast.LENGTH_SHORT).show();
            }
        });

	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :更新下载量
	 * @param i：要更新的下载量数目
	 * @param music_id 更新的目的music
	 * */
	private void Update_DownLoad_Num(String music_id)
	{
		regiment_music music = new regiment_music();
		music.increment("download_num");
		music.update(context, music_id, new UpdateListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "更新下载数目失败", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			
		});

	}
	
	/**
	 * 消息循环
	 * @author nieyihe
	 * @ 用于处理发送来的消息
	 * */
	private class ListennerHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
		
			if(b.containsKey("UpMusicLayout"))
			{	
				//显示播放音乐的界面
				Play_Params.bottomMargin += 4;
				Play_Layout.setLayoutParams(Play_Params);
				//更新界面的刷新情况	
			}
			
			if(b.containsKey("DownMusicLayout"))
			{
				//隐藏播放音乐的界面
				Play_Params.bottomMargin -= 4;
				Play_Layout.setLayoutParams(Play_Params);
				//更新界面的刷新情况	
			}
			
			if(b.containsKey("RefreshProgress"))
			{
				//Log.w("Position", ""+b.getInt("RefreshProgress"));
				Play_Bar.setProgress(b.getInt("RefreshProgress"));
				
			}
			
			if(b.containsKey("RefreshMusicList"))
			{
				ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(music_list, context);
			}
		}
		
	}
	
	
	/**
	 * @author nieyihe
	 * 播放指定音乐
	 * */
	public void PlayMusic_On(String music)
	{
		isShowIng = true;
		Show_Music_Layout();
		
		try {
			
			broadcastMusicString = music;
			media.reset();
			media.setDataSource(music);
			media.prepare();
			media.start();
			SetMusicListener();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, "播放出错", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @author nieyihe
	 * 继续播放
	 * */
	public void GoOnPlay()
	{
		
		media.start();
		SetMusicListener();
		
	}
	
	/**
	 * @author nieyihe
	 * 暂停播放
	 * */
	public void PausePlay()
	{
		if(media.isPlaying())
			media.pause();
	}
	
	/**
	 * @author nieyihe
	 * 显示播放音乐的小界面
	 * */
	public void Show_Music_Layout()
	{
		new ShowMusicLayout().start();
	}
	
	/**
	 * @author nieyihe
	 * 隐藏播放音乐的小界面
	 * */
	public void Hide_Music_Layout()
	{
		new HideMusicLayout().start();
	}
	
	/**
	 * @author nieyihe
	 * 此线程用于显示music界面
	 * */
	private class ShowMusicLayout extends Thread
	{
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			while(Play_Params.bottomMargin < 0)
			{
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("UpMusicLayout", "");
				msg.setData(b);
				handler.sendMessage(msg);
				
				//设置休眠时间 50毫秒
				try {
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
	 * 用于隐藏music界面
	 * */
	private class HideMusicLayout extends Thread
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			while(Play_Params.bottomMargin > Play_Layout_Height)
				{
						 Message msg = new Message();
						 Bundle b = new Bundle();
						 b.putString("DownMusicLayout", "");
						 msg.setData(b);
						 handler.sendMessage(msg);
						 
						 try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						 
						 if(isShowIng)//加入为再次点击显示 ,就暂停下降
						    break;
				}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * 点击隐藏按钮触发的事件
	 * */
	private class HideOnClick implements View.OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			isShowIng = false;
			Hide_Music_Layout();
		}
		
	}

	
	
	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	public void SendMessage(String key,int msgstr)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt(key, msgstr);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	/**
	 * @author nieyihe
	 * 设置音乐监听器
	 * */
	public void SetMusicListener()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				while(media.isPlaying())
				{
					SendMessage("RefreshProgress",media.getCurrentPosition());
					 
				}
			}
		}
		.start();
	}
}
