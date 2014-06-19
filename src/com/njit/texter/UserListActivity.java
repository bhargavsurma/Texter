package com.njit.texter;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserListActivity extends ListActivity {

	private AllUserAdapter useradapter;
	private EditText search;
	private Button searchbtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);
		getListView().setClickable(true);
		search = (EditText) findViewById(R.id.search);
		searchbtn = (Button) findViewById(R.id.searchbtn);

		useradapter = new AllUserAdapter(this, ParseUser.class);

		setListAdapter(useradapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				getListView().setClickable(false);
				ParseQuery<ParseObject> query = ParseQuery.getQuery("follow");
				ParseUser seluser = (ParseUser) getListView()
						.getItemAtPosition(position);
				query.whereEqualTo("target", seluser);
				query.whereEqualTo("follower", ParseUser.getCurrentUser());
				if (seluser.getString("username").equals(
						ParseUser.getCurrentUser().getString("username"))) {
					Toast.makeText(getApplicationContext(),
							"You can't follow or unfollowed yourself!",
							Toast.LENGTH_LONG).show();
				} else {
					try {
						query.getFirst();
						showbox(0, query.getFirst());
					} catch (ParseException e1) {
						showbox(1, seluser);
					}
				}
				getListView().setClickable(true);
			}
		});

		searchbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				if ((search.getText().toString()).equals("")) {
					useradapter = new AllUserAdapter(getApplicationContext(),
							ParseUser.class);
					updateUserList();
				} else {
					newUserList();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			updateUserList();
		}
	}

	private void updateUserList() {
		useradapter.clear();
		useradapter.loadObjects();
		setListAdapter(useradapter);
	}

	private void showbox(int flag, final ParseObject obj) {
		if (flag == 1) {
			Dialog alertDialog = new AlertDialog.Builder(this)
					.setTitle("You haven't followed this user yet")
					.setMessage("Are you sure you want to follow this user?")
					.setIcon(R.drawable.ic_launcher)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										final DialogInterface dialog, int which) {

									ParseObject newfollow = new ParseObject(
											"follow");
									newfollow.put("target", (ParseUser) obj);
									newfollow.put("follower",
											ParseUser.getCurrentUser());
									newfollow
											.saveInBackground(new SaveCallback() {
												@Override
												public void done(
														ParseException e) {
													dialog.cancel();
													Toast.makeText(
															getApplicationContext(),
															"You successfully followed this user!",
															Toast.LENGTH_LONG)
															.show();
												}
											});
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							}).create();
			alertDialog.show();
		} else {
			Dialog alertDialog = new AlertDialog.Builder(this)
					.setTitle("You already followed this user")
					.setMessage("Are you sure you want to unfollow this user?")
					.setIcon(R.drawable.ic_launcher)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										final DialogInterface dialog, int which) {
									obj.deleteInBackground(new DeleteCallback() {
										@Override
										public void done(ParseException e) {
											// TODO Auto-generated method stub
											dialog.cancel();
											Toast.makeText(
													getApplicationContext(),
													"You successfully unfollowed this user!",
													Toast.LENGTH_LONG).show();
										}
									});
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							}).create();
			alertDialog.show();
		}
	}

	private void newUserList() {
		ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>() {
			public ParseQuery<ParseUser> create() {
				ParseQuery<ParseUser> query = new ParseQuery<ParseUser>("_User");
				query.whereEqualTo("nickname", search.getText().toString());
				return query;
			}
		};
		useradapter = new AllUserAdapter(this, factory);
		updateUserList();
	}
}
