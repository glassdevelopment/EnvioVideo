package com.gonzalo.pruebas.enviovideo;


import java.io.File;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	public static final String TAG = "MainActivity";
	private boolean recording = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button grabar = (Button) findViewById(R.id.grabar);
		grabar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//startService(new Intent(MainActivity.this, CopyOfBackgroundVideoRecorder.class)); 
            	startService(new Intent(MainActivity.this, /*CopyOf*/BackgroundVideoRecorder.class)); 
            	recording = true;
            	
            }
        });
		
		final Button parar = (Button) findViewById(R.id.parar);
		parar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	if(recording){
            		//stopService(new Intent(MainActivity.this, CopyOfBackgroundVideoRecorder.class));
            		stopService(new Intent(MainActivity.this, /*CopyOf*/BackgroundVideoRecorder.class));
            	}
            	
                // Stop the service at the end of the message queue for proper options menu
                // animation. This is only needed when starting a new Activity or stopping a Service
                // that published a LiveCard.
//                post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                    	
//                    	
//                        stopService(new Intent(MainActivity.this, SimpleLiveCardService.class));
//                    }
// 
//            	
//            }
            }
        });
		
		final Button enviar = (Button) findViewById(R.id.enviar);
		enviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	startService(new Intent(MainActivity.this, SendService.class)); 

            	
            }
        });
		
		final Button borrar = (Button) findViewById(R.id.borrar);
		borrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	File dir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "BackgroundRecording"); 

            	for(File file : dir.listFiles()){
            		Log.e(TAG, "Borrando: " + file.getName());
            		file.delete();
            	}
            	
            	Log.e(TAG, "Termina de borrar");
            }
        });
		
		
		final Button video = (Button) findViewById(R.id.video);
		video.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
//            	String url = "rtsp://192.168.4.87:1935/vod/output.mp4";
//            	if (url.startsWith("rtsp://")) {
//            	    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            	    startActivity(intent);
//            	}
            	
            	// Start NewActivity.class
				Intent myIntent = new Intent(MainActivity.this,
						VideoViewActivity.class);
				startActivity(myIntent);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
