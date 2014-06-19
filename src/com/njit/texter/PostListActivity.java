package com.njit.texter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.njit.texter.AllPostAdapter;
import com.parse.ParseUser;

public class PostListActivity extends ListActivity {

	private AllPostAdapter mypostadapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setClickable(false);
		mypostadapter = new AllPostAdapter(this);
		setListAdapter(mypostadapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_post_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_refresh: {
			updatePostList();
			break;
		}

		case R.id.action_profile: {
			showFavorites();
			break;
		}

		case R.id.action_new: {
			newPost();
			break;
		}

		case R.id.action_search: {
			newSearch();
			break;
		}
		case R.id.action_logout: {
			logout();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	private void updatePostList() {
		mypostadapter.clear();
		mypostadapter.loadObjects();
		setListAdapter(mypostadapter);
	}

	private void showFavorites() {
		Intent i = new Intent(this, ProfileActivity.class);
		startActivityForResult(i, 0);
	}

	private void newPost() {
		Intent i = new Intent(this, NewPostActivity.class);
		startActivityForResult(i, 0);
	}

	private void newSearch() {
		Intent i = new Intent(this, UserListActivity.class);
		startActivityForResult(i, 0);
	}

	private void logout() {
		ParseUser.logOut();
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// If a new post has been added, update
			// the list of posts
			updatePostList();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updatePostList();
	}
}
