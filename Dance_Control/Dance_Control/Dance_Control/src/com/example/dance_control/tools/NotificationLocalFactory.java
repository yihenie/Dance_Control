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
	 * ����Notification
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
	 * ����View�ľ�������
	 * */
	public static void SetViewText(RemoteViews view,String word,int ViewId,Notification noti,int CancelId)
	{
		view.setTextViewText(ViewId, word);
		manager.notify(CancelId,noti);
	}
	
	/**
	 * @author nieyihe
	 * ����View�ľ�������
	 * */
	public static void SetViewImg(RemoteViews view ,int ImgId,int ViewId,Notification noti,int CancelId)
	{
		
		Bitmap bitmap = BitmapFactory.decodeResource(NotificationLocalFactory.context.getResources(), ImgId);
		view.setImageViewBitmap(ViewId, bitmap);
		manager.notify(CancelId,noti);
	}
	
	/**
	 * @author nieyihe
	 * ȡ��RemoteView
	 * */
	public static void Cancel(int Id)
	{
		if(manager != null)
			manager.cancel(Id);
	}	
	
	/**
	 * @author nieyihe
	 * ��ʾ�ύ����
	 * */
	public static Notification UpdateShowSave(Context context,int CancelId)
	{
		 Notification notification = NotificationLocalFactory.NotificationViewCreate(context, R.layout.notification_message, R.drawable.input, CancelId);
		 NotificationLocalFactory.SetViewText(notification.contentView, "С�����ڽ��������ϰ��˵��ֿ�...", R.id.notification_word, notification, CancelId);
		 return notification;
	}
	
	/**
	 * @author nieyihe
	 * ��ʾ�ύ�������
	 * */
	public static void UpdateDismissSave(Notification notification,int CancelId)
	{
		NotificationLocalFactory.SetViewText(notification.contentView, "�����������", R.id.notification_word, notification, CancelId);
		NotificationLocalFactory.Cancel(CancelId);
	}
}
