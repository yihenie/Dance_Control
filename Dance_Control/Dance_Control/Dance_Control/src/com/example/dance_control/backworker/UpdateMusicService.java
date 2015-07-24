package com.example.dance_control.backworker;

import cn.bmob.v3.listener.SaveListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.example.dance_control.R;
import com.example.dance_control.activity.GainAllMusic;
import com.example.dance_control.bmob_table.regiment_music;
import com.example.dance_control.tools.NotificationLocalFactory;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class UpdateMusicService extends Service {

	private String member_id;
	private String regiment_id;
	private String[] files;
	private String[] names;
	private Notification noti;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		GainBundle(intent);
		UpdateImgFiles();
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * @author nieyihe
	 * 接受数据
	 * */
	public void GainBundle(Intent i)
	{
		Bundle b = i.getExtras();
		member_id = b.getString("member_id");
		regiment_id = b.getString("regiment_id");
		files = b.getStringArray("files");
		names = b.getStringArray("names");
	}
	
	public void UpdateImgFiles()
	{
		noti = NotificationLocalFactory.NotificationViewCreate(UpdateMusicService.this, R.layout.notification_message,R.drawable.input, 0);
		NotificationLocalFactory.SetViewImg(noti.contentView, R.drawable.input, R.id.notification_img, noti, 0);
		BmobProFile.getInstance(UpdateMusicService.this).uploadBatch(files, new UploadBatchListener() {
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(boolean isFinish, String[] FileName, String[] FileUrls) {
				// TODO Auto-generated method stub
				if(isFinish) 
					{
						Toast.makeText(UpdateMusicService.this, "完成上传", Toast.LENGTH_SHORT).show();
						NotificationLocalFactory.Cancel(0);
						for(int i = 0 ;i < FileName.length; i++)
						{
							String filename = FileName[i];
							String fileurl = FileUrls[i];

							SavePathUrl(names[i],filename,fileurl);
						}
						
						UpdateMusicService.this.stopSelf();
					}
				
				
			}
			
			@Override
			public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
				// TODO Auto-generated method stub
				NotificationLocalFactory.SetViewText(noti.contentView, "正在上传第"+curIndex+"首曲子"+"\n当前曲子完成 "+curPercent+"%"+"\n即将完成进度："+totalPercent+"%", R.id.notification_word,noti,0);
				
			} 
		});
	}
	
	/**
	 * @author nieyihe
	 * 保存路径
	 * */
	public void SavePathUrl(final String name,String filename,String fileurl)
	{
		if(filename == null || fileurl == null ) 
				
				return ;
		regiment_music mus = new regiment_music(name,member_id,regiment_id,0,filename,fileurl);
		
		mus.save(UpdateMusicService.this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(UpdateMusicService.this, name+"完成上传", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
}
