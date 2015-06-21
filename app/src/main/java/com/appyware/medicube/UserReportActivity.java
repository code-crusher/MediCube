package com.appyware.medicube;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appyware on 28/03/15.
 */
public class UserReportActivity extends ActionBarActivity {

    ListView listView;
    // NestedScrollView scrollView;
    private TaskAdapter mAdapter;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reports);
        listView = (ListView) findViewById(R.id.task_list);
        //scrollView = (NestedScrollView) findViewById(R.id.scrollView);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Reports");


        email = getIntent().getStringExtra("email");
        Log.e("Email  : ", email);
        mAdapter = new TaskAdapter(this, new ArrayList<Task>());
        listView.setAdapter(mAdapter);

      /*  scrollView.post(new Runnable() {

            @Override
            public void run() {
                ViewTreeObserver observer = scrollView.getViewTreeObserver();

                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        int height = scrollView.getChildAt(0).getHeight();
                        scrollView.setMinimumHeight(height + scrollView.getHeight());
                        // Toast.makeText(getApplicationContext(), "" + height, Toast.LENGTH_SHORT).show();
                        // progressBar.setMax(height);

                    }
                });
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);

                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }

                return false;
            }
        });*/

        View v = null;
        updateData(v);

    }

    public void newReport(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    public void updateData(View v) {
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);

        query.whereEqualTo("Email", email);

        query.orderByDescending("createdAt");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<Task>() {
            @Override
            public void done(List<Task> tasks, ParseException error) {
                if (tasks != null) {
                    mAdapter.clear();
                    for (int i = 0; i < tasks.size(); i++) {
                        mAdapter.add(tasks.get(i));
                    }
                }
            }
        });
    }

/*
    public void download(View v) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String objectId = preferences.getString("objectIds", null);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("details");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject userid, ParseException e) {
                if (e == null) {

                    String name = userid.getString("name");
                    ParseFile file = (ParseFile) userid.get("file");
                    ParseFile file1 = (ParseFile) userid.get("pic");
                    file1.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data,
                                         ParseException e) {
                            if (e == null) {

                                // Decode the Byte[] into
                                // Bitmap
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                // Get the ImageView from main.xml
                                //ImageView image = (ImageView) findViewById(R.id.ad1);
                                imageView = (ImageView) findViewById(R.id.imageView);
                                // Set the Bitmap into the
                                // ImageView
                                imageView.setImageBitmap(bmp);
                                // Close progress dialog
                            } else {

                            }
                        }
                    });

                    try {
                        byte data[] = file.getData();
                        String text = file.getUrl();
                        try {
                            URL textUrl = new URL(text);
                            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(textUrl.openStream()));
                            String StringBuffer;
                            String stringText = "";
                            while ((StringBuffer = bufferReader.readLine()) != null) {
                                stringText += StringBuffer;
                            }
                            bufferReader.close();
                            textView.setText(stringText);
                            textView1.setText(name);

                        } catch (MalformedURLException e1) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                    // object will be your game score
                } else {
                    // something went wrong
                }
            }
        });
    }*/


    @Override
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
            Intent intent = new Intent(this, BluetoothActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
