package cn.com.bluemoon.delivery.sz.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyuehua on 16/8/22.
 */
public class FragmentPagerAdapters extends FragmentPagerAdapter {

	private List<Fragment> list = new ArrayList<Fragment>();

	public FragmentPagerAdapters(FragmentManager fm) {
		super(fm);
	}

	public FragmentPagerAdapters(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.list = list;
	}


	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}
