package com.njit.texter;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.njit.texter.Post;

public class AllPostAdapter extends ParseQueryAdapter<Post> {

	public AllPostAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Post>() {
			public ParseQuery<Post> create() {
				ParseQuery<ParseUser> innerQuery = ParseQuery
						.getQuery("follow");
				innerQuery.whereEqualTo("follower", ParseUser.getCurrentUser());
				ParseQuery<Post> query = new ParseQuery<Post>("Post");
				query.whereMatchesKeyInQuery("author", "target", innerQuery);
				query.orderByDescending("createdAt");
				return query;
			}
		});
	}

	@Override
	public View getItemView(final Post post, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.post_list, null);
		}

		super.getItemView(post, v, parent);

		ParseImageView postImage = (ParseImageView) v.findViewById(R.id.photo);
		postImage.setAdjustViewBounds(true);
		ParseFile photoFile = post.getParseFile("photo");
		if (photoFile != null) {
			postImage.setParseFile(photoFile);
			postImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		ParseImageView profileImage = (ParseImageView) v
				.findViewById(R.id.profilephoto);
		ParseFile photoFile2 = null;
		try {
			photoFile2 = post.getAuthor().fetchIfNeeded()
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

		final TextView likeitTextView = (TextView) v
				.findViewById(R.id.likeitnum);

		final ImageButton likebtn = (ImageButton) v.findViewById(R.id.likeit);
		likebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final int i = post.getlikenum() + 1;
				post.setlikenum(i);
				post.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						likeitTextView.setText(i + "");
						likebtn.setEnabled(false);
					}
				});
			}
		});

		ImageButton combtn = (ImageButton) v.findViewById(R.id.comment);
		combtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(),
						CommentListActivity.class);
				CommentListActivity.setPost(post);
				getContext().startActivity(intent);
			}
		});

		TextView titleTextView = (TextView) v.findViewById(R.id.txttitle);
		titleTextView.setText(post.getTitle());

		TextView timeTextView = (TextView) v.findViewById(R.id.txttime);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		String str = sdf.format(post.getCreatedAt());
		timeTextView.setText(str);

		TextView commentTextView = (TextView) v.findViewById(R.id.commentnum);
		commentTextView.setText(post.getcommentnum() + "");

		likeitTextView.setText(post.getlikenum() + "");

		TextView titleTextView2 = (TextView) v.findViewById(R.id.txtnick);
		try {
			titleTextView2.setText(post.getAuthor().fetchIfNeeded()
					.getString("nickname")
					+ " : ");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;
	}

}
