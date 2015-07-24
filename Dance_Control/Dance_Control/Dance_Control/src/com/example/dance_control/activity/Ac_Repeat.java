package com.example.dance_control.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

import com.example.dance_control.R;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.bmob_table.regiment_repeat;
import com.example.dance_control.bmob_table.regiment_talk;
import com.example.dance_control.resource.Repeat;
import com.example.dance_control.resource.Talk;
import com.example.dance_control.resource.member;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.example.dance_control.tools.Deal_Time;
import com.example.dance_control.tools.Repeat_Tools;
import com.example.dance_control.tools.Utility;

public class Ac_Repeat extends Activity {

	//private BmobRealTimeData rtd = new BmobRealTimeData();
	private Repeat_Tools tool;
	private String Talk_id ;
	private String member_id;
	private ArrayList<Repeat> Repeats = new ArrayList<Repeat>();
	private ArrayList<Bitmap> repeatHeadImg = new ArrayList<Bitmap>();
	private Talk talk;
	private member me;
	private ListView repeat_list;
	private BaseAdapter ada;
	private Bitmap headBitmap;//位图
	private ImageView head;
	private TextView name;
	private TextView num;
	private TextView word;
	private TextView time;
	private ImageButton emotion;
	private Button exp;
	private EditText EditWord;
	private GridView grid;//此处为表情栏
	private LinearLayout emotion_parent;//此处为表情栏的容器
	private ImageButton exit;
	private ImageButton ali;
	private ImageButton bear ;
	private ImageButton emotions ;
	private RepeatHandler handler = new RepeatHandler(); 
	
	@Override
	public void onCreate(Bundle s )
	{
		super.onCreate(s);
		this.setContentView(R.layout.all_repeat);
		
		Get_Bundle();
		Set_Repeat();
		GetBmobMemberInfo();
		GetBmobRepeat();
		GetBmobTalk();
	}
	
	/**
	 * @author nieyihe
	 * @作用:用于初始化界面
	 * */
	public void Set_Repeat()
	{
		
		head = (ImageView) this.findViewById(R.id.all_repeat_head);
		name = (TextView)this.findViewById(R.id.all_repeat_name_text);
		num  = (TextView)this.findViewById(R.id.all_repeat_praise_num);
		word = (TextView)this.findViewById(R.id.all_repeat_talkword_text);
		repeat_list = (ListView)this.findViewById(R.id.all_repeat_list);
		time = (TextView) this.findViewById(R.id.all_repeat_time_text);
		emotion = (ImageButton) this.findViewById(R.id.all_repeat_expression_button);
		exp = (Button) this.findViewById(R.id.all_repeat_repeat_button);
		EditWord = (EditText) this.findViewById(R.id.all_repeat_write_edit);
		emotion_parent = (LinearLayout) this.findViewById(R.id.repeat_emotion_layout);
		grid = (GridView) this.findViewById(R.id.emotion_gridview);
		exit = (ImageButton) this.findViewById(R.id.emotion_select_exit);
		ali = (ImageButton)this .findViewById(R.id.emotion_select_ali);
		bear = (ImageButton)this.findViewById(R.id.emotion_select_bear);
		emotions = (ImageButton)this.findViewById(R.id.emotion_select_emotion);
		
		tool = new Repeat_Tools(EditWord,grid,exit,ali,bear,emotions,emotion_parent);
		exp.setOnClickListener(new OnClick());
		emotion.setOnClickListener(new OnClick());
		EditWord.setOnClickListener(new OnClick());
		ada = new Myadapter(Ac_Repeat.this);
		
		repeat_list.setAdapter(ada);
		
		Utility.setListViewHeightBasedOnChildren(repeat_list, Ac_Repeat.this);

	}
	/**
	 * @author nieyihe
	 * @zuoyong : onclick触发事件
	 * */
	public class OnClick implements View.OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
		
			switch(arg0.getId())
			{
				case R.id.all_repeat_repeat_button:
				{
					String repeatString = EditWord.getText().toString().trim();
					
					if(repeatString.equals(""))
						break;
					if(member_id.equals("")) {
						Toast.makeText(Ac_Repeat.this, "请登录,再回复", Toast.LENGTH_SHORT).show();
						break;
					}
					Save_Repeat(repeatString,talk.talk_member_id);
					Repeats.add(new Repeat(me.name, EditWord.getText().toString(), Get_Time(), me.head_path, me.head_url));
					SendMessage("ReFresh");
					if(emotion_parent.getVisibility() == View.VISIBLE)
						emotion_parent.setVisibility(View.GONE);
					EditWord.setText("");
					break;
				}
				case R.id.all_repeat_write_edit:
				{
					emotion_parent.setVisibility(View.GONE);
					break;
				}
				case R.id.all_repeat_expression_button:
				{
					if(emotion_parent.getVisibility() == View.GONE)
						emotion_parent.setVisibility(View.VISIBLE);
					else 
						emotion_parent.setVisibility(View.GONE);
					try {
						tool.Gray_View_Show(Ac_Repeat.this);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @作用:获取上个activity传送来的数据
	 * */
	public void Get_Bundle()
	{
		Bundle s = this.getIntent().getExtras();
		Talk_id = s.getString("Talk_id");
		member_id = s.getString("member_id");
	}
	
	/**
	 * @author nieyihe
	 * @作用:ListView适配器
	 * */
	public class Myadapter extends BaseAdapter
	{
		Context context ;
		LayoutInflater l;
		
		public Myadapter(Context context)
		{
			this.context = context ;
			l = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Repeats.size();
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
		public View getView(int pos, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Repeat_Binder bind ;
			if(v == null)
			{
				v = l.inflate(R.layout.all_repeat_list, null);
				bind = new Repeat_Binder();
				
				bind.head = (ImageView) v.findViewById(R.id.all_repeat_list_head_img);
				bind.name = (TextView) v.findViewById(R.id.all_repeat_list_name_text);
				bind.time = (TextView) v.findViewById(R.id.all_repeat_list_time_text);
				bind.word = (TextView) v.findViewById(R.id.all_repeat_list_repeatword_text);
				
				v.setTag(bind);
			}
			else
			{
				bind = (Repeat_Binder)v.getTag();
			}
			
			Repeat r = Repeats.get(pos);
			Bitmap bitmap  = null;
			if(repeatHeadImg.size() > pos)
			  bitmap = repeatHeadImg.get(pos);
			if(bitmap != null)
				bind.head.setImageBitmap(bitmap);
			bind.name.setText(r.repeat_name);
			bind.time.setText(Deal_Time.When_Happen(r.repeat_time));
			
			try {
				bind.word.setText(tool.deal_Repeat(Ac_Repeat.this,r.repeat_word));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return v;
		}
		
	}
	/**
	 * @author nieyihe
	 * @作用：适配器的辅助类
	 * */
	public class Repeat_Binder
	{
		public ImageView head;
		public TextView  name;
		public TextView  time;
		public TextView  word;
	}
	
	/**
	 * @author nieyihe
	 * @作用:	获取bmob_member数据
	 * */
	public void GetBmobMemberInfo()
	{
		BmobQuery<regiment_member> query = new BmobQuery<regiment_member>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.getObject(Ac_Repeat.this, member_id, new GetListener<regiment_member>()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(regiment_member arg0) {
				// TODO Auto-generated method stub
				me = new member(arg0.member_head_path,arg0.member_head_url,arg0.regiment_id,arg0.getObjectId(),arg0.member_name,arg0.member_sex,arg0.member_power,arg0.member_host,arg0.member_password,arg0.member_ture_name,arg0.praise_num);
				
			}
			
		});
	}
	
	public void GetBmobTalk()
	{
		
		BmobQuery<regiment_talk> query = new BmobQuery<regiment_talk>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.getObject(Ac_Repeat.this, Talk_id, new GetListener<regiment_talk>()
				{

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(Ac_Repeat.this, "读取数据失败,请重新载入", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(regiment_talk arg0) {
						// TODO Auto-generated method stub
						talk = new Talk(arg0.talk_head_path,arg0.talk_head_url,arg0.talk_order,arg0.talk_time,arg0.talk_word,arg0.talk_praise_num,arg0.getObjectId(),arg0.member_id,arg0.talk_img_path,arg0.talk_img_url);
						time.setText(Deal_Time.When_Happen(talk.talk_time));
						name.setText(talk.talk_name);
						num.setText(talk.talk_praise_num+"");
						word.setText(talk.talk_word);
						
						new Thread()
						{
							public void run() 
							{
								try {
									headBitmap = BitmapLocalFactory.CreateBitmap(Ac_Repeat.this, talk.talk_head_path, talk.talk_head_url,BitmapLocalFactory.SMALL);
									SendMessage("ShowHeadImg");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							
						}.start();
					}
			
				});
	}
	
	public void GetBmobRepeat()
	{
		BmobQuery<regiment_repeat> q = new BmobQuery<regiment_repeat>();
		q.count(Ac_Repeat.this, regiment_repeat.class, new CountListener() {
			
			@Override
			public void onSuccess(int num) {
				// TODO Auto-generated method stub
				BmobQuery<regiment_repeat> query = new BmobQuery<regiment_repeat>();
				//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
				query.setLimit(num);
				query.addWhereEqualTo("repeat_talk_id", Talk_id);
				query.findObjects(Ac_Repeat.this, new FindListener<regiment_repeat>()
						{

							@Override
							public void onError(int arg0, String arg1) {
								// TODO Auto-generated method stub
								Toast.makeText(Ac_Repeat.this, "读取数据失败,请重新载入", Toast.LENGTH_LONG).show();
							}

							@Override
							public void onSuccess(List<regiment_repeat> arg0) {
								// TODO Auto-generated method stub
								Repeats.clear();
								for(regiment_repeat temp :arg0){
									
									Repeats.add(new Repeat(temp.repeat_name,temp.repeat_word,temp.repeat_time,temp.repeat_head_path,temp.repeat_head_url));
									SendMessage("ReFresh");
								}
								
								new Thread(){
									@Override
									public void run() 
									{
										for(int i = 0; i< Repeats.size();i++)
										{
											Repeat tRepeat = Repeats.get(i);
											try {
												repeatHeadImg.add(BitmapLocalFactory.CreateBitmap(Ac_Repeat.this,tRepeat.repeat_head_path,tRepeat.repeat_head_url,BitmapLocalFactory.SMALL));
												SendMessage("ReFresh");
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
								}.start();
							}
					
						});
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	public void Save_Repeat(final String word,String Create_Talk_Mm_Id)
	{
		final long time = Get_Time();
		regiment_repeat query = new regiment_repeat(me.head_path,me.head_url,time,me.name,word,Talk_id,Create_Talk_Mm_Id);
		query.save(Ac_Repeat.this, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(Ac_Repeat.this, "回复失败,请重试", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Repeats.add(new Repeat(me.name,word,time,me.head_path,me.head_url));
			}
			
		});
	}
	
	
	private long Get_Time()
	{
		return System.currentTimeMillis();
	}
	
	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	private void SendMessage(String msgString)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString(msgString, "");
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	/**
	 * @author nieyihe
	 * 消息处理
	 * */
	private class RepeatHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("ReFresh"))
			{
				ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(repeat_list, Ac_Repeat.this);
			}
			if(b.containsKey("ShowHeadImg"))
			{
				head.setImageBitmap(headBitmap);
			}
		}
	} 
}
