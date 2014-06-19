package com.njit.texter;

import com.njit.texter.Post;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Application;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(Post.class);
		ParseObject.registerSubclass(Comment.class);

		// Add your initialization code here
		Parse.initialize(this, "ng7gtUL1VhVe2VnfUarTrGn22kvEBRvhKkJx79Bu",
				"cLG0NdKguUUC0tszpMYmu83Xsb7N9cAYh4KroPp0");

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);		

		ParseACL.setDefaultACL(defaultACL, true);
	}

}
