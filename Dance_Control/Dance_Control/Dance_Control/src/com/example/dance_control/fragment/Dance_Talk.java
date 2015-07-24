package com.example.dance_control.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_AddTalk;
import com.example.dance_control.activity.Ac_Repeat;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.bmob_table.praise;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.bmob_table.regiment_repeat;
import com.example.dance_control.bmob_table.regiment_talk;
import com.example.dance_control.dialog.BtuClicking;
import com.example.dance_control.dialog.OptionDialog;
import com.example.dance_control.resource.Repeat;
import com.example.dance_control.resource.Talk;
import com.example.dance_control.resource.member;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.example.dance_control.tools.Deal_Time;
import com.example.dance_control.tools.Repeat_Tools;
import com.example.dance_control.tools.Utility;
import com.example.updownview.DownFreshLinear;
import com.example.updownview.DownFreshLinear.FreshListener;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class Dance_Talk extends Base_Fragment{

	//private BmobRealTimeData rtd = new BmobRealTimeData();//Bmob������
	private HashMap<String,Bitmap> HeadBitmaps = new HashMap<String,Bitmap>(); 
	//����ͷ��λͼ
	private HashMap<String,Bitmap> ImgBitmapMap = new HashMap<String, Bitmap>();
	//���е���̳ͼƬ
	private ArrayList<Talk> talks = new ArrayList<Talk>();
	//���е���̳��
	private HashMap<String ,ArrayList<Repeat>> repeats;
	//ÿһ����̳Id��Ӧ�Ļظ�
	private HashMap<String,String> praiseList = new HashMap<String, String>();
	//�Ѿ��޵��б� ��һ��String ΪTalkId �ڶ���Ϊ�޵ı��id �ڶ����ֶ�����ɾ���޵ļ�¼ʱ��
	private ListView Talk_List;
	//��̳��ListView
	private MyAdapter Talk_adapter;
	//������
	private String regiment_id = "";
	//����id
	private String member_id = "";
	//���˵�id
	private String member_power = "";
	//Ȩ��
	private member me = null;
	//������Ϣ
	private ImageButton Back;
	//����
	private TextView Hint;
	//��ʾ
	private ImageButton ReFresh;
	//ˢ��
	private View headView;
	//����head
	private ImageButton talk_write_btu;
	//���Talk
	private DownFreshLinear Linear;
	//�˴�Ϊ��Ӧ��˵˵�������б�,String Ϊһ��˵˵�ı�ʾ,ArraList<Repeat>Ϊ��ǰ˵˵������.
	public TalkHandler handler = new TalkHandler();
	
	private Repeat_Tools tools = new Repeat_Tools();
	
	private Animation anim;
	//����
	private Integer Last = 0 ;
	//�����ʾ����̳�ı��	
	private Integer ShowNum = 10;
	//һ����ʾ����̳�ĸ���
	
	public Dance_Talk(SlidingMenu menu ,String regiment_id,String member_id,String member_power)
	{
		this.menu = menu;
		this.regiment_id = regiment_id;
		this.member_id = member_id;
		this.member_power = member_power;
	}
	
	@Override
	public View onCreateView(LayoutInflater l , ViewGroup v , Bundle b)
	{
		return l.inflate(R.layout.talk, null);
	}
	
	@Override
	public void onActivityCreated(Bundle b)
	{
		super.onActivityCreated(b);
		this.BuildTalkView();
		this.GainTalkAndPraise();
	}
	
	/**@author nieyihe
	 * ��ȡ���е���Ϣ
	 * 
	 * */
	public void GainTalkAndPraise()
	{
		if(!member_id.equals("") && me == null)
		{
			GetMember();
			GetIsPraiseList();
		}
		if(!regiment_id.equals(""))
			Get_Bmob_All_Info();
	}
	
	/**
	 * @author nieyihe
	 * @����:����������ڳ�ʼ������
	 * */
	public void BuildTalkView()
	{
		repeats = new HashMap<String ,ArrayList<Repeat>>();
		View parent = this.getView();
		headView = parent.findViewById(R.id.talk_head);
		Back = (ImageButton) headView.findViewById(R.id.Head_Back);
		Hint = (TextView) headView.findViewById(R.id.Head_Word_Hint);
		this.ReFresh = (ImageButton) headView.findViewById(R.id.Head_ReFresh);
		ReFresh.setVisibility(View.VISIBLE);
		anim = AnimationUtils.loadAnimation(context, R.anim.btu_refresh_anim);  
		ReFresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				ReFresh.startAnimation(anim);
				GainTalkAndPraise();
			}
		});

		Hint.setText("��̳");
		talk_write_btu = (ImageButton) parent.findViewById(R.id.talk_write_btu);
		Linear = (DownFreshLinear) parent.findViewById(R.id.talk_layout_DownFresh);
		Talk_List = (ListView)parent.findViewById(R.id.talk_inner_list);
		Talk_adapter = new MyAdapter(context,talks,repeats);
		Talk_List.setAdapter(Talk_adapter);
		SetOnClick();
		Linear.setOnFresh(2, new FreshListener(){

			@Override
			public boolean onFreshTop() {
				// TODO Auto-generated method stub
				GainNewSeriesTalk(Last);
				return true;
			}

			@Override
			public boolean onFreshButton() {
				// TODO Auto-generated method stub
				GainNextSeriesTalk(Last,ShowNum);
				return true;
			}

			
		});
		Utility.setListViewHeightBasedOnChildren(Talk_List, context);
	}
	
	/**
	 * @author nieyihe
	 * ���ü�����
	 * */
	public void SetOnClick()
	{
		Talk_List.setOnItemClickListener(new Item_Onclick());
		talk_write_btu.setOnClickListener(new BtuClick());
		Back.setOnClickListener(new BtuClick());
	}
	
	/**
	 * @author nieyihe
	 * @���ã�item����¼�
	 * */
	public class Item_Onclick implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			//����ظ����۵�activity
			Talk now = talks.get(pos);
			//now.Talk_Id;��ȡ�����е�ǰ������
			Intent start = new Intent();
			start.setClass(context, Ac_Repeat.class);
			String str = me == null?"":member_id ;
			Bundle b = new Bundle();
			b.putString("Talk_id", now.Talk_Id);
			b.putString("member_id", str);
			start.putExtras(b);
			
			Dance_Talk.this.startActivity(start);
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @ZUOYONG:��ť����¼�
	 * */
	public class BtuClick implements View.OnClickListener
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
				case R.id.talk_write_btu:
				{
					if(member_id .equals("")) 
					{
						Toast.makeText(context, "�ο�ֻ�ܹۿ�", Toast.LENGTH_SHORT).show();
						break;
					}
					Intent i = new Intent();
					i.setClass(context, Ac_AddTalk.class);
					Bundle b = new Bundle();
					b.putString("member_id", member_id);
					b.putString("regiment_id", regiment_id);
					i.putExtras(b);
					context.startActivity(i);
					
					break;
				}
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @����:�������˵˵�б��������
	 * */
	public class MyAdapter extends BaseAdapter
	{
		private Context context;
		private LayoutInflater l ;
		private ArrayList<Talk> talks;
		private HashMap<String , ArrayList<Repeat>> repeats;
		public MyAdapter(Context context , ArrayList<Talk> talk,HashMap<String ,ArrayList<Repeat>> repeat)
		{
			this.context = context ;
			this.talks = talk;
			this.repeats = repeat;
			l = LayoutInflater.from(this.context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			//return this.talks.size();
			return this.talks.size();
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
			talk_binder bind ;
			if(v == null)
			{
				v = this.l.inflate(R.layout.talk_list, null);
				bind = new talk_binder();
				bind.head = (ImageView)v.findViewById(R.id.talk_head_img);
				bind.name = (TextView)v.findViewById(R.id.talk_name_text);
				bind.time = (TextView) v.findViewById(R.id.talk_time_text);
				bind.praise = (ImageView) v.findViewById(R.id.talk_praise);
				bind.praise_num = (TextView)v.findViewById(R.id.talk_praise_num_text);
				bind.word = (TextView)v.findViewById(R.id.talk_word_text);
				bind.img = (ImageView) v.findViewById(R.id.talk_img);
				bind.evaluate = (ImageView)v.findViewById(R.id.talk_evaluate);
				bind.all_repeat_layout = (RelativeLayout)v.findViewById(R.id.talk_evaluate_dital);//talk_evaluate_dital
				bind.evaluate_layout_1 = (LinearLayout)v.findViewById(R.id.talk_evaluate_dital_repeat_layout_1);
				bind.evaluate_layout_2 = (LinearLayout)v.findViewById(R.id.talk_evaluate_dital_repeat_layout_2);
				bind.evaluate_dital_name_1 = (TextView)v.findViewById(R.id.talk_evaluate_dital_repeat_name_1);
				bind.evaluate_dital_name_2 = (TextView)v.findViewById(R.id.talk_evaluate_dital_repeat_name_2);
				bind.evaluate_dital_word_1 = (TextView)v.findViewById(R.id.talk_evaluate_dital_repeat_word_1);
				bind.evaluate_dital_word_2 = (TextView)v.findViewById(R.id.talk_evaluate_dital_repeat_word_2);
				bind.more = (ImageView) v.findViewById(R.id.talk_dital_more);
				v.setTag(bind);
			}
			else
			{
				bind  = (talk_binder)v.getTag();
			}
			
			//bind.head.setImageResource()
			Talk now = talks.get(pos);
			Repeat r_1 = null;
			Repeat r_2 = null;
			ArrayList<Repeat> All_Repeat = repeats.get(now.Talk_Id);
			Bitmap headBitmap = null;
				headBitmap = HeadBitmaps.get(now.Talk_Id);
			if(headBitmap != null)
				bind.head.setImageBitmap(headBitmap);
			
			if(now.talk_img_path.equals("") || now.talk_img_url.equals(""))
				bind.img.setVisibility(View.GONE);
			else
				bind.img.setVisibility(View.VISIBLE);
			
			Bitmap imgBitmap  = ImgBitmapMap.get(now.Talk_Id);
			if(imgBitmap != null)
					bind.img.setImageBitmap(imgBitmap);
			
			if(All_Repeat != null )
			{
				if(All_Repeat.size() >= 2)
				{
					 r_1 = All_Repeat.get(0);
					 r_2 = All_Repeat.get(1);
				}
				
				if(All_Repeat.size() == 1)
				{
					r_1 = All_Repeat.get(0);
				}
			}
			bind.name.setText(now.talk_name);
			bind.time.setText(Deal_Time.When_Happen(now.talk_time)+"");
			bind.praise_num.setText(now.talk_praise_num + "");			
			bind.word.setText(now.talk_word);
			bind.praise.setTag(now);
			if(praiseList.containsKey(now.Talk_Id))
				{
						bind.praise.setImageResource(R.drawable.praise_hand_end);
						bind.praise.setOnClickListener(new MyClickLinstener(true));
				}
			else
				{
						bind.praise.setImageResource(R.drawable.praise_hand_before);
						bind.praise.setOnClickListener(new MyClickLinstener(false));
				}	
			
			bind.evaluate.setTag(now);
			bind.evaluate.setOnClickListener(new MyClickLinstener());
			if(r_1 == null && r_2 == null)
			{//����û�лظ��򶼲���ʾ�ظ�
				bind.all_repeat_layout.setVisibility(View.GONE);
			}
			
			else
			{
				bind.all_repeat_layout.setVisibility(View.VISIBLE);
				if(r_1 == null)
				{
					bind.evaluate_layout_1.setVisibility(View.GONE);
				}
				else if(r_1 != null)
				{
					bind.evaluate_layout_1.setVisibility(View.VISIBLE);
					bind.evaluate_dital_name_1.setText(r_1.repeat_name);
					try {
						bind.evaluate_dital_word_1.setText(tools.deal_Repeat(context, r_1.repeat_word));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(r_2 == null)
				{
					bind.evaluate_layout_2.setVisibility(View.GONE);
				
				}
				else if(r_2 != null)
				{
					bind.evaluate_layout_2.setVisibility(View.VISIBLE);
					bind.evaluate_dital_name_2.setText(r_2.repeat_name);
					try {
						bind.evaluate_dital_word_2.setText(tools.deal_Repeat(context,r_2.repeat_word));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
			return v;
		}
		
	}
	

	
	/**
	 * @author nieyihe
	 * @����:���ڻظ�,�Լ������ʱ����Ӧ�¼�
	 * */
	public class MyClickLinstener implements View.OnClickListener
	{

		public boolean isPraise = false;
		public MyClickLinstener(boolean is)
		{
			this.isPraise = is;
		}
		
		public MyClickLinstener() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.talk_praise)
			{
				if(isPraise == false)
				{
					Talk talk = (Talk)v.getTag();
					((ImageView)v).setImageResource(R.drawable.praise_hand_end);;
				
					InputPraise(talk.Talk_Id,talk.talk_member_id);
					isPraise = true;
				}
				else
				{
					Talk talk = (Talk)v.getTag();
					((ImageView)v).setImageResource(R.drawable.praise_hand_before);;
					
					DeletePraise(talk.Talk_Id,talk.talk_member_id);
					isPraise = false;
				}
			}
			if(v.getId() == R.id.talk_evaluate)
			{
				if(member_id.equals("")) {
					toast("���¼��,������");
					return;
				}
				//������ظ�ʱ�����Ի���,���ύ���ݵ��ƶ�
				
				String id = ((Talk)v.getTag()).Talk_Id;
				String name = ((Talk)v.getTag()).talk_name;
				String mm_id = ((Talk)v.getTag()).talk_member_id;

				Dialog dialog = new Dialog(context,R.style.Item_Dialog);
				RelativeLayout repeat_dialog = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.repeat_dialog, null);
				
				TextView talk_name = (TextView) repeat_dialog.findViewById(R.id.repeat_dialog_aim_name);
				EditText repeat_word = (EditText) repeat_dialog.findViewById(R.id.repeat_dialog_edit_word);
				Button repeat_exit = (Button) repeat_dialog.findViewById(R.id.repeat_dialog_button_exit);
				Button repeat_infrom = (Button) repeat_dialog.findViewById(R.id.repeat_dialog_button_inform);
				
				talk_name.setText(name);
				dialog.setContentView(repeat_dialog);
				
				repeat_exit.setOnClickListener(new DialogOnclick(dialog,null,"",""));
				repeat_infrom.setOnClickListener(new DialogOnclick(dialog,repeat_word,id,mm_id));
				dialog.show();
			
			}
			// TODO Auto-generated method stub			
			//����������ظ�
		}
		
	}
	
	public class DialogOnclick implements View.OnClickListener
	{
		private String talk_id;
		private Dialog dialog;
		private EditText repeat_word;
		private String Create_Talk_Mm_id;
		public DialogOnclick( Dialog dialog,EditText repeat_word,String id,String Create_Talk_Mm_id)
		{
			this.dialog = dialog;
			this.repeat_word = repeat_word;
			this.talk_id = id;
			this.Create_Talk_Mm_id = Create_Talk_Mm_id;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.repeat_dialog_button_inform:
				{
					
					if(repeat_word.getText().toString().equals(""))
					{
						toast("�ظ�Ϊ��");
						break;
					}
					
					Save_Repeat(repeat_word.getText().toString(),talk_id,Create_Talk_Mm_id);
					dialog.dismiss();
					break;
				}
				
				case R.id.repeat_dialog_button_exit:
				{
					dialog.dismiss();
					break;
				}
				
			}
		}
	}
	
	/**
	 * @author nieyihe
	 * @���ã�����ظ����� 
	 * */
	public void Save_Repeat(final String word,final String talk_id , String Talk_Create_Mm_Id)
	{

		regiment_repeat now_repeat = new regiment_repeat(me.head_path,me.head_url,Get_Now_Time(),me.name,word,talk_id,Talk_Create_Mm_Id);
		now_repeat.save(context, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("�ظ��ɹ�");
				ArrayList<Repeat> now = repeats.get(talk_id);//��ȡ��ǰ���ӵ�����;
				
				if(now != null && now.size() < 2)
				{
					now.add(new Repeat(me.name,word,Get_Now_Time(),me.head_path,me.head_url));
					Talk_adapter.notifyDataSetChanged();
					Utility.setListViewHeightBasedOnChildren(Talk_List, context);
				}
				
			}
			
		});
	}
	
	/**
	 * @author nieyihe
	 * @����:�������˵˵�б��������ĸ�����
	 * */
	public class talk_binder
	{
		public ImageView head;
		public TextView name;
		public TextView time;
		public TextView word;
		public ImageView img;
		//public Button praise;
		public ImageView praise;
		public TextView praise_num;
		//public Button evaluate;
		public ImageView evaluate;
		public ImageView more;
		public RelativeLayout all_repeat_layout;
		public LinearLayout evaluate_layout_1;
		public LinearLayout evaluate_layout_2;
		public TextView evaluate_dital_name_1;
		public TextView evaluate_dital_name_2;
		public TextView evaluate_dital_word_1;
		public TextView evaluate_dital_word_2;
		
		
	}

	/**
	 * @author nieyihe
	 * @����:��ȡ���е�˵˵�Լ��ظ������Ӧ���б�
	 * */
	public void Get_Bmob_All_Info()
	{
		
		BmobQuery<regiment_talk> query = new BmobQuery<regiment_talk>();
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.order("-createdAt");
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findObjects(context, new FindListener<regiment_talk>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<regiment_talk> arg0) {
						// TODO Auto-generated method stub
						talks.clear();
						repeats.clear();
						for(regiment_talk temp:arg0)
						{
							final String id = temp.getObjectId();
							talks.add(new Talk(temp.talk_head_path,temp.talk_head_url,temp.talk_order,temp.talk_time,temp.talk_word,temp.talk_praise_num,temp.getObjectId(),temp.member_id,temp.talk_img_path,temp.talk_img_url));
							
							BmobQuery<regiment_repeat> query_2 = new BmobQuery<regiment_repeat>();
							
							query_2.setLimit(2);
							query_2.addWhereEqualTo("repeat_talk_id", id);//ֻ��ȡ��Ӧ�Ļظ�
							query_2.findObjects(context, new FindListener<regiment_repeat>()
									{

										@Override
										public void onError(int arg0,
												String arg1) {
											// TODO Auto-generated method stub
										
										}

										@Override
										public void onSuccess(List<regiment_repeat> arg0)
										{
											
											// TODO Auto-generated method stub
											ArrayList<Repeat> result_repeat = new ArrayList<Repeat>();
											for(regiment_repeat temp_2:arg0)
											{
												result_repeat.add(new Repeat(temp_2.repeat_name,temp_2.repeat_word,temp_2.repeat_time,temp_2.repeat_head_path,temp_2.repeat_head_url));
											}
											
											repeats.put(id, result_repeat);//���浱ǰ�����лظ�
											SendMessage("ReFresh");
										}
								
									});
							
						}
						
						Last = talks.size();
						
						new Thread(){
							@Override
							public void run() 
							{
								HeadBitmaps.clear();
								ImgBitmapMap.clear();
								for(int i = 0 ;i < talks.size() ; i++)
								{
									Talk talk = talks.get(i);
									try {
										if(talk.talk_img_path != null && !talk.talk_img_path.equals(""))
											ImgBitmapMap.put(talk.Talk_Id,BitmapLocalFactory.CreateBitmap(context, talk.talk_img_path, talk.talk_img_url,BitmapLocalFactory.BIG));
										HeadBitmaps.put(talk.Talk_Id,BitmapLocalFactory.CreateBitmap(context, talk.talk_head_path, talk.talk_head_url,BitmapLocalFactory.SMALL));
										
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

	/***
	 * @author nieyihe
	 * ��ȡ���µ���̳
	 * */
	public void GainNewSeriesTalk(int Limit)
	{

		BmobQuery<regiment_talk> query = new BmobQuery<regiment_talk>();
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.order("-createdAt");
		query.setLimit(Limit > ShowNum ? Limit : ShowNum);
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findObjects(context, new FindListener<regiment_talk>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<regiment_talk> arg0) {
						// TODO Auto-generated method stub
						
						talks.clear();
						repeats.clear();
						
						for(regiment_talk temp:arg0)
						{
							final String id = temp.getObjectId();
							talks.add(new Talk(temp.talk_head_path,temp.talk_head_url,temp.talk_order,temp.talk_time,temp.talk_word,temp.talk_praise_num,temp.getObjectId(),temp.member_id,temp.talk_img_path,temp.talk_img_url));
							
							BmobQuery<regiment_repeat> query_2 = new BmobQuery<regiment_repeat>();
							query_2.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
							query_2.setLimit(2);
							query_2.addWhereEqualTo("repeat_talk_id", id);//ֻ��ȡ��Ӧ�Ļظ�
							query_2.findObjects(context, new FindListener<regiment_repeat>()
									{

										@Override
										public void onError(int arg0,
												String arg1) {
											// TODO Auto-generated method stub
										
										}

										@Override
										public void onSuccess(List<regiment_repeat> arg0)
										{
											
											// TODO Auto-generated method stub
											ArrayList<Repeat> result_repeat = new ArrayList<Repeat>();
											for(regiment_repeat temp_2:arg0)
											{
												result_repeat.add(new Repeat(temp_2.repeat_name,temp_2.repeat_word,temp_2.repeat_time,temp_2.repeat_head_path,temp_2.repeat_head_url));
											}
											
											repeats.put(id, result_repeat);//���浱ǰ�����лظ�
											SendMessage("ReFresh");
										}
								
									});
							
						}
						
						Last = talks.size();
						
						new Thread(){
							@Override
							public void run() 
							{
								HeadBitmaps.clear();
								ImgBitmapMap.clear();
								for(int i = 0 ;i < talks.size() ; i++)
								{
									Talk talk = talks.get(i);
									try {
										if(talk.talk_img_path != null && !talk.talk_img_path.equals(""))
											ImgBitmapMap.put(talk.Talk_Id,BitmapLocalFactory.CreateBitmap(context, talk.talk_img_path, talk.talk_img_url,BitmapLocalFactory.BIG));
										HeadBitmaps.put(talk.Talk_Id,BitmapLocalFactory.CreateBitmap(context, talk.talk_head_path, talk.talk_head_url,BitmapLocalFactory.SMALL));
										
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
	
	/**
	 * @author nieyihe
	 * ��ȡ��һ��ϵ����̳
	 * */
	public void GainNextSeriesTalk(final int Skip , int Limit)
	{
		BmobQuery<regiment_talk> query = new BmobQuery<regiment_talk>();
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.order("-createdAt");
		query.setLimit(Limit);
		query.setSkip(Skip);
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findObjects(context, new FindListener<regiment_talk>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<regiment_talk> arg0) {
						// TODO Auto-generated method stub
						for(regiment_talk temp:arg0)
						{
							final String id = temp.getObjectId();
							talks.add(new Talk(temp.talk_head_path,temp.talk_head_url,temp.talk_order,temp.talk_time,temp.talk_word,temp.talk_praise_num,temp.getObjectId(),temp.member_id,temp.talk_img_path,temp.talk_img_url));
							
							BmobQuery<regiment_repeat> query_2 = new BmobQuery<regiment_repeat>();
							query_2.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
							query_2.setLimit(2);
							query_2.order("-createdAt");
							query_2.addWhereEqualTo("repeat_talk_id", id);//ֻ��ȡ��Ӧ�Ļظ�
							query_2.findObjects(context, new FindListener<regiment_repeat>()
									{

										@Override
										public void onError(int arg0,
												String arg1) {
											// TODO Auto-generated method stub
										
										}

										@Override
										public void onSuccess(List<regiment_repeat> arg0)
										{
											
											// TODO Auto-generated method stub
											ArrayList<Repeat> result_repeat = new ArrayList<Repeat>();
											for(regiment_repeat temp_2:arg0)
											{
												result_repeat.add(new Repeat(temp_2.repeat_name,temp_2.repeat_word,temp_2.repeat_time,temp_2.repeat_head_path,temp_2.repeat_head_url));
											}
											
											repeats.put(id, result_repeat);//���浱ǰ�����лظ�
											SendMessage("ReFresh");
										}
								
									});
							
						}
						
						Last = talks.size();
						
						new Thread(){
							@Override
							public void run() 
							{

								for(int i = Skip ;i < talks.size() ; i++)
								{
									Talk talk = talks.get(i);
									try {
										if(talk.talk_img_path != null && !talk.talk_img_path.equals(""))
											ImgBitmapMap.put(talk.Talk_Id,BitmapLocalFactory.CreateBitmap(context, talk.talk_img_path, talk.talk_img_url,BitmapLocalFactory.BIG));
										HeadBitmaps.put(talk.Talk_Id,BitmapLocalFactory.CreateBitmap(context, talk.talk_head_path, talk.talk_head_url,BitmapLocalFactory.SMALL));
										
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
	
	/**
	 * @author nieyihe
	 * ��ȡ���˵���Ϣ
	 * */
	public void GetMember()
	{
		BmobQuery<regiment_member> query = new BmobQuery<regiment_member>();
		query.getObject(context, member_id, new GetListener<regiment_member>() {
			
			@Override
			public void onSuccess(regiment_member arg0) {
				// TODO Auto-generated method stub
				me = new member(arg0.member_head_path, arg0.member_head_url, arg0.regiment_id, arg0.getObjectId(), arg0.member_name, arg0.member_sex, arg0.member_power, arg0.member_host, arg0.member_password, arg0.member_ture_name, arg0.praise_num);
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				final OptionDialog optionDialog = new OptionDialog(context);
				optionDialog.PrepareDialog();
				optionDialog.SetMiddleShow("������Ϣ����ʧ��,������");
				optionDialog.SetFirstOnlick(new BtuClicking() {
					
					@Override
					public void OnClick() {
						// TODO Auto-generated method stub
						GetMember();
						optionDialog.DisMiss();
					}
				});
				
				optionDialog.SetSecondOnclick(new BtuClicking() {
					
					@Override
					public void OnClick() {
						// TODO Auto-generated method stub
						optionDialog.DisMiss();
					}
				});
				
				optionDialog.Show();
				
			}
		});
	}
	
	/**
	 * @author nieyihe
	 * ��ȡ�Ѿ��޵���̳
	 * */
	public void GetIsPraiseList()
	{
		
		BmobQuery<praise> query = new BmobQuery<praise>();
		query.addWhereEqualTo("mem_id", member_id);		
		query.addWhereEqualTo("type", "Talk");
		query.findObjects(context, new FindListener<praise>() {
			
			@Override
			public void onSuccess(List<praise> list) {
				// TODO Auto-generated method stub
				praiseList.clear();
				for(praise praise : list){
						praiseList.put(praise.audio_id,praise.getObjectId());
						Talk_adapter.notifyDataSetChanged();
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
	 * �ύ��
	 * */
	public void InputPraise(final String TalkId,final String CreateMemId)
	{
		final praise praise = new praise(TalkId, member_id, Get_Now_Time(), CreateMemId,"Talk");
		praise.save(context, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				regiment_talk talk = new regiment_talk();
				talk.increment("talk_praise_num",1);
				talk.update(context,TalkId,new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						praiseList.put(TalkId,praise.getObjectId());
						//�����޵���Ŀ
						UpdateTalkPraise(CreateMemId,1);
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	} 
	
	/**
	 * @author nieyihe
	 * ɾ����
	 * */
	public void DeletePraise(final String TalkId,final String mem_id)
	{
		praise praise = new praise();
		praise.delete(context,praiseList.get(TalkId),new DeleteListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
		
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				regiment_talk talk = new regiment_talk();
				talk.increment("talk_praise_num", -1);
				talk.update(context, TalkId, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						praiseList.remove(TalkId);
						UpdateTalkPraise(mem_id,-1);
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
				
			}
			
		});
		
	}
	
	/**
	 * @author nieyihe
	 * �����޵���Ŀ
	 * */
	public void UpdateTalkPraise(String mem_id,int Num)
	{
		regiment_member mem = new regiment_member();
		mem.increment("praise_num", Num);
		mem.update(context, mem_id, new UpdateListener() {
			
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
	
	/**
	 * @author nieyihe
	 * ��Ϣ������
	 * */
	public class TalkHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("ReFresh"))
			{
				Talk_adapter.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(Talk_List, context);
			}
		}
	}
	
	/**
	 * @author nieyihe
	 * ������Ϣ
	 * */
	public void SendMessage(String msgString)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString(msgString, "");
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	/**
	 * @author nieyihe
	 * ��ʾ��Ϣ
	 * */
	public void toast(String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * ��ȡlong���µ�ʱ�� ��λΪ����
	 * */
	public long Get_Now_Time()
	{
		return System.currentTimeMillis();
	}
}
