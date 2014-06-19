package com.njit.texter;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProfileFragment extends Fragment {

	private ImageButton photoButton;
	private Button saveButton;
	private Button galleryButton;
	private EditText nickname;
	private EditText dob;
	private ParseImageView photoPreview;
	private ParseUser user = ParseUser.getCurrentUser();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile, parent, false);

		nickname = ((EditText) v.findViewById(R.id.nickname));
		try {
			nickname.setText(user.fetchIfNeeded().getString("nickname"));
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		dob = ((EditText) v.findViewById(R.id.dob));
		try {
			dob.setText(user.fetchIfNeeded().getString("dob"));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(nickname.getWindowToken(), 0);
				startCamera();
			}
		});

		galleryButton = ((Button) v.findViewById(R.id.gallery_button));
		galleryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, 100);
			}
		});

		saveButton = ((Button) v.findViewById(R.id.save_button));
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				user.put("nickname", nickname.getText().toString());
				user.put("dob", dob.getText().toString());

				user.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							getActivity().setResult(Activity.RESULT_OK);
							getActivity().finish();
						} else {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Error saving: " + e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});

			}
		});

		// Until the user has taken a photo, hide the preview
		photoPreview = (ParseImageView) v.findViewById(R.id.profilephoto);
		photoPreview.setVisibility(View.INVISIBLE);

		return v;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getActivity();
		if (requestCode == 100 && resultCode == Activity.RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			Bitmap Image = BitmapFactory.decodeFile(picturePath);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			byte[] Data = bos.toByteArray();
			cursor.close();
			ParseFile photoFile = new ParseFile("photo.jpg", Data);
			ParseUser.getCurrentUser().put("profilephoto",photoFile);
		}
	}

	public void startCamera() {
		Fragment cameraFragment = new CameraFragment();
		((CameraFragment) cameraFragment).setParent("profile");
		FragmentTransaction transaction = getActivity().getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragmentContainer, cameraFragment);
		transaction.addToBackStack("ProfileFragment");
		transaction.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		ParseFile photoFile = null;
		try {
			photoFile = ParseUser.getCurrentUser().fetchIfNeeded()
					.getParseFile("profilephoto");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (photoFile != null) {
			photoPreview.setParseFile(photoFile);
			photoPreview.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					photoPreview.setVisibility(View.VISIBLE);
				}
			});
		}
	}

}
