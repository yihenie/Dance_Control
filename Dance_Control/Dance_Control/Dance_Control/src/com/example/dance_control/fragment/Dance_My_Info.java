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
	private All_Info informations;//所有资讯的封装/...里面包含通知的id;
	private HashMap<String,Bitmap> infosBitmaps = new HashMap<String,Bitmap>();
	private TextView show_inform;//通知
	private EditText edit_inform;
	private ListView show_info;//资讯
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
	//最后显示的成员的编号	
	private Integer ShowNum = 10;
	//一次显示的成员的个数
	
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
	 * 获取所有的信息
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
	 * @作用：初始化界面
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
		Hint.setText("舞团资讯");
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
					Toast.makeText(context, "游客只能观看", Toast.LENGTH_SHORT).show();
				}
			});
		}
		show_inform.setOnClickListener(new Btuclick());//编辑通知
		
		Utility.setListViewHeightBasedOnChildren(show_info, context);
	}
	
	
	/**
	 * @author nieyihe
	 * @zuoyong :用于点击事件
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
					//传递regiment_id
					//在结果中 接收 resultcode为2  数据为info_id;//不接受了 ,此处为fragment
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
	 * @作用:编辑通知;
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
	 * @作用:用于保存数据到bmob云端
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
				Toast.makeText(context, "更新通知失败,请重试！", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(context, "更新通知成功", Toast.LENGTH_LONG).show();
			}
			
		});
	}
	
	/**
	 * 
	 * @author nieyihe
	 * @作用：用于dialog是否删除点击事件
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
					//确定删除  设置云端删除
					//info_id可以用来删除云端数据
					Delete_info(info_id);
					dialog.dismiss();
					
					break;
					
				}
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :当点击item访问资讯时,更新点击次数.
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
	 * @作用:删除info云端的数据
	 * */
	public void Delete_info(final String id)
	{
		regiment_info now_info = new regiment_info();
		now_info.delete(context, id, new DeleteListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
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
	 * @作用:从云端提取数据,用于舞团资讯
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
	 * 获取下一系列信息
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
	 * 获取最新的信息
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
	 * 获取通知
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
	 * @作用:ListView适配器
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
	 * @zuoyong :用户监听点击option按钮
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
			//默认的x,y为屏幕中央所以要减去屏幕的各一半,在取在整个屏幕的位置
			
			win.setAttributes(params);
			View view = LayoutInflater.from(context).inflate(R.layout.item_option, null);
			optiondialog.setContentView(view);
			Button delete = (Button) view.findViewById(R.id.item_option_delete);
			if(member_power.equals("order"))
				delete.setVisibility(View.VISIBLE);
			Button look = (Button) view.findViewById(R.id.item_option_look);
			
			delete.setOnClickListener(new Option_Onclick(bind,optiondialog));
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
	 * @作用：适配器的辅助类 
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
	 * @作用：用于删除,给delete按钮设置tag
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
	 * 发送消息
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
	 * 消息回调函数
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
