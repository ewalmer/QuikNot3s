/**Copyright 2013 Evan Walmer, Miles Camp, Dillon Lynch, Clyde Zuber, Elliot Wehmueller
 * quiknote3s.Record Activity
 */
package edu.elon.cs.quiknot3s;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * RecordActivity is the main activity of the app that displays a list view 
 * of all the audio notes. the option to record, delete, and play audio files
 * is accessed from this screen. random change
 * @author Team Socrat3s
 *
 */
public class RecordActivity extends Activity {

	double currentLatitude;
	double currentLongitude;
	private LocationManager locManager;
	private MediaRecorder mediaRecorder = null;
	private String fileName = null;
	private File file;
	boolean isRecording = true;

	private ArrayList fileList;
	protected String[] FILENAMES = new String[] { "Test1", "TestERIC",
			"TestSocrat3s" };
	private ListView listView;
	private ListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		
		
		ImageButton recordButton = (ImageButton) findViewById(R.id.imagebuttonrecord);
		recordButton.setOnClickListener(recordClicker);

	}

	/**
	 * updates the ListView on the screen to accurately display the 
	 * current AudioNotes folder
	 */
	private void updateListView () {
		
		createFileLists();

		listAdapter = new ArrayAdapter<String>(this,
				R.layout.activity_notelist, fileList);

		listView = (ListView) findViewById(R.id.listviewnotes);
		listView.setTextFilterEnabled(true);

		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();

				// adds the audio path to a bundle
				FileList current = (FileList) fileList.get(position);

				String audioPath = current.getUri();
				String audioName = current.getFileName();
				Bundle sendBundle = new Bundle();
				sendBundle.putString("audioPath", audioPath);
				sendBundle.putString("audioName", audioName);
				// creates an intent add the
				Intent i = new Intent(RecordActivity.this, PlayActivity.class);
				i.putExtras(sendBundle);
				startActivity(i);

			}
		});
		
		/**
		 * long clicks are used to delete
		 */
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileList current = (FileList) fileList.get(position);

				String audioPath = current.getUri();
				file = new File(audioPath);
				deleteDialog();
				return false;
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateListView();
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_record, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}
		locManager.removeUpdates(locationListener);
	}

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			//grab location
			currentLatitude = location.getLatitude();
			currentLongitude = location.getLongitude();
			
		}

		@Override
		public void onProviderDisabled(String arg0) {}

		@Override
		public void onProviderEnabled(String arg0) {}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
		
	};
	
	/**
	 * creates the file list the ListView will access to display on the main screen
	 */
	private void createFileLists() {

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			File path = getExternalFilesDir("AudioNotes");

			fileList = new ArrayList();
			String doodPath = path.toString();
			File list = new File(doodPath);
			File[] files = list.listFiles();
			for (File inFile : files) {
				if (inFile.isFile()) {
					int lastSlash = inFile.toString().lastIndexOf("/");
					String fileName = inFile.toString()
							.substring(lastSlash + 1);
					FileList current = new FileList(fileName, inFile.toString());
					fileList.add(current);
				}
			}
			Collections.sort(fileList);
		}
	}

	private void saveDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Save Note?");
		adb.setMessage("Would you like to save this note?");
		adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Action for 'Ok' Button
				updateListView();
			}
		});
		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				file.delete();
				dialog.cancel();
			}
		});
		// adb.setIcon(R.drawable.icon);
		adb.show();
		
	}
	
	private void deleteDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Delete Note");
		adb.setMessage("Are you sure you want to delete this note?");
		adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Action for 'Ok' Button
				file.delete();
				updateListView();
			}
		});
		adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				dialog.cancel();
			}
		});
		// adb.setIcon(R.drawable.icon);
		adb.show();
		
	}

	
	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
			saveDialog();
		}
	}

	// creates the correct filename and path for new recording
	private void doFileName() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			File path = getExternalFilesDir("AudioNotes");

			// Create filename from date and time
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd-HH_mm_ss");
			Date now = new Date();
			String saveName = formatter.format(now) + ".3gp";

			file = new File(path, saveName);
			fileName = file.toString();
		}
	}

	private void startRecording() {

		doFileName();
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediaRecorder.setOutputFile(fileName);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mediaRecorder.prepare();
		} catch (IOException e) {
			Log.e("mediaRecorder", "prepare() failed");
		}

		mediaRecorder.start();
	}

	private void stopRecording() {
		mediaRecorder.stop();
		mediaRecorder.release();
		mediaRecorder = null;
	}

	OnClickListener recordClicker = new OnClickListener() {
		public void onClick(View v) {
			onRecord(isRecording);
			if (isRecording) {
				v.setSelected(true);
			} else {
				v.setSelected(false);
			}
			isRecording = !isRecording;
		}
	};

}