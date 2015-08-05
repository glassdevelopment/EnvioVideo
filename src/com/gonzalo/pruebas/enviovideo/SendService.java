package com.gonzalo.pruebas.enviovideo;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;




import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;





public class SendService extends Service {

	 public static final String TAG = "SendService";
	 private static final int STATUS_CODE_OK = 200;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
    	//Log.e(TAG, "servicio on start");
    	/*
    	
    	 File dir = new File(Environment.getExternalStoragePublicDirectory(
                 Environment.DIRECTORY_PICTURES), "BackgroundRecording");
         // This location works best if you want the created images to be shared
         // between applications and persist after your app has been uninstalled.

         // Create the storage directory if it does not exist
         if (!dir.exists()){
             if (!dir.mkdirs()) {
                 Log.d("BackgroundRecording", "failed to create directory");
                 return -100;
             }
         }
         
         String url = "http://169.254.123.41:8080/server/rest/services/upload";
         for(File file : dir.listFiles()){
        	 try {
        		    HttpClient httpclient = new DefaultHttpClient();

        		    HttpPost httppost = new HttpPost(url);

        		    InputStreamEntity reqEntity = new InputStreamEntity(
        		            new FileInputStream(file), -1);
        		    reqEntity.setContentType("binary/octet-stream");
        		    reqEntity.setChunked(true); // Send in multiple parts if needed
        		    httppost.setEntity(reqEntity);
        		    HttpResponse response = httpclient.execute(httppost);
        		    //Do something with response...

        		} catch (Exception e) {
        		    // show error
        		}
        	 
        	 
         }
        
*/
        return START_STICKY;
    }

    
    @Override
    public void onCreate() {

    	Log.e(TAG, "servicio send onCreate");
    	
    	new SendPostTask().execute();
       	 
       	 
       
       

    }
    
    @Override
    public void onDestroy() {
    	Log.e(TAG, "onDestroy");
    	
        super.onDestroy();
    }
    
    private class SendPostTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
        	
        	File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "BackgroundRecording");
        	try {
        		while(true){
        			if(dir.listFiles().length > 0){				
        				System.out.println("Tiene archivos");
        				if(!((dir.listFiles().length == 1 && isBeingCreated(dir.listFiles()[0])))){
        					sendFiles(dir);
        				}
        				
        			}else{
        				System.out.println("Directorio vacio");
        			}
        			Thread.sleep(5000);
        		}
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	
                // Make your request POST here. Example:
        	
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
//            if (!dir.exists()){
//                if (!dir.mkdirs()) {
//                    Log.d("BackgroundRecording", "failed to create directory");
//                   
//                }
//            }
//            
//            String url = "http://192.168.1.44:8080/server/rest/services/upload";
//            String url2 = "http://192.168.1.44:8080/server/rest/services/get";
//            String fileName = "";
//            
//            Log.e(TAG, "Antes for");
//            for(File file : dir.listFiles()){
//           	 try {
//           		 Log.e(TAG, "Dentro for");
//           		 fileName = file.getName();
//           		 Log.e(TAG, fileName);
//           		 
//           		 if(fileName.endsWith(".mp4")){
//           			 HttpClient httpclient = new DefaultHttpClient();
//
//            		    HttpPost httppost = new HttpPost(url);
//            		    HttpGet get = new HttpGet(url2);
//            		    
//            		    InputStreamEntity reqEntity = new InputStreamEntity(
//            		            new FileInputStream(file), -1);
//            		    reqEntity.setContentType("binary/octet-stream");
//            		    reqEntity.setChunked(true); // Send in multiple parts if needed
//            		    httppost.setEntity(reqEntity);
//            		    HttpResponse response = httpclient.execute(httppost);
//            		// HttpResponse response2 = httpclient.execute(get);
//            		    //Do something with response...
//            		    if(response.getStatusLine().getStatusCode() == STATUS_CODE_OK){
//            		    	file.delete();
//            		    }
////            		 Log.e(TAG, String.valueOf(response.getStatusLine().getStatusCode()));
////            		 Log.e(TAG, response.getStatusLine().getReasonPhrase());
//           		 }
//           		 
//           		   
//           		} catch (Exception e) {
//           		    // show error
//           		}
//            }
                return null;
                
        }
        
        private boolean isBeingCreated(File file){
        	
        	long lastModified1, lastModified2;
        	
        	lastModified1 = file.lastModified();
        	
        	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	lastModified2 = file.lastModified();
        	
        	 Log.e(TAG, "lastModified1: " + lastModified1);
        	 Log.e(TAG, "lastModified2: " + lastModified2);
        	 
        	return !(lastModified1 == lastModified2);
        }
        
        private void sendFiles(File dir){
        	
        	//String uploadFilesEndpoint = "http://192.168.1.45:8080/server/rest/services/upload";
        	String uploadFilesEndpoint = "http://192.168.4.174:8080/server/rest/services/upload";
        	String fileName = "";
            
            Log.e(TAG, "Antes for");
            for(File file : dir.listFiles()){
           	 try {
           		 Log.e(TAG, "Dentro for");
           		 fileName = file.getName();
           		 Log.e(TAG, fileName);
           		 
           		 if(!isBeingCreated(file)){
           			 
           			 HttpClient httpclient = new DefaultHttpClient();

        		     HttpPost httppost = new HttpPost(uploadFilesEndpoint);
        		    
        		    
        		     InputStreamEntity reqEntity = new InputStreamEntity(
        		            new FileInputStream(file), -1);
        		     reqEntity.setContentType("binary/octet-stream");
        		     reqEntity.setChunked(true); // Send in multiple parts if needed
        		     httppost.setEntity(reqEntity);
        		     HttpResponse response = httpclient.execute(httppost);
        		
        		    //Do something with response...
        		     if(response.getStatusLine().getStatusCode() == STATUS_CODE_OK){        		    
        		    	 file.delete();
        		     }
//            		 Log.e(TAG, String.valueOf(response.getStatusLine().getStatusCode()));
//            		 Log.e(TAG, response.getStatusLine().getReasonPhrase());
           		 }
           		 
           		   
           		} catch (Exception e) {
           		    // show error
           		}
            }
        }

        protected void onPostExecute(Void result) {
          // Do something when finished.
        }
    }
}
