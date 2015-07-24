package com.example.dance_control.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.FindListener;

import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_AddRegiment;
import com.example.dance_control.activity.Ac_Join;
import com.example.dance_control.activity.Ac_Visit;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.bmob_table.regiment_table;
import com.example.dance_control.resource.Regiment;
import com.example.dance_control.tools.BitmapLocalFactory;
import com.example.dance_control.tools.Utility;
import com.example.updownview.DownFreshLinear;
import com.example.updownview.DownFreshLinear.FreshListener;
import com.lxh.slidingmenu.lib.SlidingMenu;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("ValidFragment")
public class Dance_Regiment extends Base_Fragment{
	
	private RegimentHandler regimentHandler = new RegimentHandler();
	private ListView dance_regiment;//���������б�
	private ArrayList<Regiment> regs = new ArrayList<Regiment>();
	private HashMap<String,Bitmap> AllRegiment = new HashMap<String,Bitmap>();
	private Myadapter ada;
	private ImageButton create_regiment;
	private DownFreshLinear Linear;
	private View headView;//������ʾ��
	private ImageButton backButton;//���ذ�ť
	private TextView hintTextView;	//��ʾ�ı���
	private ImageButton ReFresh;//ˢ�°�ť
	private Animation anim;
	private Integer Last = 0;
	private Integer ShowNum = 10;
	public Dance_Regiment(SlidingMenu menu) {
		// TODO Auto-generated constructor stub
		this.menu = menu;
	}
	
	@Override
	public View onCreateView(LayoutInflater l , ViewGroup V, Bundle B)
	{
		return l.inflate(R.layout.regiment, null);
		//����������false��˼Ϊ�������view��ӵ�root��
	}
	
	@Override
	public void onActivityCreated(Bundle s)
	{
		super.onActivityCreated(s);
		this.BuildRegiment();
	}
	
	/**
	 * @author nieyihe
	 * @����:��ʼ����ҳ���
	 * */
	public void BuildRegiment()
	{
		BindView();
		SetFresh();
		GetInfoRegiment();
		
	}
	
	/**
	 * @author nieyihe
	 * �󶨿ؼ�
	 * */
	public void BindView()
	{
		View parent = this.getView();
		headView = parent.findViewById(R.id.regiment_head);
		backButton = (ImageButton) headView.findViewById(R.id.Head_Back);
		hintTextView = (TextView) headView.findViewById(R.id.Head_Word_Hint);
		ReFresh = (ImageButton) headView.findViewById(R.id.Head_ReFresh);
		hintTextView.setText("С������Ŭ����������");
		create_regiment = (ImageButton)parent.findViewById(R.id.regiment_create_regiment);
		Linear = (DownFreshLinear) parent.findViewById(R.id.regiment_layout_fresh);
		ReFresh.setVisibility(View.VISIBLE);
		anim = AnimationUtils.loadAnimation(context, R.anim.btu_refresh_anim);  
		ReFresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				ReFresh.startAnimation(anim);
				GetInfoRegiment();
			}
		});
		create_regiment.setOnClickListener(new BtuClick());
		backButton.setOnClickListener(new BtuClick());
		ada = new Myadapter(context);
		dance_regiment = (ListView)parent.findViewById(R.id.regiment_inner_list);
		dance_regiment.setAdapter(ada);
		dance_regiment.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int pos,
					long id) {
				// TODO Auto-generated method stub
				Dialog dialog = onCreateDialog(pos);
				
				dialog.show();
				
			}
			
		});
		Utility.setListViewHeightBasedOnChildren(dance_regiment, context);
	}
	
	
	
	/**
	 * @author nieyihe
	 * ��������ˢ�µĺ���
	 * */
	public void SetFresh()
	{
		Linear.setOnFresh(0, new FreshListener(){

			@Override
			public boolean onFreshTop() {
				// TODO Auto-generated method stub
				GainNewSeriesRegiment(Last);
				return true;
			}

			@Override
			public boolean onFreshButton() {
				// TODO Auto-generated method stub
				GainNextSeriesRegiment(Last,ShowNum);
				return true;
			}

			});
	}
	
	/**
	 * @author nieyihe
	 * @���� ����ť����¼�
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
				case R.id.regiment_create_regiment:
				{
					Intent start = new Intent();
					start.setClass(context, Ac_AddRegiment.class);
					context.startActivity(start);
					break;
				}
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @���� ������dialog��ʾ��ǰ���ŵ���ϸ��Ϣ
	 * */
	public Dialog onCreateDialog(int pos)
	{
		//Builder builder = new AlertDialog.Builder(context,R.style.Item_Dialog);
		Dialog dialog = new Dialog(context,R.style.Item_Dialog);
		RelativeLayout regiment_layout = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.regiment_dital, null);
		ImageView head_img = (ImageView)regiment_layout.findViewById(R.id.regiment_dital_img);
		ImageButton exit_btu = (ImageButton)regiment_layout.findViewById(R.id.regiment_dital_exit_btu);
		TextView regiment_name = (TextView)regiment_layout.findViewById(R.id.regiment_dital_text_1);
		TextView regiment_order = (TextView)regiment_layout.findViewById(R.id.regiment_dital_text_2);
		TextView regiment_secre = (TextView)regiment_layout.findViewById(R.id.regiment_dital_text_3);
		TextView regiment_sec_order = (TextView) regiment_layout.findViewById(R.id.regiment_dital_text_4);
		TextView regiment_addr = (TextView)regiment_layout.findViewById(R.id.regiment_dital_text_5);
		TextView regiment_num = (TextView)regiment_layout.findViewById(R.id.regiment_dital_text_6);
		TextView regiment_intro = (TextView)regiment_layout.findViewById(R.id.regiment_dital_text_intro);
		Button regiment_join = (Button)regiment_layout.findViewById(R.id.regiment_dital_join);
		Button regiment_visit = (Button) regiment_layout.findViewById(R.id.regiment_dital_visit);
		Regiment r = regs.get(pos);
		
		Bitmap bitmap = null;
			bitmap = AllRegiment.get(r.regiment_id);
		if(bitmap != null)
			head_img.setImageBitmap(bitmap);
		regiment_name.setText(r.regiment_name);
		regiment_order.setText(r.regiment_order);
		regiment_addr.setText(r.regiment_addr);
		regiment_secre.setText(r.regiment_secretary);
		regiment_num.setText(r.regiment_number+"");
		regiment_sec_order.setText(r.regiment_sec_order);
		regiment_intro.setText(r.regiment_intro);
		
		regiment_join.setTag(r.regiment_id);
		regiment_visit.setTag(r.regiment_id);
		
		exit_btu.setOnClickListener(new Regiment_Onclick(dialog));
		regiment_join.setOnClickListener(new Regiment_Onclick(null));
		regiment_visit.setOnClickListener(new Regiment_Onclick(null));
		
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(regiment_layout);
		return dialog;
	}
	
	/**
	 * @author nieyihe
	 * @zuoyong :����¼��Ĵ����¼���
	 * */
	public class Regiment_Onclick implements View.OnClickListener
	{
		Dialog dialog;
		
		public Regiment_Onclick(Dialog dialog)
		{
			this.dialog = dialog;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.regiment_dital_join:
				{
					String id = (String)v.getTag();
					Intent i = new Intent();
					i.setClass(context, Ac_Join.class);
					Bundle b = new Bundle();
					b.putString("regiment_id", id);
					i.putExtras(b);
					context.startActivity(i);
					//����
					//��Ҫ����һ��regiment_id;
					  
					break;
				}
				case R.id.regiment_dital_visit:
				{
					String id = (String)v.getTag();
					//�Թ۲���ģʽ//����Ա�鿴
					Intent i = new Intent();
					i.setClass(context, Ac_Visit.class);
					Bundle b = new Bundle();
					b.putString("regiment_id", id);
					i.putExtras(b);
					context.startActivity(i);
					//��Ҫ����һ��regiment_id;
					break;
				}
				
				case R.id.regiment_dital_exit_btu:
				{
					dialog.dismiss();
					break;
				}
			}
		}
		
	}
	/**
	 * @author nieyihe
	 * @����:���ƶ���ȡ����
	 * */
	public void GetInfoRegiment()
	{
		
		BmobQuery<regiment_table> query = new BmobQuery<regiment_table>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.order("-createdAt");
		query.setLimit(Last > ShowNum ? Last : ShowNum);
		query.findObjects(context, new FindListener<regiment_table>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						hintTextView.setText("С��û��������");
					}

					@Override
					public void onSuccess(List<regiment_table> arg0) {
						// TODO Auto-generated method stub//
						regs.clear();
						hintTextView.setText("�����б�");
						for(regiment_table reg:arg0)
						{
							regs.add(new Regiment(reg.getObjectId(),reg.regiment_head_img_path,reg.regiment_head_img_url,reg.regiment_name,reg.regiment_num,reg.regiment_order,reg.regiment_addr,reg.regiment_secretary,reg.regiment_sec_order,reg.regiment_intro,reg.regiment_informs));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(dance_regiment, context);
						}
							
						Last = regs.size();
						
						new Thread()
						{
							@Override
							public void run() 
							{
								AllRegiment.clear();
								for(int i = 0;i < regs.size() ; i++)
								{
									Regiment regiment = regs.get(i);
									try {
										Bitmap bitmap = BitmapLocalFactory.CreateBitmap(context, regiment.regiment_img_path, regiment.regiment_img_url,BitmapLocalFactory.NORMAL);
										if(bitmap!= null)
										{
											AllRegiment.put(regiment.regiment_id,bitmap);
										}
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
	 * ��ȡ���µ�������Ϣ
	 * */
	public void GainNewSeriesRegiment(int Limit)
	{
		
		BmobQuery<regiment_table> query = new BmobQuery<regiment_table>();
		query.setLimit(Limit > ShowNum ? Limit : ShowNum);
		query.order("-createdAt");
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findObjects(context, new FindListener<regiment_table>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						SendMessage("Error");
					}

					@Override
					public void onSuccess(List<regiment_table> arg0) {
						// TODO Auto-generated method stub//
						regs.clear();
						AllRegiment.clear();
						
						SendMessage("Success");
						for(regiment_table reg:arg0)
						{
							regs.add(new Regiment(reg.getObjectId(),reg.regiment_head_img_path,reg.regiment_head_img_url,reg.regiment_name,reg.regiment_num,reg.regiment_order,reg.regiment_addr,reg.regiment_secretary,reg.regiment_sec_order,reg.regiment_intro,reg.regiment_informs));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(dance_regiment, context);
						}
							
						Last = regs.size();
						
						new Thread()
						{
							@Override
							public void run() 
							{
								
								for(int i = 0;i < regs.size() ; i++)
								{
									Regiment regiment = regs.get(i);
									try {
										Bitmap bitmap = BitmapLocalFactory.CreateBitmap(context, regiment.regiment_img_path, regiment.regiment_img_url,BitmapLocalFactory.NORMAL);
										if(bitmap!= null)
										{
											AllRegiment.put(regiment.regiment_id,bitmap);
										}
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
	 * ��ȡ��һ��ϵ�е�������Ϣ
	 * */
	public void GainNextSeriesRegiment(final int Skip,int Limit)
	{
		BmobQuery<regiment_table> query = new BmobQuery<regiment_table>();
		//query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.setLimit(Limit);
		query.setSkip(Skip);
		query.order("-createdAt");
		query.findObjects(context, new FindListener<regiment_table>()
				{
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						 SendMessage("Error");
						 
					}

					@Override
					public void onSuccess(List<regiment_table> arg0) {
						// TODO Auto-generated method stub//
						
							SendMessage("Success");
						for(regiment_table reg:arg0)
						{
							regs.add(new Regiment(reg.getObjectId(),reg.regiment_head_img_path,reg.regiment_head_img_url,reg.regiment_name,reg.regiment_num,reg.regiment_order,reg.regiment_addr,reg.regiment_secretary,reg.regiment_sec_order,reg.regiment_intro,reg.regiment_informs));
							ada.notifyDataSetChanged();
							Utility.setListViewHeightBasedOnChildren(dance_regiment, context);
						}
							
						Last = regs.size();
						
						new Thread()
						{
							@Override
							public void run() 
							{
								
								for(int i = Skip;i < regs.size() ; i++)
								{
									Regiment regiment = regs.get(i);
									try {
										Bitmap bitmap = BitmapLocalFactory.CreateBitmap(context, regiment.regiment_img_path, regiment.regiment_img_url,BitmapLocalFactory.NORMAL);
										if(bitmap!= null)
										{
											AllRegiment.put(regiment.regiment_id,bitmap);
										}
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
	 * @���ã��б��������
	 * */
	public class Myadapter extends BaseAdapter
	{
		private Context context ;
		private LayoutInflater i;
		public Myadapter(Context context)
		{
			this.context = context ;
			i = LayoutInflater.from(this.context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return regs.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Binder bind ;
			if(arg1 == null)
			{
				arg1 = i.inflate(R.layout.regiment_list, null);
				bind = new Binder();
				bind.regiment_img = (ImageView)arg1.findViewById(R.id.regiment_img);
				bind.regiment_name = (TextView)arg1.findViewById(R.id.regiment_text_right_1);
				bind.regiment_number = (TextView)arg1.findViewById(R.id.regiment_text_right_3);
				bind.regiment_order = (TextView)arg1.findViewById(R.id.regiment_text_right_2);
				bind.regiment_addr = (TextView)arg1.findViewById(R.id.regiment_text_right_4);
				
				arg1.setTag(bind);
			}
			else
			{
				bind = (Binder)arg1.getTag();
			}
			
			Regiment r = regs.get(arg0);
			Bitmap bitmap = null;
				bitmap = AllRegiment.get(r.regiment_id);
			
			if(bitmap != null)
				bind.regiment_img.setImageBitmap(bitmap);
			bind.regiment_addr.setText(r.regiment_addr);
			bind.regiment_name.setText(r.regiment_name);
			bind.regiment_number.setText(r.regiment_number+"");
			bind.regiment_order.setText(r.regiment_order);
			
			
			return arg1;
		}
		
	}
	/**
	 * @author nieyihe
	 * @����:��һ��������,����baseadapter;
	 * */
	public class Binder
	{
		public ImageView regiment_img;
		public TextView regiment_number;
		public TextView regiment_addr;
		public TextView regiment_name;
		public TextView regiment_order;
	}
	
	/**
	 * @author nieyihe
	 * ������Ϣ
	 * */
	public class RegimentHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("ReFresh"))
			{
				ada.notifyDataSetChanged();
				Utility.setListViewHeightBasedOnChildren(dance_regiment, context);
			}
			if(b.containsKey("Success"))
			{
				hintTextView.setText("�����б�");
			}
			
			if(b.containsKey("Error"))
			{
				hintTextView.setText("С��û����������");
			}
		}
	}
	
	/**
	 * @author nieyihe
	 * ������Ϣ
	 * */
	public synchronized void SendMessage(String msgstring)
	{
			Message msg = new Message();
			Bundle b = new Bundle();
			b.putString(msgstring, "");
			msg.setData(b);
			regimentHandler.sendMessage(msg);
	}
}
