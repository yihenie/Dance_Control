package com.example.dance_control.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.example.dance_control.R;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.bmob_table.regiment_table;
import com.example.dance_control.tools.NotificationLocalFactory;
import com.example.dance_control.utils.Act_MyRigment;

public class Ac_Join extends Activity {

	private String member_id;
	private String regiment_id;
	private ImageView head;
	private EditText truename_edit;
	private EditText name_edit;
/*	private EditText sex_edit;*/
	private RadioButton sex_select_boy;
	private RadioButton sex_select_girl;
	private EditText host_edit;
	private EditText password_edit;
	private EditText sure_password_edit;
	private Button register;
	private Button exit;
	private String true_name;
	private String name;
	private String sex;
	private String host;
	private String password;
	private String ImagePath = "";
	/*保存Time*/
	private SharedPreferences Save_Time;
	//private SharedPreferences Trends ;
	private Notification notification;
	private final static int Get_Image = 1;
	@Override
	public void onCreate(Bundle s)
	{
		super.onCreate(s);
		this.setContentView(R.layout.join);
		GetBundle();
		SetJoinView();
	}
	
	/**
	 * @author nieyihe
	 * 获取上一个Activity发送的数据
	 * **/
	public void GetBundle()
	{
		Bundle d = this.getIntent().getExtras();
		regiment_id = d.getString("regiment_id");
	}
	
	/**
	 * @author nieyihe
	 * 设置View
	 * */
	public void SetJoinView()
	{
		Save_Time = Ac_Join.this.getSharedPreferences("Time", Context.MODE_PRIVATE);
//		Trends = Ac_Join.this.getSharedPreferences("Trends", Context.MODE_PRIVATE);
		head = (ImageView) this.findViewById(R.id.join_head_img);
		truename_edit =  (EditText) this.findViewById(R.id.join_true_name_edit);
		name_edit = (EditText) this.findViewById(R.id.join_name_edit_edit);
		sex_select_boy = (RadioButton) this.findViewById(R.id.join_sex_select_boy);
		sex_select_girl = (RadioButton) this.findViewById(R.id.join_sex_select_girl);
		host_edit = (EditText) this.findViewById(R.id.join_host_edit_edit);
		password_edit = (EditText) this.findViewById(R.id.join_password_edit_edit);
		sure_password_edit = (EditText) this.findViewById(R.id.join_surepassword_edit_edit);
		register = (Button) this.findViewById(R.id.join_register_button);
		exit = (Button) this.findViewById(R.id.join_exit_button);
		
		head.setOnClickListener(new BtuOnclick());
		register.setOnClickListener(new BtuOnclick());
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
				case R.id.join_register_button:
				{
					if(ImagePath.equals(""))
					{
						Toast.makeText(Ac_Join.this, "头像没有设置", Toast.LENGTH_LONG).show();
						break;
					}
					if(name_edit.getText().toString().trim().equals("")||host_edit.getText().toString().trim().equals("")||password_edit.getText().toString().trim().equals("")||sure_password_edit.getText().toString().trim().equals("")||truename_edit.getText().toString().trim().equals(""))
					{
						Toast.makeText(Ac_Join.this, "有些项没填,请仔细检查", Toast.LENGTH_LONG).show();
						break;
					}
					
					if(!password_edit.getText().toString().trim().equals(sure_password_edit.getText().toString().trim()))
					{
						Toast.makeText(Ac_Join.this,"密码输入不一致", Toast.LENGTH_LONG).show();
						sure_password_edit.setText("");
						break;
					}
					true_name = truename_edit.getText().toString().trim();
					name = name_edit.getText().toString().trim();
					sex =  sex_select_boy.isChecked()?"男":"女";
					host = host_edit.getText().toString().trim();
					password = password_edit.getText().toString().trim();
					
					notification = NotificationLocalFactory.UpdateShowSave(Ac_Join.this,0);
					finish();

					
					Update();
					//注册成功
					break;
				}
				case R.id.join_exit_button:
				{
					Dialog dialog = new Dialog(Ac_Join.this,R.style.Item_Dialog);
					RelativeLayout view = (RelativeLayout) LayoutInflater.from(Ac_Join.this).inflate(R.layout.exit_dialog, null);
					Button sure = (Button)view.findViewById(R.id.exit_dialog_sure);
					Button exit = (Button)view.findViewById(R.id.exit_dialog_cancel);
					
					dialog.setContentView(view);
					sure.setOnClickListener(new Dialog_Onclick(dialog));
					exit.setOnClickListener(new Dialog_Onclick(dialog));
					dialog.show();
					break;
				}
				case R.id.join_head_img:
				{
					Intent intent = new Intent();
			        /* 设置为image */
			        intent.setType("image/*");
			        /* 使用Intent.ACTION_GET_CONTENT这个Action */
			        intent.setAction(Intent.ACTION_PICK);
			        /* 取得图片后返回本画面 */
			        Ac_Join.this.startActivityForResult(intent, Get_Image);
			        break;
				}
			}
		}
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null && requestCode == Get_Image && resultCode == RESULT_OK)//result_ok系统自带的常量
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
                
            	Toast.makeText(Ac_Join.this, "图片太大,请重新设置", Toast.LENGTH_SHORT).show();
            	ImagePath = "";
            	return ;
            }
            
            head.setImageBitmap(bitmap);
		}
	}

	
	public class Dialog_Onclick implements View.OnClickListener
	{
		private Dialog dialog;
		
		public Dialog_Onclick(Dialog dialog)
		{
			this.dialog = dialog;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.exit_dialog_cancel:
				{
					dialog.dismiss();
					break;
				}
				
				case R.id.exit_dialog_sure:
				{
					Ac_Join.this.finish();
					break;
				}
			}
		}
		
	}

	/**
	 * @author nieyihe
	 * @zuoyong :上传数据
	 * */
	public void Update()
	{
		
		BmobProFile.getInstance(Ac_Join.this).upload(ImagePath,new UploadListener() {
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(String Path, String Url) {
				// TODO Auto-generated method stub
				Save_Member(Path,Url,name,sex,host,password,"member",regiment_id,true_name);
			}
			
			@Override
			public void onProgress(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	/**
	 *@author nieyihe
	 *@作用：保存数据
	 * */
	public void Save_Member(String member_head_path,String member_head_url,String member_name,String member_sex,String member_host,String member_password,String power,String regiment_id,String member_true_name)
	{
		final regiment_member New_Mem  = new regiment_member(member_head_path,member_head_url,member_name,regiment_id,member_sex,member_host,power,member_password,member_true_name,0);
		
		New_Mem.save(Ac_Join.this, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				NotificationLocalFactory.UpdateDismissSave(notification, 0);
				member_id = New_Mem.getObjectId();
				InitTime();
				Toast.makeText(Ac_Join.this, "注册成功", Toast.LENGTH_LONG).show();
				UpdateNumRegiment();
				
			}
			
		});
	}
	
	/**
	 * @author nieyihe
	 * 保存
	 * */
	public void InitTime()
	{
		Editor editer =  Save_Time.edit();
		editer.putLong("music_time",System.currentTimeMillis());
		editer.putLong("pic_time",System.currentTimeMillis());
		editer.putLong("movie_time",System.currentTimeMillis());
		editer.putLong("talk_time",System.currentTimeMillis());
		editer.commit();
	}
	
	/**
	 * @author nieyihe
	 * 更新舞团的人数
	 * */
	public void UpdateNumRegiment()
	{
				regiment_table regiment = new regiment_table();
				regiment.increment("regiment_num");
				regiment.update(Ac_Join.this, regiment_id, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Intent i = new Intent();
						i.setClass(Ac_Join.this, Act_MyRigment.class);
						Bundle b = new Bundle();
						b.putString("member_id", member_id);
						b.putString("regiment_id", Ac_Join.this.regiment_id);
						b.putString("member_power","member");
						i.putExtras(b);
						Ac_Join.this.startActivity(i);
						finish();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
		});
		
	}
	
}
