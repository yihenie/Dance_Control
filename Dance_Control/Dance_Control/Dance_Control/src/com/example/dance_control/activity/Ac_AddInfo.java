package com.example.dance_control.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.example.dance_control.R;
import com.example.dance_control.bmob_table.regiment_info;
import com.example.dance_control.tools.NotificationLocalFactory;

public class Ac_AddInfo extends Activity {
	
	
	private String regiment_id;
	private String info_text;
	private String info_brief;
	private String info_title;
	private String info_time = "没有设置时间";
	private EditText edit_info;
	private EditText edit_title;
	private EditText edit_brief;
	private ImageButton add_img;
	private String ImagePath = "";
	private Button put;
	private Button exit;
	private Notification notification;
	private final static int Get_Image = 1;
	
	@Override
	public void onCreate(Bundle s)
	{
		super.onCreate(s);
		this.setContentView(R.layout.add_info);
		Get_Bundle();
		BuildAddInfoView();
	}
	
	/**
	 * @author nieyihe
	 * 获取上一个Activity设置的Bundle
	 * */
	public void Get_Bundle()
	{
		Bundle b = this.getIntent().getExtras();
		this.regiment_id = b.getString("regiment_id");
	}
	
	/**
	 * @author nieyihe
	 * 设置View的样式
	 * */
	public void BuildAddInfoView()
	{
		edit_info = (EditText) this.findViewById(R.id.add_info_intro);
		edit_title = (EditText) this.findViewById(R.id.add_info_title);
		edit_brief = (EditText) this.findViewById(R.id.add_info_brief);
	
		add_img = (ImageButton) this.findViewById(R.id.add_info_img);
		put = (Button) this.findViewById(R.id.add_info_put);
		exit = (Button) this.findViewById(R.id.add_info_exit);
		
		add_img.setOnClickListener(new BtuOnclick());
		put.setOnClickListener(new BtuOnclick());
		exit.setOnClickListener(new BtuOnclick());
		
	}
	
	/**
	 * @author nieyihe
	 * 监听器
	 * */
	public class BtuOnclick implements View.OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				 case R.id.add_info_img:
				 {
					 Intent set = new Intent();
					 set.setType("image/*");
					 set.setAction(Intent.ACTION_PICK);
					 Ac_AddInfo.this.startActivityForResult(set, Get_Image);
					 break;
				 }
				 
				 case R.id.add_info_put:
				 {
					 if(edit_info.getText().toString().trim().equals(""))
					 {
						 Toast.makeText(Ac_AddInfo.this, "资讯介绍没有填写", Toast.LENGTH_LONG).show();
						 break;
					 }
					 if(edit_title.getText().toString().trim().equals(""))
					 {
						 Toast.makeText(Ac_AddInfo.this, "资讯标题没有填写", Toast.LENGTH_LONG).show();
						 break;
					 }
					 if(edit_brief.getText().toString().trim().equals(""))
					 {
						 Toast.makeText(Ac_AddInfo.this, "资讯简介没有填写", Toast.LENGTH_LONG).show();
						 break;
					 }
					 info_text = edit_info.getText().toString().trim();
					 info_title = edit_title.getText().toString().trim();
					 info_brief = edit_brief.getText().toString().trim();
					 Calendar now = Calendar.getInstance();
					 info_time = now.get(Calendar.YEAR)+"-" +now.get(Calendar.MONTH) +"-"+now.get(Calendar.DAY_OF_MONTH) +"  " +now.get(Calendar.HOUR_OF_DAY) +":"+now.get(Calendar.MINUTE);
					 
					 notification = NotificationLocalFactory.UpdateShowSave(Ac_AddInfo.this,0);
					 finish();
					 
					 if(!ImagePath.equals(""))
					 {
						 update();
					 }
					 else
					 {
						 Save_Info("",""); 
					 }
					 
					 break;
				 }
				 
				 case R.id.add_info_exit:
				 {
					 Ac_AddInfo.this.finish();
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
            
            Bitmap bitmap = BitmapFactory.decodeFile(ImagePath);
            if(bitmap.getWidth() > 1024 || bitmap.getHeight() > 1024){
                
            	Toast.makeText(Ac_AddInfo.this, "图片太大,请重新设置", Toast.LENGTH_SHORT).show();
            	ImagePath = "";
            	return ;
            }
            
            add_img.setImageBitmap(bitmap);
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @作用：上传图片
	 * */
	public void update()
	{
	
	BmobProFile.getInstance(Ac_AddInfo.this).upload(ImagePath,new UploadListener() {
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(String fileName, String Url) {
				// TODO Auto-generated method stub
				Save_Info(fileName,Url);
			}
			
			@Override
			public void onProgress(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * @author nieyihe
	 * @作用：保存info:
	 * */
	public void Save_Info(String path,String url)
	{
		
		regiment_info now_info = new regiment_info(regiment_id,path,url,info_text,info_brief,info_title,0,info_time);
		now_info.save(Ac_AddInfo.this, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(Ac_AddInfo.this, "发表成功", Toast.LENGTH_SHORT).show();
				NotificationLocalFactory.UpdateDismissSave(notification, 0);
				
			}
			
		});
	}
}
