package com.example.dance_control.basefragment;

import com.lxh.slidingmenu.lib.SlidingMenu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class Base_Fragment extends Fragment{

	public SlidingMenu menu;

	public Context context;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.context = this.getActivity();
	}
}
