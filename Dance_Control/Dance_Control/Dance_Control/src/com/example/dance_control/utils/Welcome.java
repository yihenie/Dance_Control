package com.example.dance_control.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.dance_control.R;

public class Welcome extends Activity {

	private Animation animation;
	private ImageView img;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.welcome);
		img = (ImageView) this.findViewById(R.id.welcome_img);
	}
	
	 @Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus)
		{
			animation = AnimationUtils.loadAnimation(Welcome.this, R.anim.welcome_loading_anim);
			img.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent();
					i.setClass(Welcome.this, Act_Show.class);
					Welcome.this.startActivity(i);
					finish();
				}
			});
		}
		 
		super.onWindowFocusChanged(hasFocus);
	}
	
}

