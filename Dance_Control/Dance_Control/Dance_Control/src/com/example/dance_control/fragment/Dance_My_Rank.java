package com.example.dance_control.fragment;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

import com.example.dance_control.R;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.bmob_table.regiment_repeat;
import com.example.dance_control.bmob_table.regiment_talk;
import com.example.dance_control.circleimageview.CircleImage;
import com.example.dance_control.resource.member;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.example.dance_control.tools.Utility;
import com.example.updownview.DownFreshLinear;
import com.example.updownview.DownFreshLinear.FreshListener;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint({ "NewApi", "ValidFragment" })
public class Dance_My_Rank extends Base_Fragment {

	private View headView;
	private ImageButton ReFresh;
	private ImageButton Back;
	private TextView Hint ;
	private ListView Rank_List;
	private String regiment_id = "";
	private String member_power = "";
	private String member_id = "";
	private ArrayList<member> RanksArrayList = new ArrayList<member>();
	private HashMap<String , Bitmap> RankHeadBitmaps = new HashMap<String , Bitmap>();
	private Rankhandler handler = new Rankhandler();
	private RankAdapter adapter;
	private Animation anim;
	//下拉刷新Linear
		private DownFreshLinear Linear;
	private Integer Last = 0 ;
		//最后显示的成员的编号	
	private Integer ShowNum = 10;
	//一次显示的成员的个数
	public Dance_My_Rank(SlidingMenu menu,String regiment_id,String member_id,String member_power)
	{
		this.menu = menu;
		this.member_id = member_id;
		this.regiment_id = regiment_id;
		this.member_power = member_power;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		BuildRankView();
		GainAllMemberInfo();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.all_member, null);
	}

	/***
	 * @author nieyihe
	 * 创建View
	 * 
	 * */
	public void BuildRankView()
	{
		View parent = this.getView();
		headView = parent.findViewById(R.id.all_member_head);
		Rank_List = (ListView) parent.findViewById(R.id.all_member_list);
		Linear = (DownFreshLinear) parent.findViewById(R.id.all_member_DownFresh);
		adapter = new RankAdapter();
		Rank_List.setAdapter(adapter);
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
				GainAllMemberInfo();
			}
		});
		Hint.setText("成员信息");
		SetOnclick();
		
		Utility.setListViewHeightBasedOnChildren(Rank_List, context);
	}
	
	/**
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
		
		this.Linear.setOnFresh(4, new FreshListener() {
			
			@Override
			public boolean onFreshTop() {
				// TODO Auto-generated method stub
				
				new Thread()
				{
					public void run() 
					{
						ShowNewSeriesMem(Last);
					};
				}.start();
				
				return true;
			}
			
			@Override
			public boolean onFreshButton() {
				// TODO Auto-generated method stub
				new Thread()
				{
					public void run() 
					{
						ShowNextSeriesMem(Last,ShowNum);
					}
				}.start();

				return true;
			}
		});
	}
	
	/**
	 * @author nieyihe
	 * 适配器
	 * */
	private class RankAdapter extends BaseAdapter
	{
		LayoutInflater i ;
		public RankAdapter() {
			// TODO Auto-generated constructor stub
			i = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return RanksArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Bind bind;
			if(convertView == null)
			{
				convertView = i.inflate(R.layout.rank_person, null);
				bind = new Bind();
				bind.Num = (ImageView) convertView.findViewById(R.id.rank_person_number);
				bind.NumText = (TextView) convertView.findViewById(R.id.rank_person_numberText);
				bind.Head = (CircleImage) convertView.findViewById(R.id.rank_person_img);
				bind.Hots = (TextView) convertView.findViewById(R.id.rank_person_hots);
				bind.TrueName = (TextView) convertView.findViewById(R.id.rank_person_name);
				bind.RegimentName = (TextView) convertView.findViewById(R.id.rank_person_othername);
				bind.Delete = (ImageButton) convertView.findViewById(R.id.rank_person_delete);
				convertView.setTag(bind);
			}
			else 
			{
				bind = (Bind)convertView.getTag();
			}
			
			final member me = RanksArrayList.get(position);
			Bitmap head = RankHeadBitmaps.get(me.member_id);
			if(head != null)
				bind.Head.setImageBitmap(head);
			switch(position)
			{
				case 0 :
					{
						bind.Num.setVisibility(View.VISIBLE);
						bind.NumText.setVisibility(View.INVISIBLE);
						bind.Num.setImageResource(R.drawable.no_1);
						break;
					}
				case 1 :
					{
						bind.Num.setVisibility(View.VISIBLE);
						bind.NumText.setVisibility(View.INVISIBLE);
						bind.Num.setImageResource(R.drawable.no_2);
						break;
					}
				case 2 : 
					{
						bind.Num.setVisibility(View.VISIBLE);
						bind.NumText.setVisibility(View.INVISIBLE);
						bind.Num.setImageResource(R.drawable.no_3);
						break;
					}
				default:
					{
						bind.Num.setVisibility(View.INVISIBLE);
						int pos = position + 1;
						bind.NumText.setText( "" +  pos);
						break;
					}
			}
				
			bind.TrueName.setText(me.member_true_name);
			bind.RegimentName.setText(me.name);
			bind.Hots.setText(me.praise_num +"");
			
			if(member_power.equals("order") &&  !member_id.equals(me.member_id)){
				bind.Delete.setVisibility(View.VISIBLE);
				bind.Delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ImageButton i = (ImageButton)v;
						i.setImageResource(R.drawable.delete_end);
						i.setImageResource(R.drawable.delete_before);
						DeletePerson(me.member_id,position);
					}
				});
			}
			else{
				bind.Delete.setVisibility(View.INVISIBLE);
			}
				
			
			return convertView;
		}
		
	}
	
	public class Bind
	{
		 ImageView Num;
		 TextView NumText;
		 CircleImage Head;
		 TextView TrueName;
		 TextView RegimentName;
		 TextView Hots;
		 ImageButton Delete;
	}
	
	/***
	 * @author nieyihe
	 * 获取所有成员的信息
	 * */
	public void GainAllMemberInfo()
	{
		RanksArrayList.clear();
		BmobQuery<regiment_member> query = new BmobQuery<regiment_member>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.order("-praise_num");
		query.findObjects(context, new FindListener<regiment_member>() {
			
			@Override
			public void onSuccess(List<regiment_member> memlist) {
				// TODO Auto-generated method stub
				for(regiment_member mem : memlist)
					{
						RanksArrayList.add(new member(mem.member_head_path, mem.member_head_url, mem.regiment_id, mem.getObjectId(), mem.member_name, mem.member_sex, mem.member_power, mem.member_host, mem.member_password, mem.member_ture_name, mem.praise_num));
						SendMessage("ReFresh","");
					}
				
				Last = RanksArrayList.size(); 
				
				new Thread()
				{
					@Override
					public void run()
					{
						for(int i = 0 ; i < RanksArrayList.size(); i ++)
						{
							member mem = RanksArrayList.get(i);
							try {
								RankHeadBitmaps.put(mem.member_id, BitmapLocalFactory.CreateBitmap(context, mem.head_path, mem.head_url, BitmapLocalFactory.SMALL));
								SendMessage("ReFresh","");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}.start();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	/**
	 * @author nieyihe
	 * 消息处理函数
	 * */
	private class Rankhandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("ReFresh"))
			{
				adapter.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(Rank_List, context);
			}
			
		}
	}
	
	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	public void SendMessage(String key,String value)
	{
		Bundle b = new Bundle();
		Message msg = new Message();
		b.putString(key, value);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	/**
	 * @author nieyihe
	 * 显示更多的成员
	 * */
	public void ShowNextSeriesMem(final int Skip , int Limit)
	{
		BmobQuery<regiment_member> query = new BmobQuery<regiment_member>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.setSkip(Skip);
		query.setLimit(Limit);
		query.order("-praise_num");
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.findObjects(context, new FindListener<regiment_member>() {
			
			@Override
			public void onSuccess(List<regiment_member> list) {
				// TODO Auto-generated method stub
				for(regiment_member mem : list)
				{
					RanksArrayList.add(new member(mem.member_head_path, mem.member_head_url, mem.regiment_id, mem.getObjectId(), mem.member_name, mem.member_sex, mem.member_power, mem.member_host, mem.member_password, mem.member_ture_name, mem.praise_num));
					adapter.notifyDataSetChanged();
					Utility.setListViewHeightBasedOnChildren(Rank_List, context);
				}
				
				Last = RanksArrayList.size();
				
				new Thread()
				{
					@Override
					public void run()
					{
						for(int i = Skip; i < RanksArrayList.size() ; i ++)
						{
							member mem = RanksArrayList.get(i);
							try {
								RankHeadBitmaps.put(mem.member_id, BitmapLocalFactory.CreateBitmap(context, mem.head_path, mem.head_url, BitmapLocalFactory.SMALL));
								SendMessage("ReFresh", "");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}.start();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * @author nieyihe
	 * 刷新最新的数据
	 * */
	public void ShowNewSeriesMem(int Limit)
	{
		
		BmobQuery<regiment_member> query = new BmobQuery<regiment_member>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.setLimit(Limit > ShowNum ? Limit : ShowNum);
		query.order("-praise_num");
		query.addWhereEqualTo("regiment_id", regiment_id);
		
		
		query.findObjects(context, new FindListener<regiment_member>() {
			
			@Override
			public void onSuccess(List<regiment_member> list) {
				// TODO Auto-generated method stub
				RanksArrayList.clear();
				RankHeadBitmaps.clear();

				for(regiment_member mem : list)
				{
					RanksArrayList.add(new member(mem.member_head_path, mem.member_head_url, mem.regiment_id, mem.getObjectId(), mem.member_name, mem.member_sex, mem.member_power, mem.member_host, mem.member_password, mem.member_ture_name, mem.praise_num));
					
					adapter.notifyDataSetChanged();
					
					Utility.setListViewHeightBasedOnChildren(Rank_List, context);
					
				}
				
				Last = RanksArrayList.size();
				new Thread()
				{
					@Override
					public void run()
					{
						for(int i = 0; i < RanksArrayList.size() ; i ++)
						{
							member mem = RanksArrayList.get(i);
							
							try {
								RankHeadBitmaps.put(mem.member_id, BitmapLocalFactory.CreateBitmap(context, mem.head_path, mem.head_url, BitmapLocalFactory.SMALL));
								
								SendMessage("ReFresh", "");
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}.start();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});

	}
	
	/**
	 * @author nieyihe
	 * 删除这个人员
	 * */
	public void DeletePerson(final String personid,int pos)
	{
		regiment_member mem = new regiment_member();
		mem.delete(context, personid, new DeleteListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
		/*int talknum = 0;
		int repeatnum = 0;*/
		
		BmobQuery<regiment_talk> query = new BmobQuery<regiment_talk>();
		query.count(context, regiment_talk.class, new CountListener() {
			
			@Override
			public void onSuccess(int num) {
				// TODO Auto-generated method stub
				BmobQuery<regiment_talk> query_1 = new BmobQuery<regiment_talk>();
				query_1.addWhereEqualTo("member_id", personid);
				query_1.setLimit(num);
				query_1.findObjects(context, new FindListener<regiment_talk>() {
					
					@Override
					public void onSuccess(List<regiment_talk> talks) {
						// TODO Auto-generated method stub
						for(regiment_talk talk : talks)
						{
							regiment_talk delete_talk = new regiment_talk();
							delete_talk .delete(context, talk.getObjectId() , new DeleteListener() {
								
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
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		BmobQuery<regiment_repeat> query_2 = new BmobQuery<regiment_repeat>();
		query_2.count(context, regiment_repeat.class, new CountListener() {
			
			@Override
			public void onSuccess(int num) {
				// TODO Auto-generated method stub
				BmobQuery<regiment_repeat> query_3 = new BmobQuery<regiment_repeat>();
				query_3.addWhereEqualTo("Talk_Create_Mm_Id", personid);
				query_3.setLimit(num);
				query_3.findObjects(context,new FindListener<regiment_repeat>() {
					
					@Override
					public void onSuccess(List<regiment_repeat> repeats) {
						// TODO Auto-generated method stub
						for(regiment_repeat repeat : repeats){
						
							regiment_repeat r = new regiment_repeat();
							r.delete(context, repeat.getObjectId(), new DeleteListener() {
								
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
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
		RanksArrayList.remove(pos);
		adapter.notifyDataSetChanged();
		Utility.setListViewHeightBasedOnChildren(Rank_List, context);
	}
}
