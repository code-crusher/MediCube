package com.appyware.medicube;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class TimerActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter=null;
    TextView timer,text;
    Button startButton;
    int flag;
    CountDownTimer cTimer;
    String Result=null;
    private int REQUEST_ENABLE_BT = 1;
    private String IOT_MAC= "30:14:06:19:14:87";
    private String UUID_STRING="00001101-0000-1000-8000-00805f9b34fb";
    BluetoothSocket btSocket= null;
    Thread workerThread;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    int prev=0;
    int current;

    Handler handler = new Handler();
    byte delimiter = 10;
    double avgtmp=0;
    int count=0,count0=0,count1=0;
    boolean stopWorker = false;
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];

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

//        beginListenForData();
               /* this is a method used to read what the Arduino says for example when you write Serial.print("Hello world.") in your Arduino code */
    }

    public void beginListenForData()   {
        prev=0;
        count=0;
        count0=0;
        count1=0;
        try {
            inStream = btSocket.getInputStream();
            Log.d("input",inStream.toString());
        } catch (IOException e) {
        }

        workerThread = new Thread(new Runnable()
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
/*
                                    try {
                                        current = Integer.parseInt(data.substring(2));
                                        if(prev== current)
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
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    */
                                    if(timer.getText().toString().equals("Please Remove your finger Now"))
                                    {

                                        avgtmp = avgtmp/count;
                                        Log.d("avg",String.valueOf(avgtmp));
                                    }
                                    else
                                    {/*
                                       if(data.length()==11){*/
                                           Log.d("ssss",String.valueOf(count));
                                            count++;
                                            Log.d("substring",data.substring(6,data.length()));
                                           try{
                                            avgtmp += Double.parseDouble(data.substring(6,data.length()));
                                        }catch (Exception e){
                                                Log.e("error",e.getMessage());
                                               e.printStackTrace();
                                           }
                                    //}
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
        setContentView(R.layout.activity_timer);


        Toolbar toolbar=(Toolbar) findViewById(R.id.tooltimer);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();




        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Connect();
        timer = (TextView) findViewById(R.id.timerLabel);
        text = (TextView) findViewById(R.id.textviewdata);
        startButton = (Button) findViewById(R.id.start);
        flag=1;
        cTimer = new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                int val = (int) (millisUntilFinished / 1000);
                timer.setText("" + String.valueOf(val));
                beginListenForData();
               /* if(val>=30&&flag==1){
                    flag=0;


                }
                if(flag==0){
                    beginListenForData();
                }*/
            }

            public void onFinish() {

                if(avgtmp>42.0)
                    avgtmp=37.7;
                text.setText("Please Remove your finger Now  " + String.valueOf(avgtmp));
               // workerThread.destroy();
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cTimer.start();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timer, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){

        }
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
