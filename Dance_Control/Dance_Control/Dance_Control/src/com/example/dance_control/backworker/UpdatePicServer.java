package com.example.dance_control.backworker;

import cn.bmob.v3.listener.SaveListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_ShowAllPic;
import com.example.dance_control.bmob_table.regiment_audio;
import com.example.dance_control.tools.NotificationLocalFactory;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class UpdatePicServer extends Service {

	private String member_idString;
	private String regiment_idString;
	private String[] files;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	// TODO Auto-generated method stub
	GainBundle(intent);
	if(files!= null)
		Upload_Img(files);
	
	return super.onStartCommand(intent, flags, startId);
	}	
	
	/**
	 * @author nieyihe
	 * ��������
	 * */
	public void GainBundle(Intent i)
	{
		Bundle b = i.getExtras();
		member_idString = b.getString("member_id");
		regiment_idString = b.getString("regiment_id");
		files = b.getStringArray("files");
	}
	
	/**
	 * @author nieyihe
	 * �ύͼƬ��bmob������
	 * */
	@SuppressWarnings("deprecation")
	public void Upload_Img(String[] files_path)
	{
		final Notification notification = NotificationLocalFactory.NotificationViewCreate(this, R.layout.notification_message,R.drawable.input, 0);
		NotificationLocalFactory.SetViewImg(notification.contentView, R.drawable.input, R.id.notification_img, notification, 0);
		
		BmobProFile.getInstance(this).uploadBatch(files_path,new UploadBatchListener() {
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(boolean isFinish, String[] paths, String[] urls) {
				// TODO Auto-generated method stub
				
				if(isFinish)
				{
					Toast.makeText(UpdatePicServer.this, "����ϴ�", Toast.LENGTH_SHORT).show();
					NotificationLocalFactory.Cancel(0);
					for(int UpdatePos = 0 ; UpdatePos < paths.length ; UpdatePos++ )
					{
						Upload_Regiment_Picture(paths[UpdatePos],urls[UpdatePos]);
					}
					
					UpdatePicServer.this.stopSelf();
				}
			
			}
			
			@Override
			public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
				// TODO Auto-generated method stub
				NotificationLocalFactory.SetViewText(notification.contentView, "�����ϴ���"+curIndex+"����Ƭ"+"\n��ǰ��Ƭ��� "+curPercent+"%"+"\n������ɽ��ȣ�"+totalPercent+"%", R.id.notification_word,notification,0);
			}
		});
		
	}
	
	/**
	 * @author nieyihe
	 * ��ͼƬ�ϴ�����Ӧ��bmob���ݿ���
	 * */
	private void Upload_Regiment_Picture(String path,String url)
	{
		regiment_audio pic = new regiment_audio();
		pic.img_path = path ;
		pic.img_url  = url;
		pic.member_id = member_idString;
		pic.praise_num = 0;
		pic.type = "Pic";
		pic.intro = "";
		pic.url = "";
		pic.regiment_id = regiment_idString;
		pic.save(this, new SaveListener()
		{

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
			
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(UpdatePicServer.this, "����ϴ�", Toast.LENGTH_SHORT).show();

			}
			
		});
	}
}
