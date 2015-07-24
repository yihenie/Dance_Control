package com.example.dance_control.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;

import com.example.dance_control.R;
import com.example.dance_control.activity.Ac_AddInfo;

public class NotificationLocalFactory {

	static NotificationManager manager;
	static Context context;
	/**
	 * @author nieyihe
	 * 创建Notification
	 * */
	public static Notification NotificationViewCreate(Context context ,final int LayoutId,int IconId,final int CancelId)
	{
		NotificationLocalFactory.context = context;
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = IconId;
		notification.when = System.currentTimeMillis() + 1000;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		
		RemoteViews views = new RemoteViews(context.getPackageName(), LayoutId);
		notification.contentView = views;
		manager.notify(CancelId,notification);
		return notification;
	}
	
	/**
	 * @author nieyihe
	 * 设置View的具体内容
	 * */
	public static void SetViewText(RemoteViews view,String word,int ViewId,Notification noti,int CancelId)
	{
		view.setTextViewText(ViewId, word);
		manager.notify(CancelId,noti);
	}
	
	/**
	 * @author nieyihe
	 * 设置View的具体内容
	 * */
	public static void SetViewImg(RemoteViews view ,int ImgId,int ViewId,Notification noti,int CancelId)
	{
		
		Bitmap bitmap = BitmapFactory.decodeResource(NotificationLocalFactory.context.getResources(), ImgId);
		view.setImageViewBitmap(ViewId, bitmap);
		manager.notify(CancelId,noti);
	}
	
	/**
	 * @author nieyihe
	 * 取消RemoteView
	 * */
	public static void Cancel(int Id)
	{
		if(manager != null)
			manager.cancel(Id);
	}	
	
	/**
	 * @author nieyihe
	 * 显示提交数据
	 * */
	public static Notification UpdateShowSave(Context context,int CancelId)
	{
		 Notification notification = NotificationLocalFactory.NotificationViewCreate(context, R.layout.notification_message, R.drawable.input, CancelId);
		 NotificationLocalFactory.SetViewText(notification.contentView, "小控正在将您的资料搬运到仓库...", R.id.notification_word, notification, CancelId);
		 return notification;
	}
	
	/**
	 * @author nieyihe
	 * 显示提交数据完成
	 * */
	public static void UpdateDismissSave(Notification notification,int CancelId)
	{
		NotificationLocalFactory.SetViewText(notification.contentView, "搬运资料完成", R.id.notification_word, notification, CancelId);
		NotificationLocalFactory.Cancel(CancelId);
	}
}
