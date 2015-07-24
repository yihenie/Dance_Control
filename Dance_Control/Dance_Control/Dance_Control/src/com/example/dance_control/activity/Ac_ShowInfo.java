package com.example.dance_control.activity;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.GetListener;

import com.example.dance_control.R;
import com.example.dance_control.bmob_table.regiment_info;
import com.example.dance_control.resource.Info;
import com.example.dance_control.tools.BitmapLocalFactory;

public class Ac_ShowInfo extends Activity {

	private String info_id;
	private TextView title;
	private TextView brief;
	private TextView time;
	private Bitmap bitmapimg;
	private ImageView img;
	private TextView word;
	private ImageButton refresh;
	private Info now;
	private InfoHandler handler = new InfoHandler();
	private Animation animation;
	@Override
	public void onCreate(Bundle s) {
		super.onCreate(s);
		this.setContentView(R.layout.show_info_dital);
		GetBundle();
		BuildShowInfoView();
		GetInfo();
	}

	/**
	 * @author nieyihe
	 * @作用：初始化显示界面
	 * */
	public void BuildShowInfoView() {
		
		title = (TextView) this.findViewById(R.id.show_info_dital_title);
		brief = (TextView) this.findViewById(R.id.show_info_dital_brief);
		time = (TextView) this.findViewById(R.id.show_info_dital_time);
		img = (ImageView) this.findViewById(R.id.show_info_dital_img);
		word = (TextView) this.findViewById(R.id.show_info_dital_word);
		refresh = (ImageButton) this.findViewById(R.id.show_info_dital_refresh);
		animation = AnimationUtils.loadAnimation(Ac_ShowInfo.this, R.anim.btu_refresh_anim);
		
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GetInfo();
				refresh.startAnimation(animation);
			}

		});
	}

	/**
	 * @author nieyihe
	 * 显示详细数据
	 * */
	public void ShowDitalInfo() {
		
		title.setText(now.title);
		brief.setText(now.brief);
		time.setText(now.time);
		word.setText(now.word);
	}

	/**
	 * @author nieyihe
	 * @作用：设置获取上一个activity返回的数据
	 * */
	public void GetBundle() {
		Bundle s = this.getIntent().getExtras();
		info_id = s.getString("info_id");

	}

	/**
	 * @author nieyihe
	 * @zuoyong:从bmob云端获取当前资讯的具体数据
	 * */
	public void GetInfo() {
		BmobQuery<regiment_info> query = new BmobQuery<regiment_info>();
		query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.getObject(Ac_ShowInfo.this, info_id,
				new GetListener<regiment_info>() {

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(regiment_info arg0) {
						// TODO Auto-generated method stub
						
						now = new Info(arg0.img_path, arg0.img_url, arg0.word,
								arg0.getObjectId(), arg0.brief, arg0.title,
								arg0.hit_num, arg0.time);
						
						ShowDitalInfo();
						if(!now.img_path.equals("") && !now.img_url.equals(""))
						{
							new Thread() {
								
								public void run() {
									try {
										bitmapimg = BitmapLocalFactory.CreateBitmap(
												Ac_ShowInfo.this, now.img_path,
												now.img_url,BitmapLocalFactory.NORMAL);
										if (bitmapimg != null)
											SendMessage("ShowImg");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								};
							}.start();
						}
						else 
							img.setVisibility(View.GONE);
					}

				});

	}

	/**
	 * @author nieyihe
	 * 消息处理类
	 * */
	private class InfoHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			if (b.containsKey("ShowImg")) {
				img.setImageBitmap(bitmapimg);
			}
		}
	}

	/**
	 * @author nieyihe
	 * 发送消息
	 * */
	private void SendMessage(String msgString) {
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString(msgString, "");
		msg.setData(b);
		handler.sendMessage(msg);
	}
}
