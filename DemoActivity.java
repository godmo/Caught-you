package com.android.caughtu;
/*
 * 定義欄位並儲存資料 1 2 3 device
 * */
import com.marakana.android.devicepolicydemo.R;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DemoActivity extends Activity implements
		OnCheckedChangeListener {
	static final String TAG = "DevicePolicyDemoActivity";
	static final int ACTIVATION_REQUEST = 47; // identifies our request id
	DevicePolicyManager devicePolicyManager;
	ComponentName demoDeviceAdmin;
	ToggleButton toggleButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		String fileName = "save_file";
		final SharedPreferences sharedPreferences = getSharedPreferences(fileName,MODE_MULTI_PROCESS);
		String email = sharedPreferences.getString("email", "");//每個欄位存在sharedPreferences裡面的名稱
		String password = sharedPreferences.getString("password", "");
		String email2 = sharedPreferences.getString("email2", "");
		EditText et = (EditText) findViewById(R.id.editText1);//元件初始化
		EditText et2 = (EditText) findViewById(R.id.editText2);//元件初始化
		EditText et3 = (EditText) findViewById(R.id.editText3);//元件初始化
		et.setText(email);//string called et
		et2.setText(password);//string called et2
		et3.setText(email2);//string called et3
		
		Button save = (Button) findViewById(R.id.button1);
		
		
		//////////////////////////////////////////////////////
		 save.setOnClickListener(new View.OnClickListener() {   	///   
		 public void onClick(View view) {                           ///
		 //SharedPreferences.Editor                                                               			///
		 SharedPreferences.Editor editor = sharedPreferences.edit(); ///
		 															///
		 									///			
		 EditText et = (EditText) findViewById(R.id.editText1);		///
		 EditText et2 = (EditText) findViewById(R.id.editText2);	///
		 EditText et3 = (EditText) findViewById(R.id.editText3);	///
		 															///
		 editor.putString("email", et.getText().toString());		///	
		 editor.putString("password", et2.getText().toString());	///
		 editor.putString("email2", et3.getText().toString());		///
		 //�N�ƾ��x�s													///
		 editor.commit();											///
		 }	 														///
		 });														///
		 //////////////////////////////////////////////////////////////
		

		toggleButton = (ToggleButton) super
				.findViewById(R.id.toggle_device_admin);
		toggleButton.setOnCheckedChangeListener(this);

		// Initialize Device Policy Manager service and our receiver class
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		demoDeviceAdmin = new ComponentName(this, AdminReceiver.class);
	}

	/**
	 * Called when a button is clicked on. We have Lock Device and Reset Device
	 * buttons that could invoke this method.
	 */
	

	/**
	 * Called when the state of toggle button changes. In this case, we send an
	 * intent to activate the device policy administration.
	 */
	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		if (isChecked) {
			// Activate device administration
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					demoDeviceAdmin);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"You have to agree");
			startActivityForResult(intent, ACTIVATION_REQUEST);
		}
		Log.d(TAG, "onCheckedChanged to: " + isChecked);
	}

	/**
	 * Called when startActivityForResult() call is completed. The result of
	 * activation could be success of failure, mostly depending on user okaying
	 * this app's request to administer the device.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTIVATION_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Administration enabled!");
				toggleButton.setChecked(true);
			} else {
				Log.i(TAG, "Administration enable FAILED!");
				toggleButton.setChecked(false);
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
