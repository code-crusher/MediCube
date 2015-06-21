package com.appyware.medicube;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    String objectId, imgPath1, ImagePath, email;
    int j = 0;
    SharedPreferences preferences;
    EditText editText, docName;


    private static final int ACTION_TAKE_PHOTO = 1;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap, bitmap;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private String getAlbumName() {
        return "MediCube";
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("MediCube", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "External storage is not mounted READ/WRITE.", Toast.LENGTH_LONG).show();
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        //imgPath = albumF.getAbsolutePath();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        bitmap = Reduce.getResizedBitmap(bitmap, 320);
        /* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
        j = 1;
        /*File file = new File(imgPath1);
        file.delete();*/
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;
        try {

            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            Toast.makeText(this, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        imgPath1 = f.getAbsolutePath();
        //f.delete();
        startActivityForResult(takePictureIntent, actionCode);
    }


    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            ImagePath = mCurrentPhotoPath;
            setPic();
            //galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    public void takePhoto(View v) {
        dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setVisibility(View.INVISIBLE);

        email = getIntent().getStringExtra("email");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle("New Report");

        //choose class as per Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        editText = (EditText) findViewById(R.id.editText);
        docName = (EditText) findViewById(R.id.docName);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    public void createTask(View v) {
        if (editText.getText().length() > 0) {

            //image upload
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ParseFile file1 = new ParseFile("Image.jpg", image);
            file1.saveInBackground();

            Task t = new Task();
            t.setEmail(email);
            //t.setUser(ParseUser.getCurrentUser());
            t.setDescription(editText.getText().toString());
            t.setName(docName.getText().toString());
            //t.setName("");

            t.setImage(file1);

            t.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getApplicationContext(), "Upload Successful!", Toast.LENGTH_LONG).show();

                }
            });
            //mAdapter.insert(t, 0);
            editText.setText("");
        }
    }

    public void upload(View v) {
        //enter string details
        if (editText.getText().toString().trim().length() > 0) {
            byte[] data = editText.getText().toString().getBytes();

            ParseFile file = new ParseFile("history.txt", data);
            file.saveInBackground();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ParseFile file1 = new ParseFile("Image.jpg", image);
            file1.saveInBackground();


            final ParseObject medDetails = new ParseObject("details");
            medDetails.put("name", "Riya");
            medDetails.put("file", file);
            medDetails.put("pic", file1);
            //medDetails.put("Disease", "AIDS");

//        medDetails.saveInBackground();


            medDetails.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException ex) {
                    if (ex == null) {
                        //medDetails.setObjectId(ParseUser.getCurrentUser().getObjectId());
                        objectId = medDetails.getObjectId();
                        Toast.makeText(getApplicationContext(), "Upload Successful!", Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("objectIds", objectId);
                        editor.commit();

                        // objectId = medDetails.getObjectId();
                        // Toast.makeText(getApplicationContext(), objectId, Toast.LENGTH_LONG).show();

                    } else {
                        // Failed
                        Toast.makeText(getApplicationContext(), "Upload Unsuccessful!", Toast.LENGTH_LONG).show();

                    }
                }
            });
        } else
            Toast.makeText(getApplicationContext(), "Enter Details!", Toast.LENGTH_LONG).show();

    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ParseUser.logOut();
            Intent intent = new Intent(this, SignUpOrLoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void reports(View view) {
        Intent intent = new Intent(this, UserReportActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
/*            case ACTION_SELECT_FILE: {
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    try {
                        i = 1;
                        filePath = FileUtils.getPath(this, uri);
                        Toast.makeText(getApplicationContext(), "" + filePath, Toast.LENGTH_LONG).show();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }*/
        } // switch
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    "Cannot " + btn.getText());
            btn.setClickable(false);
        }
    }

    @Override
    protected void onResume() {
        j = 0;
        super.onResume();
    }
}
