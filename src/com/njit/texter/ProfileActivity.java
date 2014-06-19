package com.njit.texter;

import com.parse.ParseUser;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ProfileActivity extends Activity {

	private ParseUser curuser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		curuser = ParseUser.getCurrentUser();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_new_post);
		FragmentManager manager = getFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new ProfileFragment();
			manager.beginTransaction().add(R.id.fragmentContainer, fragment)
					.commit();
		}
	}

	public ParseUser getCuruser() {
		return curuser;
	}

}
