package com.appyware.medicube;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SleepActivity extends AppCompatActivity {



    BluetoothAdapter mBluetoothAdapter=null;
    private int REQUEST_ENABLE_BT = 1;
    private String IOT_MAC= "30:14:06:19:14:87";
    private String UUID_STRING="00001101-0000-1000-8000-00805f9b34fb";
    BluetoothSocket btSocket= null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    int prev=0;
    int current;
    GraphView graph;

    Handler handler = new Handler();
    byte delimiter = 10;
    double avgtmp=0;
    int count=0,count0=0,count1=0;
    boolean stopWorker = false;
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];
    LineGraphSeries<DataPoint> series;


    public void Connect() {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(IOT_MAC);
        Log.d("", "Connecting to ... " + device);
        mBluetoothAdapter.cancelDiscovery();
        try {
            btSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_STRING));
            btSocket.connect();
            Log.d("", "Connection made.");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d("", "Unable to end the connection");
            }
            Log.d("", "Socket creation failed");
        }

        beginListenForData();
               /* this is a method used to read what the Arduino says for example when you write Serial.print("Hello world.") in your Arduino code */
    }

    public void beginListenForData()   {
        prev=0;
        count=2;
        count0=0;
        count1=0;
        try {
            inStream = btSocket.getInputStream();
            Log.d("input",inStream.toString());
        } catch (IOException e) {
        }

        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {

                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = inStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    String data = new String(encodedBytes, "US-ASCII");

                                    for(int pp=0;pp>data.length();pp++)
                                    {
                                        if(!((data.charAt(pp)>='0'&&data.charAt(pp)<='9')||(data.charAt(pp)==',')||
                                                (data.charAt(pp)==':')||(data.charAt(pp)=='.')))
                                            data= data.substring(0,i-1)+ data.substring(i+1,data.length());
                                    }
                                    Log.d("subdata: ",data);
                                    readBufferPosition = 0;

                                    try {
                                        current = Integer.parseInt(String.valueOf(data.charAt(2)));
                                        series.appendData(new DataPoint(count++,current),false,count+40);

                                        graph.addSeries(series);
                                        /*if(prev== current)
                                        {
                                            if(prev==1)
                                            {
                                                count1++;
                                                if(count1 > 5){
                                                    count0=0;
                                                }

                                            }
                                            else
                                                count0++;
                                        }
                                        else
                                        {
                                            if(count>5)
                                            {
                                                //send report to parse


                                            }
                                        }*/
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }


                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolsleep);
        setSupportActionBar(toolbar);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 0),
                new DataPoint(1, 0)
        });

        Connect();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sleep, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
