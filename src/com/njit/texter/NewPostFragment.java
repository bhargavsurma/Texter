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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewPostFragment extends Fragment {

	private ImageButton photoButton;
	private Button saveButton;
	private Button galleryButton;
	private TextView posttitle;
	private ParseImageView photoPreview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_new_post, parent, false);

		posttitle = ((EditText) v.findViewById(R.id.post_name));

		photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(posttitle.getWindowToken(), 0);
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
				Post post = ((NewPostActivity) getActivity()).getCurrentPost();

				post.setTitle(posttitle.getText().toString());

				post.setlikenum(0);
				post.setcommentnum(0);

				post.setAuthor(ParseUser.getCurrentUser());

				post.saveInBackground(new SaveCallback() {

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

		photoPreview = (ParseImageView) v.findViewById(R.id.post_preview_image);
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
			((NewPostActivity) getActivity()).getCurrentPost().put("photo",photoFile);
		}
	}

	public void startCamera() {
		Fragment cameraFragment = new CameraFragment();
		((CameraFragment) cameraFragment).setParent("newpost");
		FragmentTransaction transaction = getActivity().getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragmentContainer, cameraFragment);
		transaction.addToBackStack("NewPostFragment");
		transaction.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		ParseFile photoFile = ((NewPostActivity) getActivity())
				.getCurrentPost().getPhotoFile();
		if (photoFile != null) {
			photoPreview.setAdjustViewBounds(true);
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
