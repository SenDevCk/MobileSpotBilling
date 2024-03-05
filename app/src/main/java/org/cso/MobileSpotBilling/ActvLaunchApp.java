package org.cso.MobileSpotBilling;

import org.cso.MSBAsync.AsyncValidateDevice;
import org.cso.MSBUtil.UtilAppCommon;
import org.cso.MSBUtil.UtilDB;
import org.cso.service.MyJobService;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;


import java.lang.ref.WeakReference;


import static org.cso.MSBUtil.UtilAppCommon.WORK_DURATION_KEY;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActvLaunchApp extends AppCompatActivity implements TaskCallback {

	TextView txtVersion;
	private static final int PERMISSION_READ_STATE = 10;
	private static final int PERMISSION_INTERNET = 11;
	//private static final int PERMISSION_BLUETOOTH = 12;

	private static final int PERMISSION_ACCESS_COARSE_LOCATION = 14;
	private static final int PERMISSION_CHANGE_NETWORK_STATE = 15;

	public static final int MSG_UNCOLOR_START = 0;
	public static final int MSG_UNCOLOR_STOP = 1;
	public static final int MSG_COLOR_START = 2;
	public static final int MSG_COLOR_STOP = 3;
	public static String TAG = "BSB-" + R.string.app_fileversion;

	private ComponentName mServiceComponent;
	private int mJobId = 0;

	// Handler for incoming messages from the service.
	private IncomingMessageHandler mHandler;

	TelephonyManager telephonyManager = null;
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actv_launch_app);
		toolbar = findViewById(R.id.toolbar_act_lanch);
		//toolbar.setLogo(getResources().getDrawable(R.drawable.sbpscl_logo));
		toolbar.setTitle(getResources().getString(R.string.app_version));
		//setSupportActionBar(toolbar);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setDisplayShowHomeEnabled(true);
		if (ContextCompat.checkSelfPermission(ActvLaunchApp.this, Manifest.permission.READ_PHONE_STATE)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(ActvLaunchApp.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
		}

		if (ContextCompat.checkSelfPermission(ActvLaunchApp.this, Manifest.permission.INTERNET)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(ActvLaunchApp.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_READ_STATE);
		}

		/*telephonyManager = (TelephonyManager) this
				.getSystemService(this.TELEPHONY_SERVICE);

		UtilAppCommon.IMEI_Number = telephonyManager.getDeviceId();
		*///UtilAppCommon.IMEI_Number = "911542201041919";
		UtilAppCommon.IMEI_Number = getImei();
				txtVersion = (TextView) findViewById(R.id.TxtVersion);
		Log.e("IMEI Number", UtilAppCommon.IMEI_Number);

		UtilDB utildb = new UtilDB(getApplicationContext());
		utildb.readLocalAuthentication();

		//mServiceComponent = new ComponentName(this, MyJobService.class);

		//mHandler = new IncomingMessageHandler(this);

		//scheduleJob();
		functionCall();   //changed for diect login
		//startActivity(new Intent(this, ActvLogin.class));
	}


	public String getImei(){
		String  imei = null;
		try {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
				imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


			} else {
				TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
						PackageManager.PERMISSION_GRANTED) {
					imei = telephonyManager.getDeviceId();
				}

			}
		}catch (Exception e){
			e.printStackTrace();
			imei = null;
		}
		return imei;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case PERMISSION_READ_STATE: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission granted!
					// you may now do the action that requires this permission

					/*telephonyManager = (TelephonyManager) this
							.getSystemService(this.TELEPHONY_SERVICE);
*/
					try {
						//UtilAppCommon.IMEI_Number = telephonyManager.getDeviceId();
						UtilAppCommon.IMEI_Number = getImei();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					//UtilAppCommon.IMEI_Number = "911542201041919";
					txtVersion = (TextView) findViewById(R.id.TxtVersion);
					Log.e("IMEI Number", UtilAppCommon.IMEI_Number);

					UtilDB utildb = new UtilDB(getApplicationContext());
					utildb.readLocalAuthentication();

				} else {
					// permission denied
					ActivityCompat.requestPermissions(ActvLaunchApp.this,
							new String[]{Manifest.permission.READ_PHONE_STATE},
							PERMISSION_READ_STATE);
				}
				return;


			}

			case PERMISSION_INTERNET: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission granted!
					// you may now do the action that requires this permission
					functionCall();

				} else {
					// permission denied
					ActivityCompat.requestPermissions(ActvLaunchApp.this,
							new String[]{Manifest.permission.INTERNET},
							PERMISSION_INTERNET);
				}
				return;
			}
		}
	}

	/*@Override
	public boolean onSupportNavigateUp() {
		//  closePrinter();
		onBackPressed();
		return true;
	}*/

	@Override
	public void done() {
		// TODO Auto-generated method stub
		//ActvLaunchApp.this.finish();
		finish();
	}
	
	public void functionCall()
	{

		String validateParam[] = new String[2];
		validateParam[0] = UtilAppCommon.IMEI_Number;	//"353835062918882";
		validateParam[1] = txtVersion.getText().toString();
		UtilAppCommon.strAppVersion = txtVersion.getText().toString();
		
		Log.e("Parameters ==>> ", UtilAppCommon.IMEI_Number + "  ==  " + txtVersion.getText().toString());
		AsyncValidateDevice asyncValidate = new AsyncValidateDevice(this);
		asyncValidate.execute(validateParam);
	}

	@TargetApi(21)
	public void cancelAllJobs(View v) {
		JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
		tm.cancelAll();
		//Toast.makeText(MainActivity.this, R.string.all_jobs_cancelled, Toast.LENGTH_SHORT).show();
	}

	@TargetApi(21)
	public void scheduleJob() {
		JobInfo.Builder builder = new JobInfo.Builder(mJobId++, mServiceComponent);

		String delay = "10000";
		if (!TextUtils.isEmpty(delay)) {
			builder.setPeriodic(Long.valueOf(delay) * 1000);
		}
		//String deadline = mDeadlineEditText.getText().toString();
		//if (!TextUtils.isEmpty(deadline)) {
		//    builder.setOverrideDeadline(Long.valueOf(deadline) * 1000);
		//}
		boolean requiresUnmetered = false;
		boolean requiresAnyConnectivity = false;
		if (requiresUnmetered) {
			builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
		} else if (requiresAnyConnectivity) {
			builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
		}
		builder.setRequiresDeviceIdle(false);
		builder.setRequiresCharging(false);

		// Extras, work duration.
		PersistableBundle extras = new PersistableBundle();
		String workDuration = "1";
		if (TextUtils.isEmpty(workDuration)) {
			workDuration = "1";
		}
		extras.putLong(WORK_DURATION_KEY, Long.valueOf(workDuration) * 1000);

		builder.setExtras(extras);

		// Schedule job
		Log.d(TAG, "Scheduling job");
		JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
		tm.schedule(builder.build());
	}


	/**
	 * A {@link Handler} allows you to send messages associated with a thread. A {@link Messenger}
	 * uses this handler to communicate from {@link org.cso.service.MyJobService}. It's also used to make
	 * the start and stop views blink for a short period of time.
	 */
	private static class IncomingMessageHandler extends Handler {

		// Prevent possible leaks with a weak reference.
		private WeakReference<ActvLaunchApp> mActivity;

		IncomingMessageHandler(ActvLaunchApp activity) {
			super(/* default looper */);
			this.mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ActvLaunchApp mainActivity = mActivity.get();
			if (mainActivity == null) {
				// Activity is no longer available, exit.
				return;
			}
			//View showStartView = mainActivity.findViewById(R.id.onstart_textview);
			//View showStopView = mainActivity.findViewById(R.id.onstop_textview);
			Message m;
			switch (msg.what) {
                /*
                 * Receives callback from the service when a job has landed
                 * on the app. Turns on indicator and sends a message to turn it off after
                 * a second.
                 */
				case MSG_COLOR_START:
					// Start received, turn on the indicator and show text.
					//showStartView.setBackgroundColor(getColor(R.color.start_received));
					updateParamsTextView(msg.obj, "started");

					// Send message to turn it off after a second.
					m = Message.obtain(this, MSG_UNCOLOR_START);
					sendMessageDelayed(m, 1000L);
					break;
                /*
                 * Receives callback from the service when a job that previously landed on the
                 * app must stop executing. Turns on indicator and sends a message to turn it
                 * off after two seconds.
                 */
				case MSG_COLOR_STOP:
					// Stop received, turn on the indicator and show text.
					//showStopView.setBackgroundColor(getColor(R.color.stop_received));
					updateParamsTextView(msg.obj, "stopped");

					// Send message to turn it off after a second.
					m = obtainMessage(MSG_UNCOLOR_STOP);
					sendMessageDelayed(m, 2000L);
					break;
				case MSG_UNCOLOR_START:
					//showStartView.setBackgroundColor(getColor(R.color.none_received));
					updateParamsTextView(null, "");
					break;
				case MSG_UNCOLOR_STOP:
					//showStopView.setBackgroundColor(getColor(R.color.none_received));
					updateParamsTextView(null, "");
					break;
			}
		}

		private void updateParamsTextView(@Nullable Object jobId, String action) {
			TextView paramsTextView = null;//(TextView) mActivity.get().findViewById(R.id.task_params);
			if (jobId == null) {
				paramsTextView.setText("");
				return;
			}
			String jobIdText = String.valueOf(jobId);
			paramsTextView.setText(String.format("Job ID %s %s", jobIdText, action));
		}
	}

}

	
