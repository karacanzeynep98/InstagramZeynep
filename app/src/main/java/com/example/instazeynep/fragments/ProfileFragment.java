package com.example.instazeynep.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instazeynep.HomeActivity;
import com.example.instazeynep.MainActivity;
import com.example.instazeynep.PostsAdapter;
import com.example.instazeynep.R;
import com.example.instazeynep.models.BitmapScaler;
import com.example.instazeynep.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> mPosts;
    private ImageView myImage;
    private TextView tvHandle;
    private EditText etBio;

    private Button logOutButton;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    private String my_username = "zeycorder";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        myImage = view.findViewById(R.id.ivMyImage);
        logOutButton = (Button) view.findViewById(R.id.btnLogOut);
        tvHandle = (TextView) view.findViewById(R.id.tvHandle);
        tvHandle.setText(my_username);
        etBio = (EditText) view.findViewById(R.id.etBio);

        //TODO how to make this stay there? The cursor should go away
        String inputBio = etBio.getText().toString();
        etBio.setText(inputBio);

        logOutButton.setOnClickListener(this);


        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        return view;
    }

    @Override
    public void onClick(View view)
    {
        Log.d("ProfileFragment", "Logout successful!");
        ParseUser.logOut();
        final Intent intent = new Intent(getContext(), MainActivity.class);
        getActivity().startActivity(intent);
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ProfileFragment");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("ProfileFragment", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
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
    }

    protected void loadTopPosts(){
        final Post.Query postsQuery = new Post.Query();
        postsQuery.setLimit(20);
        postsQuery.include(Post.KEY_USER);
        postsQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postsQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 200);
                // Load the taken image into a preview
                myImage.setImageBitmap(takenImage);

                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedFile = getPhotoFileUri(photoFileName + "_resized");
                try {
                    resizedFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(resizedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // Write the bytes of the bitmap to file
                try {
                    fos.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
