package com.njit.texter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class AllUserAdapter extends ParseQueryAdapter<ParseUser> {

	public AllUserAdapter(Context context, Class<ParseUser> clazz) {
		super(context, clazz);
	}

	public AllUserAdapter(Context context, QueryFactory<ParseUser> factory) {
		super(context, factory);
	}

	@Override
	public View getItemView(final ParseUser user, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.user_list_view, null);
		}

		super.getItemView(user, v, parent);

		ParseImageView profileImage = (ParseImageView) v
				.findViewById(R.id.profilephoto);
		ParseFile photoFile2 = null;
		try {
			photoFile2 = user.fetchIfNeeded().getParseFile("profilephoto");
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

		TextView titleTextView2 = (TextView) v.findViewById(R.id.txtnick);
		try {
			titleTextView2.setText(user.fetchIfNeeded().getString("nickname"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;
	}

}
