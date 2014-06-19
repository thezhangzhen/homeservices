package com.example.homeservice;

import com.example.homeservice.base.BaseFragment;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class MyTabListener implements TabListener {

	private BaseFragment mFragment;

	public MyTabListener(BaseFragment fragment) {
		super();
		this.mFragment = fragment;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.add(R.id.container, mFragment);
		ft.show(mFragment);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(mFragment);
	}

}
