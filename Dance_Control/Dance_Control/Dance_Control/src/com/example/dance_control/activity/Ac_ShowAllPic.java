package com.example.dance_control.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.example.dance_control.R;
import com.example.dance_control.backworker.UpdatePicServer;
import com.example.dance_control.bmob_table.regiment_audio;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.example.dance_control.tools.NotificationLocalFactory;

public class Ac_ShowAllPic extends Activity {

	private String member_id = "";
	private String regiment_id = "";
	private ArrayList<String> Paths = new ArrayList<String>();//保存所有的路径
	private ArrayList<String> Now_Paths = new ArrayList<String>();//保存当前页显示的所有路径
	private ArrayList<String> Is_Select_Img = new ArrayList<String>();
	private HashMap<String,Bitmap> Pics = new HashMap<String,Bitmap>();
	//保存所有已经选择的图片的路径
	private GridView gridview;
	private GridViewAdapter ada; 
	private Button submit ;
	private MyHandler Handler = new MyHandler();
	private ProgressDialog pd;
	private Button Next;
	private Button Privious;
	private Integer Offset = 0;//标示现在的显示的Paths的初始位置 - 指针
	private Integer Standard = 10;//一次显示图片的数量
	private Integer Number = 0;//已经选择的图片数量
	private GestureDetector gs;
	private Notification notification ;
	
	@Override
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		this.setContentView(R.layout.show_allpic);
		
		gs = new GestureDetector(this,new Gesture());
		Get_ExtrasData();//获取额外数据
		new Thread_For_AllPath().start();//用于加载图片路径的线程
		gridview = (GridView) this.findViewById(R.id.show_allpic_gridview);
		submit = (Button) this.findViewById(R.id.show_allpic_submit);
		Next = (Button) this.findViewById(R.id.show_allpic_next);
		Privious = (Button) this.findViewById(R.id.show_allpic_previous);
		
		submit.setOnClickListener(new SuBmit());
		gridview.setOnItemClickListener(new GridView_Item_Click());
		Next.setOnClickListener(new Show());
		Privious.setOnClickListener(new Show());
		
		ada = new GridViewAdapter();
		gridview.setAdapter(ada);
		gridview.setOnTouchListener(new GridOnTouch());
		
	}
	
	
	/**
	 * @author nieyihe
	 * @ 加载所有的路径
	 * @ 通过访问external.db数据库来获取sd卡中所有图片的路径
	 * */
	public void Init_AllPath()
	{
		if(!Paths.isEmpty()) return ;
		//假如已经有数据则不用再获取
		//此处为当异常退出后恢复数据时调用
		
		Cursor cu = Ac_ShowAllPic.this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null , MediaStore.Images.Media.MIME_TYPE +"= ? or "+ MediaStore.Images.Media.MIME_TYPE +"= ?", new String[]{"image/jpeg","image/png"}, null);
		if(cu == null)
		{
			return ;
		}
		
		while(cu.moveToNext())
		{
			Paths.add(cu.getString(cu.getColumnIndex(MediaStore.Images.Media.DATA)));
		}
		
		cu.close();
		
		Cursor cursor = Ac_ShowAllPic.this.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, null,MediaStore.Images.Media.MIME_TYPE +"= ? or "+ MediaStore.Images.Media.MIME_TYPE +"= ?", new String[]{"image/jpeg","image/png"}, null);
		if(cursor == null)
		{
			return ;
		
		}
		
		while(cursor.moveToNext())
		{
			Paths.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
		}
		
		cursor.close();
	}
	
	/**
	 * @author nieyihe
	 * 消息回调函数
	 * */
	private class MyHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
				
			if(b.containsKey("Fresh"))
			{
				ada.notifyDataSetChanged();
			}
			if(b.containsKey("Show_Previous"))
			{
				Privious.setEnabled(true);
			}
			
			if(b.containsKey("Show_Next"))
			{
				Next.setEnabled(true);
			}
			
			if(b.containsKey("Pre_Enable"))
			{
				Privious.setEnabled(false);
			}
			
			if(b.containsKey("Next_Enable"))
			{
				Next.setEnabled(false);
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @ 获取当前页面需要显示的Paths
	 * */
	public void Init_NowPaths(int Init_Start,int Init_End)
	{
		Now_Paths.clear(); 
		
		for(int i = Init_Start;i < (Paths.size() > Init_End ? Init_End:Paths.size()) ;i++)
		{
			Now_Paths.add(Paths.get(i));//初始化现在的图片路径
		}
	}
	
	/**
	 * @author nieyihe
	 * 用于读取所有的图片的线程
	 * */
	private class Thread_For_AllPath extends Thread
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			//发送消息显示提示正在加载图片

			synchronized (Now_Paths) {
				
			
			//获取所有路径
			Init_AllPath();
			Init_NowPaths(Offset,Offset + Standard);
				//当所有的路径已经读取完了之后,在初始化当前显示的页面的所有路径
			Offset += Standard;//移动指针
			
			for(String path : Now_Paths)
			{
				if(!Pics.containsKey(path))
					Pics.put(path, BitmapLocalFactory.DecodeFile(path));
				SendMessage("Fresh");
			
			}

			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @GridView的适配器
	 * */
	private class GridViewAdapter extends BaseAdapter
	{
		private LayoutInflater i ;
		
		public GridViewAdapter()
		{
			 i = LayoutInflater.from(Ac_ShowAllPic.this);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Now_Paths.size();
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
		public View getView(int pos, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			Bindler bind ;
			if(view == null)
			{
				view = i.inflate(R.layout.single_pic, null);
				bind = new Bindler();
				bind.Img = (ImageView) view.findViewById(R.id.single_pic_img);
				bind.is_Select = (ImageView) view.findViewById(R.id.single_pic_select_img);
				view.setTag(bind);
			}
			else
			{
				bind = (Bindler) view.getTag();
			}
			
			String path = Now_Paths.get(pos);
		
			if(Pics.containsKey(path))
				bind.Img.setImageBitmap(Pics.get(path));
			else
				bind.Img.setImageResource(R.drawable.img_set);
			
			if(Is_Select_Img != null && Is_Select_Img.contains(path))
				bind.is_Select.setImageResource(R.drawable.select_yes);
			else
				bind.is_Select.setImageResource(R.drawable.select_no);
				
			return view;
		}
		
	}
	
	private class Bindler 
	{
		public ImageView Img;
		public ImageView is_Select;
	}
	
	/**
	 * @author nieyihe
	 * GridView控件的监听事件
	 * */
	private class GridView_Item_Click implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			
			String path = Now_Paths.get(pos);//获取当前选择的图片的路径
			if(Is_Select_Img.contains(path))//假设已经选择了此图片,则取消选择;
			{
				Number --;
				if(Number == 0)
				{
					submit.setEnabled(false);
					submit.setText("提交");
				}
				else
				{
					submit.setText("提交("+Number+")");
				}
				Is_Select_Img.remove(path);//取消选择
				ada.notifyDataSetChanged();

			}
			else
			{
				Number ++;
				submit.setEnabled(true);
				submit.setText("提交("+Number+")");
				Is_Select_Img.add(path);//则加入当前的已经选择项
				ada.notifyDataSetChanged();

			}
			
		}
		
	}
	
	/**
	 * @author nieyihe
	 * 提交图片
	 * */
	private class SuBmit implements View.OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(Is_Select_Img.isEmpty())//当没有选择
			{
				toast("没有已选图片,请选择需要提交的图片.");
			}
			else
			{
				String[] file_paths = new String[Is_Select_Img.size()];
				Is_Select_Img.toArray(file_paths);
				Intent i = new Intent();
				i.putExtra("member_id", member_id);
				i.putExtra("regiment_id", regiment_id);
				i.putExtra("files", file_paths);
				i.setClass(Ac_ShowAllPic.this, UpdatePicServer.class);
				Ac_ShowAllPic.this.startService(i);
				finish();
			}				
	
		}
	}
	
	
	
	/**
	 * @author nieyihe
	 * 点击上一页,下一页时触发的事件 
	 * */
	private class Show implements View.OnClickListener
	{

		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.show_allpic_next:
				{
					Show_Next();	
					break;
					
				}
				case R.id.show_allpic_previous:
				{
					Show_Pre();
					break;
				}
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * 
	 * */
	private class Gesture extends GestureDetector.SimpleOnGestureListener
	{

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			if(Math.abs(velocityX) > 10 && Math.abs((e1.getY() - e2.getY() )) <300)
			{
				if((e1.getX() - e2.getX()) < 0  )
					Show_Pre();
				
				if((e2.getX() - e1.getY()) < 0)
					Show_Next();
			}
			
			return true;
		}
		
	}
	
	private class GridOnTouch implements View.OnTouchListener
	{

		@Override
		public boolean onTouch(View arg0, MotionEvent ev) {
			// TODO Auto-generated method stub
			return gs.onTouchEvent(ev);
		}
		
	}
	
	/**
	 * @author nieyihe
	 * 异常退出后返回,及时恢复重要数据
	 * */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		
		Offset = Integer.parseInt(savedInstanceState.getString("OFFSET"));
		
		//开始获取路径
		if(Is_Select_Img == null)
			Is_Select_Img = new ArrayList<String>();
		
		String[] str = savedInstanceState.getStringArray("Is_Selected");
		for(int i = 0 ; i < str.length ;i ++)
		{
			Is_Select_Img.add(str[i]);
		}
		
		new Thread_For_AllPath().start();
		//当所有的已选择的图片已经保存完成后
		//调用新的线程获取所有路径
		//以及当前显示的路径的bitmap的加载
		//启动界面显示
		
	}

	
	/**
	 * @author nieyihe
	 * 异常退出时,及时保存重要数据
	 * */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		outState.putString("OFFSET", Offset +"");
		String str[]  = new String[Is_Select_Img.size()];
		for(int i = 0 ;i< Is_Select_Img.size() ; i++)
		{
			str[i] = Is_Select_Img.get(i);
		}
		outState.putStringArray("Is_Selected", str);
	}

	
/*	*//**
	 * @author nieyihe
	 * 提交图片到bmob服务器
	 * *//*
	@SuppressWarnings("deprecation")
	public void Upload_Img(String[] files_path)
	{
		notification = NotificationLocalFactory.NotificationViewCreate(Ac_ShowAllPic.this, R.layout.notification_message,R.drawable.input, 0);
		NotificationLocalFactory.SetViewImg(notification.contentView, R.drawable.input, R.id.notification_img, notification, 0);
		
		BmobProFile.getInstance(Ac_ShowAllPic.this).uploadBatch(files_path,new UploadBatchListener() {
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(boolean isFinish, String[] paths, String[] urls) {
				// TODO Auto-generated method stub
				
				if(isFinish)
				{
					toast("完成上传");
					NotificationLocalFactory.Cancel(0);
					for(int UpdatePos = 0 ; UpdatePos < paths.length ; UpdatePos++ )
					{
						Upload_Regiment_Picture(paths[UpdatePos],urls[UpdatePos]);
					}
				}
			
			}
			
			@Override
			public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
				// TODO Auto-generated method stub
				NotificationLocalFactory.SetViewText(notification.contentView, "正在上传第"+curIndex+"张照片"+"\n当前照片完成 "+curPercent+"%"+"\n即将完成进度："+totalPercent+"%", R.id.notification_word,notification,0);
			}
		});
		
	}
	
	*//**
	 * @author nieyihe
	 * 将图片上传到相应的bmob数据库中
	 * *//*
	private void Upload_Regiment_Picture(String path,String url)
	{
		regiment_audio pic = new regiment_audio();
		pic.img_path = path ;
		pic.img_url  = url;
		pic.member_id = Ac_ShowAllPic.this.member_id;
		pic.praise_num = 0;
		pic.type = "Pic";
		pic.intro = "";
		pic.url = "";
		pic.regiment_id = Ac_ShowAllPic.this.regiment_id;
		pic.save(Ac_ShowAllPic.this, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				toast("上传失败");
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("上传成功");
			}
			
		});
	}*/
	/**
	 * @author nieyihe
	 * 向上显示图片
	 * */
	public void Show_Pre()
	{
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();

				SendMessage("Pre_Enable");
				synchronized (Now_Paths)
				{
					if(Offset > Standard) 
					{
						Offset -= Standard;//指针减少
						Init_NowPaths(Offset - Standard,Offset);
						SendMessage("Fresh");
						for(String path : Now_Paths)
						{
							if(!Pics.containsKey(path))
								Pics.put(path, BitmapLocalFactory.DecodeFile(path));
							SendMessage("Fresh");
						}
							
						if(Offset > Standard)
							SendMessage("Show_Previous");
								
					}
					
					if(Offset < Paths.size())
						SendMessage("Show_Next");
				}
			}}.start();
	}
	
	/**
	 * @author nieyihe
	 * 显示向右的图片
	 * */
	public void Show_Next()
	
		{
		new Thread(){
	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
					
				SendMessage("Next_Enable");
				synchronized (Now_Paths) 
				{
					Init_NowPaths(Offset,Offset + Standard);
					SendMessage("Fresh");
					if(Offset < Paths.size())
					{
						Offset += Standard;//指针增加
						//初始化当前的路径位置
						for(String path : Now_Paths)
						{
							if(!Pics.containsKey(path))
								Pics.put(path, BitmapLocalFactory.DecodeFile(path));
							SendMessage("Fresh");
						}
						
						
						if(Offset < Paths.size())
							SendMessage("Show_Next");
					}
					
					if(Offset > Standard)
						SendMessage("Show_Previous");
					
				 }
	
	
			
			}}.start();
		}
	
	/**
	 * @author nieyihe
	 * 获取上一个activity发送过来的数据
	 * 
	 * */
	public void Get_ExtrasData()
	{
		Bundle b = this.getIntent().getExtras();
		member_id =  b.getString("member_id") == null ? "" : b.getString("member_id");
		regiment_id = b.getString("regiment_id");
	}
	
	/**
	 * @author nieyihe
	 * @param word为提示具体信息
	 * 提示消息
	 * */
	private void toast(String word)
	{
		Toast.makeText(Ac_ShowAllPic.this, word, Toast.LENGTH_LONG).show();
	}
	
	
	/**
	 * @author nieyihe
	 * @param word 发送消息的内容
	 * @ 用于发送消息给主线程的ui消息循环
	 * */
	private void SendMessage(String word)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString(word, "");
		msg.setData(b);
		Handler.sendMessage(msg);
	}
	
}
