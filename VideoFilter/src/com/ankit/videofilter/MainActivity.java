package com.ankit.videofilter;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.VideoSurfaceView;
import com.google.android.exoplayer.util.PlayerControl;

public class MainActivity extends Activity {
	private static final String TAG = "VideoFilter::MainActivity";

	String path;
	TextView textView;
	private ExoPlayer player;
	MediaCodecVideoTrackRenderer videoRenderer;
	MediaCodecAudioTrackRenderer audioRenderer;
	private VideoSurfaceView surfaceView;

	private MediaController mediaController;
	public static final int RENDERER_COUNT = 2;

	private int playerPosition = 0;
	SampleSource source;
	Uri uri;
	SurfaceView filter;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		path = "";
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.main_textView1);
		surfaceView = (VideoSurfaceView) findViewById(R.id.main_surface_view);

		filter = (SurfaceView) findViewById(R.id.main_filter);

		mediaController = new MediaController(this);
		mediaController.setAnchorView((View) surfaceView);

		surfaceView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (player != null
							&& player.getPlaybackState() == ExoPlayer.STATE_READY) {
						toggleControlsVisibility();
					}
				}
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeVideo();
		if (player != null)
			player.setPlayWhenReady(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		playerPosition = player==null?0:player.getCurrentPosition();
		stopVideo();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	public void fileChooser(View v) {
		FileChooserDialog dialog = new FileChooserDialog(this);
		dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
			public void onFileSelected(Dialog source, File file) {
				source.hide();
				path = file.getAbsolutePath();
				textView.setText(path);
				uri = Uri.parse(path);
				Toast toast = Toast.makeText(source.getContext(),
						"File selected: " + path, Toast.LENGTH_LONG);
				Log.d("File Selected", path);
				toast.show();
			}

			public void onFileSelected(Dialog source, File folder, String name) {
				source.hide();
				Toast toast = Toast.makeText(source.getContext(),
						"File created: " + folder.getName() + "/" + name,
						Toast.LENGTH_LONG);
				toast.show();
			}
		});
		dialog.setFilter(".*mp4|.*avi|.*3gp|.*flv|.*MP4|.*AVI|.*3GP|.*FLV");
		dialog.show();
	}

	public void startVideo(View v) {
		stopVideo();
		playerPosition = 0;
		if (uri != null) {
			initializeVideo();
			player.setPlayWhenReady(true);
		}

	}

	public void initializeVideo() {
		if (uri == null || uri.equals("")) {
			Toast.makeText(getApplicationContext(), "Please select a file!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		source = new FrameworkSampleSource(getApplicationContext(), uri, null,
				RENDERER_COUNT);

		// Build the track renderers
		videoRenderer = new MediaCodecVideoTrackRenderer(source,
				MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
		audioRenderer = new MediaCodecAudioTrackRenderer(source);
		Log.d(TAG, "Audio/video rendered");
		// Build the ExoPlayer and start playback
		player = ExoPlayer.Factory.newInstance(RENDERER_COUNT);
		player.prepare(videoRenderer, audioRenderer);
		Log.d(TAG, "Audio/video Loaded");

		Surface surface = surfaceView.getHolder().getSurface();
		// Pass the surface to the video renderer.
		player.sendMessage(videoRenderer,
				MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);

		mediaController.setMediaPlayer(new PlayerControl(player));
		mediaController.setEnabled(true);
		Log.d(TAG, "media controlls enabled");
		player.seekTo(playerPosition);
	}

	public void stopVideo() {
		if (player != null) {
			player.stop();
			player.release();
			mediaController.setEnabled(false);
			videoRenderer = null;
			audioRenderer = null;
			// playerPosition = 0;
			player = null;
		}
	}

	private void toggleControlsVisibility() {
		if (mediaController.isShowing()) {
			mediaController.hide();
		} else {
			mediaController.show(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);// Menu Resource, Menu
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1:
			Toast.makeText(getApplicationContext(), "Filter Removed",
					Toast.LENGTH_LONG).show();
			filter.setBackgroundColor(getResources().getColor(R.color.white));
			return true;

		case R.id.item2:
			Toast.makeText(getApplicationContext(), "Warm selected",
					Toast.LENGTH_LONG).show();
			filter.setBackgroundColor(getResources().getColor(R.color.warm));

			return true;

		case R.id.item3:
			Toast.makeText(getApplicationContext(), "Cold Selected",
					Toast.LENGTH_LONG).show();
			filter.setBackgroundColor(getResources().getColor(R.color.cold));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
