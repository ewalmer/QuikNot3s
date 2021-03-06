/**Copyright 2013 Evan Walmer, Miles Camp, Dillon Lynch, Clyde Zuber, Elliot Wehmueller
 * quiknote3s.PlayActivity
 */
package edu.elon.cs.quiknot3s;


import java.io.File;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Play activity is the second activity of quiknot3s that displays 
 * information about the audio file and media controls to control
 * play back of the file.
 * @author Team Socrat3s
 *
 */
public class PlayActivity extends Activity {

	private CameraUpdate camera;
	private LatLng location;
	private GoogleMap map;
	private VideoView videoView;
	private MediaController mediaController;
	private Bundle receiveBundle;
	private String audioPath;
	private String audioName;
	private Double longitude;
	private Double latitude;
	private File audioFile;
	private TextView lat;
	private TextView lng;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        videoView = (VideoView) findViewById(R.id.videoviewaudio);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        receiveBundle = this.getIntent().getExtras();
		audioPath = receiveBundle.getString("audioPath");
		audioName = receiveBundle.getString("audioName");
		longitude = receiveBundle.getDouble("longitude");
		latitude = receiveBundle.getDouble("latitude");
		lat = (TextView) findViewById(R.id.mapLat);
		lng = (TextView) findViewById(R.id.mapLng);
		lat.setText("Latitude: " + latitude);
		lng.setText("Longitude: " + longitude);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		location = new LatLng(latitude, longitude);
		map.moveCamera(CameraUpdateFactory.newLatLng(location));
		map.animateCamera(CameraUpdateFactory.zoomBy(17));
		map.addMarker(new MarkerOptions().position(location).title("Hello world"));
		TextView title = (TextView) findViewById(R.id.textviewname);
		title.setText(audioName);
		videoView.setVideoPath(audioPath);
		videoView.requestFocus();
		videoView.start();
		
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
      //the MediaController will hide after 3 seconds - tap the screen to make it appear again
      mediaController.show(0);
      return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_play, menu);
        return true;
    }

}
