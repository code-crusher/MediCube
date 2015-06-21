package com.appyware.medicube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    private Context mContext;
    private List<Task> mTasks;
    de.hdodenhof.circleimageview.CircleImageView
            imageView;

    public TaskAdapter(Context context, List<Task> objects) {
        super(context, R.layout.task_row_item, objects);
        this.mContext = context;
        this.mTasks = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.task_row_item, null);
        }

        Task task = mTasks.get(position);

        TextView descriptionView = (TextView) convertView.findViewById(R.id.task_description);
        TextView titleView = (TextView) convertView.findViewById(R.id.itemDate);
        TextView docNamne = (TextView) convertView.findViewById(R.id.itemName);
        imageView = (de.hdodenhof.circleimageview.CircleImageView
                ) convertView.findViewById(R.id.imageView);


        descriptionView.setText(task.getDescription());
        docNamne.setText(task.getName());
        titleView.setText(task.getDate());

        final ParseFile image = task.getImage();

        try {
            final byte[] data = image.getData();
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setImageBitmap(bmp);

        } catch (ParseException e) {
            e.printStackTrace();
        }


      /*  image.getDataInBackground( new GetDataCallback() {
            public void done(byte[] data,
                             ParseException e) {
                if (e == null) {

                    // Decode the Byte[] into
                    // Bitmap
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    // Get the ImageView from main.xml
                    //ImageView image = (ImageView) findViewById(R.id.ad1);
                    // Set the Bitmap into the
                    // ImageView
                    imageView.setImageBitmap(bmp);
                    // Close progress dialog
                } else {

                }
            }
        });*/

	/*	if(task.isCompleted()){
            descriptionView.setPaintFlags(descriptionView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}else{
			descriptionView.setPaintFlags(descriptionView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}*/

        return convertView;
    }

}
