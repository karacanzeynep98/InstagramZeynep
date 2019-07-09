package com.example.instazeynep;

import android.app.Application;

import com.example.instazeynep.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //custom parse model we created when accessing parses & models
        ParseObject.registerSubclass(Post.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("insta_love")
                .clientKey("this_the_key")
                .server("http://zeynep-fbu-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }


}
