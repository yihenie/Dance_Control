package com.example.dance_control.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;

import com.bmob.BmobProFile;

public class BitmapLocalFactory {

	public static final int BIG = 1;
	public static final int NORMAL = 2;
	public static final int SMALL = 3;
	
	public static Bitmap CreateBitmap(Context context,String pathstring,String urlstring,int model) throws IOException
	{
		if(pathstring.equals("") || pathstring == null || urlstring.equals("") || urlstring == null)
			return null;
		
		String Urlstring = BmobProFile.getInstance(context)
				.signURL(pathstring
						,urlstring
						,"4e304c876861bedb6bf43d8be675775a"
						,0
						,null);
		URL url = new URL(Urlstring);
		
		HttpURLConnection  httpConnection = (HttpURLConnection ) url.openConnection();
		httpConnection.setConnectTimeout(0);
		httpConnection.setDoInput(true);
		httpConnection.connect();
		
		InputStream in = httpConnection.getInputStream();
		Bitmap bitmap = null;
		bitmap = android.graphics.BitmapFactory.decodeStream(in);
		in.close();
		bitmap = ScaleBitmap(bitmap , model);
		return bitmap;
	}
	
	/**
	 * @author nieyihe
	 * 获取缩放的图片
	 * */
	public static Bitmap DecodeFile(String path)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();//这个选项用于设置返回的bitmap的
		options.inJustDecodeBounds = true;
		//当设置为true时,就可以不返回真实的bitmap
		//只是返回这个bitmap的必要的信息部分
		//当前返回的bitmap不可以显示
		Bitmap bitmap = BitmapFactory.decodeFile(path , options);
		
		float realWidth = options.outWidth;
		float realHeight = options.outHeight;
		
		int scale = (int)((realHeight > realWidth ? realHeight:realWidth) / 100);
		if(scale <= 0)
		{
			scale = 1;
		}
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		//此处设置为了获取真实的bitmap
		//并且将此bitmap按比例缩放
		bitmap = BitmapFactory.decodeFile(path, options);

		return bitmap;
		
	}
	/**
	 * @author nieyihe
	 * 缩放图片
	 * */
	public static Bitmap ScaleBitmap(Bitmap bitmap,int model)
	{
		Matrix m = new Matrix();
		float viewheight = 0 ;
		float viewwidth = 0;
		if(model == BitmapLocalFactory.SMALL)
		{
			viewheight = 110;
			viewwidth = 110;
		}
		else if(model == BitmapLocalFactory.NORMAL)
		{
			viewheight = 520;
			viewwidth  = 520;
		}
		else if(model == BitmapLocalFactory.BIG)
		{
			viewheight = 1024;
			viewwidth  = 1024;
		}
		
		float y = viewheight / (float)bitmap.getHeight();
		float x = viewwidth  / (float)bitmap.getWidth();
		m.postScale(x, y);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
		
		return newBitmap;
		
	}
}
