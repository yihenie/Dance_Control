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
	private ArrayList<String> Paths = new ArrayList<String>();//�������е�·��
	private ArrayList<String> Now_Paths = new ArrayList<String>();//���浱ǰҳ��ʾ������·��
	private ArrayList<String> Is_Select_Img = new ArrayList<String>();
	private HashMap<String,Bitmap> Pics = new HashMap<String,Bitmap>();
	//���������Ѿ�ѡ���ͼƬ��·��
	private GridView gridview;
	private GridViewAdapter ada; 
	private Button submit ;
	private MyHandler Handler = new MyHandler();
	private ProgressDialog pd;
	private Button Next;
	private Button Privious;
	private Integer Offset = 0;//��ʾ���ڵ���ʾ��Paths�ĳ�ʼλ�� - ָ��
	private Integer Standard = 10;//һ����ʾͼƬ������
	private Integer Number = 0;//�Ѿ�ѡ���ͼƬ����
	private GestureDetector gs;
	private Notification notification ;
	
	@Override
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		this.setContentView(R.layout.show_allpic);
		
		gs = new GestureDetector(this,new Gesture());
		Get_ExtrasData();//��ȡ��������
		new Thread_For_AllPath().start();//���ڼ���ͼƬ·�����߳�
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
	 * @ �������е�·��
	 * @ ͨ������external.db���ݿ�����ȡsd��������ͼƬ��·��
	 * */
	public void Init_AllPath()
	{
		if(!Paths.isEmpty()) return ;
		//�����Ѿ������������ٻ�ȡ
		//�˴�Ϊ���쳣�˳���ָ�����ʱ����
		
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
	 * ��Ϣ�ص�����
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
	 * @ ��ȡ��ǰҳ����Ҫ��ʾ��Paths
	 * */
	public void Init_NowPaths(int Init_Start,int Init_End)
	{
		Now_Paths.clear(); 
		
		for(int i = Init_Start;i < (Paths.size() > Init_End ? Init_End:Paths.size()) ;i++)
		{
			Now_Paths.add(Paths.get(i));//��ʼ�����ڵ�ͼƬ·��
		}
	}
	
	/**
	 * @author nieyihe
	 * ���ڶ�ȡ���е�ͼƬ���߳�
	 * */
	private class Thread_For_AllPath extends Thread
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			//������Ϣ��ʾ��ʾ���ڼ���ͼƬ

			synchronized (Now_Paths) {
				
			
			//��ȡ����·��
			Init_AllPath();
			Init_NowPaths(Offset,Offset + Standard);
				//�����е�·���Ѿ���ȡ����֮��,�ڳ�ʼ����ǰ��ʾ��ҳ�������·��
			Offset += Standard;//�ƶ�ָ��
			
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
	 * @GridView��������
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
	 * GridView�ؼ��ļ����¼�
	 * */
	private class GridView_Item_Click implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			
			String path = Now_Paths.get(pos);//��ȡ��ǰѡ���ͼƬ��·��
			if(Is_Select_Img.contains(path))//�����Ѿ�ѡ���˴�ͼƬ,��ȡ��ѡ��;
			{
				Number --;
				if(Number == 0)
				{
					submit.setEnabled(false);
					submit.setText("�ύ");
				}
				else
				{
					submit.setText("�ύ("+Number+")");
				}
				Is_Select_Img.remove(path);//ȡ��ѡ��
				ada.notifyDataSetChanged();

			}
			else
			{
				Number ++;
				submit.setEnabled(true);
				submit.setText("�ύ("+Number+")");
				Is_Select_Img.add(path);//����뵱ǰ���Ѿ�ѡ����
				ada.notifyDataSetChanged();

			}
			
		}
		
	}
	
	/**
	 * @author nieyihe
	 * �ύͼƬ
	 * */
	private class SuBmit implements View.OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(Is_Select_Img.isEmpty())//��û��ѡ��
			{
				toast("û����ѡͼƬ,��ѡ����Ҫ�ύ��ͼƬ.");
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
	 * �����һҳ,��һҳʱ�������¼� 
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
	 * �쳣�˳��󷵻�,��ʱ�ָ���Ҫ����
	 * */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		
		Offset = Integer.parseInt(savedInstanceState.getString("OFFSET"));
		
		//��ʼ��ȡ·��
		if(Is_Select_Img == null)
			Is_Select_Img = new ArrayList<String>();
		
		String[] str = savedInstanceState.getStringArray("Is_Selected");
		for(int i = 0 ; i < str.length ;i ++)
		{
			Is_Select_Img.add(str[i]);
		}
		
		new Thread_For_AllPath().start();
		//�����е���ѡ���ͼƬ�Ѿ�������ɺ�
		//�����µ��̻߳�ȡ����·��
		//�Լ���ǰ��ʾ��·����bitmap�ļ���
		//����������ʾ
		
	}

	
	/**
	 * @author nieyihe
	 * �쳣�˳�ʱ,��ʱ������Ҫ����
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
	 * �ύͼƬ��bmob������
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
					toast("����ϴ�");
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
				NotificationLocalFactory.SetViewText(notification.contentView, "�����ϴ���"+curIndex+"����Ƭ"+"\n��ǰ��Ƭ��� "+curPercent+"%"+"\n������ɽ��ȣ�"+totalPercent+"%", R.id.notification_word,notification,0);
			}
		});
		
	}
	
	*//**
	 * @author nieyihe
	 * ��ͼƬ�ϴ�����Ӧ��bmob���ݿ���
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
				toast("�ϴ�ʧ��");
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("�ϴ��ɹ�");
			}
			
		});
	}*/
	/**
	 * @author nieyihe
	 * ������ʾͼƬ
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
						Offset -= Standard;//ָ�����
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
	 * ��ʾ���ҵ�ͼƬ
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
						Offset += Standard;//ָ������
						//��ʼ����ǰ��·��λ��
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
	 * ��ȡ��һ��activity���͹���������
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
	 * @param wordΪ��ʾ������Ϣ
	 * ��ʾ��Ϣ
	 * */
	private void toast(String word)
	{
		Toast.makeText(Ac_ShowAllPic.this, word, Toast.LENGTH_LONG).show();
	}
	
	
	/**
	 * @author nieyihe
	 * @param word ������Ϣ������
	 * @ ���ڷ�����Ϣ�����̵߳�ui��Ϣѭ��
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
