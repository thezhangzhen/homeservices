package com.example.homeservice;


import com.example.homeservice.base.BaseFragment;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.view.Menu;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initTabs();
	}

	private void initTabs() {

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		String[] titleStrings = getResources().getStringArray(R.array.typeArray);
		BaseFragment[] fragments = initFragments(titleStrings.length);
		for (int i = 0; i < titleStrings.length; i++) {
			Tab tab = actionBar.newTab().setText(titleStrings[i]);
			tab.setTabListener(new MyTabListener(fragments[i]));
			actionBar.addTab(tab);
		}
	}

	private BaseFragment[] initFragments(int count) {
		BaseFragment[] fragments = new BaseFragment[count];
		fragments[0] = new SettingFragment();
		fragments[1] = new SettingFragment();
		fragments[2] = new SettingFragment();
		fragments[3] = new SettingFragment();
		fragments[4] = new SettingFragment();
		fragments[5] = new SettingFragment();
		return fragments;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
