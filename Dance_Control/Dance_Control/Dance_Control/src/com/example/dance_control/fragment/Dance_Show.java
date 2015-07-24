package com.example.dance_control.fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.htmlparser.util.ParserException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_Show_Shuffle;
import com.example.dance_control.basefragment.Base_Fragment;
import com.example.dance_control.tools.Html_Par;
import com.example.dance_control.tools.Utility;
import com.lxh.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class Dance_Show extends Base_Fragment{
	
	private ListView MovieList;
	private ArrayList<String> Hrefs ;
	private ArrayList<String> Names ;
	private Html_Par pra;
	private ListAda ada;
	private Handle handle = new Handle();
	private ImageButton Back;
	private TextView Hint;
	
	public Dance_Show(SlidingMenu menu) {
		// TODO Auto-generated constructor stub
		this.menu = menu;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.content_show, null);
	}

	
	//当activity创建的时候 fragment调用这个函数
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.initViews();
		
	}
	
	/**
	 * @author nieyihe
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws ParserException 
	 * @作用:用于创建fragment
	 * */
	private void initViews()
	{
		View parent = this.getView();
		MovieList = (ListView) parent.findViewById(R.id.content_show_list);
		View head = parent.findViewById(R.id.content_show_head);
		Back = (ImageButton) head.findViewById(R.id.Head_Back);
		Hint = (TextView) head.findViewById(R.id.Head_Word_Hint);
		Hint.setText("小控努力加油中-。-");
		SetOnclick();
		pra = new Html_Par();
		new MyThread().start();
		
		MovieList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(context,  Ac_Show_Shuffle.class);
				Bundle b = new Bundle();
				Log.w("Href", Hrefs.get(pos));
				b.putString("url", Hrefs.get(pos));
				i.putExtras(b);
				context.startActivity(i);
			}
			
		});
		
	}
	
	/**
	 * @author nieyihe
	 * 监听器
	 * */
	public void SetOnclick()
	{
		Back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				menu.toggle();
			}
		});
	}
	
	/**
	 * @author nieyihe
	 * 消息相应函数
	 * */
	public class Handle extends Handler
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("Over"))
			{
				Hrefs = pra.Hrefs;
				Names = pra.Names;
				Hint.setText("曳舞学习");
				
				ada = new ListAda(context);
				MovieList.setAdapter(ada);
				Utility.setListViewHeightBasedOnChildren(MovieList, context);
			}
		}
		
	}
	
	private class ListAda extends BaseAdapter
	{
		private Context context ;
		private LayoutInflater l;
		
		public ListAda(Context context)
		{
			this.context = context;
			l = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Names.size();
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
			Ada_Bindler bind;
			if(contentview == null)
			{
				contentview = l.inflate(R.layout.content_show_shuffle_dital, null);
				bind = new Ada_Bindler();
				bind.intro = (TextView) contentview.findViewById(R.id.content_show_shuffle_dital_intro);
				
				contentview.setTag(bind);
			}
			else
			{
				bind = (Ada_Bindler)contentview.getTag();
			}
			
			if(Names != null)
				bind.intro.setText(Names.get(pos));
			
			return contentview;
		}
		
	}
	
	
	private class Ada_Bindler
	{
		public TextView intro;
	}
	
	
	public class MyThread extends Thread
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Message msg = new Message();
			Bundle b = new Bundle();
			try {
				pra.Praser_Html();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			finally
			{
				b.putString("Over", "");
				msg.setData(b);
				handle.sendMessage(msg);
			}
			
		}
		
	}
}
