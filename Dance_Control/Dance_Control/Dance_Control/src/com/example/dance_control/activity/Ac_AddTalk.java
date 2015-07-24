package com.example.dance_control.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.example.dance_control.R;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.bmob_table.regiment_talk;
import com.example.dance_control.dialog.BtuClicking;
import com.example.dance_control.dialog.OptionDialog;
import com.example.dance_control.resource.member;
import com.example.dance_control.tools.NotificationLocalFactory;

public class Ac_AddTalk extends Activity {

	private static int Get_Image = 1;	
	
	private ImageButton imgSet;
	private EditText word;
	private Button Input;
	private String Member_id;
	private String regiment_id;
	private String ImgPath = "";
	private ImageView img;
	private member me;
	private Notification notification;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_talk);
		
		this.Get_Bundle();
		this.Ini_Add_Talk();
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(!Member_id.equals(""))
			GetMe();
	}
	/**
	 * @author nieyihe
	 * @作用：提取当前用户的member_id,由上一个activity发送过来.
	 * 
	 * */
	private void Get_Bundle()
	{
		Bundle b = this.getIntent().getExtras();
		Member_id = b.getString("member_id");
		regiment_id = b.getString("regiment_id");
	}
	
	/**
	 * @author nieyihe
	 * @作用：用于初始化当前界面
	 * */
	private void Ini_Add_Talk()
	{
		this.imgSet = (ImageButton) this.findViewById(R.id.add_talk_addpic);
		this.Input = (Button) this.findViewById(R.id.add_talk_input);
		this.word = (EditText) this.findViewById(R.id.add_talk_edit_word);
		this.img = (ImageView) this.findViewById(R.id.add_talk_pic);
		
		this.Input.setOnClickListener(new Onclick());
		this.imgSet.setOnClickListener(new Onclick());
	}

	private class Onclick implements View.OnClickListener
	{
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.add_talk_addpic:
				{
					Intent i = new Intent();
					i.setType("image/*");
					i.setAction(Intent.ACTION_PICK);
					Ac_AddTalk.this.startActivityForResult(i, Get_Image);
					break;
				} 
				
				case R.id.add_talk_input:
				{
					if(!word.getText().toString().equals(""))
					{
						
						final OptionDialog optionDialog = new OptionDialog(Ac_AddTalk.this);
						optionDialog.PrepareDialog();
						optionDialog.SetMiddleShow("是否提交？");
						optionDialog.SetFirstOnlick(new BtuClicking() {
							
							@Override
							public void OnClick() {
								// TODO Auto-generated method stub
								notification = NotificationLocalFactory.UpdateShowSave(Ac_AddTalk.this,0);
								finish();
								
								if(!ImgPath.equals(""))
									Input_Talk();
								else 
									SaveTalk();
								optionDialog.dismiss();
							}
						});
						optionDialog.SetSecondOnclick(new BtuClicking() {
							
							@Override
							public void OnClick() {
								// TODO Auto-generated method stub
								optionDialog.dismiss();
							}
						});
						optionDialog.SetCanceledOnTouchOutside(false);
						optionDialog.Show();
					}
					else
					{
						Toast.makeText(Ac_AddTalk.this, "内容为空", Toast.LENGTH_LONG).show();
					}
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
			
            ImgPath = cursor.getString(columnIndex);
           
            cursor.close();
            img.setVisibility(View.VISIBLE);
            img.setImageBitmap(BitmapFactory.decodeFile(ImgPath));
		}
	}
	
	/**
	 * @author nieyihe
	 * 保存论坛的图片
	 * */
	private void Input_Talk()
	{
		
		BmobProFile.getInstance(Ac_AddTalk.this).upload(ImgPath,new UploadListener() {
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(String path, String url) {
				// TODO Auto-generated method stub
				SaveTalk(path,url);
			}
			
			@Override
			public void onProgress(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * @author nieyihe
	 * 保存发表
	 * */
	private void SaveTalk(String path,String url)
	{
		regiment_talk now_talk = new regiment_talk(regiment_id,word.getText().toString(),me.name,GetNowTime(),me.head_path,me.head_url,0,me.member_id,path,url);
		now_talk.save(Ac_AddTalk.this, new SaveListener(){

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(Ac_AddTalk.this, "提交成功", Toast.LENGTH_LONG).show();
				NotificationLocalFactory.UpdateDismissSave(notification, 0);
			}});
	}
	
	/**
	 * @author nieyihe
	 * 提交论坛
	 * */
	public void SaveTalk()
	{
		regiment_talk now_talk = new regiment_talk(regiment_id,word.getText().toString(),me.name,GetNowTime(),me.head_path,me.head_url,0,me.member_id);
		now_talk.save(Ac_AddTalk.this, new SaveListener(){

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(Ac_AddTalk.this, "提交成功", Toast.LENGTH_LONG).show();
				NotificationLocalFactory.UpdateDismissSave(notification, 0);
				
			}});
	}
	
	/**
	 * @author nieyihe
	 * 初始化自己的信息
	 * */
	private void GetMe()
	{
		
		BmobQuery<regiment_member> mem_info = new BmobQuery<regiment_member>();
		mem_info.getObject(Ac_AddTalk.this, Member_id, new GetListener<regiment_member>()
				{

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						final OptionDialog optionDialog = new OptionDialog(Ac_AddTalk.this);
						optionDialog.PrepareDialog();
						optionDialog.SetMiddleShow("本人信息加载失败,请重试获取.");
						optionDialog.SetFirstOnlick(new BtuClicking() {
							
							@Override
							public void OnClick() {
								// TODO Auto-generated method stub
								GetMe();
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

					@Override
					public void onSuccess(regiment_member arg0) {
						// TODO Auto-generated method stub
						
						me = new member(arg0.member_head_path,arg0.member_head_url,arg0.regiment_id,arg0.getObjectId(),arg0.member_name,arg0.member_sex,arg0.member_power,arg0.member_host,arg0.member_password,arg0.member_ture_name,arg0.praise_num);
						
					}
			
				});	
	}
	
	public long GetNowTime()
	{
		return System.currentTimeMillis();

	}
}


