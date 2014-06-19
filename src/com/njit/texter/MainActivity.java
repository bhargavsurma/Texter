package com.njit.texter;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

public class MainActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.setTimeZone("America/New_York");

		if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {

			Intent intent = new Intent(MainActivity.this,
					LoginSignupActivity.class);
			startActivity(intent);
			finish();
		} else {

			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {

				Intent intent = new Intent(MainActivity.this,
						PostListActivity.class);
				startActivity(intent);
				finish();
			} else {

				Intent intent = new Intent(MainActivity.this,
						LoginSignupActivity.class);
				startActivity(intent);
				finish();
			}
		}

	}
}
