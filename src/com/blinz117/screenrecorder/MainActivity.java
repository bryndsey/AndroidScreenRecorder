package com.blinz117.screenrecorder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {

	Button m_recordButton;
	SeekBar timeSlider;
	TextView currTimeText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		currTimeText = (TextView)findViewById(R.id.timeValue);
		m_recordButton = (Button)findViewById(R.id.record_start);
		m_recordButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				startRecord();
			}
		});
		
		timeSlider = (SeekBar) findViewById(R.id.timeSlider);
		currTimeText.setText("" + getTimeSliderVal());
		 
		timeSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            //int progressChanged = 0;
 
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //progressChanged = progress;
                currTimeText.setText("" + (progress + 1));
            }
 
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
 
            public void onStopTrackingTouch(SeekBar seekBar) {
                //toastMessage("seek bar progress:"+progressChanged);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private int getTimeSliderVal()
	{
		return timeSlider.getProgress() + 1;
	}
	
	private void enableUI(boolean bEnabled)
	{
		m_recordButton.setEnabled(bEnabled);
		timeSlider.setEnabled(bEnabled);
	}
	
	private void startRecord()
	{
		enableUI(false);
		new RecordTask().execute(getTimeSliderVal());
	}
	
	private void doRecord(int length)
	{
		try {
			
		    String state = Environment.getExternalStorageState();
		    if (!Environment.MEDIA_MOUNTED.equals(state))
		    {
		    	// couldn't access external storage
		        return;// false;
		    }

		    // @TODO: Apparently something doesn't like using a file path other than at the sdcard level...
		    // Figure out why and fix it.
/*		    File parentDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
		    if (!parentDir.exists())
		    	parentDir.mkdirs();*/
		    
		    String filePath = /*parentDir.getAbsolutePath() +*/ "/sdcard/test.mp4";

			Process process;

			process = Runtime.getRuntime().exec("su");

			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("/system/bin/screenrecord --time-limit " + length + " " + filePath + "\n");

			// Close the terminal
			os.writeBytes("exit\n");
			os.flush();
			//os.close();

			try {
				process.waitFor();
				//return true;

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//return false;
	}
	
	private void endRecord()
	{
		String message = "Recording finished!";
		//if (!success)
		//	message = "Recording failed.";
    	toastMessage(message);
        enableUI(true);
	}
	
		
	private void toastMessage(String message)
	{
		Context context = getApplicationContext();
		Toast toast;
		int duration = Toast.LENGTH_SHORT;
		
		toast = Toast.makeText(context, message, duration);
		toast.show();
	}
	
	
	private class RecordTask extends AsyncTask<Integer, Void, Void> {
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() 
	     * @return */
	    protected Void doInBackground(Integer...integers) {
	        doRecord(integers[0]);
	        return null;
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(Void result) {
	    	endRecord();
	    }
	    

	}

}
