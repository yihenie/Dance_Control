package com.example.dance_control.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dance_control.R;

public class Ac_Show_Movie extends Activity {

	private String url;

	@Override
	public void onCreate(Bundle s) {
		super.onCreate(s);
		this.setContentView(R.layout.show_movie);
		this.Get_Bundle();
		this.Ini_Show();
	}

	/**
	 * @author nieyihe
	 * @作用：获取上一个activity发送过来的数据
	 * */
	private void Get_Bundle() {
		Bundle b = this.getIntent().getExtras();
		this.url = b.getString("Url");
	}

	public void Ini_Show() {

		if (checkFlash() && checkUc()) {

			Intent i = new Intent();
			i.setAction("android.intent.action.VIEW");
			
			Uri uri = Uri.parse(url);
			i.setData(uri);
			i.setClassName("com.UCMobile", "com.UCMobile.main.UCMobile");
			this.startActivity(i);
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
		Toast.makeText(Ac_Show_Movie.this, "没有加载插件,请下载Flash插件",
				Toast.LENGTH_LONG).show();
		return false;
	}
	
	private boolean checkUc(){
		PackageManager pm = getPackageManager();
		List<PackageInfo> infoList = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo info : infoList) {

			if ("com.UCMobile".equals(info.packageName)) {

				return true;
			}
		}
		Toast.makeText(Ac_Show_Movie.this, "播放视频,请下载UC浏览器",
				Toast.LENGTH_LONG).show();
		return false;
	}

}
