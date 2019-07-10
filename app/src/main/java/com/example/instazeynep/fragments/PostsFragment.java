package com.example.instazeynep.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instazeynep.PostsAdapter;
import com.example.instazeynep.R;
import com.example.instazeynep.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

    private SwipeRefreshLayout swipeContainer;

    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> mPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    //The handle to the view that has been created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPosts = view.findViewById(R.id.rvPosts);

        //create the data source ==> list of different post objects
        mPosts = new ArrayList<>();
        //create the adapter ==> how to show the contents from the view
        adapter = new PostsAdapter(getContext(), mPosts);
        //set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        //set the layout manager on the recycler view ==> how you layout your contents onto the screen
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTopPosts();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void fetchTimelineAsync(int page) {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.setLimit(20);
        postsQuery.include(Post.KEY_USER);
        postsQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {

                if (e == null) {
                    adapter.clear();
                    adapter.addAll(objects);
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);

                    for (int i = 0; i < objects.size(); ++i) {
                        Log.d("PostsFragment", "Post[" + i + "] = " + objects.get(i).getDescription()
                                + "\nusername =" + objects.get(i).getUser().getUsername());
                    }
                } else {
                    Log.e("PostsFragment", "Error with query");
                    e.printStackTrace();
                }
            }
        });
    }


    private void loadTopPosts() { //or QueryPosts()
        final Post.Query postsQuery = new Post.Query();
        postsQuery.setLimit(20);
        postsQuery.include(Post.KEY_USER);
        postsQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {

                if (e == null) {
                    mPosts.addAll(objects);
                    adapter.notifyDataSetChanged();

                    for (int i = 0; i < objects.size(); ++i) {
                        Log.d("PostsFragment", "Post[" + i + "] = " + objects.get(i).getDescription()
                                + "\nusername =" + objects.get(i).getUser().getUsername());
                    }
                } else {
                    Log.e("PostsFragment", "Error with query");
                    e.printStackTrace();
                }
            }
        });
    }
}
