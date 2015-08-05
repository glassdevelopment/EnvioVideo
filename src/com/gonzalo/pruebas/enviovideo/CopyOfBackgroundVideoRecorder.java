package com.gonzalo.pruebas.enviovideo;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;











import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class CopyOfBackgroundVideoRecorder extends Service implements SurfaceHolder.Callback, OnInfoListener{

	private static final String TAG = "BackgroundVideoRecorder";
	public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
	
    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera camera = null;
    private MediaRecorder mediaRecorder = null;
    private boolean first = true;
    private SurfaceHolder sh;

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	Log.e(TAG, "Constructor");
    	
    	return 1;}
    
    @Override
    public void onCreate() {

    	Log.e(TAG, "onCreate");
        // Start foreground service to avoid unexpected kill
//        Notification notification = new Notification.Builder(this)
//            .setContentTitle("Background Video Recorder")
//            .setContentText("")
//            .setSmallIcon(R.drawable.ic_launcher)
//            .build();
//        startForeground(1234, notification);
        

        // Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        LayoutParams layoutParams = new WindowManager.LayoutParams(
            1, 1,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(surfaceView, layoutParams);
        surfaceView.getHolder().addCallback(this);

    }

    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    	
    	sh = surfaceHolder;
    	
    	Log.e(TAG, "surfaceCreated");
    	
    	try {
    		camera = Camera.open();
			camera.setPreviewDisplay(null);
			camera.stopPreview();
			camera.unlock();
			startRecording();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
//    	try{
//    		createMediaRecorder(surfaceHolder);
//    	}catch(Exception e){
//    		if(cameraOpened)
//    			camera.release();
//    	}
        
      
        

    }
    

    private FileDescriptor getStreamFd() {
        ParcelFileDescriptor[] pipe=null;

        try {
          pipe=ParcelFileDescriptor.createPipe();

          new TransferThread(new AutoCloseInputStream(pipe[0])).start();
        }
        catch (IOException e) {
          Log.e(getClass().getSimpleName(), "Exception opening pipe", e);
        }

        return(pipe[1].getFileDescriptor());
      }
    
    private void startRecording(){
    	try{

        	
        	
            initRecorder();
            prepareRecorder();

            mediaRecorder.start();
    		
    	}catch (Exception e){
    		 Log.e(TAG, "Se rompe todo!");
    		 e.printStackTrace();
    	}
    	
    	
    }
    
    private void initRecorder(){
    	mediaRecorder = new MediaRecorder();
    	
    	// Step 1: Unlock and set camera to MediaRecorder
    	mediaRecorder.setCamera(camera);
    	
    	// Step 2: Set sources
    	//mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
    	
    	
    	// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
    	//mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
    	
    	mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    	mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    	
    	// Step 3: Set output format and encoding (for versions prior to API Level 8)
//    	mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//      mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//    	mediaRecorder.setVideoSize(1280, 720);
//    	mediaRecorder.setVideoFrameRate(30);
//      mediaRecorder.setVideoEncodingBitRate(690000);
    	
    	// Step 4: Set output file
    	//mediaRecorder.setOutputFile(getOutputMediaFile(this.MEDIA_TYPE_VIDEO).toString() );
    	mediaRecorder.setOutputFile(getStreamFd());
    	
    	
    	// Step 5: Set the preview output
    	mediaRecorder.setPreviewDisplay(sh.getSurface());
    	
    	mediaRecorder.setVideoFrameRate(30);
    	//mediaRecorder.setVideoFrameRate(30);
    	mediaRecorder.setVideoEncodingBitRate(3000000);
    	//mediaRecorder.setVideoEncodingBitRate(690000);
    	  
        //mediaRecorder.setMaxFileSize(5242880);
//    	mediaRecorder.setMaxDuration(1000*60*2);
//        mediaRecorder.setOnInfoListener(this);
    }
    
    private void stopRecording(){
    	mediaRecorder.stop();
    	mediaRecorder.reset();
    	mediaRecorder.release();
    	mediaRecorder = null;
    	camera.lock();
    	camera.stopPreview();
		camera.unlock();
    }
    
    


	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		
		if(what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) 
		 { 
			Log.e(TAG, "Llena tamanio");
			
				stopRecording();
//				
				 initRecorder();
				 prepareRecorder();
				 
				 mediaRecorder.start();
				

		 }
		
	}

	private void prepareRecorder() {
		
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
    private void record(){
    	Log.e(TAG, "record");
    	 mediaRecorder.setOutputFile(getOutputMediaFile(this.MEDIA_TYPE_VIDEO).toString() );
    	 Log.e(TAG, "record - setOutputFile");
    	 mediaRecorder.setMaxFileSize(5242880);
    	 Log.e(TAG, "record - setMaxFileSize");
    	 
    	
    	try { 
    		mediaRecorder.prepare();
    		Log.e(TAG, "record - prepare");
    		} catch (Exception e) {
    			Log.e(TAG, "record - error");
    			e.printStackTrace();
    		}
    	try{
            mediaRecorder.start();
            Log.e(TAG, "record - start");
    	}catch (Exception e){
            e.printStackTrace();
    	}

    }

    // Stop recording and remove SurfaceView
    @Override
    public void onDestroy() {
    	Log.e(TAG, "onDestroy");
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();

        camera.lock();
        camera.release();

        windowManager.removeView(surfaceView);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}

    @Override
    public IBinder onBind(Intent intent) { return null; }
    
    public  static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return  null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "BackgroundRecording");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()) {
                Log.d("BackgroundRecording", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        System.out.println("DIR: " + mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
        return mediaFile;
    }

    
    
    static class TransferThread extends Thread {
        InputStream in;
        FileOutputStream out;
        // 1024 = 1KB, 1MB = 1K*1K --> 8MB = 10*1024*1024
        private static long FILE_SIZE_IN_BYTES = 2*1024*1024;
        
        TransferThread(InputStream in) {
          this.in=in;    
        }

        @Override
        public void run() {
          byte[] buf=new byte[8];
          int len;
          final long readSize = 8;
          long count = 0;
         
          try {
        	out = new FileOutputStream(getOutputMediaFile(2).toString());
        	
        	byte buffer[] = new byte[4];
        	while (in.read() != 'm');
        	in.read(buffer,0,3);
			if (buffer[0] == 'd' && buffer[1] == 'a' && buffer[2] == 't') System.out.println("HOLAAAAAAAAAAAAAA 21321312");;
        	
//            while ((len=in.read(buf)) >= 0) {
//            	System.out.println(buf);
//              out.write(buf, 0, len);
//              count = count + readSize;
//              if(count >= FILE_SIZE_IN_BYTES){
//            	  System.out.println("yeahhhh");
//            	  out.flush();
//                  out.getFD().sync();
//                  out.close();
//                  out = new FileOutputStream(getOutputMediaFile(2).toString());
//                  count = 0;
//              }
//            }

            in.close();

            out.flush();
            out.getFD().sync();
            out.close();
          }
          catch (IOException e) {
            Log.e(getClass().getSimpleName(),
                  "Exception transferring file", e);
          }
        }
      }

    
    
    
}