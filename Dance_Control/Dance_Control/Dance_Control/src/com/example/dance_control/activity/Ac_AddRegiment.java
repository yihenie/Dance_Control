package com.example.dance_control.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.example.dance_control.R;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.bmob_table.regiment_table;
import com.example.dance_control.tools.NotificationLocalFactory;
import com.example.dance_control.utils.Act_MyRigment;

public class Ac_AddRegiment extends Activity {

	private String regiment_id;
	private String member_id;
	
	private ImageView img;
	private String name_text;
	private String order_text;
	private String sec_order_text;
	private String secor_text;
	private String addr_text;
	private String host_text;
	private String password_text;
	private String intro_text;
	private String time_text;
	
	private EditText regiment_name;
	private EditText regiment_order;
	private EditText regiment_sec_order;
	private EditText regiment_secor;
	private EditText regiment_addr;
	private EditText regiment_host;
	private EditText regiment_password;
	private EditText regiment_intro;
	private EditText regiment_time;
	private Button regiment_sure;
	private Button regiment_cancel;
	private String ImagePath = "";
	
	private final static int Get_Image = 1;
	
	private Notification notification;
	@Override
	public void onCreate(Bundle s)
	{
		super.onCreate(s);
		this.setContentView(R.layout.add_regiment);
		this.Ini_AddRegiment();
	}
	
	/**
	 * @author nieyihe
	 * @作用：用于初始化界面
	 * */
	public void Ini_AddRegiment()
	{
		/*imgset = (ImageButton) this.findViewById(R.id.add_regiment_setimg);*/
		img = (ImageView) this.findViewById(R.id.add_regiment_img);
		regiment_name = (EditText) this.findViewById(R.id.add_regiment_name_edit);
		regiment_order = (EditText) this.findViewById(R.id.add_regiment_order_edit);
		regiment_sec_order = (EditText) this.findViewById(R.id.add_regiment_second_ord_edit);
		regiment_secor = (EditText) this.findViewById(R.id.add_regiment_secor_edit);
		regiment_addr = (EditText) this.findViewById(R.id.add_regiment_addr_edit);
		regiment_host = (EditText) this.findViewById(R.id.add_regiment_host_edit);
		regiment_password = (EditText) this.findViewById(R.id.add_regiment_password_edit);
		regiment_intro = (EditText) this.findViewById(R.id.add_regiment_intro_edit);
		regiment_time = (EditText) this.findViewById(R.id.add_regiment_time_edit);
		regiment_sure = (Button)this.findViewById(R.id.add_regiment_sure);
		regiment_cancel = (Button) this.findViewById(R.id.add_regiment_cancel);
		
		regiment_sure.setOnClickListener(new Onclick());
		regiment_cancel.setOnClickListener(new Onclick());
		img.setOnClickListener(new Onclick());
	}
	
	public class Onclick implements View.OnClickListener
	{
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.add_regiment_cancel:
				{
					Builder builder = new AlertDialog.Builder(Ac_AddRegiment.this);
					RelativeLayout view = (RelativeLayout) LayoutInflater.from(Ac_AddRegiment.this).inflate(R.layout.exit_dialog, null);
					Button sure = (Button) view.findViewById(R.id.exit_dialog_sure);
					Button cancel = (Button) view.findViewById(R.id.exit_dialog_cancel);
					
					builder.setView(view);
					AlertDialog dialog = builder.create();
					dialog.show();
					sure.setOnClickListener(new Dialog_Onclick(dialog));
					cancel.setOnClickListener(new Dialog_Onclick(dialog));
					break;
				}
				
				case R.id.add_regiment_sure:
				{
					name_text = regiment_name.getText().toString().trim();
					order_text = regiment_order.getText().toString().trim();
					sec_order_text = regiment_sec_order.getText().toString().trim();
					secor_text = regiment_secor.getText().toString().trim();
					addr_text = regiment_addr.getText().toString().trim();
					host_text = regiment_host.getText().toString().trim();
					password_text = regiment_password.getText().toString().trim();
					intro_text = regiment_intro.getText().toString().trim();
					time_text = regiment_time.getText().toString().trim();
					if(ImagePath.equals(""))
					{
						toast("没有设置舞团图片！");
						break;
					}
					
					if(name_text.equals("") || order_text.equals("") || sec_order_text.equals("") || secor_text.equals("") || addr_text.equals("") || host_text.equals("") || password_text.equals("") || intro_text.equals("") || time_text.equals(""))
					{
						toast("有数据没有填写,请仔细检查！");
						break;
					}	
					
					
					Builder builder = new AlertDialog.Builder(Ac_AddRegiment.this);
					RelativeLayout view = (RelativeLayout) LayoutInflater.from(Ac_AddRegiment.this).inflate(R.layout.tip_dialog, null);
					Button sure = (Button) view.findViewById(R.id.tip_dialog_sure);
					Button cancel = (Button) view.findViewById(R.id.tip_dialog_exit);
					TextView tip = (TextView) view.findViewById(R.id.tip_dialog_text);
					tip.setText("是否确定？");
					builder.setView(view);
					AlertDialog dialog = builder.create();
					dialog.show();
					sure.setOnClickListener(new Dialog_Onclick(dialog));
					cancel.setOnClickListener(new Dialog_Onclick(dialog));
					break;
				}
				
				case R.id.add_regiment_img:
				{
					Intent setimg = new Intent();
					setimg.setType("image/*");
					setimg.setAction(Intent.ACTION_PICK);
					Ac_AddRegiment.this.startActivityForResult(setimg,Get_Image);
					break;
				}
			}
		}
		
	}


	public class Dialog_Onclick implements View.OnClickListener
	{
		AlertDialog dialog;
		
		public Dialog_Onclick(AlertDialog dialog)
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
					toast(ImagePath);
					//保存数据
					notification = NotificationLocalFactory.UpdateShowSave(Ac_AddRegiment.this,0);
					
					update();					
					dialog.dismiss();
					break;
				}
			
				case R.id.tip_dialog_exit:
				{
					
					dialog.dismiss();
					break;
				}
				
				case R.id.exit_dialog_sure:
				{
					dialog.dismiss();
					Ac_AddRegiment.this.finish();
					break;
				}
				
				case R.id.exit_dialog_cancel:
				{
					dialog.dismiss();
					break;
				}
			}
		}
	}
	
	/**
	 * @author nieyihe
	 * @作用：创建舞团
	 * */
	public void update()
	{
		BmobProFile.getInstance(Ac_AddRegiment.this).upload(ImagePath, new UploadListener() {

            @Override
            public void onSuccess(String fileName,String url) {
                // TODO Auto-generated method stub
            	Save_Regiment(fileName,url);
            }

            @Override
            public void onProgress(int ratio) {
                // TODO Auto-generated method stub
               
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
               
            }
        });
	}
	
	/**
	 * @author nieyihe
	 * @作用：在云端保存舞团数据
	 * */
	public void Save_Regiment(final String path,final String url)
	{
	
		final regiment_table now_regiment = new regiment_table(name_text,0,time_text,order_text,addr_text,path,url,secor_text,sec_order_text,intro_text,"");
		now_regiment.save(Ac_AddRegiment.this, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				//Save_Regiment(path);
				toast("舞团创建失败");
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				toast("舞团创建成功");
				regiment_id = now_regiment.getObjectId();
				Save_Member(path,url);
			}
			
		});
	}
	
	/**
	 * @author nieyihe
	 * @作用：同时创建管理员用户
	 * */
	public void Save_Member(String path,String url)
	{
		//BmobFile head,String name,String regiment_id , String sex,String host,String power,String password
		final regiment_member order_member = new regiment_member(path,url,"管理员",regiment_id,"",host_text,"order",password_text,name_text,0);
		order_member.save(Ac_AddRegiment.this, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				NotificationLocalFactory.UpdateDismissSave(notification,0);
				toast("管理员创建成功");
				member_id = order_member.getObjectId();
				Intent start = new Intent();
				start.setClass(Ac_AddRegiment.this, Act_MyRigment.class);
				Bundle b = new Bundle();
				b.putString("regiment_id", regiment_id);
				b.putString("member_id", member_id);
				b.putString("member_power","order");
				start.putExtras(b);
				Ac_AddRegiment.this.startActivity(start);
				finish();
			}
			
		});
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
            
            	toast("图片太大,请重新设置");
            	ImagePath = "";
            	return ;
            }
            img.setImageBitmap(bitmap);
		}
	}
	
	/**
	 * @author nieyihe
	 * @作用：显示提示
	 * */
	public void toast(String text)
	{
		android.widget.Toast.makeText(Ac_AddRegiment.this, text , android.widget.Toast.LENGTH_LONG).show();
	}
	
}
