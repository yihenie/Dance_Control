package com.example.dance_control.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_AddMovie;
import com.example.dance_control.activity.Ac_Show_Movie;
import com.example.dance_control.activity.Ac_Show_Shuffle;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.bmob_table.praise;
import com.example.dance_control.bmob_table.regiment_audio;
import com.example.dance_control.praisetools.PraiseHelperOnclicker;
import com.example.dance_control.resource.Movie;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.example.dance_control.tools.Utility;
import com.example.updownview.DownFreshLinear;
import com.example.updownview.DownFreshLinear.FreshListener;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class Dance_My_Movie extends Base_Fragment{

	private ArrayList<Movie> Movies ;
	private HashMap<String, Bitmap> bitmapsHashMap = new HashMap<String, Bitmap>();
	private HashMap<String, String> praiseHashMap = new HashMap<String, String>();
	private Myadapter ada ;
	private ImageButton Back;
	private TextView Hint;
	private ImageButton ReFresh;
	private Animation anim;
	private View headView;
	private ListView movie_intro;
	private String regiment_id;
	private String member_power;
	private ImageButton add_movie;
	private String member_id;
	private DownFreshLinear  Linear;
	private MovieHandler Moviehandler = new MovieHandler();
	private Integer Last = 0;
	private Integer ShowNum = 10;
	
	public Dance_My_Movie(SlidingMenu menu,String regiment_id,String power,String mem_id)
	{
		this.menu = menu;
		this.member_power = power;
		this.regiment_id  = regiment_id;
		this.member_id = mem_id;
	}
	
	@Override
	public void onActivityCreated(Bundle b)
	{
		super.onActivityCreated(b);
		this.BuildMovieView();
		GetMovie();
		GainAllPraise();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater l,ViewGroup v ,Bundle b)
	{
		return l.inflate(R.layout.regimet_movie, null);
	}
	/**
	 * @author nieyihe
	 * @作用: 初始化Movie的界面
	 * */
	public void BuildMovieView()
	{

		this.Movies = new ArrayList<Movie>();
		
		ada = new Myadapter(context , Movies);
		View v =  this.getView();
		
		headView = v.findViewById(R.id.regiment_movie_head);
		this.Back = (ImageButton) headView.findViewById(R.id.Head_Back);
		this.Hint = (TextView) headView.findViewById(R.id.Head_Word_Hint);
		this.ReFresh = (ImageButton) headView.findViewById(R.id.Head_ReFresh);
		ReFresh.setVisibility(View.VISIBLE);
		anim = AnimationUtils.loadAnimation(context, R.anim.btu_refresh_anim);  
		ReFresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				ReFresh.startAnimation(anim);
				GetMovie();
				GainAllPraise();
			}
		});
		this.movie_intro = (ListView)v.findViewById(R.id.regiment_movie_inner_list);
		this.add_movie = (ImageButton) v.findViewById(R.id.regiment_movie_addmovie_button);
		Hint.setText("视频站");
		Linear = (DownFreshLinear) v.findViewById(R.id.regiment_movie_layoutDownFresh);
		Linear.setOnFresh(4, new FreshListener(){

			@Override
			public boolean onFreshTop() {
				// TODO Auto-generated method stub
				ShowNewSeriesMovie(Last);
				return true;
			}

			@Override
			public boolean onFreshButton() {
				// TODO Auto-generated method stub
				ShowNextSeriesMovie(Last, ShowNum);
				return true;
			}
			});
		
		
		movie_intro.setAdapter(ada);
		SetOnclick();
		Utility.setListViewHeightBasedOnChildren(movie_intro, context);
	}
	
	/***
	 * @author nieyihe
	 * 设置监听器
	 * */
	public void SetOnclick()
	{
		if(member_power.equals("order") || member_power.equals("member"))
			add_movie.setOnClickListener(new OnClick());
		else{
			add_movie.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "游客只能观看", Toast.LENGTH_SHORT).show();
				}
			});
		}
		movie_intro.setOnItemClickListener(new Item_Onclick());
		Back.setOnClickListener(new OnClick());
	}
	
	/**
	 * @author nieyihe
	 * 获取所有的赞
	 * */
	public void GainAllPraise()
	{
		BmobQuery<praise> query = new BmobQuery<praise>();
		query.addWhereEqualTo("mem_id", member_id);		
		query.addWhereEqualTo("type", "Pic");
		query.findObjects(context, new FindListener<praise>() {
			
			@Override
			public void onSuccess(List<praise> list) {
				// TODO Auto-generated method stub
				praiseHashMap.clear();
				for(praise p : list)
					{
						praiseHashMap.put(p.audio_id, p.getObjectId());
						SendMessage("ReFresh","");
					}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/***
	 * 
	 * @author nieyihe
	 * 查看视频
	 * */
	public class Item_Onclick implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			Movie movie = Movies.get(pos);
			Intent intent = new Intent();
			intent.setClass(context, Ac_Show_Movie.class);
			intent.putExtra("Url", movie.url);
			context.startActivity(intent);
			
		}

		
		
	}
	
	
	/**
	 * @author nieyihe
	 * @作用：点击事件类
	 * */
	public class OnClick implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.Head_Back:
				{
					menu.toggle();
					break;
				}
				case R.id.regiment_movie_addmovie_button:
				{
					
					Intent addmovie = new Intent();
					addmovie.setClass(context, Ac_AddMovie.class);
					Bundle b = new Bundle();
					b.putString("regiment_id", regiment_id);
					b.putString("member_id",member_id);
					addmovie.putExtras(b);
					context.startActivity(addmovie);
				}
			}
		}
		
	}

	/**
	 * @author nieyihe
	 * @作用：从bmob云端获取Movie
	 * */
	public void GetMovie()
	{
		Movies.clear();
		bitmapsHashMap.clear();
		BmobQuery<regiment_audio> query = new BmobQuery<regiment_audio>();
		query.addWhereEqualTo("regiment_id", this.regiment_id);
		query.order("-createdAt");
		query.addWhereEqualTo("type", "Movie");
		query.setLimit(Last < ShowNum ? ShowNum : Last);
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findObjects(context, new FindListener<regiment_audio>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Hint.setText("小控没有找到网");
					}

					@Override
					public void onSuccess(List<regiment_audio> arg0) {
						// TODO Auto-generated method stub
						Hint.setText("视频站");
						for(regiment_audio temp : arg0)
						{
							Movies.add(new Movie(temp.url,temp.getObjectId(),temp.img_path,temp.img_url,temp.intro,temp.member_id,temp.praise_num));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(movie_intro, context);
						}
						
						Last = Movies.size();
						
						new Thread()
						{
							@Override
							public void run()
							{
								for(int i = 0;i < Movies.size(); i ++){
									Movie mo = Movies.get(i);
									try {
							synchronized (bitmapsHashMap) {
									if(!mo.img_path.equals(""))
										bitmapsHashMap.put(mo.Movie_id, BitmapLocalFactory.CreateBitmap(context,mo.img_path,mo.img_url,BitmapLocalFactory.NORMAL));
								}
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
										SendMessage("ReFresh","");
								}
							}
						}.start();
					}
			
				});
	}
	
	/***
	 * @author nieyihe
	 * 显示下面一系列的视频
	 * */
	public void ShowNextSeriesMovie(final int Skip , final int Limit)
	{
		BmobQuery<regiment_audio> query = new BmobQuery<regiment_audio>();
		query.addWhereEqualTo("regiment_id", this.regiment_id);
		query.setLimit(Limit);
		query.setSkip(Skip);
		query.order("-createdAt");
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findObjects(context, new FindListener<regiment_audio>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Hint.setText("小控没有找到网");
					}

					@Override
					public void onSuccess(List<regiment_audio> arg0) {
						// TODO Auto-generated method stub
						Hint.setText("视频站");
						for(regiment_audio temp : arg0)
						{
							Movies.add(new Movie(temp.url,temp.getObjectId(),temp.img_path,temp.img_url,temp.intro,temp.member_id,temp.praise_num));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(movie_intro, context);
						}
						
						Last = Movies.size();
						
						new Thread()
						{
							@Override
							public void run()
							{
								for(int i = Skip ;i < Movies.size(); i ++){
									Movie mo = Movies.get(i);
									try {
							synchronized (bitmapsHashMap) {
									if(!mo.img_path.equals(""))
										bitmapsHashMap.put(mo.Movie_id, BitmapLocalFactory.CreateBitmap(context,mo.img_path,mo.img_url,BitmapLocalFactory.NORMAL));
							}		
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
										SendMessage("ReFresh","");
								}
							}
						}.start();
					}
			
				});

	}
	
	/**
	 * @author nieyihe
	 * 显示最新的视频
	 * */
	public void ShowNewSeriesMovie(int Limit)
	{
		BmobQuery<regiment_audio> query = new BmobQuery<regiment_audio>();
		query.addWhereEqualTo("regiment_id", this.regiment_id);
		query.setLimit(Limit);
		query.order("-createdAt");
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findObjects(context, new FindListener<regiment_audio>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Hint.setText("小控没有找到网");
					}

					@Override
					public void onSuccess(List<regiment_audio> arg0) {
						// TODO Auto-generated method stub
						Hint.setText("视频站");
						Movies.clear();
						bitmapsHashMap.clear();
						
						for(regiment_audio temp : arg0)
						{
							Movies.add(new Movie(temp.url,temp.getObjectId(),temp.img_path,temp.img_url,temp.intro,temp.member_id,temp.praise_num));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(movie_intro, context);
						}
						
						Last = Movies.size();
						
						new Thread()
						{
							@Override
							public void run()
							{
								for(int i = 0 ;i < Movies.size(); i ++){
									Movie mo = Movies.get(i);
									try {
							synchronized (bitmapsHashMap) {
									if(!mo.img_path.equals(""))
										bitmapsHashMap.put(mo.Movie_id, BitmapLocalFactory.CreateBitmap(context,mo.img_path,mo.img_url,BitmapLocalFactory.NORMAL));
							}		
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
										SendMessage("ReFresh","");
								}
							}
						}.start();
					}
			
				});
	}
	
	/**
	 * @author nieyihe
	 * @作用: 适配器
	 * */
	public class Myadapter extends BaseAdapter
	{
		private ArrayList<Movie> Movies;
		private Context context;
		private LayoutInflater l;
		
		public Myadapter(Context context ,ArrayList<Movie> movie)
		{
			this.context = context ;
			this.Movies = movie;
			l = LayoutInflater.from(this.context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.Movies.size();
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
			Binder bind ;
			if(contentview == null)
			{
				contentview = l.inflate(R.layout.regimenr_movie_list, null);
				bind = new Binder();
				bind.img = (ImageView)contentview.findViewById(R.id.regiment_movie_list_img);
				bind.intro = (TextView)contentview.findViewById(R.id.regiment_movie_list_text);
				bind.options = (ImageView)contentview.findViewById(R.id.regiment_movie_list_button_option);
				bind.praise = (ImageView) contentview.findViewById(R.id.regiment_movie_list_praise);
				contentview.setTag(bind);
			}
			else
			{
				bind = (Binder)contentview.getTag();
			}
			
			Movie mo = Movies.get(pos);
			Bitmap bitmap = null;
			if(mo.img_path.equals(""))
				bind.img.setVisibility(View.GONE);
			else{
				bind.img.setVisibility(View.VISIBLE);
				bitmap = bitmapsHashMap.get(mo.Movie_id);
				
				if(bitmap != null)
					bind.img.setImageBitmap(bitmap);
			}
			
			if(member_power.equals("order") || member_power.equals("member"))
			{
				if(praiseHashMap.containsKey(mo.Movie_id))
				{
					bind.praise.setImageResource(R.drawable.praise_end);
					bind.praise.setOnClickListener(new PraiseHelperOnclicker(context, true, mo.Movie_id, mo.Movie_member_id, member_id, "Movie", praiseHashMap));
				}
				else {
					bind.praise.setImageResource(R.drawable.praise_before);
					bind.praise.setOnClickListener(new PraiseHelperOnclicker(context, false, mo.Movie_id, mo.Movie_member_id, member_id, "Movie", praiseHashMap));
				}
			}
			else
			{
				Toast.makeText(context, "游客不可以刷赞", Toast.LENGTH_SHORT).show();
			}
			bind.intro.setText(mo.intro);
			
			if(member_power.equals("order"))
			{
			
				bind.options.setVisibility(View.VISIBLE);
				bind.options.setTag(new Delete_Binder(mo.Movie_id,pos));
				bind.options.setOnClickListener(new Option_Dialog_Onclick());
			}
			return contentview;
		}
		
	}
	
	
	
	/**
	 * @author nieyihe
	 * @作用：用于适配器的辅助类
	 * */
	public class Binder
	{
		public ImageView img;
		public TextView intro;
		public ImageView options;
		public ImageView praise;
	}
	
	/**
	 * @author nieyihe
	 * @作用：用于删除的tag
	 * */
	public class Delete_Binder
	{
		public String id;
		public int pos;
		
		public Delete_Binder(String id,int pos)
		{
			this.id = id;
			this.pos = pos;
		}
	}
	
	
	/**
	 * @author nieyihe
	 * @zuoyong :用户监听点击option按钮
	 * */
	public class Option_Dialog_Onclick implements View.OnClickListener
	{
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Delete_Binder bind = (Delete_Binder)arg0.getTag();
			WindowManager wm = Dance_My_Movie.this.getActivity().getWindowManager();
			int width = wm.getDefaultDisplay().getWidth();
		    int height = wm.getDefaultDisplay().getHeight();
			int locate[] = new int[2];
			arg0.getLocationOnScreen(locate);
			Dialog optiondialog = new Dialog(context,R.style.Item_Dialog);
			Window win = optiondialog.getWindow();
			LayoutParams params = new LayoutParams();
			params.x = locate[0] - width/2 + arg0.getWidth()/2;
			params.y = locate[1] - height/2 + arg0.getHeight();
			//默认的x,y为屏幕中央所以要减去屏幕的各一半,在取在整个屏幕的位置
			View view = LayoutInflater.from(context).inflate(R.layout.item_option, null);
			win.setAttributes(params);
			optiondialog.setContentView(view);
			
			if(member_power.equals("order"))
			{
				Button delete = (Button) view.findViewById(R.id.item_option_delete);
				delete.setVisibility(View.VISIBLE);
				delete.setOnClickListener(new Option_Onclick(bind,optiondialog));
			}
			Button look = (Button) view.findViewById(R.id.item_option_look);
			
			look.setOnClickListener(new Option_Onclick(bind,optiondialog));
			
			optiondialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
			
			optiondialog.show();
		}
		
	} 
	
	/**
	 * @author nieyihe
	 * @zuoyong :用户监听点击option内部的选项
	 * */
	public class Option_Onclick implements View.OnClickListener
	{
		private Delete_Binder bind;
		private Dialog dialog;
		public Option_Onclick(Delete_Binder bind,Dialog dialog)
		{
			this.bind = bind;
			this.dialog = dialog;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			switch(arg0.getId())
			{
				case R.id.item_option_delete:
				{
					dialog.dismiss();
					Dialog deleteDialog = new Dialog(context, R.style.Item_Dialog);
					RelativeLayout view = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.delet_dialog, null);
					Button dialog_true = (Button) view.findViewById(R.id.delet_dialog_true);
					dialog_true.setOnClickListener(new Dialog_Onclick(deleteDialog,bind.id));
					Button dialog_cancel = (Button) view.findViewById(R.id.delet_dialog_cancel);
					dialog_cancel.setOnClickListener(new Dialog_Onclick(deleteDialog,bind.id));
					deleteDialog.setContentView(view);
					deleteDialog.show();
					break;
				}
				
				case R.id.item_option_look:
				{
					dialog.dismiss();
					Movie m = Movies.get(bind.pos);
					Intent start = new Intent();
					Bundle b = new Bundle();
					b.putString("URL",m.url);
					start.putExtras(b);
					start.setClass(context, Ac_Show_Movie.class);
					context.startActivity(start);
					break;
				}
			}
		}
		
	}
	
	
	
	/**
	 * @author nieyihe
	 * 
	 * */
	public class Dialog_Onclick implements View.OnClickListener
	{

		private Dialog dialog;
		private String Movie_id ;

		public Dialog_Onclick(Dialog dialog,String id)
		{
			this.dialog = dialog;
			this.Movie_id = id;
		}
		
		@Override
		public void onClick(View V) {
			// TODO Auto-generated method stub
			switch(V.getId())
			{
				case R.id.delet_dialog_true:
				{
					Delete_Movie(Movie_id);
					dialog.dismiss();
					break;
				}
				
				case R.id.delet_dialog_cancel:
				{
					dialog.dismiss();
					break;
				}
				
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @作用:删除Moive
	 * */
	public void Delete_Movie(final String id)
	{
		regiment_audio now_movie = new regiment_audio();
		now_movie.delete(context, id , new DeleteListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
				Iterator<Movie> ref = Movies.iterator();
				while(ref.hasNext())
				{
					Movie movie = ref.next();
					if(movie.Movie_id.equals(id))
					{
						ref.remove();
						ada.notifyDataSetChanged();
						Utility.setListViewHeightBasedOnChildren(movie_intro, context);
						break;
					}
					
				}
			}
			
		});
		
		BmobQuery<praise> query = new BmobQuery<praise>();
		query.addWhereEqualTo("audio_id", id);
		query.findObjects(context, new FindListener<praise>() {
			
			@Override
			public void onSuccess(List<praise> list) {
				// TODO Auto-generated method stub
				for(praise p:list)
				{
					praise pra = new praise();
					pra.delete(context, p.getObjectId(), new DeleteListener() {
						
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

			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});

		
		
	}

	/**
	 * @author nieyihe
	 * 消息处理类
	 * */
	public class MovieHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			if(bundle.containsKey("ReFresh"))
			{
				ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(movie_intro, context);
			}
		}
	}
	
	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	public void SendMessage(String key,String values)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString(key,values);
		msg.setData(b);
		Moviehandler.sendMessage(msg);
	}
}
