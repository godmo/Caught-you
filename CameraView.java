package com.android.caughtu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.caughtu.GMailSender;
import com.marakana.android.devicepolicydemo.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class CameraView extends Activity implements SurfaceHolder.Callback,
		OnClickListener {
	private static final String TAG = "CameraTest";
	Camera mCamera;
	boolean mPreviewRunning = false;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Log.e(TAG, "onCreate");

		setContentView(R.layout.cameraview);

		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);


		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		//SURFACE_TYPE_PUSH_BUFFERS不包含原生數據
		//Camera負責提供給預覽Surface數據，這樣圖像預覽會比較流暢
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.setKeepScreenOn(true);


	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	protected void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.e(TAG, "surfaceChanged");

		// XXX stopPreview() will crash if preview is not running
		if(mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters p = mCamera.getParameters();

		mCamera.setParameters(p);

		mCamera.startPreview();
		mPreviewRunning = true;
		mCamera.takePicture(null, null, mPictureCallback);

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");

		stopCamera();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	public void onClick(View v) {
		mCamera.takePicture(null, mPictureCallback, mPictureCallback);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");

		int i = findFrontFacingCamera();

		if(i > 0) ;
		while(true) {
			try {
				this.mCamera = Camera.open(i);
				try {
					this.mCamera.setPreviewDisplay(holder);
					return;
				} catch(IOException localIOException2) {
					stopCamera();
					return;
				}
			} catch(RuntimeException localRuntimeException) {
				localRuntimeException.printStackTrace();
				if(this.mCamera == null) continue;
				stopCamera();
				this.mCamera = Camera.open(i);
				try {
					this.mCamera.setPreviewDisplay(holder);
					Log.d("HiddenEye Plus", "Camera open RE");
					return;
				} catch(IOException localIOException1) {
					stopCamera();
					localIOException1.printStackTrace();//列印錯誤行的例外訊息
					return;
				}

			} catch(Exception localException) {
				if(this.mCamera != null) stopCamera();
				localException.printStackTrace();//列印錯誤行的例外訊息
				return;
			}
		}
	}

	private void stopCamera() {
		if(this.mCamera != null) {
			/*
			 * this.mCamera.stopPreview(); this.mCamera.release(); this.mCamera = null;
			 */
			this.mPreviewRunning = false;
		}
	}

	private int findFrontFacingCamera() {
		int i = Camera.getNumberOfCameras();
		for(int j = 0 ; ; j++) {
			if(j >= i) return -1;
			Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(j, localCameraInfo);
			if(localCameraInfo.facing == 1) return j;
		}
	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			if(data != null) {

				mCamera.stopPreview();
				mPreviewRunning = false;
				mCamera.release();

				try {
					BitmapFactory.Options opts = new BitmapFactory.Options();
					//將圖片縮放
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length, opts);
					bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					int newWidth = 300;
					int newHeight = 300;

					// calculate the scale - in this case = 0.4f
					float scaleWidth = ((float) newWidth) / width;
					float scaleHeight = ((float) newHeight) / height;

					// createa matrix for the manipulation
					Matrix matrix = new Matrix();
					// resize the bit map
					matrix.postScale(scaleWidth, scaleHeight);
					// rotate the Bitmap
					matrix.postRotate(-90);
					//得到新圖片
					Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
						width, height, matrix, true);

					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40,
						bytes);

					// you can create a new file name "test.jpg" in sdcard
					// folder.
					File f = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "test.jpg");

					System.out.println("File F : " + f);

					f.createNewFile();
					// write the bytes in file
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());

					// remember close de FileOutput
					fo.close();
					
					
					
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				setResult(585);
				
				  
		        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
		          .detectDiskReads().detectDiskWrites().detectNetwork()  
		          .penaltyLog().build());  
		     // "fcuuuuu@gmail.com", "fcuuuuuu"
		        try {     
		        	
		        	String fileName = "save_file";
		            
		            SharedPreferences sharedPreferences = getSharedPreferences(fileName,MODE_MULTI_PROCESS);

		        	
                    GMailSender sender = new GMailSender(sharedPreferences.getString("email", ""), sharedPreferences.getString("password", ""));  
                    sender.sendMail("Someone wants to unlock your phone!",   
                            "?",    
                            sharedPreferences.getString("email", ""),   
                            sharedPreferences.getString("email2", ""));   
                		} 
                catch (Exception e) {     
                    Log.e("SendMail", e.getMessage(), e);     
                } 
				
				finish();
			}
		}
	};
}
