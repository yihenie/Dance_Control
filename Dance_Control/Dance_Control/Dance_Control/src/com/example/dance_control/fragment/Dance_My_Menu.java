package com.example.dance_control.fragment;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_Dital_Info;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.resource.member;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class Dance_My_Menu extends Base_Fragment{
	private ArrayList<Options> select;
	private ListView My_Menu_List;
	private Myadapter ada;
	private MyHandler handler = new MyHandler();
	private String member_id = "";
	private member me;
	private Bitmap headBitmap;
	private String member_power = "";
	private String regiment_id = "";
	private ImageView head;
	private TextView name;
	private ImageView sex;
	private ImageView power;
	private SlidingMenu menu; 
	
	private Base_Fragment info;
	private Base_Fragment movie;
	private Base_Fragment pic;
	private Base_Fragment talk;
	private Base_Fragment music;
	private Base_Fragment allmember;
	private Base_Fragment feedback;
	/*private Base_Fragment me_info;*/
	private FragmentManager fg;
	@Override
	public View onCreateView(LayoutInflater l,ViewGroup v, Bundle s)
	{
		fg = Dance_My_Menu.this.getActivity().getSupportFragmentManager();
		return l.inflate(R.layout.show_menu_1, null);
	}
	
	@Override
	public void onActivityCreated(Bundle s)
	{
		super.onActivityCreated(s);
		this.BuildMyMenuView();
	}
	
	
	public Dance_My_Menu(SlidingMenu menu,String member_id,String member_power,String regiment_id,Dance_My_Info info)
	{
		this.menu = menu;
		this.member_id = member_id;
		this.member_power = member_power;
		this.regiment_id = regiment_id;
		this.info = info;
		this.movie = new Dance_My_Movie(menu,regiment_id,member_power,member_id);
		this.pic = new Dance_My_Pic(menu,regiment_id,member_power,member_id);
		this.music = new Dance_My_Music(menu,regiment_id,member_id,member_power);
		this.allmember = new Dance_My_Rank(this.menu,regiment_id,member_id,member_power);
		this.talk = new Dance_Talk(menu,regiment_id,member_id,member_power);
		/*this.me_info = new Dance_My_Identity(member_id);*/
		this.feedback = new Dance_My_FeedBack(); 
	}
	
	/**
	 * @author nieyihe
	 * @throws IOException 
	 * @����:��ʼ��menu
	 * */
	public void BuildMyMenuView()
	{
		select = new ArrayList<Options>();
		this.ini_item();
		View parent = this.getView();
		if(!member_id.equals("")) 	
			Get_Member();
		head = (ImageView)parent.findViewById(R.id.show_menu_member_img);
		name = (TextView)parent.findViewById(R.id.show_menu_member_name);
		sex = (ImageView)parent.findViewById(R.id.show_menu_member_sex);
		power = (ImageView)parent.findViewById(R.id.show_menu_member_power);
		My_Menu_List = (ListView)parent.findViewById(R.id.show_menu_1_list);
		ada = new Myadapter(context,select);
		My_Menu_List.setAdapter(ada);
		My_Menu_List.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int pos,
					long id) {
				// TODO Auto-generated method stub
				
				SendMessage("Select", pos+"");
			}
			
		});
	}
	/**
	 * @author nieyihe
	 * @���ã����ڳ�ʼ��menuitem
	 * */
	public void ini_item()
	{
		select.add(new Options(R.drawable.zixun,"����������Ѷ"));
		select.add(new Options(R.drawable.luntan,"��̳"));
		select.add(new Options(R.drawable.shipin,"��Ƶ"));
		select.add(new Options(R.drawable.tupian,"ͼƬ"));
		select.add(new Options(R.drawable.yinyue,"����"));
		/*select.add(new Options(R.drawable.liuyan,"���ų�����"));*/
		select.add(new Options(R.drawable.geren,"������Ϣ"));
		select.add(new Options(R.drawable.chengyun,"���г�Ա"));
		select.add(new Options(R.drawable.fankui,"����"));
		select.add(new Options(R.drawable.tuichu,"�˳�"));
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :���ڻ�ȡmember
	 * */
	public void Get_Member()
	{
		BmobQuery<regiment_member> query= new BmobQuery<regiment_member>();
		query.getObject(context, member_id, new GetListener<regiment_member> ()
				{

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "��ȡ����ʧ��,����������", Toast.LENGTH_LONG).show();
						//Get_Member();
					}

					@Override
					public void onSuccess(regiment_member arg0) {
						// TODO Auto-generated method stub
						me = new member(arg0.member_head_path,arg0.member_head_url,arg0.regiment_id,arg0.getObjectId(),arg0.member_name,arg0.member_sex,arg0.member_power,arg0.member_host,arg0.member_password,arg0.member_ture_name,arg0.praise_num);

						Set_Identity();

						
					}
			
				});
	}
	/**
	 * @author nieyihe
	 * @throws IOException 
	 * @���ã��������
	 * */
	public void Set_Identity()
	{	
		if(me != null)
		{
			
			new Thread()
			{
				@Override
				public void run()
				{
					try {
						headBitmap = BitmapLocalFactory.CreateBitmap(context, me.head_path, me.head_url,BitmapLocalFactory.SMALL);
						SendMessage("ShowHeadImg","");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			if(headBitmap != null)
				head.setImageBitmap(headBitmap);
			name.setText(me.name);
			if(me.sex.equals("Ů"))
				sex.setImageResource(R.drawable.icon_pop_qz_girl_1);
			else  
				sex.setImageResource(R.drawable.icon_pop_qz_boy);
			
			if(me.member_power.equals("order"))
				power.setImageResource(R.drawable.icon_manager);
			else 
				power.setImageResource(R.drawable.icon_group);
		}
	}
	
	/**
	 * @author nieyihe
	 * @���ã�ListView��������
	 * */
	public class Myadapter extends BaseAdapter
	{
		LayoutInflater l ;
		Context context;
		ArrayList<Options> op;
		
		public Myadapter(Context context,ArrayList<Options> ops)
		{
			this.context = context ;
			this.op = ops;
			l = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.op.size();
		}

		@Override
		public Object getItem(int postion) {
			// TODO Auto-generated method stub
			return postion;
		}

		@Override
		public long getItemId(int postion) {
			// TODO Auto-generated method stub
			return postion;
		}

		@Override
		public View getView(int postion, View contentview, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Binder bind ;
			if(contentview == null)
			{
				contentview = l.inflate(R.layout.menu_option, null);
				bind = new Binder();
				bind.head = (ImageView)contentview.findViewById(R.id.icon);
				bind.text = (TextView)contentview.findViewById(R.id.option_string);
				contentview.setTag(bind);
			}
			else
			{
				bind = (Binder)contentview.getTag();
			}
			
			Options now = op.get(postion);
			
			bind.head.setImageResource(now.path);
			bind.text.setText(now.select);
			
			return contentview;
		}
		
	}
	/**
	 * @author nieyihe
	 * @���ã�ÿһ��item�ĸ�����
	 * */
	public class Binder
	{
		ImageView head;
		TextView text;
	}
	
	
	/**
	 * @author nieyihe
	 * @���ã��������ڸ���������	
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
	/**
	 * @author nieyihe
	 * @����:����һ����Ϣѭ����������Ϣ�����޸�ui����
	 * */
	public class MyHandler extends Handler
	{
				
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("ShowHeadImg"))
			{
				head.setImageBitmap(headBitmap);
			}
			if(b.containsKey("Select"))
			{
				if(b.getString("Select").equals("0"))
				{
					/*Toast.makeText(context, "Selected", Toast.LENGTH_LONG).show();*/
					//���ⴴ��fragment�ಢ��ת
					if(!info.isAdded())
					{
						fg.beginTransaction()
						.add(R.id.main_content_1, info)
						.hide(movie)
						.hide(pic)
						.hide(talk)
						.hide(feedback)
						.show(info)
						.commit();
						menu.toggle();
					}
					else
					{
						fg.beginTransaction()
						.hide(movie)
						.hide(pic)
						.hide(talk)
						.hide(feedback)
						.show(info)
						.commit();
						menu.toggle();
					}
				}
				//��Ѷ��̳��ƵͼƬ
				if(b.getString("Select").equals("1"))
				{
					if(!talk.isAdded())
					{
						fg.beginTransaction()
						.add(R.id.main_content_1,talk)
						.hide(info)
						.hide(movie)
						.hide(pic)
						.hide(music)
						.hide(feedback)
						.hide(allmember)
						.show(talk)
						.commit();
						menu.toggle();
						
					}
					else
					{
						fg.beginTransaction()
						.hide(info)
						.hide(movie)
						.hide(pic)
						.hide(music)
						.hide(allmember)
						.hide(feedback)
						.show(talk)
						.commit();
						menu.toggle();
					}
				}
				
				if(b.getString("Select").equals("2"))
				{
					if(!movie.isAdded())
					{
						fg.beginTransaction()
						.add(R.id.main_content_1,movie)
						.hide(info)
						.hide(talk)
						.hide(pic)
						.hide(music)
						.hide(allmember)
						.hide(feedback)
						.show(movie)
						.commit();
						menu.toggle();
						
					}
					else
					{
						fg.beginTransaction()
						.hide(info)
						.hide(talk)
						.hide(pic)
						.hide(music)
						.hide(allmember)
						.hide(feedback)
						.show(movie)
						.commit();
						menu.toggle();
					}
				}
				
				if(b.getString("Select").equals("3"))
				{
					if(!pic.isAdded())
					{
						fg.beginTransaction().add(R.id.main_content_1,pic)
						.hide(info)
						.hide(talk)
						.hide(movie)
						.hide(music)
						.hide(allmember)
						.hide(feedback)
						.show(pic)
						.commit();
						menu.toggle();
						
					}
					
					else
					{
						fg.beginTransaction()
						.hide(info)
						.hide(talk)
						.hide(movie)
						.hide(music)
						.hide(allmember)
						.hide(feedback)
						.show(pic)
						.commit();
						menu.toggle();
					}
				}
				
				if(b.getString("Select").equals("4"))
				{
					
					if(!music.isAdded())
					{
						fg.beginTransaction().add(R.id.main_content_1,music)
						.hide(info)
						.hide(talk)
						.hide(movie)
						.hide(pic)
						.hide(allmember)
						.hide(feedback)
						.show(music)
						.commit();
						menu.toggle();
						
					}
					
					else
					{
						fg.beginTransaction()
						.hide(info)
						.hide(talk)
						.hide(movie)
						.hide(pic)
						.hide(allmember)
						.hide(feedback)
						.show(music)
						.commit();
						menu.toggle();
					}
				}
				
				if(b.getString("Select").equals("5"))
				{
					if(!member_id.equals(""))
					{
						Intent i = new Intent();
						i.setClass(context, Ac_Dital_Info.class);
						Bundle bb = new Bundle();
						bb.putString("member_id", member_id);
						bb.putString("regiment_id", regiment_id);
						i.putExtras(bb);
						context.startActivity(i);
					}
					else
						Toast.makeText(context, "�οͲ�����������Ϣ" , Toast.LENGTH_SHORT).show();
				}
				
				if(b.getString("Select").equals("6"))
				{
					
					if(!allmember.isAdded())
					{
						fg.beginTransaction().add(R.id.main_content_1,allmember)
						.hide(info)
						.hide(talk)
						.hide(movie)
						.hide(pic)
						.hide(music)
						.hide(feedback)
						.show(allmember)
						.commit();
						menu.toggle();
						
					}
					
					else
					{
						fg.beginTransaction()
						.hide(info)
						.hide(talk)
						.hide(movie)
						.hide(pic)
						.hide(music)
						.hide(feedback)
						.show(allmember)
						.commit();
						menu.toggle();
					}
				}
				
				if(b.getString("Select").equals("7"))
				{
					if(!feedback.isAdded())
					{
						fg.beginTransaction()
						.add(R.id.main_content_1,feedback)
						.hide(info)
						.hide(talk)
						.hide(movie)
						.hide(pic)
						.hide(music)
						.hide(allmember)
						.show(feedback)
						.commit();
						menu.toggle();
						
					}
					
					else
					{
						fg.beginTransaction()
						.hide(info)
						.hide(talk)
						.hide(movie)
						.hide(pic)
						.hide(music)
						.hide(allmember)
						.show(feedback)
						.commit();
						menu.toggle();
					}
				}
				
				if(b.getString("Select").equals("8"))
				{
					FragmentActivity gActivity = (FragmentActivity)context;
					gActivity.finish();
				}
			}
		}
	}
	/**
	 * @author nieyihe
	 * ������Ϣ
	 * */
	public void SendMessage(String msgString,String msgString2)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString(msgString, msgString2);
		msg.setData(b);
		handler.sendMessage(msg);
	}
}
