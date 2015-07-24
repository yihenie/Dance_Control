package com.example.dance_control.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.htmlparser.util.ParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.dance_control.R;
import com.example.dance_control.tools.Html_Par;

@SuppressLint("SetJavaScriptEnabled")
public class Ac_Show_Shuffle extends Activity {

	private String url;
	private String video;
	private ProgressDialog pd;
	private MyHandler handle = new MyHandler();

	private void Get_Bundle() {
		Bundle b = this.getIntent().getExtras();
		this.url = b.getString("url");
	}

	public void onCreate(Bundle b) {
		super.onCreate(b);
		this.setContentView(R.layout.content_show_shufflemovie);
		this.Get_Bundle();
		try {
			this.show();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void show() throws ParserException, MalformedURLException,
			IOException {
		pd = ProgressDialog.show(Ac_Show_Shuffle.this, null, "���ڽ�����Ƶ��..");
		new MyThread().start();

	}

	public class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();

			if (b.containsKey("Over")) {
				if (video.equals("")) {
					Toast.makeText(Ac_Show_Shuffle.this,
							"����Ƶ���ɲ��Ż�������������,����������Ƶ�ۿ�", Toast.LENGTH_LONG)
							.show();

				} else {

					if (checkFlash() && checkUc()) {
						Intent i = new Intent();
						i.setAction("android.intent.action.VIEW");

						Uri uri = Uri.parse(video);
						i.setData(uri);
						i.setClassName("com.UCMobile",
								"com.UCMobile.main.UCMobile");
						Ac_Show_Shuffle.this.startActivity(i);
						finish();
					}
				}
				pd.dismiss();

			}

		}

	}

	private class MyThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			try {
				video = new Html_Par().Praser_Movie(url);
				Log.d("video", video);
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Message msg = new Message();
			Bundle b = new Bundle();
			b.putString("Over", "");
			msg.setData(b);
			handle.sendMessage(msg);
		}

	}

	private boolean checkFlash() {
		PackageManager pm = getPackageManager();
		List<PackageInfo> infoList = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo info : infoList) {

			if ("com.adobe.flashplayer".equals(info.packageName)) {

				return true;
			}
		}
		Toast.makeText(this, "û�м��ز��,������Flash���", Toast.LENGTH_LONG).show();
		return false;
	}

	private boolean checkUc() {
		PackageManager pm = getPackageManager();
		List<PackageInfo> infoList = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo info : infoList) {

			if ("com.UCMobile".equals(info.packageName)) {

				return true;
			}
		}
		Toast.makeText(this, "������Ƶ,������UC�����", Toast.LENGTH_LONG).show();
		return false;
	}
}
