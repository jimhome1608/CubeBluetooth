package com.example.tut1;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import java.math.BigInteger;
import android.graphics.Color;

public  class MainActivity extends Activity implements View.OnClickListener, SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mGyroSensor;
    private  Sensor myAccellor;
    private Sensor myLight;
    private Vibrator vibrator;

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        final int type = event.sensor.getType();
        int y = 0;
        int x = 0;
        int leftMotorSpeed = 0;
        int righMotorSpeed = 0;
        String NewEventFlag = new String();
        if (type != Sensor.TYPE_ACCELEROMETER)
            return;
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;
        float Gx = event.values[0];
        float Gy = event.values[1];
        float Gz = event.values[2];
        boolean underMark = false;
        boolean overMark = false;
        tvLeft.setText("x:"+ String.format("%.1f", Gx) +"  y:"+ String.format("%.1f", Gy) + " z:"+ String.format("%.1f", Gz));
    }

    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, myAccellor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, myLight, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//Bluetooth.write("Q");}//Stop streaming
		super.onBackPressed();
	}

    LedCube ledCube = new LedCube();

    static int upLeft = 0;

    String strReadData  = new String();


	Button btnCLEAR;
    Button btnYplus;
    Button btnYminus;
    Button btnXplus;
    Button btnXminus;
    Button btnZplus;
    Button btnZminus;

    TextView tvLeft;

	Button bConnect, bDisconnect;

	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case Bluetooth.SUCCESS_CONNECT:
				Bluetooth.connectedThread = new Bluetooth.ConnectedThread((BluetoothSocket)msg.obj);
				Toast.makeText(getApplicationContext(), "Connected!", 0).show();
				String s = "successfully connected";
				Bluetooth.connectedThread.start();
				break;
			case Bluetooth.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				String strIncom = new String(readBuf, 0, msg.arg1);
                strReadData = strReadData+strIncom;
               // Log.d("strIncom", strReadData);
               // tvLeft.setText(strReadData);
                int _CloseBracketPos = strReadData.indexOf('}');
                if ( _CloseBracketPos>= 0 ) {
                    String strMessage = new String();
                    strMessage = strReadData.substring(0,_CloseBracketPos);
                   // Log.d("strMessage", strMessage);
                    strReadData = "";
                    tvLeft.setText(strMessage);
                }
				break;
			}
		}

		public boolean isFloatNumber(String num){
			//Log.d("checkfloatNum", num);
			try{
				Double.parseDouble(num);
			} catch(NumberFormatException nfe) {
				return false;
			}
			return true;
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//Hide title
		this.getWindow().setFlags(WindowManager.LayoutParams.
				FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//Hide Status bar
		setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        myAccellor= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        myLight= mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		LinearLayout background = (LinearLayout)findViewById(R.id.bg);
		background.setBackgroundColor(Color.BLACK);
		init();
		InitControls();
	}

	void init(){
		Bluetooth.gethandler(mHandler);
	}

	void InitControls(){
		bConnect = (Button)findViewById(R.id.bConnect);
		bConnect.setOnClickListener(this);
        btnCLEAR = (Button)findViewById(R.id.btnCLEAR);
        btnCLEAR.setOnClickListener(this);
        btnYplus = (Button)findViewById(R.id.btnYplus);
        btnYplus.setOnClickListener(this);
        btnYminus = (Button)findViewById(R.id.btnYminus);
        btnYminus.setOnClickListener(this);
        btnXplus = (Button)findViewById(R.id.btnXplus);
        btnXplus.setOnClickListener(this);
        btnXminus = (Button)findViewById(R.id.btnXminus);
        btnXminus.setOnClickListener(this);

        btnZplus = (Button)findViewById(R.id.btnZplus);
        btnZplus.setOnClickListener(this);
        btnZminus = (Button)findViewById(R.id.btnZminus);
        btnZminus.setOnClickListener(this);

        tvLeft = (TextView)findViewById(R.id.tvLeft);
		//init toggleButton
	}


	@Override
	public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bConnect:
                if (Bluetooth.connectedThread != null) {
                    Bluetooth.disconnect();
                    bConnect.setText("CONNECT");
                    bConnect.setBackgroundColor(Color.DKGRAY);
                }
                else {
                    startActivity(new Intent("android.intent.action.BT1"));
                    bConnect.setText("DISCONNECT");
                    bConnect.setBackgroundColor(Color.GREEN);
                }
                break;
            case R.id.btnCLEAR:
                ledCube.AllOff();
                ledCube.BlueToothWrite();
                ledCube.MoveTo(0,0,0);
                ledCube.WalkThisWayBasic();
                //ledCube.direction = "xup";
                //while (ledCube.direction != "stop")
                //    ledCube.Step();
                //ledCube.WalkThisWay();
                break;
            case R.id.btnYplus:
                ledCube.Yplus();
                ledCube.WallY(0);
                ledCube.BlueToothWrite();
                break;
            case R.id.btnYminus:
                ledCube.Yminus();
                ledCube.WallY(0);
                ledCube.BlueToothWrite();
                break;
            case R.id.btnXplus:
                ledCube.Xplus();
                ledCube.WallX(0);
                ledCube.BlueToothWrite();
                break;
            case R.id.btnXminus:
                ledCube.Xminus();
                ledCube.WallX(0);
                ledCube.BlueToothWrite();
                break;
            case R.id.btnZplus:
                ledCube.Zplus();
                ledCube.LevelZ(0);
                ledCube.BlueToothWrite();
                break;
            case R.id.btnZminus:
                ledCube.Zminus();
                ledCube.LevelZ(0);
                ledCube.BlueToothWrite();
                break;
        }

    }
}
