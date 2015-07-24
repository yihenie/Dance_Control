package com.example.dance_control.fragment;

import java.io.IOException;
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
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_AddInfo;
import com.example.dance_control.activity.Ac_ShowInfo;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.bmob_table.regiment_info;
import com.example.dance_control.bmob_table.regiment_table;
import com.example.dance_control.resource.All_Info;
import com.example.dance_control.resource.Info;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.example.dance_control.tools.Utility;
import com.example.updownview.DownFreshLinear;
import com.example.updownview.DownFreshLinear.FreshListener;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class Dance_My_Info extends Base_Fragment {

	private InfoHandler Infohandler = new InfoHandler();
	private All_Info informations;//������Ѷ�ķ�װ/...�������֪ͨ��id;
	private HashMap<String,Bitmap> infosBitmaps = new HashMap<String,Bitmap>();
	private TextView show_inform;//֪ͨ
	private EditText edit_inform;
	private ListView show_info;//��Ѷ
	private ImageButton add_info;
	private Myadapter ada;
	private String regiment_id;
	private ImageButton Back;
	private TextView Hint;
	private ImageButton ReFresh;
	private String member_power;
	private DownFreshLinear Linear;
	private View headView ;
	private Animation anim;
	private Integer Last = 0 ;
	//�����ʾ�ĳ�Ա�ı��	
	private Integer ShowNum = 10;
	//һ����ʾ�ĳ�Ա�ĸ���
	
	/*private final static int Add_Info = 1;
	private final static int Get_Info = 2;*/
	public Dance_My_Info(SlidingMenu menu,String regiment_id,String member_power)
	{
		this.menu = menu;
		this.regiment_id = regiment_id;
		this.member_power = member_power;
	}
	
	@Override
	public void onActivityCreated(Bundle b)
	{
		super.onActivityCreated(b);
		BuildMyInfo();
		GainAllInfo();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater l , ViewGroup v, Bundle b)
	{
		return l.inflate(R.layout.my_info, null);
	}

	/***
	 * @author nieyihe
	 * ��ȡ���е���Ϣ
	 * */
	public void GainAllInfo()
	{
		if(!regiment_id.equals(""))
		{
			GetInforms();
			GetBmobInfo();
		}
	}
	/**
	 * @author nieyihe
	 * @���ã���ʼ������
	 * */
	public void BuildMyInfo()
	{
		informations = new All_Info();
		View parent = this.getView();
		headView = parent.findViewById(R.id.my_info_head);
		Back = (ImageButton) headView.findViewById(R.id.Head_Back);
		Hint = (TextView) headView.findViewById(R.id.Head_Word_Hint);
		ReFresh = (ImageButton) headView.findViewById(R.id.Head_ReFresh);
		Back.setOnClickListener(new Btuclick());
		Hint.setText("������Ѷ");
		ReFresh.setVisibility(View.VISIBLE);
		anim = AnimationUtils.loadAnimation(context, R.anim.btu_refresh_anim);  
		ReFresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				ReFresh.startAnimation(anim);
				GainAllInfo();
			}
		});
		ada = new Myadapter(context);
		show_inform = (TextView)parent.findViewById(R.id.my_info_inform);
		show_inform.setText(informations.Inform);
		show_info = (ListView)parent.findViewById(R.id.my_info_list);
		Linear = (DownFreshLinear) parent.findViewById(R.id.my_info_DownFresh);
		Linear.setOnFresh(1, new FreshListener(){

			@Override
			public boolean onFreshTop() {
				// TODO Auto-generated method stub
				GainNewSeriesInfo(Last);
				return true;
			}

			@Override
			public boolean onFreshButton() {
				// TODO Auto-generated method stub
				GainNextSeriesInfo(Last,ShowNum);
				return true;
			}

			});
		
		show_info.setAdapter(ada);
		show_info.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				Info info = informations.infos.get(pos);
				
				Update_Hit_Num(info.id);
				
				Intent intent = new Intent();
				intent.setClass(context, Ac_ShowInfo.class);
				Bundle b = new Bundle();
				b.putString("info_id",info.id);
				intent.putExtras(b);
				context.startActivity(intent);
				
			}
			
		});
		add_info = (ImageButton) parent.findViewById(R.id.my_infor_insert);
		if(member_power.equals("order") || member_power.equals("member"))
			add_info.setOnClickListener(new Btuclick());
		else{
			add_info.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "�ο�ֻ�ܹۿ�", Toast.LENGTH_SHORT).show();
				}
			});
		}
		show_inform.setOnClickListener(new Btuclick());//�༭֪ͨ
		
		Utility.setListViewHeightBasedOnChildren(show_info, context);
	}
	
	
	/**
	 * @author nieyihe
	 * @zuoyong :���ڵ���¼�
	 * */
	public class Btuclick implements View.OnClickListener
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
				
				case R.id.my_info_inform:
				{
					if(member_power.equals("order"))
					{
			
						Dialog dialog = new Dialog(context,R.style.Item_Dialog);
						RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.edit_inform_dialog, null);
						Button sure = (Button) view.findViewById(R.id.edit_inform_dialog_sure_button);
						Button cancel = (Button) view.findViewById(R.id.edit_inform_dialog_cancel_button);
						edit_inform  = (EditText) view.findViewById(R.id.edit_inform_dialog_edit);
						sure.setOnClickListener(new Dialog_Onclick_Edit(dialog));
						cancel.setOnClickListener(new Dialog_Onclick_Edit(dialog));
						dialog.setContentView(view);
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();
					}
					break;
				}
				
				case R.id.my_infor_insert:
				{
					//����regiment_id
					//�ڽ���� ���� resultcodeΪ2  ����Ϊinfo_id;//�������� ,�˴�Ϊfragment
					Intent setinfo = new Intent();
					setinfo.setClass(context, Ac_AddInfo.class);
					Bundle b = new Bundle();
					b.putString("regiment_id",regiment_id);
					setinfo.putExtras(b);
					context.startActivity(setinfo);
					break;
				}
			}
		}
		
	}
	/**
	 * @author nieyihe
	 * @����:�༭֪ͨ;
	 * */
	public class Dialog_Onclick_Edit implements View.OnClickListener
	{
		Dialog dialog;
		
		public Dialog_Onclick_Edit(Dialog dialog)
		{
			this.dialog = dialog;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				 case R.id.edit_inform_dialog_sure_button:
				 {
					 //Dance_My_Info.this.informations.inform_id;
					 Save_Inform();
					 show_inform.setText(edit_inform.getText().toString());
					 dialog.dismiss();
					 break;
				 }
				 
				 case R.id.edit_inform_dialog_cancel_button:
				 {
					 dialog.dismiss();
					 break;
				 }
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @����:���ڱ������ݵ�bmob�ƶ�
	 * 
	 * */
	public void Save_Inform()
	{
		regiment_table regiment = new regiment_table(); 
		regiment.regiment_informs = edit_inform.getText().toString();
		regiment.update(context, regiment_id, new UpdateListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "����֪ͨʧ��,�����ԣ�", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(context, "����֪ͨ�ɹ�", Toast.LENGTH_LONG).show();
			}
			
		});
	}
	
	/**
	 * 
	 * @author nieyihe
	 * @���ã�����dialog�Ƿ�ɾ������¼�
	 * */
	public class Dialog_Onclick implements View.OnClickListener
	{
		public Dialog dialog;
		public String info_id;
		public Dialog_Onclick(Dialog dialog , String id)
		{
			this.dialog = dialog;
			this.info_id = id;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.delet_dialog_cancel:
				{
					dialog.dismiss();
					break;
					
				}
				
				case R.id.delet_dialog_true:
				{
					//ȷ��ɾ��  �����ƶ�ɾ��
					//info_id��������ɾ���ƶ�����
					Delete_info(info_id);
					dialog.dismiss();
					
					break;
					
				}
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :�����item������Ѷʱ,���µ������.
	 * */
	public void Update_Hit_Num(String id)
	{
		regiment_info now_info = new regiment_info();
		now_info.increment("hit_num");
		now_info.update(context, id, new UpdateListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/**
	 * @author nieyihe
	 * @����:ɾ��info�ƶ˵�����
	 * */
	public void Delete_info(final String id)
	{
		regiment_info now_info = new regiment_info();
		now_info.delete(context, id, new DeleteListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "ɾ��ʧ��", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(context, "ɾ���ɹ�", Toast.LENGTH_LONG).show();
				Iterator<Info> ref = informations.infos.iterator();
				while(ref.hasNext())
				{
					Info info = ref.next();
					if(info.id.equals(id))
					{
						ref.remove();
						SendMessage("ReFresh");
						break;
					}
					
				}
			}
			
		});
	}
	
	
	/**
	 * @author nieyihe
	 * @����:���ƶ���ȡ����,����������Ѷ
	 * */
	public void GetBmobInfo()
	{
		
		BmobQuery<regiment_info> query = new BmobQuery<regiment_info>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.setLimit(Last < ShowNum ? ShowNum : Last);
		query.order("-createdAt");
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.findObjects(context , new FindListener<regiment_info>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<regiment_info> arg0) {
						// TODO Auto-generated method stub
						informations.infos.clear();
						for(regiment_info result:arg0)
						{
							informations.infos.add(new Info(result.img_path,result.img_url,result.word,result.getObjectId(),result.brief,result.title,result.hit_num,result.time));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(show_info, context);
						}
						
						Last = informations.infos.size();
						
						new Thread()
						{
							@Override
							public void run()
							{
								infosBitmaps.clear();
								for(int i = 0;i < informations.infos.size();i++)
								{
									Info info = informations.infos.get(i);
									try {
										if(!info.img_path.equals("") && !info.img_url.equals(""))
											infosBitmaps.put(info.id,BitmapLocalFactory.CreateBitmap(context, info.img_path
												, info.img_url, BitmapLocalFactory.NORMAL));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
										SendMessage("ReFresh");
								}
							}
						}.start();
					}
			
				});
 
		}
	
	/**
	 * @author nieyihe
	 * ��ȡ��һϵ����Ϣ
	 * */
	public void GainNextSeriesInfo(final int Skip, int Limit)
	{
		BmobQuery<regiment_info> query = new BmobQuery<regiment_info>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.setSkip(Skip);
		query.setLimit(Limit);
		query.order("-createdAt");
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.findObjects(context , new FindListener<regiment_info>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<regiment_info> arg0) {
						// TODO Auto-generated method stub
						for(regiment_info result:arg0)
						{
							informations.infos.add(new Info(result.img_path,result.img_url,result.word,result.getObjectId(),result.brief,result.title,result.hit_num,result.time));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(show_info, context);
						}
						
						Last = informations.infos.size();
						
						new Thread()
						{
							@Override
							public void run()
							{
								
								for(int i = Skip ;i < informations.infos.size();i++)
								{
									Info info = informations.infos.get(i);
									try {
										if(!info.img_path.equals(""))
											infosBitmaps.put(info.id,BitmapLocalFactory.CreateBitmap(context, info.img_path
												, info.img_url, BitmapLocalFactory.NORMAL));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
										SendMessage("ReFresh");
								}
							}
						}.start();
					}
			
				});
 
	}
	
	/**
	 * @author nieyihe
	 * ��ȡ���µ���Ϣ
	 * */
	public void GainNewSeriesInfo(int Limit)
	{
		BmobQuery<regiment_info> query = new BmobQuery<regiment_info>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.setLimit(Limit > ShowNum ? Limit : ShowNum);
		query.order("-createdAt");
		query.addWhereEqualTo("regiment_id", regiment_id);
		query.findObjects(context , new FindListener<regiment_info>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<regiment_info> arg0) {
						// TODO Auto-generated method stub
						informations.infos.clear();
						infosBitmaps.clear();
						for(regiment_info result:arg0)
						{
							informations.infos.add(new Info(result.img_path,result.img_url,result.word,result.getObjectId(),result.brief,result.title,result.hit_num,result.time));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(show_info, context);
						}
						
						Last = informations.infos.size();
						
						new Thread()
						{
							@Override
							public void run()
							{
								
								for(int i = 0 ;i < informations.infos.size();i++)
								{
									Info info = informations.infos.get(i);
									try {
										if(!info.img_path.equals(""))
											infosBitmaps.put(info.id,BitmapLocalFactory.CreateBitmap(context, info.img_path
												, info.img_url, BitmapLocalFactory.NORMAL));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
										SendMessage("ReFresh");
								}
							}
						}.start();
					}
			
				});
 
	}
	
	/**
	 * @author nieyihe
	 * ��ȡ֪ͨ
	 * */
	public void GetInforms()
	{
		BmobQuery<regiment_table> query = new BmobQuery<regiment_table>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.getObject(context, regiment_id, new GetListener<regiment_table>() {
			
			@Override
			public void onSuccess(regiment_table now) {
				// TODO Auto-generated method stub
				show_inform.setText(now.regiment_informs);
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	/**
	 * @author nieyihe
	 * @����:ListView������
	 * */
	public class Myadapter extends BaseAdapter
	{
		private Context context;
		private LayoutInflater l;
	
		public Myadapter(Context context)
		{
			this.context = context ;
			this.l = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return informations.infos.size();
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
			Binder bind;
			if(v == null)
			{
				v = l.inflate(R.layout.my_info_list, null);
				bind = new Binder();
				bind.left_img = (ImageView)v.findViewById(R.id.my_info_list_img);
				bind.right_title = (TextView)v.findViewById(R.id.my_info_list_title);
				bind.right_hit_num = (TextView) v.findViewById(R.id.my_info_hit_num);
				bind.right_brief = (TextView) v.findViewById(R.id.my_info_brief);
				bind.right_time = (TextView) v.findViewById(R.id.my_info_time);
				bind.option = (ImageView)v.findViewById(R.id.my_info_list_option);
				v.setTag(bind);
			}
			else
			{
				bind = (Binder)v.getTag();
			}
			
			Info now_info = informations.infos.get(pos);
			Bitmap bitmap = null;
			if(!now_info.img_path.equals("") && !now_info.img_url.equals(""))
				bind.left_img.setVisibility(View.VISIBLE);
			else {
				bind.left_img.setVisibility(View.GONE);
			}	
				bitmap = infosBitmaps.get(now_info.id);
			if(bitmap != null)
				bind.left_img.setImageBitmap(bitmap);
			
			bind.right_title.setText(now_info.title);
			bind.right_hit_num.setText(now_info.hit_num + "");
			bind.right_brief.setText(now_info.brief);
			bind.right_time.setText(now_info.time);
			
			if(member_power.equals("order"))
			{
				bind.option.setVisibility(View.VISIBLE);
				bind.option.setOnClickListener(new Option_Dialog_Onclick());
				bind.option.setTag(new Delete_Bind(pos,now_info.id));
			}
			return v;
		}
		
		
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :�û��������option��ť
	 * */
	public class Option_Dialog_Onclick implements View.OnClickListener
	{
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Delete_Bind bind = (Delete_Bind)arg0.getTag();
			WindowManager wm = Dance_My_Info.this.getActivity().getWindowManager();
			int width = wm.getDefaultDisplay().getWidth();
		    int height = wm.getDefaultDisplay().getHeight();
			int locate[] = new int[2];
			arg0.getLocationOnScreen(locate);
			Dialog optiondialog = new Dialog(context,R.style.Item_Dialog);
			Window win = optiondialog.getWindow();
			LayoutParams params = new LayoutParams();
			params.x = locate[0] - width/2 + arg0.getWidth()/2;
			params.y = locate[1] - height/2 + arg0.getHeight();
			//Ĭ�ϵ�x,yΪ��Ļ��������Ҫ��ȥ��Ļ�ĸ�һ��,��ȡ��������Ļ��λ��
			
			win.setAttributes(params);
			View view = LayoutInflater.from(context).inflate(R.layout.item_option, null);
			optiondialog.setContentView(view);
			Button delete = (Button) view.findViewById(R.id.item_option_delete);
			if(member_power.equals("order"))
				delete.setVisibility(View.VISIBLE);
			Button look = (Button) view.findViewById(R.id.item_option_look);
			
			delete.setOnClickListener(new Option_Onclick(bind,optiondialog));
			look.setOnClickListener(new Option_Onclick(bind,optiondialog));
			
			optiondialog.setCanceledOnTouchOutside(true);//���õ��Dialog�ⲿ��������ر�Dialog
			optiondialog.show();
			
		}
		
	} 
	
	
	
	/**
	 * @author nieyihe
	 * @zuoyong :�û��������option�ڲ���ѡ��
	 * */
	public class Option_Onclick implements View.OnClickListener
	{
		private Delete_Bind bind;
		private Dialog dialog;
		public Option_Onclick(Delete_Bind bind,Dialog dialog)
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
					
					Dialog dialogg = new Dialog(context,R.style.Item_Dialog);
					RelativeLayout view = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.delet_dialog, null);
					
					Button dialog_true = (Button) view.findViewById(R.id.delet_dialog_true);
						dialog_true.setOnClickListener(new Dialog_Onclick(dialogg,bind.id));
					Button dialog_cancel = (Button) view.findViewById(R.id.delet_dialog_cancel);
						dialog_cancel.setOnClickListener(new Dialog_Onclick(dialogg,bind.id));
					
					dialogg.setContentView(view);
					dialogg.show();
					dialogg.setCanceledOnTouchOutside(false);
							
					break;
				}
				
				case R.id.item_option_look:
				{
					Info info = informations.infos.get(bind.pos);
					
					Update_Hit_Num(info.id);
					
					Intent intent = new Intent();
					intent.setClass(context, Ac_ShowInfo.class);
					Bundle b = new Bundle();
					b.putString("info_id",info.id);
					intent.putExtras(b);
					context.startActivity(intent);
					dialog.dismiss();
					break;
				}
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @���ã��������ĸ����� 
	 * */
	public class Binder
	{
		public ImageView left_img;
		public TextView right_title ;
		public TextView right_brief;
		public TextView right_hit_num;
		public TextView right_time;
		public ImageView option;
	}
	
	/**
	 * @author nieyihe
	 * @���ã�����ɾ��,��delete��ť����tag
	 * */
	public class Delete_Bind
	{
		public int pos;
		public String id;
		
		public Delete_Bind(int pos,String id)
		{
			this.pos = pos;
			this.id = id;
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
		Infohandler.sendMessage(msg);
	}
	
	/**
	 * @author nieyihe
	 * ��Ϣ�ص�����
	 * */
	private class InfoHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("ReFresh"))
			{
				ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(show_info, context);
			}
		}
	}
}
