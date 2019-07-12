package com.example.instazeynep;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.net.ParseException;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instazeynep.models.Post;
import com.parse.ParseFile;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    int whichFragment; //tells us whether we're coming form timeline or profile fragment

    public PostsAdapter(Context context, List<Post> posts, int whichFragment) {
        this.context = context;
        this.posts = posts;
        this.whichFragment=whichFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override//TODO figure out how to pass the data between two chunks
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        /*if(whichFragment == 0){

        }else if (whichFragment == 1) {

        }*/
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> newposts) {
        posts.addAll(newposts);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvHandle;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvTime;
        private ImageView imageUser;
        private ImageView likeImage;
        private TextView tvNumLikes;
        private ImageView commentImage;
        private ImageView directMessage;
        private ImageView saveImage;
        private ImageView ivDetailsButton;

        public ViewHolder(View itemView) {
            super(itemView);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvCreationTime);
            imageUser = itemView.findViewById(R.id.ivUserImage);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            likeImage = itemView.findViewById(R.id.ivLikeButton);
            commentImage = itemView.findViewById(R.id.ivCommentImage);
            directMessage = itemView.findViewById(R.id.ivDirectMessage);
            saveImage = itemView.findViewById(R.id.ivSaveButton);
            ivDetailsButton = itemView.findViewById(R.id.ivDetailsButton);

            itemView.setOnClickListener(this);
        }

        public void bind (final Post post){

            if(whichFragment == 0) {
                tvHandle.setText(post.getUser().getUsername());
                ParseFile image = post.getImage();
                if (image != null) {
                    Glide.with(context).load(image.getUrl()).into(ivImage);
                }
                tvDescription.setText(post.getDescription());

                String time = post.getCreatedAt().toString();
                String timeshort = time.substring(0, 10);
                tvTime.setText(timeshort);

                if (post.getUser().getParseFile("profileImage") != null) {
                    Glide.with(context).load(post.getUser().getParseFile("profileImage").getUrl())
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageUser);
                } else {
                    imageUser.setImageResource(R.drawable.instagram_user_outline_24);
                }

                if (post.isLiked()) {
                    likeImage.setImageResource(R.drawable.ufi_heart_active);
                    likeImage.setColorFilter(Color.argb(255, 255, 0, 0));
                } else {
                    likeImage.setImageResource(R.drawable.ufi_heart);
                    likeImage.setColorFilter(Color.argb(255, 0, 0, 0));
                }

                tvNumLikes.setText(Integer.toString(post.getNumLikes()));

                likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!post.isLiked()) {
                            //like
                            post.like();
                            likeImage.setImageResource(R.drawable.ufi_heart_active);
                            likeImage.setColorFilter(Color.argb(255, 255, 0, 0));
                        } else {
                            //un-like
                            post.unlike();
                            likeImage.setImageResource(R.drawable.ufi_heart);
                            likeImage.setColorFilter(Color.argb(255, 0, 0, 0));
                        }

                        post.saveInBackground();
                        tvNumLikes.setText(Integer.toString(post.getNumLikes()));
                    }
                });

            } else {
                tvHandle.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                tvNumLikes.setVisibility(View.GONE);
                likeImage.setVisibility(View.GONE);
                commentImage.setVisibility(View.GONE);
                directMessage.setVisibility(View.GONE);
                saveImage.setVisibility(View.GONE);
                imageUser.setVisibility(View.GONE);
                ivDetailsButton.setVisibility(View.GONE);

                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                int pxWidth = displayMetrics.widthPixels;

                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(pxWidth/3, pxWidth/3);
                ivImage.setLayoutParams(layoutParams);

                ParseFile image = post.getImage();
                if (image != null) {
                    Glide.with(context).load(image.getUrl()).into(ivImage);
                }

                if (post.getUser().getParseFile("profileImage") != null) {
                    Glide.with(context).load(post.getUser().getParseFile("profileImage").getUrl())
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageUser);
                } else {
                    imageUser.setImageResource(R.drawable.instagram_user_outline_24);
                }
            }
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Post post = posts.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, PostDetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra("description_key", post.getDescription());
                intent.putExtra("image_key", post.getImage());
                intent.putExtra("user_key", post.getUser());
                intent.putExtra("createdAt_key",post.getCreatedAt().toString());

                // show the activity
                context.startActivity(intent);

            }
        }
    }
}
