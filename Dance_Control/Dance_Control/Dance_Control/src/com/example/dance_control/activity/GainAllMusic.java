package com.example.dance_control.activity;

import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dance_control.R;
import com.example.dance_control.backworker.UpdateMusicService;

public class GainAllMusic extends Activity {

	private Vector<String> NameArrayList = new Vector<String>();
	//保存所有的名字
	private HashMap<String,String> IdPathMap = new HashMap<String, String>(); 
	//所有的音乐的id对于路径的映射
	private Vector<String> idsArrayList = new Vector<String>();
	//所有的id
	private Vector<String> inputItemArrayListPath = new Vector<String>();
	//提交的项
	private Vector<String> inputItemArrayListName = new Vector<String>();
	private ListView MusicListView ;
	//列表
	private Button Submit ; 
	//提交按钮
	private Integer SubmitNumber = 0;
	//提交的个数
	private MusicHandler musicHandler = new MusicHandler();
	
	private MusicAdapter musicAdapter ;
	
	private String mem_id = "";
	//成员id
	private String regiment_id = "";
	//舞团id
	private Notification noti;
	
	private int MusicSizes = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.gainallmusic);
		GetBundle();
		BuildGainAllMusicView();
	}

	/**
	 * @author nieyihe
	 * 设置界面
	 * */
	public void BuildGainAllMusicView()
	{
		MusicListView = (ListView) this.findViewById(R.id.gainallmusic_list);
		musicAdapter = new MusicAdapter();
		MusicListView.setAdapter(musicAdapter);
		MusicListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				String name = NameArrayList.get(position);
				String Id = idsArrayList.get(position);
				String path = IdPathMap.get(Id);
				
				if(!inputItemArrayListPath.contains(path)){
					
					inputItemArrayListPath.add(path);
					inputItemArrayListName.add(name);
					
					SubmitNumber ++ ;
					if(SubmitNumber > 0)
						Submit.setEnabled(true);
					Submit.setText("提交("+SubmitNumber+")");
				}
				else 
				{
					inputItemArrayListPath.remove(path);
					inputItemArrayListName.remove(name);
					SubmitNumber -- ;
					if(SubmitNumber == 0)
						Submit.setEnabled(false);
					Submit.setText("提交("+SubmitNumber+")");
				}
				
					musicAdapter.notifyDataSetChanged();
			}
			
		});
		
		Submit = (Button) this.findViewById(R.id.gainallmusic_submit);
		Submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toSubmit();
			}
		});
		
		new Thread()
		{
			@Override
			public void run()
			{
				 GainMusicPath();
			}
		}.start();
		
	}
	
	/**
	 * @author nieyihe
	 * 接受发送来的数据
	 * */
	public void GetBundle()
	{
		Bundle b = this.getIntent().getExtras() ;
		this.mem_id = b.getString("member_id");
		this.regiment_id = b.getString("regiment_id");
		
	}
	
	/**
	 * @author nieyihe
	 * 获取所有的音乐的路径
	 * */
	public void GainMusicPath()
	{
	
		Cursor cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.SIZE +" > 0 and " + MediaStore.Audio.Media.MIME_TYPE + " in ('audio/mpeg','audio/mp3')", null, null);
		
		if(cursor == null) 
			{
				return ;
			}
		
		while(cursor.moveToNext())
		{
			String datapath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			String displayname = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
			
				Bundle b = new Bundle();
				b.putString("DisplayName", displayname);
				b.putString("Id", id);
				b.putString("DataPath", datapath);
				SendMessage("ReFresh",b );	
		}
			
		cursor.close();
	}
	
	/**
	 * @author nieyihe
	 * 事件处理类
	 * */
	private class MusicHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if(b.containsKey("ReFresh"))
			{
				
				String displayname = b.getString("DisplayName");
				String id = b.getString("Id");
				String datapath = b.getString("DataPath");
				NameArrayList.add(displayname);
				idsArrayList.add(id);
				IdPathMap.put(id, datapath);
				musicAdapter.notifyDataSetChanged();
			}
			
			if(b.containsKey("Nothing"))
			{
				Toast.makeText(GainAllMusic.this, "没有内容", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * @author nieyihe
	 * 适配器
	 * */
	private class MusicAdapter extends BaseAdapter
	{
		LayoutInflater i ;
		
		MusicAdapter()
		{
			i = LayoutInflater.from(GainAllMusic.this);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			
				return NameArrayList.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Bind bind ;
			
			if(convertView == null)
			{
				bind = new Bind();
				convertView = i.inflate(R.layout.gainallmusicitem,null);
				bind.Input = (ImageView) convertView.findViewById(R.id.gainallmusicitem_input);
				bind.Name = (TextView) convertView.findViewById(R.id.gainallmusicitem_name);
				convertView.setTag(bind);
			}
			else
			{
				bind = (Bind)convertView.getTag();
			}
			
			String id = "";
			String path = "" ;
			String name = "" ;
			/*synchronized (NameArrayList) {*/
				name = NameArrayList.get(position);
			
			if(idsArrayList.size() > position)
				id = idsArrayList.get(position);
			if(id != null || !id.equals(""))
				path = IdPathMap.get(id);
			/*}*/
			if(path != null && inputItemArrayListPath.contains(path))
				bind.Input.setImageResource(R.drawable.select_yes);
			else {
				bind.Input.setImageResource(R.drawable.select_no);
			}
			bind.Name.setText(name);
			
			return convertView;
		}
		
	}
	
	/**
	 * @author nieyihe
	 * 辅助类
	 * 
	 * */
	private class Bind 
	{
		TextView Name;
		ImageView Input;
	}
	
	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	public void SendMessage(String keyString ,Bundle b)
	{
		Message msg = new Message();
		b.putString(keyString, "");
		msg.setData(b);
		musicHandler.sendMessage(msg);
	}
	
	/**
	 * @author nieyihe
	 * 提交所有的已选择的音乐
	 * */
	public void toSubmit()
	{
		
		String paths[] = new String[inputItemArrayListPath.size()];
		inputItemArrayListPath.toArray(paths);
		
		String names[] = new String[inputItemArrayListName.size()];
		inputItemArrayListName.toArray(names);
		
		Intent i = new Intent();
		i.setClass(GainAllMusic.this, UpdateMusicService.class);
		i.putExtra("member_id", mem_id);
		i.putExtra("regiment_id", regiment_id);
		i.putExtra("files", paths);
		i.putExtra("names", names);
		GainAllMusic.this.startService(i);
		finish();
		
	}
	
	
}
