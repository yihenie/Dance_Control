package com.example.dance_control.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.example.dance_control.R;
import com.example.dance_control.bmob_table.regiment_audio;
import com.example.dance_control.tools.NotificationLocalFactory;

public class Ac_AddMovie extends Activity {

	private ImageView head;
	private ImageButton help;//点击展开动画,用于显示帮助获取url
	private ImageButton imgset;
	private EditText url;
	private EditText movie_intro;
	private Button putmovie;
	private Button exit;
	private String ImagePath = "";
	private String Intro;
	private String url_text;
	private String regiment_id;
	private String member_id;
	private Notification notification;
	private static final int Get_Image  = 1;
	@Override
	public void onCreate(Bundle s)
	{
		super.onCreate(s);
		this.setContentView(R.layout.add_movie);
		Get_Bundle();
		this.Set_AddMovie();

	}
	
	/**
	 * @author nieyihe
	 * @作用：获取上一个activity传递过来的数据
	 * */
	public void Get_Bundle()
	{
		Bundle b = this.getIntent().getExtras();
		regiment_id = b.getString("regiment_id");
		member_id = b.getString("member_id");
	}
	
	/**
	 * @author nieyihe
	 * @作用:设置显示
	 * */
	public void Set_AddMovie()
	{
		head = (ImageView) this.findViewById(R.id.add_movie_img);
		help = (ImageButton) this.findViewById(R.id.add_movie_help);
		/*imgset = (ImageButton) this.findViewById(R.id.add_movie_setimg);*/
		url = (EditText) this.findViewById(R.id.add_movie_url);
		movie_intro = (EditText) this.findViewById(R.id.add_movie_intro);
		putmovie = (Button) this.findViewById(R.id.add_movie_put);
		exit = (Button) this.findViewById(R.id.add_movie_exit);
		
		help.setOnClickListener(new Onclick());
		head.setOnClickListener(new Onclick());
		putmovie.setOnClickListener(new Onclick());
		exit.setOnClickListener(new Onclick());
	}
	
	/**
	 * @author nieyihe
	 * @作用：自己的点击事件类
	 * */
	public class Onclick implements View.OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.add_movie_help:
				{
					Builder builder = new AlertDialog.Builder(Ac_AddMovie.this);
					LinearLayout view = (LinearLayout) LayoutInflater.from(Ac_AddMovie.this).inflate(R.layout.add_movie_help_dialog, null);
					builder.setView(view);
					builder.create().show();
					break;
				}
				
				case R.id.add_movie_img:
				{
					Intent set = new Intent();
					set.setType("image/*");
					set.setAction(Intent.ACTION_PICK);
					Ac_AddMovie.this.startActivityForResult(set, Get_Image);
					break;
				}
				
				case R.id.add_movie_put:
				{
					
					
					if(url.getText().toString().equals(""))
					{
						Toast.makeText(Ac_AddMovie.this, "视频地址没有设置", Toast.LENGTH_LONG).show();
						break;
					}
					
					if(movie_intro.getText().toString().equals(""))
					{
						Toast.makeText(Ac_AddMovie.this, "视频的介绍没有写", Toast.LENGTH_LONG).show();
						break;
					}
					
					Intro = movie_intro.getText().toString();
					url_text = url.getText().toString();
					Dialog dialog = new Dialog(Ac_AddMovie.this,R.style.Item_Dialog);
					RelativeLayout view = (RelativeLayout) LayoutInflater.from(Ac_AddMovie.this).inflate(R.layout.tip_dialog, null);
					Button sure = (Button) view.findViewById(R.id.tip_dialog_sure);
					Button exit = (Button) view.findViewById(R.id.tip_dialog_exit);
					dialog.setContentView(view);
					sure.setOnClickListener(new Dialog_Onclick(dialog));
					exit.setOnClickListener(new Dialog_Onclick(dialog));
					dialog.show();
						
					break;
				}
				
				case R.id.add_movie_exit:
				{
					Ac_AddMovie.this.finish();
					break;
				}
			}
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Get_Image && resultCode == RESULT_OK && data != null)
		{
			Uri ImageUri = data.getData();
			String []proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(ImageUri,proj,null,null,null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(proj[0]);
			
            ImagePath = cursor.getString(columnIndex);
           
            cursor.close();
            
            head.setImageBitmap(BitmapFactory.decodeFile(ImagePath));
		}
		
	}
	
	public class Dialog_Onclick implements View.OnClickListener
	{
		Dialog dialog;
		
		public Dialog_Onclick(Dialog dialog)
		{
			this.dialog = dialog;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.tip_dialog_sure:
				{
					notification = NotificationLocalFactory.NotificationViewCreate(Ac_AddMovie.this, R.layout.notification_message,R.drawable.input, 0);
					NotificationLocalFactory.SetViewText(notification.contentView, "小控搬运图片中", R.id.notification_word, notification, 0);
					
					dialog.dismiss();
					if(!ImagePath.equals(""))
					{
						update();
					}
					else {
						Save_Movie("","");
					}
					Ac_AddMovie.this.finish();
					break;
				} 
				
				case R.id.tip_dialog_exit:
				{
					dialog.dismiss();
					break;
				}
			}
		}
		
	}
	
	
	/**
	 * @author nieyihe
	 * @作用：保存数据
	 * */
	public void update()
	{
		BTPFileResponse response = BmobProFile.getInstance(Ac_AddMovie.this).upload(ImagePath,new UploadListener() {
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(String Path, String Url) {
				// TODO Auto-generated method stub
				Save_Movie(Path,Url);
			}
			
			@Override
			public void onProgress(int arg0) {
				// TODO Auto-generated method stub
				
			}
		}) ;
	}
	
	
	/**
	 * @author nieyihe
	 * @作用：保存数据到Bmob_movie
	 * */
	public void Save_Movie(String imghead_path,String url)
	{
		//String img_path,String img_url,String regiment_id,Integer num,String mem_id,String intro,String url,String type
		regiment_audio now_movie = new regiment_audio(imghead_path,url,regiment_id,0,member_id,Intro,url_text,"Movie");
		now_movie.save(Ac_AddMovie.this, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(Ac_AddMovie.this, "设置成功", Toast.LENGTH_SHORT).show();
				NotificationLocalFactory.SetViewText(notification.contentView, "小控完成任务", R.id.notification_word, notification, 0);
				NotificationLocalFactory.Cancel(0);
			}
			
		});
	}
}
