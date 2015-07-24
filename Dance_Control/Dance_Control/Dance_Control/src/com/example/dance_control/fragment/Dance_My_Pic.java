package com.example.dance_control.fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_ShowAllPic;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.bmob_table.praise;
import com.example.dance_control.bmob_table.regiment_audio;
import com.example.dance_control.praisetools.PraiseHelperOnclicker;
import com.example.dance_control.resource.Picture;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.lxh.slidingmenu.lib.SlidingMenu;


@SuppressLint("ValidFragment")
public class Dance_My_Pic extends Base_Fragment {
	/**
	 * 正在刷新
	 * 
	 * */
	private final static int STATE_REFRESHING = 0;
	/**
	 * 没有刷新
	 * */
	private final static int STATE_NO_STATE = 1;
	/**
	 * 刷新状态
	 * */
	private int FlingState = STATE_NO_STATE;
	
	private final int ShowNum = 6 ;  
	//显示的图片个数
	private HashMap<String,Bitmap> AllBitmaps = new HashMap<String,Bitmap>();
	//当前的菜单
	private Integer Last = 0;
	//显示的当前最大值初始值为 10 
	private ArrayList<Picture> Pics = new ArrayList<Picture>();;
	//所有的图片
	private Integer Pos;//设置现在显示图片的编号
	//现在显示的图片的pos
	private Myadapter ada ;
	//适配器
	private String regiment_id;
	//舞团id
	private View headView;
	//顶部的菜单
	private View parent;
	//当前的View
	private ImageButton Back;
	//返回键
	private TextView Hint;
	//提示文本
	private ImageButton ReFresh;
	//刷新按钮
	private Animation anim;
	private ImageView Show_Img;
	//表示用于显示具体图片
	private Bitmap Show_Bitmap;
	//展示的图片
	private ImageButton add_pic;
	//添加图片的按钮
	private String member_power;
	//当前用户的权限
	private String member_id;
	//用户的成员Id
	private Handler picHandler = new PicHandler();
	
	private GridView showGridView;
	//显示所有的图片
	private GestureDetector detector ;
	//
	private HashMap<String, String> praiseList = new HashMap<String, String>();
	//所有的赞
	private int touchSlop ;
	//滑动最小的基础值
	public Dance_My_Pic(SlidingMenu menu,String regiment_id,String member_power,String member_id)
	{
		this.menu = menu;
		this.regiment_id = regiment_id;
		this.member_power = member_power;
		this.member_id = member_id;
	}
	
	@Override
	public View onCreateView(LayoutInflater l,ViewGroup v,Bundle b)
	{
		return l.inflate(R.layout.regiment_pic, null);
	}
	
	@Override
	public void onActivityCreated(Bundle s)
	{
		super.onActivityCreated(s);
		BuildPicView();
		GetIsPraiseList();
		GetBmobPics();
	}
	
	/**
	 * @author nieyihe
	 * 获取已经赞的论坛
	 * */
	public void GetIsPraiseList()
	{
		
		BmobQuery<praise> query = new BmobQuery<praise>();
		query.addWhereEqualTo("mem_id", member_id);		
		query.addWhereEqualTo("type", "Pic");
		query.findObjects(context, new FindListener<praise>() {
			
			@Override
			public void onSuccess(List<praise> list) {
				// TODO Auto-generated method stub
				praiseList.clear();
				for(praise praise : list){
						praiseList.put(praise.audio_id,praise.getObjectId());
						ada.notifyDataSetChanged();
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
	 * @作用:从bmob云端中获取图片数据
	 * */
	public void GetBmobPics()
	{
		
		BmobQuery<regiment_audio> query = new BmobQuery<regiment_audio>();
		query.addWhereEqualTo("regiment_id", regiment_id);
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.setLimit(Last < 10 ? 10 : Last);
		query.order("-createdAt");
		query.addWhereEqualTo("type", "Pic");
		query.findObjects(context, new FindListener<regiment_audio>()
				{
				
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
							
					}

					@Override
					public void onSuccess(List<regiment_audio> arg0) {
						// TODO Auto-generated method stub
						Pics.clear();
						for(regiment_audio pic : arg0)
						{
							Pics.add(new Picture(pic.getObjectId(),pic.img_path,pic.img_url,pic.praise_num,pic.member_id));
							ada.notifyDataSetChanged();
						}
						
						Last = Pics.size();
						
						new Thread()
						{
							@Override
							public void run() {
								AllBitmaps.clear();
								for(int i = 0; i < Pics.size(); i ++){
			
									Picture picture = Pics.get(i);
									try {
										if(!AllBitmaps.containsKey(picture.pic_id))
											AllBitmaps.put(picture.pic_id,BitmapLocalFactory.CreateBitmap(context, picture.pic_path, picture.pic_url,BitmapLocalFactory.BIG));
										//发送消息 刷新界面
										SendMessage("ReFresh","");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							};
							
						}.start();
					}
			
				});
	}
	
	/**
	 * @author nieyihe
	 * 获取下一个系列图片
	 * */
	public void GetNextSeriesPic(Integer Limit ,final Integer Skip)
	{
		BmobQuery<regiment_audio> query = new BmobQuery<regiment_audio>();
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.order("-createdAt");
		query.setSkip(Skip);
		query.setLimit(Limit);
		query.addWhereEqualTo("type", "Pic");
		query.findObjects(context, new FindListener<regiment_audio>()
				{
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
							
					}

					@Override
					public void onSuccess(List<regiment_audio> arg0) {
						// TODO Auto-generated method stub
						for(regiment_audio pic : arg0)
						{
							Pics.add(new Picture(pic.getObjectId(),pic.img_path,pic.img_url,pic.praise_num,pic.member_id));
							ada.notifyDataSetChanged();
						}
						
						Last = Pics.size();
						
						FlingState = STATE_NO_STATE;
						Hint.setText("图库("+Pics.size()+")");
						new Thread()
						{
							@Override
							public void run() {
								
								for(int i = Skip; i < Pics.size(); i ++){
								
									Picture picture = Pics.get(i);
									try {
										Bitmap bitmap = BitmapLocalFactory.CreateBitmap(context, picture.pic_path, picture.pic_url,BitmapLocalFactory.BIG);
										
										synchronized (AllBitmaps) {
											if(bitmap != null)
												AllBitmaps.put(picture.pic_id,bitmap);
										}
										//发送消息 刷新界面
										SendMessage("ReFresh","");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							};
							
						}.start();
					}
			
				});
	}
	
	/**
	 * @author nieyihe
	 * @作用:初始化图片界面
	 * */
	public void BuildPicView()
	{
		touchSlop = Integer.valueOf(ViewConfiguration.get(context).getScaledTouchSlop());
		detector = new GestureDetector(context,new FlingGestrue());		
		parent = this.getView();
		headView = parent.findViewById(R.id.regiment_pic_head);
		Back  = (ImageButton) headView.findViewById(R.id.Head_Back);
		Hint  = (TextView) headView.findViewById(R.id.Head_Word_Hint);
		ReFresh = (ImageButton) headView.findViewById(R.id.Head_ReFresh);
		Hint.setText("图库");
		ReFresh.setVisibility(View.VISIBLE);
		anim = AnimationUtils.loadAnimation(context, R.anim.btu_refresh_anim);  
		ReFresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				ReFresh.startAnimation(anim);
				GetBmobPics();
				GetIsPraiseList();
			}
		});
		add_pic = (ImageButton)parent.findViewById(R.id.regiment_pic_add_pic);
		ada = new Myadapter(context , Pics);
		BuildMidPic();
		SetOnclick();
	}

	/**
	 * @author nieyihe
	 * 设置监听器
	 * */
	public void SetOnclick()
	{
		if(member_power.equals("order") || member_power.equals("member"))
			add_pic.setOnClickListener(new Btu_Click());
		else {
			add_pic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "游客只能观看", Toast.LENGTH_SHORT).show();
				}
			});
		}
		Back.setOnClickListener(new Btu_Click());
	}
	
	/**
	 * @author nieyihe
	 * 创建中间图片的View
	 * */
	public void BuildMidPic()
	{
		showGridView = (GridView) parent.findViewById(R.id.regiment_pic_gridview);
		showGridView.setAdapter(ada);
		showGridView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		showGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				Pos = pos;//设置初始显示的pos;
				//显示具体图片
				Dialog Show_Dialog = new Dialog(context,R.style.Show_Dialog);
				RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.show_dialog, null);
				Show_Img =  (ImageView) view.findViewById(R.id.show_dialog_img);
				ImageView Right  = (ImageView) view.findViewById(R.id.show_dialog_right);
				ImageView left   = (ImageView) view.findViewById(R.id.show_dialog_left);
				Button save = (Button)view.findViewById(R.id.show_dialog_save);
 			
				final Picture picture = Pics.get(pos); 
				final Bitmap bitmap = AllBitmaps.get(picture.pic_id);
				if(bitmap != null)
					Show_Img.setImageBitmap(bitmap);
				save.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
					if(bitmap != null)
						SavePicture(bitmap, picture.pic_path);
					}
				});
				
				left.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				Right .setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						ShowNextPic();
					}
				});
				Show_Img.setOnTouchListener(new Item_Touch());
				Show_Dialog.setContentView(view);
				Show_Dialog.show();
				
			}
		});
		showGridView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				
				return detector.onTouchEvent(arg1);
				
			}
		});
	}
	
	/**
	 * @author nieyihe
	 * @作用：Button的响应事件onclick
	 * */
	
	public class Btu_Click implements View.OnClickListener
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
					
					case R.id.regiment_pic_add_pic:
					{
						Intent I = new Intent();
						I.setClass(context, Ac_ShowAllPic.class);
						Bundle b = new Bundle();
						b.putString("member_id", member_id);
						b.putString("regiment_id", regiment_id);
						I.putExtras(b);
						context.startActivity(I);
						break;
					}
					
					
			}
		}
		
	}
		
	/**
	 * @author nieyihe
	 * @作用：通过这个适配器来获取每一张图片的view
	 * */
	public class Myadapter extends BaseAdapter
	{
		
		private Context context ;
		private LayoutInflater inflater;
		private ArrayList<Picture> Pics;
		
		public Myadapter(Context context ,ArrayList<Picture> pic)
		{
			this.context = context ;
			this.Pics = pic;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Pics.size();
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
				contentview = inflater.inflate(R.layout.regiment_pic_dital, null);
				bind = new Binder();
				bind.img = (ImageView)contentview.findViewById(R.id.regiment_pic_dital_img);
				bind.num = (TextView)contentview.findViewById(R.id.regiment_pic_dital_number);
				bind.praise = (ImageView)contentview.findViewById(R.id.regiment_pic_dital_praise);
				bind.option = (ImageView) contentview.findViewById(R.id.regiment_pic_dital_option);
				contentview.setTag(bind);
			}
			else
			{
				bind = (Binder)contentview.getTag();
			}
			
			int number = pos + 1;
			
			bind.num.setText("" + number);
		
			Picture p = Pics.get(pos);
			Bitmap bitmap = null;
				bitmap = AllBitmaps.get(p.pic_id);
			if(bitmap != null)
				bind.img.setImageBitmap(bitmap);
				
			bind.img.setTag(p.pic_id);
			if(member_power.equals("order") || member_power.equals("member"))
				{
					if(praiseList.containsKey(p.pic_id))
					
					{
						bind.praise.setImageResource(R.drawable.praise_end);
						bind.praise.setOnClickListener(new PraiseHelperOnclicker(context,true,p.pic_id,p.pic_member_id,member_id,"Pic",praiseList));
					}
					else {
						bind.praise.setImageResource(R.drawable.praise_before);
						bind.praise.setOnClickListener(new PraiseHelperOnclicker(context,false,p.pic_id,p.pic_member_id,member_id,"Pic",praiseList));
					}
				}
			else
			{
				bind.praise.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "游客不可以刷赞", Toast.LENGTH_SHORT).show();	
					}
				});
			}
			
			if(member_power.equals("order"))//当是管理员时 可以删除图片
				{
					bind.option.setVisibility(View.VISIBLE);
					bind.option.setOnClickListener(new DeleteOnClick(p.pic_id));
				}
			
				
			return contentview;
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :用于响应点击事件 
	 * */
	public class DeleteOnClick implements View.OnClickListener
	{
		private String idString; 
		public DeleteOnClick(String picid) {
			// TODO Auto-generated constructor stub
			this.idString = picid;
		}
		@Override
		public void onClick(View v) {
			
			// TODO Auto-generated method stub
			
			Dialog dia = new Dialog(context,R.style.Item_Dialog);
			RelativeLayout view = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.delet_dialog, null);
			
			Button dialog_true = (Button) view.findViewById(R.id.delet_dialog_true);
			dialog_true.setOnClickListener(new Dialog_Onclick(dia,idString));
			Button dialog_cancel = (Button) view.findViewById(R.id.delet_dialog_cancel);
			dialog_cancel.setOnClickListener(new Dialog_Onclick(dia,idString));
			dia.setContentView(view);
			dia.show();
			//获取id
			//管理员可以删除
		}
		
	}
	/**
	 * @author nieyihe
	 * @作用：dialog的点击触发事件
	 * */
	public class Dialog_Onclick implements View.OnClickListener
	{

		public Dialog dialog;
		public String id;
		
		public Dialog_Onclick(Dialog dialog , String dialog_id)
		{
			this.dialog = dialog;
			this.id = dialog_id;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.delet_dialog_true:
				{
					//删除当前图片
					Delete_Pic(id);
					
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
	 * 删除图片
	 * */
	public void Delete_Pic(final String pic_id)
	{
		regiment_audio now_pic = new regiment_audio();
		now_pic.delete(context, pic_id, new DeleteListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				DeleteLocalPicInfo(pic_id);
				ada.notifyDataSetChanged();
				
			}
			
		});
		
		BmobQuery<praise> query = new BmobQuery<praise>();
		query.addWhereEqualTo("audio_id", pic_id);
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
	 * 删除本地的图片信息
	 * */
	public void DeleteLocalPicInfo(String picidString)
	{
		if(praiseList.containsKey(picidString))
			praiseList.remove(picidString);
		//删除涉及的赞
		ListIterator<Picture> iterator = Pics.listIterator();
		while(iterator.hasNext())
		{
			Picture pic = iterator.next();
			if(pic.pic_id.equals(picidString))
				iterator.remove();
		}
		//删除图片对象
		if(AllBitmaps.containsKey(picidString))
			AllBitmaps.remove(picidString);
		//删除位图
	}
	
	
	/**
	 * @author nieyihe
	 * @作用：这是一个适配器的辅助类
	 * */
	public class Binder {
		
		public ImageView img;
		public TextView num;
		public ImageView praise;
		public ImageView option;
	}
	
	
	public void toast(String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * @author nieyihe
	 * @Dialog的触摸回调函数
	 * */
	private class Item_Touch implements View.OnTouchListener
	{
		
		private final int No_State = -1; 
		private final int ShowNext = 1;
		private final int ShowPerious = 0;
		private int State = No_State;
		private int DownX;//点击的位置
		@Override
		public boolean onTouch(View arg0, MotionEvent ev) {
			// TODO Auto-generated method stub
		
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				{
					DownX = (int)ev.getRawX();	
					State = No_State;
					return true;
				}

			case MotionEvent.ACTION_MOVE:
				{
					int Distance = (int)(DownX - ev.getRawX());
					if( Distance > 0 && Math.abs(Distance) > touchSlop)
					{
						State = ShowNext;
					}
					else if (Distance < 0 && Math.abs(Distance) > touchSlop)
					{
						State = ShowPerious;
					}
					break;
				}
			case MotionEvent.ACTION_UP:
			default:
			{
				if(State == ShowNext)
					ShowNextPic();
				if(State == ShowPerious)
					ShowPerviouPic();
				break;
			}
			}
			
			return false;
			
		}
		
	}
	
	/**
	 * @author nieyihe
	 * 显示下一张图片
	 * */
	public void ShowNextPic(){
		if(Pos + 1 < Pics.size())
			Pos++;
		SendMessage("ShowBlankPic","");
		final Picture pic = Pics.get(Pos);
		synchronized (AllBitmaps) {
			Show_Bitmap = AllBitmaps.get(pic.pic_id);
		}
		new Thread()
		{
			@Override
			public void run()
			{
				if(Show_Bitmap == null)
					try {
						Show_Bitmap = BitmapLocalFactory.CreateBitmap(context, pic.pic_path, pic.pic_url,BitmapLocalFactory.BIG);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				SendMessage("ShowBitmap","");
			}
		}.start();
	}
	
	/**@author nieyihe
	 * 获取上一张图片
	 * */
	public void ShowPerviouPic()
	{
		if(Pos > 0)
			Pos--;
		SendMessage("ShowBlankPic","");
		final Picture pic = Pics.get(Pos);
		synchronized(AllBitmaps) {
			Show_Bitmap = AllBitmaps.get(pic.pic_id);
		}
		new Thread()
		{
			@Override
			public void run()
			{
				if(Show_Bitmap == null)
					try {
						Show_Bitmap = BitmapLocalFactory.CreateBitmap(context, pic.pic_path, pic.pic_url,BitmapLocalFactory.BIG);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				SendMessage("ShowBitmap","");
			}
		}.start();
	}
	
	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	public class PicHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("ReFresh"))
			{
				ada.notifyDataSetChanged();
			}
			if(b.containsKey("ShowBitmap"))
			{
				if(Show_Bitmap != null)
					Show_Img.setImageBitmap(Show_Bitmap);
			}
			if(b.containsKey("ShowBlankPic"))
			{
				Show_Img.setImageResource(R.drawable.img_set);
			}
			
			if(b.containsKey("SaveFinish"))
			{
				Toast.makeText(context,"图片保存成功："+ b.getString("SaveFinish"), Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	public void SendMessage(String msgstring,String word)
	{
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString(msgstring, word);
		msg.setData(bundle);
		picHandler.sendMessage(msg);
	}
	
	/**
	 * @author nieyihe
	 * 手势
	 * */
	public class FlingGestrue extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			int Distance = (int) (e1.getRawY() - e2.getRawY());
			if(Distance > 10 && velocityY < 0 && FlingState == STATE_NO_STATE)
			{
				FlingState = STATE_REFRESHING;
				GetNextSeriesPic(ShowNum,Last);
				Hint.setText("正在加载其他图片..");
				
			}
			
			return true;
		}
	}
	
	/***
	 * @author nieyihe
	 * 保存图片到本地
	 * */
	public void SavePicture(final Bitmap bitmap,final String name)
	{
		new Thread(){
			public void run()
			{
				File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
				File save = new File(path,name);
				if(!path.exists())
					path.mkdirs();
				try {
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(save));
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ContentValues values = new ContentValues();
				values.put(Media.DATA, save.getAbsolutePath());
				values.put(Media.DISPLAY_NAME ,name);
				values.put(Media.MIME_TYPE, "image/jpeg");
				context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
				
				SendMessage("SaveFinish",save.getAbsolutePath());
			}
		}.start();
		
		
	}
}
