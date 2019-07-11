package com.example.instazeynep;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PostDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        TextView caption_details = findViewById(R.id.tvDescription);
        ImageView image_details = findViewById(R.id.ivImage);
        TextView handle_details = findViewById(R.id.tvHandle);
        TextView time_stamp = findViewById(R.id.tvTimeStamp);


        Intent intent = getIntent();
        String caption = intent.getStringExtra("description_key");
        ParseFile image = intent.getParcelableExtra("image_key");
        ParseUser user = intent.getParcelableExtra("user_key");
        String createdAt = intent.getStringExtra("createdAt_key");

        //set variables
        caption_details.setText(caption);
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(image_details);
        }
        handle_details.setText(user.getUsername());
        time_stamp.setText(getRelativeTimeAgo(createdAt));


    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (android.net.ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }


}
