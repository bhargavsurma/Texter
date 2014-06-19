package com.njit.texter;

import java.text.SimpleDateFormat;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CommentListActivity extends ListActivity {

	private AllCommentAdapter mycommentadapter;
	static Post parentpost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			parentpost.fetchIfNeeded();
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		setContentView(R.layout.comment_list);
		getListView().setClickable(false);
		ParseImageView postImage = (ParseImageView) findViewById(R.id.photo);
		postImage.setAdjustViewBounds(true);
		ParseFile photoFile = parentpost.getParseFile("photo");
		if (photoFile != null) {
			postImage.setParseFile(photoFile);
			postImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		ParseImageView profileImage = (ParseImageView) findViewById(R.id.profilephoto);
		ParseFile photoFile2 = null;
		try {
			photoFile2 = parentpost.getAuthor().fetchIfNeeded()
					.getParseFile("profilephoto");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (photoFile2 != null) {
			profileImage.setParseFile(photoFile2);
			profileImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		TextView titleTextView = (TextView) findViewById(R.id.txttitle);
		titleTextView.setText(parentpost.getTitle());

		TextView timeTextView = (TextView) findViewById(R.id.txttime);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		String str = sdf.format(parentpost.getCreatedAt());
		timeTextView.setText(str);

		final EditText commenttxt = (EditText) findViewById(R.id.comment);

		TextView titleTextView2 = (TextView) findViewById(R.id.txtnick);
		try {
			titleTextView2.setText(parentpost.getAuthor().fetchIfNeeded()
					.getString("nickname")
					+ " : ");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Button NewcommentBtn = (Button) findViewById(R.id.NewCommentBtn);
		NewcommentBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Comment comment = new Comment();
				comment.setTitle(commenttxt.getText().toString());
				comment.setPost(parentpost);
				comment.setAuthor(ParseUser.getCurrentUser());
				comment.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							parentpost.increment("commentnum",1);
							parentpost.saveInBackground(new SaveCallback() {
								@Override
								public void done(ParseException e) {
									updateCommentList();									
								}								
							});
						} else {
							Toast.makeText(getApplicationContext(),
									"Error saving: " + e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
			}
		});

		mycommentadapter = new AllCommentAdapter(this);
		setListAdapter(mycommentadapter);
	}

	private void updateCommentList() {
		mycommentadapter.clear();
		mycommentadapter.loadObjects();
		setListAdapter(mycommentadapter);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// If a new post has been added, update
			// the list of posts
			updateCommentList();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateCommentList();
	}

	static void setPost(Post post) {
		parentpost = post;
	}

	static Post getPost() {
		return parentpost;
	}
}
