package com.jwplayer.opensourcedemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

import com.google.android.gms.cast.framework.CastContext;
import com.jwplayer.pub.api.JWPlayer;
import com.jwplayer.pub.api.configuration.PlayerConfig;
import com.jwplayer.pub.api.events.EventType;
import com.jwplayer.pub.api.events.FullscreenEvent;
import com.jwplayer.pub.api.events.listeners.VideoPlayerEvents;
import com.jwplayer.pub.api.license.LicenseUtil;
import com.jwplayer.pub.api.media.captions.Caption;
import com.jwplayer.pub.api.media.captions.CaptionType;
import com.jwplayer.pub.api.media.playlists.PlaylistItem;
import com.jwplayer.pub.view.JWPlayerView;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class JWPlayerViewExample extends AppCompatActivity
		implements VideoPlayerEvents.OnFullscreenListener {

	private JWPlayerView mPlayerView;

	private CastContext mCastContext;

	private CallbackScreen mCallbackScreen;
	private JWPlayer mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jwplayerview);

		// TODO: Add your license key
		new LicenseUtil().setLicenseKey(this, "" );
		mPlayerView = findViewById(R.id.jwplayer);
		mPlayer = mPlayerView.getPlayer();


		// Handle hiding/showing of ActionBar
		mPlayer.addListener(EventType.FULLSCREEN, this);

		// Keep the screen on during playback
		new KeepScreenOnHandler(mPlayer, getWindow());

		// Event Logging
		mCallbackScreen = findViewById(R.id.callback_screen);
		mCallbackScreen.registerListeners(mPlayer);

		/* The following example loads captions correctly, but we have to specify both the
		 * video: https://cdn.jwplayer.com/manifests/ljKLME9W.m3u8
		 * AND
		 * captions: https://cdn.jwplayer.com/tracks/VIDr5JXz.vtt
		 */
//		ArrayList<Caption> captionTracks = new ArrayList();
//		Caption captionEn = new Caption.Builder()
//				.file("https://cdn.jwplayer.com/tracks/VIDr5JXz.vtt")
//				.label("English")
//				.kind(CaptionType.CAPTIONS)
//				.isDefault(true)
//				.build();
//		captionTracks.add(captionEn);
//
//
//		PlaylistItem playlistItem = new PlaylistItem.Builder()
//				.file("https://cdn.jwplayer.com/manifests/ljKLME9W.m3u8")
//				.tracks(captionTracks)
//				.build();
//		ArrayList<PlaylistItem> playlist = new ArrayList();
//		playlist.add(playlistItem);
//		PlayerConfig config = new PlayerConfig.Builder()
//				.playlist(playlist)
//				.build();

		/*
		 * I was hoping to be able to do something like the following, load both the video and
		 * captions while only specifying the mediaId.
		 */
		String mediaId = "ljKLME9W";

		PlaylistItem playlistItem = new PlaylistItem.Builder()
				.mediaId(mediaId)
				.build();
		ArrayList<PlaylistItem> playlist = new ArrayList();
		playlist.add(playlistItem);
		PlayerConfig config = new PlayerConfig.Builder()
				.playlist(playlist)
				.build();

		mPlayer.setup(config);

		// Get a reference to the CastContext
		mCastContext = CastContext.getSharedInstance(this);


	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (!mPlayer.isInPictureInPictureMode()) {
			final boolean isFullscreen = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
			mPlayer.setFullscreen(isFullscreen, true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Exit fullscreen when the user pressed the Back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPlayer.getFullscreen()) {
				mPlayer.setFullscreen(false, true);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onFullscreen(FullscreenEvent fullscreenEvent) {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			if (fullscreenEvent.getFullscreen()) {
				actionBar.hide();
			} else {
				actionBar.show();
			}
		}
	}
}
