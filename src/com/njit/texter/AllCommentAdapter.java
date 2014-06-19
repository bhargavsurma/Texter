package com.njit.texter;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.njit.texter.Comment;

public class AllCommentAdapter extends ParseQueryAdapter<Comment> {

	public AllCommentAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Comment>() {
			public ParseQuery<Comment> create() {
				ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
				query.whereEqualTo("post", CommentListActivity.getPost());
				query.orderByDescending("createdAt");
				return query;
			}
		});
	}

	@Override
	public View getItemView(Comment comment, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.comment_list_view, null);
		}

		super.getItemView(comment, v, parent);

		ParseImageView profileImage = (ParseImageView) v
				.findViewById(R.id.profilephoto2);
		ParseFile photoFile2 = null;
		try {
			photoFile2 = comment.getAuthor().fetchIfNeeded()
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

		TextView titleTextView = (TextView) v.findViewById(R.id.txttitle2);
		titleTextView.setText(comment.getTitle());

		TextView timeTextView = (TextView) v.findViewById(R.id.txttime);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		String str = sdf.format(comment.getCreatedAt());
		timeTextView.setText(str);

		TextView titleTextView2 = (TextView) v.findViewById(R.id.txtnick2);
		try {
			titleTextView2.setText(comment.getAuthor().fetchIfNeeded()
					.getString("nickname")
					+ " : ");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;
	}

}
